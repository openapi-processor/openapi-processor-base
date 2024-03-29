/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model.datatypes

import io.openapiprocessor.core.converter.mapping.ParameterValue

/**
 * additional annotation type.
 */
class AnnotationDataType(
    private val name: String,
    private val pkg: String,
    private val parameters: LinkedHashMap<String, ParameterValue>?
): DataType {

    override fun getName(): String {
        return name
    }

    override fun getPackageName(): String {
        return pkg
    }

    override fun getImports(): Set<String> {
        return setOf("${getPackageName()}.${getTypeName()}")
    }

    fun getParameters(): LinkedHashMap<String, ParameterValue>? {
        return parameters
    }
}
