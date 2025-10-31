/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.openapiprocessor.core.converter.AnnotationTargetType.METHOD
import io.openapiprocessor.core.converter.AnnotationTargetType.TYPE
import java.util.EnumSet

enum class AnnotationTargetType {
    TYPE, FIELD, METHOD, PARAMETER
}

class AnnotationTargets {
    private val targets = mutableMapOf<String, EnumSet<AnnotationTargetType>>()

    fun prefillCommon() {
        add("lombok.Builder", TYPE, METHOD)
    }

    fun add(annotationName: String, vararg types: AnnotationTargetType) {
        val values = EnumSet.noneOf(AnnotationTargetType::class.java)
        values.addAll(types.asList())
        targets[annotationName] = values
    }

    fun isAllowedOnType(annotationName: String): Boolean {
        return isAllowedOn(annotationName, TYPE)
    }

    fun isAllowedOnField(annotationName: String): Boolean {
        return isAllowedOn(annotationName, AnnotationTargetType.FIELD)
    }

    fun isAllowedOnMethod(annotationName: String): Boolean {
        return isAllowedOn(annotationName, METHOD)
    }

    fun isAllowedOnParameter(annotationName: String): Boolean {
        return isAllowedOn(annotationName, AnnotationTargetType.PARAMETER)
    }

    private fun isAllowedOn(annotationName: String, targetType: AnnotationTargetType): Boolean {
        if (!targets.containsKey(annotationName)) {
            return true
        }

        return targets[annotationName]?.contains(targetType) == true
    }
}
