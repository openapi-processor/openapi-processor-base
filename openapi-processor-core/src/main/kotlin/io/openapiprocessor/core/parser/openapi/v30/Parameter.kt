/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi.v30

import io.openapiparser.model.v30.Parameter as Parameter30
import io.openapiprocessor.core.openapi.Parameter as OpenApiParameter
import io.openapiprocessor.core.openapi.Schema as OpenApiSchema

/**
 * openapi-parser Parameter abstraction.
 */
class Parameter(private val parameter: Parameter30): OpenApiParameter {

    override fun getIn(): String = parameter.`in`

    override fun getName(): String = parameter.name

    override fun getSchema(): OpenApiSchema = Schema (parameter.schema!!)

    override fun isRequired(): Boolean = parameter.required

    override fun isDeprecated(): Boolean = parameter.deprecated

    override val description: String?
        get() = parameter.description

}
