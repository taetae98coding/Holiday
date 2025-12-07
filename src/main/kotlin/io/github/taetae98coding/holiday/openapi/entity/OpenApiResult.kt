package io.github.taetae98coding.holiday.openapi.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenApiResult<T>(
    @SerialName("response")
    val response: OpenApiResponse<T>,
)
