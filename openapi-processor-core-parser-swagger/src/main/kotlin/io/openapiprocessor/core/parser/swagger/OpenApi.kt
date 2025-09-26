/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.swagger

import io.openapiprocessor.core.parser.Schema
import io.openapiprocessor.core.parser.Server
import io.openapiprocessor.core.parser.OpenApi as ParserOpenApi
import io.openapiprocessor.core.parser.Path as ParserPath
import io.openapiprocessor.core.parser.RefResolver as ParserRefResolver
import io.swagger.v3.oas.models.PathItem as SwaggerPath
import io.swagger.v3.oas.models.media.Schema as SwaggerSchema
import io.swagger.v3.parser.core.models.SwaggerParseResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Swagger parser result.
 */
class OpenApi(private val result: SwaggerParseResult): ParserOpenApi {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    override fun getServers(): List<Server> {
        val servers = mutableListOf<Server>()

        result.openAPI.servers.forEach { server ->
            servers.add(Server(server))
        }

        return servers
    }

    override fun getPaths(): Map<String, ParserPath> {
        val paths = linkedMapOf<String, ParserPath>()

        result.openAPI.paths.forEach { (name: String, value: SwaggerPath) ->
            paths[name] = Path(name, value, RefResolverNative(result.openAPI))
        }

        return paths
    }

    override fun getSchemas(): Map<String, Schema> {
        val schemas = linkedMapOf<String, Schema>()

        result.openAPI.components?.schemas?.forEach { (name: String, schema: SwaggerSchema<*>) ->
            schemas[name] = Schema(schema)
        }

        return schemas
    }

    override fun getRefResolver(): ParserRefResolver = RefResolver (result.openAPI)

    override fun printWarnings() {
        result.messages?.forEach {
            log.warn(it)
        }
    }

    override fun hasWarnings(): Boolean {
        return result.messages.isNotEmpty()
    }

}
