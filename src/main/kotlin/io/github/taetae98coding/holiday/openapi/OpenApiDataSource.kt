package io.github.taetae98coding.holiday.openapi

import io.github.taetae98coding.holiday.file.FileDataSource
import io.github.taetae98coding.holiday.openapi.entity.OpenApiResult
import io.github.taetae98coding.holiday.openapi.kasi.OpenApiKasi
import io.github.taetae98coding.holiday.openapi.kasi.OpenApiKasiItem
import io.github.taetae98coding.holiday.openapi.kasi.toItemList
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.DefaultJson
import io.ktor.serialization.kotlinx.json.json
import java.io.File
import kotlin.time.Clock
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.YearMonth
import kotlinx.datetime.number
import kotlinx.datetime.todayIn
import kotlinx.datetime.yearMonth
import kotlinx.serialization.json.Json

data object OpenApiDataSource {
    private val semaphore = Semaphore(4)

    private val apiJson by lazy {
        Json(DefaultJson) {
            ignoreUnknownKeys = true
        }
    }

    private val client by lazy {
        HttpClient {
            install(DefaultRequest) {
                url.takeFrom("https://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/")
                url.parameters.append("serviceKey", System.getenv("SERVICE_KEY"))
                url.parameters.append("numOfRows", "20")
                url.parameters.append("_type", "json")
            }

            install(ContentNegotiation) {
                json(apiJson)
            }
        }
    }

    suspend fun getHoliday(year: Int): List<OpenApiKasiItem> {
        return getInternal(year, "holiday", "getHoliDeInfo")
    }

    suspend fun getRest(year: Int): List<OpenApiKasiItem> {
        return getInternal(year, "rest", "getRestDeInfo")
    }

    suspend fun getAnniversary(year: Int): List<OpenApiKasiItem> {
        return getInternal(year, "anniversary", "getAnniversaryInfo")
    }

    suspend fun getDivisions(year: Int): List<OpenApiKasiItem> {
        return getInternal(year, "divisions", "get24DivisionsInfo")
    }

    suspend fun getSundryDayInfo(year: Int): List<OpenApiKasiItem> {
        return getInternal(year, "sundry", "getSundryDayInfo")
    }

    private suspend fun getInternal(year: Int, path: String, api: String): List<OpenApiKasiItem> {
        return coroutineScope {
            (1..12).map { month -> YearMonth(year, month) }
                .map { yearMonth -> async { getInternal(yearMonth, path, api) } }
                .awaitAll()
                .flatten()
        }
    }

    private suspend fun getInternal(yearMonth: YearMonth, path: String, api: String): List<OpenApiKasiItem> {
        val useCache = useCache(yearMonth)
        val file = File("docs/kasi/$path", "$yearMonth.json")

        return if (useCache && file.exists()) {
            FileDataSource.read(file)
        } else {
            val response = semaphore.withPermit {
                client.get(api) {
                    parameter("solYear", yearMonth.year)
                    parameter("solMonth", yearMonth.month.number.toString().padStart(2, '0'))
                }
            }
            val body = response.body<OpenApiResult<OpenApiKasi>>()

            if (response.status != HttpStatusCode.OK && body.response.header.code != "00") {
                throw Exception("Status=${response.status}, Code=${body.response.header.code}, Message=${body.response.header.message}")
            }

            body.response.body.toItemList(apiJson)
                .also { value -> FileDataSource.write(value, file) }
        }
    }

    private fun useCache(yearMonth: YearMonth): Boolean {
        if (System.getenv("FETCH_ENFORCE") == "true") {
            return false
        }

        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val isPast = yearMonth < today.yearMonth

        return isPast
    }
}
