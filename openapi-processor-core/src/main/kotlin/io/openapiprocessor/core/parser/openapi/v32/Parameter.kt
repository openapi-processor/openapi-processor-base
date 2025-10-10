/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi.v32

import io.openapiparser.model.v32.Parameter as Parameter32
import io.openapiprocessor.core.parser.Parameter as ParserParameter
import io.openapiprocessor.core.parser.Schema as ParserSchema

/**
 * openapi-parser Parameter abstraction.
 */
class Parameter(private val parameter: Parameter32): ParserParameter {

    override fun getIn(): String = parameter.`in`

    override fun getName(): String = parameter.name

    override fun getSchema(): ParserSchema = Schema (parameter.schema!!)

    override fun isRequired(): Boolean = parameter.required

    override fun isDeprecated(): Boolean = parameter.deprecated

    override val description: String?
        get() = parameter.description
}
