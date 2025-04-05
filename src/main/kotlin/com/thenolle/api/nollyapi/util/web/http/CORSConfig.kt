package com.thenolle.api.nollyapi.util.web.http

import io.ktor.server.plugins.cors.*

/**
 * Configures the default CORS (Cross-Origin Resource Sharing) settings for the application.
 *
 * This configuration allows non-simple content types, disables same-origin restrictions, allows credentials,
 * and specifically allows hosts from `localhost` with HTTP and HTTPS schemes.
 */
fun CORSConfig.defaultCorsConfig() {
    allowNonSimpleContentTypes = true
    allowSameOrigin = false
    allowCredentials = true
    allowHost("localhost", schemes = listOf("http", "https"))
}