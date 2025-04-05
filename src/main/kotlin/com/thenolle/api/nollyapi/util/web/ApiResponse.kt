package com.thenolle.api.nollyapi.util.web

import kotlinx.serialization.Serializable

/**
 * A generic data class representing the structure of an API response.
 *
 * @param T The type of the data returned in the response.
 * @property success A boolean indicating whether the request was successful.
 * @property data The data returned in the response (null if there is no data).
 * @property error An optional error message, in case of failure.
 */
@Serializable
data class ApiResponse<T>(
    val success: Boolean, val data: T? = null, val error: String? = null
)
