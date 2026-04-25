package org.example

import jakarta.servlet.http.HttpServletRequest
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate

private val log = KotlinLogging.logger {}

@RestController
@RequestMapping("/proxy/**")
class ProxyController {

    private val restTemplate = RestTemplate()

    @Value("\${proxy.target-base-url}")
    private lateinit var targetBaseUrl: String

    @RequestMapping(
        method = [
            RequestMethod.GET, RequestMethod.POST,
            RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH
        ]
    )
    fun proxy(request: HttpServletRequest, @RequestBody(required = false) body: String?): ResponseEntity<ByteArray> {
        val path = request.requestURI.removePrefix("/proxy")
        val targetUrl = "$targetBaseUrl$path${request.queryString?.let { "?$it" } ?: ""}"

        val headers = HttpHeaders()
        request.headerNames.asSequence()
            .filter { it.lowercase() !in HOP_BY_HOP }
            .forEach { headers[it] = request.getHeaders(it).toList() }

        val entity = HttpEntity(body, headers)

        log.info { "Proxying ${request.method} $targetUrl" }

        return restTemplate.exchange(
            targetUrl,
            HttpMethod.valueOf(request.method),
            entity,
            ByteArray::class.java
        )
    }

    companion object {
        private val HOP_BY_HOP = setOf(
            "host", "connection", "keep-alive",
            "proxy-authenticate", "proxy-authorization",
            "te", "trailer", "transfer-encoding", "upgrade"
        )
    }
}
