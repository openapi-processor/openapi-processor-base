/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi.v32

import io.github.stduritemplate.StdUriTemplate
import java.net.URI
import io.openapiparser.model.v32.Server as Server32
import io.openapiprocessor.core.parser.Server as ParserServer

class Server(private val server: Server32): ParserServer {

    override fun getUri(): URI {
        val variables = mutableMapOf<String, Any>()

        server.variables.forEach { variable ->
            variables[variable.key] = variable.value.default
        }

        return URI.create(StdUriTemplate.expand(server.url, variables))
    }
}
