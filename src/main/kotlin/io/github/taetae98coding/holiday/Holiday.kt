package io.github.taetae98coding.holiday

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Holiday(
    @SerialName("name")
    val name: String,
    @SerialName("isHoliday")
    val isHoliday: Boolean,
    @SerialName("start")
    val start: LocalDate,
    @SerialName("endInclusive")
    val endInclusive: LocalDate,
)

fun List<Holiday>.holidayFold(): List<Holiday> {
    return groupBy(Holiday::name).values
        .map { list -> list.sortedBy(Holiday::start) }
        .map { list ->
            list.fold(emptyList<Holiday>()) { acc, holiday ->
                if (acc.isEmpty()) {
                    return@fold listOf(holiday)
                }

                val last = acc.last()
                if (last.endInclusive.plus(1, DateTimeUnit.DAY) == holiday.start) {
                    acc.dropLast(1) + last.copy(start = last.start, endInclusive = holiday.endInclusive)
                } else {
                    acc + holiday
                }
            }
        }
        .flatten()
}
