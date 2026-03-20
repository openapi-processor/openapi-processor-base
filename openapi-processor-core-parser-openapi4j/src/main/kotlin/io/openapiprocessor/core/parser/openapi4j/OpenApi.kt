/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi4j

import io.openapiprocessor.core.openapi.Schema
import org.openapi4j.core.validation.ValidationResults
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import io.openapiprocessor.core.openapi.OpenApi as OpenApiOpenApi
import io.openapiprocessor.core.openapi.Path as OpenApiPath
import io.openapiprocessor.core.openapi.RefResolver as OpenApiRefResolver
import io.openapiprocessor.core.openapi.Schema as OpenApiSchema
import io.openapiprocessor.core.openapi.Server as OpenApiServer
import org.openapi4j.parser.model.v3.OpenApi3 as O4jOpenApi
import org.openapi4j.parser.model.v3.Path as O4jPath
import org.openapi4j.parser.model.v3.Schema as O4jSchema

/**
 * openapi4j parser result.
 */
class OpenApi(
    private val api: O4jOpenApi,
    private val validations: ValidationResults,
): OpenApiOpenApi {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    private val refResolver: RefResolverNative = RefResolverNative(api)

    override fun getServers(): List<OpenApiServer> {
        val servers = mutableListOf<OpenApiServer>()

        api.servers.forEach { server ->
            servers.add(Server(server))
        }

        return servers
    }

    override fun getPaths(): Map<String, OpenApiPath> {
        val paths = linkedMapOf<String, OpenApiPath>()

        api.paths.forEach { (name: String, value: O4jPath) ->
            var path = value
            if (path.isRef) {
                path = refResolver.resolve(path)
            }

            paths[name] = Path(name, path, refResolver)
        }

        return paths
    }

    override fun getSchemas(): Map<String, Schema> {
        val schemas = linkedMapOf<String, OpenApiSchema>()

        api.components?.schemas?.forEach { (name: String, schema: O4jSchema) ->
            schemas[name] = Schema(schema)
        }

        return schemas
    }

    override fun getRefResolver(): OpenApiRefResolver = RefResolver (api)

    override fun printWarnings() {
        validations.items()
            .forEach {
                log.warn("{} - {} ({}}", it.severity(), it.message(), it.code())
            }
    }

    override fun hasWarnings(): Boolean {
        return validations.items().isNotEmpty()
    }

}
