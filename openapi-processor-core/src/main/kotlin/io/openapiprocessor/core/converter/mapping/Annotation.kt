/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

/**
 * (additional) annotation from mapping
 */
class Annotation(

    /**
     * additional annotation of parameter.
     */
    val type: String,

    /**
     * parameter key/value map.
     */
    val parameters: LinkedHashMap<String, ParameterValue> = linkedMapOf()
)

interface ParameterValue {
    val value: String
    val import: String?
}

class SimpleParameterValue(override val value: String, override val import: String? = null): ParameterValue

class ClassParameterValue(private val clazz: String): ParameterValue {
    override val value: String
        get() = clazz.substring(import.substringBeforeLast('.').length + 1)

    override val import: String
        get() = clazz.substringBeforeLast('.')
}
