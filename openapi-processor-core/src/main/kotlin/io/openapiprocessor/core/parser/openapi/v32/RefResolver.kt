/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi.v32

import io.openapiprocessor.core.openapi.NamedSchema
import io.openapiprocessor.core.openapi.getRefName
import io.openapiparser.model.v32.OpenApi as OpenApi32
import io.openapiparser.model.v32.Schema as Schema32
import io.openapiprocessor.core.openapi.RefResolver as OpenApiRefResolver
import io.openapiprocessor.core.openapi.Schema as OpenApiSchema

/**
 * openapi-parser $ref resolver.
 */
class RefResolver(private val api: OpenApi32): OpenApiRefResolver {

    override fun resolve(ref: OpenApiSchema): NamedSchema {
        val schema: Schema32 = (ref as Schema).schema
        return NamedSchema(getRefName(ref.getRef()!!), Schema(schema.refObject))
    }
}
