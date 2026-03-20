/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi4j

import io.openapiprocessor.core.openapi.Parameter as OpenApiParameter
import io.openapiprocessor.core.openapi.Schema as OpenApiSchema
import org.openapi4j.parser.model.v3.Parameter as O4jParameter

/**
 * openapi4j Parameter abstraction.
 */
class Parameter(private val parameter: O4jParameter): OpenApiParameter {

    override fun getIn(): String = parameter.`in`

    override fun getName(): String = parameter.name

    override fun getSchema(): OpenApiSchema = Schema (parameter.schema)

    override fun isRequired(): Boolean = if(parameter.required != null) {
        parameter.required
    } else {
        false
    }

    override fun isDeprecated(): Boolean = parameter.deprecated ?: false

    override val description: String?
        get() = parameter.description

}
