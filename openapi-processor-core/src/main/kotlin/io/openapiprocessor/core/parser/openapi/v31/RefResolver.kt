/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi.v31

import io.openapiprocessor.core.openapi.NamedSchema
import io.openapiprocessor.core.openapi.getRefName
import io.openapiparser.model.v31.OpenApi as OpenApi31
import io.openapiparser.model.v31.Schema as Schema31
import io.openapiprocessor.core.openapi.RefResolver as OpenApiRefResolver
import io.openapiprocessor.core.openapi.Schema as OpenApiSchema

/**
 * openapi-parser $ref resolver.
 */
class RefResolver(private val api: OpenApi31): OpenApiRefResolver {

    override fun resolve(ref: OpenApiSchema): NamedSchema {
        val schema: Schema31 = (ref as Schema).schema
        return NamedSchema(getRefName(ref.getRef()!!), Schema(schema.refObject))
    }
}
