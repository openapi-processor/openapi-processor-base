/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi.v30

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import io.openapiparser.model.v30.OpenApi as OpenApi30
import io.openapiparser.model.v30.PathItem as PathItem30
import io.openapiparser.model.v30.Schema as Schema30
import io.openapiprocessor.core.openapi.OpenApi as OpenApiOpenApi
import io.openapiprocessor.core.openapi.Path as OpenApiPath
import io.openapiprocessor.core.openapi.RefResolver as OpenApiRefResolver
import io.openapiprocessor.core.openapi.Schema as OpenApiSchema
import io.openapiprocessor.core.openapi.Server as OpenApiServer
import io.openapiprocessor.core.parser.openapi.v30.Path as ParserPath30
import io.openapiprocessor.core.parser.openapi.v30.Schema as ParserSchema30


/**
 * openapi-parser result.
 */
open class OpenApi(
    private val api: OpenApi30
): OpenApiOpenApi {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    override fun getServers(): List<OpenApiServer> {
        val servers = mutableListOf<OpenApiServer>()

        api.servers.forEach { server ->
            servers.add(Server(server))
        }

        return servers
    }

    override fun getPaths(): Map<String, OpenApiPath> {
        val paths = linkedMapOf<String, OpenApiPath>()

        api.paths.pathItems.forEach { (name: String, value: PathItem30) ->
            var path = value
            if (path.isRef) {
                path = path.refObject
            }
            paths[name] = ParserPath30(name, path)
        }

        return paths
    }

    override fun getSchemas(): Map<String, OpenApiSchema> {
        val schemas = linkedMapOf<String, OpenApiSchema>()

        api.components?.schemas?.forEach { (name: String, schema: Schema30) ->
            schemas[name] = ParserSchema30(schema)
        }

        return schemas
    }

    override fun getRefResolver(): OpenApiRefResolver = RefResolver(api)

    override fun printWarnings() {
        // unused
    }

    override fun hasWarnings(): Boolean {
        return false;
    }

}

