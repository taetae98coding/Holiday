package io.github.taetae98coding.holiday.openapi.kasi

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenApiKasiSingleItems(
    @SerialName("item")
    val item: OpenApiKasiItem,
)
