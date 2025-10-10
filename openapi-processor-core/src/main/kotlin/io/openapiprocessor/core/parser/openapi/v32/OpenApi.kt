/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi.v32

import io.openapiparser.model.v32.OpenApi as OpenApi32
import io.openapiparser.model.v32.PathItem as PathItem32
import io.openapiparser.model.v32.Schema as Schema32
import io.openapiprocessor.core.parser.Schema
import io.openapiprocessor.core.parser.Server
import io.openapiprocessor.core.parser.Path
import io.openapiprocessor.core.parser.OpenApi as ParserOpenApi
import io.openapiprocessor.core.parser.RefResolver as ParserRefResolver
import io.openapiprocessor.core.parser.openapi.v32.Path as ParserPath32
import io.openapiprocessor.core.parser.openapi.v32.Schema as ParserSchema32
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class OpenApi(
    private val api: OpenApi32
) : ParserOpenApi {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    override fun getServers(): List<Server> {
        val servers = mutableListOf<Server>()

        api.servers.forEach { server ->
            servers.add(Server(server))
        }

        return servers
    }

    override fun getPaths(): Map<String, Path> {
        val paths = linkedMapOf<String, Path>()

        api.paths?.pathItems?.forEach { (name: String, value: PathItem32) ->
            var path = value
            if (path.isRef) {
                path = path.refObject
            }
            paths[name] = ParserPath32(name, path)
        }

        return paths
    }

    override fun getSchemas(): Map<String, Schema> {
        val schemas = linkedMapOf<String, Schema>()

        api.components?.schemas?.forEach { (name: String, schema: Schema32) ->
            schemas[name] = ParserSchema32(schema)
        }

        return schemas
    }

    override fun getRefResolver(): ParserRefResolver = RefResolver(api)

    override fun printWarnings() {
        // unused
    }

    override fun hasWarnings(): Boolean {
        return false
    }
}
