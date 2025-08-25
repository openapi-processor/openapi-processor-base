/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi.v31

import io.openapiprocessor.core.parser.NamedSchema
import io.openapiprocessor.core.parser.getRefName
import io.openapiprocessor.core.parser.RefResolver as ParserRefResolver
import io.openapiprocessor.core.parser.Schema as ParserSchema
import io.openapiparser.model.v31.OpenApi as OpenApi31
import io.openapiparser.model.v31.Schema as Schema31

/**
 * openapi-parser $ref resolver.
 */
class RefResolver(private val api: OpenApi31): ParserRefResolver {

    override fun resolve(ref: ParserSchema): NamedSchema {
        val schema: Schema31 = (ref as Schema).schema
        return NamedSchema(getRefName(ref.getRef()!!), Schema(schema.refObject))
    }
}
