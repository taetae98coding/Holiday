package io.github.taetae98coding.holiday.openapi.kasi

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class OpenApiKasiItemKind(
    val kind: String,
) {
    companion object {
        val None = OpenApiKasiItemKind("")
        val NationalHoliday = OpenApiKasiItemKind("01")
        val Anniversary = OpenApiKasiItemKind("02")
        val TwentyFourSolarTerms = OpenApiKasiItemKind("03")
        val MiscellaneousHoliday = OpenApiKasiItemKind("04")
    }
}
