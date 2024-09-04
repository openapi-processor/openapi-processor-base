/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.swagger

import io.github.stduritemplate.StdUriTemplate
import java.net.URI
import io.openapiprocessor.core.parser.Server as ParserServer
import io.swagger.v3.oas.models.servers.Server as SwaggerServer

class Server(private val server: SwaggerServer): ParserServer {

    override fun getUri(): URI {
        val variables = mutableMapOf<String, Any>()

        server.variables.forEach { variable ->
            variables[variable.key] = variable.value.default
        }

        return URI.create(StdUriTemplate.expand(server.url, variables))
    }
}
