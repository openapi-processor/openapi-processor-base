/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping.matcher

import io.openapiprocessor.core.converter.mapping.MappingSchema
import io.openapiprocessor.core.converter.mapping.NameTypeMapping

/**
 * [io.openapiprocessor.core.converter.mapping.MappingFinder] matcher for parameter type mappings.
 */
class ParameterTypeMatcher(private val schema: MappingSchema): (NameTypeMapping) -> Boolean {

    override fun invoke(mapping: NameTypeMapping): Boolean {
        return mapping.parameterName == schema.getName()
    }

}
