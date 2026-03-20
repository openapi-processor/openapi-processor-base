/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.swagger

import io.openapiprocessor.core.openapi.getRefName
import io.openapiprocessor.core.openapi.NamedSchema as OpenApiNamedSchema
import io.openapiprocessor.core.openapi.RefResolver as OpenApiRefResolver
import io.openapiprocessor.core.openapi.Schema as OpenApiSchema
import io.swagger.v3.oas.models.media.Schema as SwaggerSchema
import io.swagger.v3.oas.models.OpenAPI

/**
 * Swagger $ref resolver.
 */
class RefResolver(private val openapi: OpenAPI): OpenApiRefResolver {

    override fun resolve(ref: OpenApiSchema): OpenApiNamedSchema {
        val refName = getRefName(ref.getRef()!!)

        val schema: SwaggerSchema<*>? = openapi.components?.schemas?.get(refName)
        if (schema == null) {
            throw Exception("failed to resolve ${ref.getRef()}")
        }

        return OpenApiNamedSchema(refName, Schema(schema))
    }
}
