/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi.v30

import io.openapiprocessor.core.openapi.NamedSchema
import io.openapiprocessor.core.openapi.getRefName
import io.openapiparser.model.v30.OpenApi as OpenApi30
import io.openapiparser.model.v30.Schema as Schema30
import io.openapiprocessor.core.openapi.RefResolver as OpenApiRefResolver
import io.openapiprocessor.core.openapi.Schema as OpenApiSchema

/**
 * openapi-parser $ref resolver.
 */
class RefResolver(private val api: OpenApi30): OpenApiRefResolver {

    override fun resolve(ref: OpenApiSchema): NamedSchema {
        val schema: Schema30 = (ref as Schema).schema
        return NamedSchema(getRefName(ref.getRef()!!), Schema(schema.refObject))
    }
}
