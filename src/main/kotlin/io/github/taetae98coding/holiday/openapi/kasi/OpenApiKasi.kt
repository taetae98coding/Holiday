package io.github.taetae98coding.holiday.openapi.kasi

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

@Serializable
data class OpenApiKasi(
    @SerialName("items")
    val items: JsonElement,
    @SerialName("numOfRows")
    val row: Int,
    @SerialName("pageNo")
    val page: Int,
    @SerialName("totalCount")
    val count: Int,
)

fun OpenApiKasi.toItemList(json: Json): List<OpenApiKasiItem> {
    return when (count) {
        0 -> emptyList()
        1 -> listOf(json.decodeFromJsonElement<OpenApiKasiSingleItems>(items).item)
        else -> json.decodeFromJsonElement<OpenApiKasiMultiItems>(items).item
    }
}
