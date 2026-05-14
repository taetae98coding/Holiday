package io.github.taetae98coding.holiday

import io.github.taetae98coding.holiday.file.FileDataSource
import io.github.taetae98coding.holiday.openapi.OpenApiDataSource
import io.github.taetae98coding.holiday.openapi.kasi.OpenApiKasiItem
import java.io.File
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.YearMonth
import kotlinx.datetime.yearMonth

suspend fun main() {
    val startYear = System.getenv("START_YEAR").toInt()
    val endInclusiveYear = System.getenv("END_INCLUSIVE_YEAR").toInt()

    println("RUN $startYear ~ $endInclusiveYear")

    coroutineScope {
        (startYear..endInclusiveYear).map { year -> async { updateHolidayJsonFile(year) } }
            .awaitAll()
    }
}

suspend fun updateHolidayJsonFile(year: Int) {
    val kasiItems = coroutineScope {
        listOf(
            async { OpenApiDataSource.getRest(year) },
            async { OpenApiDataSource.getHoliday(year) },
            async { OpenApiDataSource.getAnniversary(year) },
            async { OpenApiDataSource.getDivisions(year) },
            async { OpenApiDataSource.getSundryDayInfo(year) },
        )
            .awaitAll()
            .flatten()
            .distinctBy { it.name.replace(" ", "") to it.date }
    }

    val holidays = kasiItems.map(OpenApiKasiItem::toHoliday)
        .holidayFold()
        .sortedWith { a, b ->
            if (a.start != b.start) return@sortedWith compareValues(a.start, b.start)
            if (a.name != b.name) return@sortedWith compareValues(a.name, b.name)

            0
        }

    coroutineScope {
        writeYearApi(year, holidays)
        (1..12).map { month -> YearMonth(year, month) }
            .forEach { yearMonth -> launch { writeYearMonthApi(yearMonth, holidays.filter { it.start.yearMonth <= yearMonth && yearMonth <= it.endInclusive.yearMonth }) } }
    }
}

suspend fun writeYearApi(year: Int, value: List<Holiday>) {
    FileDataSource.write(value, File("docs/holiday", "$year.json"))
}

suspend fun writeYearMonthApi(yearMonth: YearMonth, value: List<Holiday>) {
    FileDataSource.write(value, File("docs/holiday", "$yearMonth.json"))
}

fun OpenApiKasiItem.toHoliday(): Holiday {
    return Holiday(
        name = prettyName,
        isHoliday = isHoliday,
        start = date,
        endInclusive = date,
    )
}
