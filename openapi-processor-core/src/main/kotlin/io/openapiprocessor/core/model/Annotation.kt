/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model

import io.openapiprocessor.core.converter.mapping.ParameterValue
import io.openapiprocessor.core.converter.mapping.SimpleParameterValue

open class Annotation(
    private val canonicalName: String,
    val parameters: LinkedHashMap<String, ParameterValue> = linkedMapOf()
) {
    val typeName: String
        get() {
            return canonicalName.substring(canonicalName.lastIndexOf('.') + 1)
        }

    val packageName: String
        get() {
            return canonicalName.substring(0, canonicalName.lastIndexOf('.') + 1)
        }

    val imports = setOf(canonicalName)

    val referencedImports: Set<String>
        get() {
            return parameters
                .values.mapNotNull { it.import }
                .toSet()
        }

    /**
     * The full annotation name with a leading @.
     */
    val annotationName: String
        get() = "@${typeName}"

    fun withParameter(parameter: String): Annotation {
        val newParameters = linkedMapOf<String, ParameterValue>()
        newParameters.putAll(parameters)
        newParameters[""] = SimpleParameterValue(parameter)
        return Annotation(canonicalName, newParameters)
    }

    fun withParameter(key: String, parameter: ParameterValue): Annotation {
        val m = linkedMapOf<String, ParameterValue>()
        m.putAll(parameters)
        m[key] = parameter
        return Annotation(canonicalName, m)
    }
}
