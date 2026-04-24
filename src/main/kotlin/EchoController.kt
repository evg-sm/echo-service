package org.example

import jakarta.servlet.http.HttpServletRequest
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

private val log = KotlinLogging.logger {}

@RestController
@RequestMapping("/**")
class EchoController {

    @RequestMapping(
        method = [
            RequestMethod.GET,
            RequestMethod.POST,
            RequestMethod.PUT,
            RequestMethod.DELETE,
            RequestMethod.PATCH
        ]
    )
    fun logRequest(
        request: HttpServletRequest,
        @RequestBody(required = false) body: String?
    ): ResponseEntity<String> {
        log.info {
            "${request.method} ${request.requestURI} | " +
                    "Headers: ${request.headerNames.asSequence().associateWith { request.getHeader(it) }} | " +
                    "Body: $body"
        }
        return ResponseEntity.ok("OK")
    }
}
