/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi.v31

import io.github.stduritemplate.StdUriTemplate
import java.net.URI
import io.openapiparser.model.v31.Server as Server31
import io.openapiprocessor.core.parser.Server as ParserServer

class Server(private val server: Server31): ParserServer {

    override fun getUri(): URI {
        val variables = mutableMapOf<String, Any>()

        server.variables.forEach { variable ->
            variables[variable.key] = variable.value.default
        }

        return URI.create(StdUriTemplate.expand(server.url, variables))
    }
}
