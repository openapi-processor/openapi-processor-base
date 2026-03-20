/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.swagger

import io.openapiprocessor.core.openapi.Schema
import io.openapiprocessor.core.openapi.Server
import io.openapiprocessor.core.openapi.OpenApi as OpenApiOpenApi
import io.openapiprocessor.core.openapi.Path as OpenApiPath
import io.openapiprocessor.core.openapi.RefResolver as OpenApiRefResolver
import io.swagger.v3.oas.models.PathItem as SwaggerPath
import io.swagger.v3.oas.models.media.Schema as SwaggerSchema
import io.swagger.v3.parser.core.models.SwaggerParseResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Swagger parser result.
 */
class OpenApi(private val result: SwaggerParseResult): OpenApiOpenApi {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    override fun getServers(): List<Server> {
        val servers = mutableListOf<Server>()

        result.openAPI.servers.forEach { server ->
            servers.add(Server(server))
        }

        return servers
    }

    override fun getPaths(): Map<String, OpenApiPath> {
        val paths = linkedMapOf<String, OpenApiPath>()

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

    override fun getRefResolver(): OpenApiRefResolver = RefResolver (result.openAPI)

    override fun printWarnings() {
        result.messages?.forEach {
            log.warn(it)
        }
    }

    override fun hasWarnings(): Boolean {
        return result.messages.isNotEmpty()
    }

}
