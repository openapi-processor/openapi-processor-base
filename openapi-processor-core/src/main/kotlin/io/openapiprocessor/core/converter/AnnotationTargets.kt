/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

enum class AnnotationTargetType {
    Type, Field, Method, Parameter
}

class AnnotationTargets {
    private val targets = mutableMapOf<String, List<AnnotationTargetType>>()

    fun add(annotationName: String, vararg types: AnnotationTargetType) {
        targets[annotationName] = types.asList()
    }

    fun isAllowedOnType(annotationName: String): Boolean {
        return isAllowedOn(annotationName, AnnotationTargetType.Type)
    }

    fun isAllowedOnField(annotationName: String): Boolean {
        return isAllowedOn(annotationName, AnnotationTargetType.Field)
    }

    fun isAllowedOnMethod(annotationName: String): Boolean {
        return isAllowedOn(annotationName, AnnotationTargetType.Method)
    }

    fun isAllowedOnParameter(annotationName: String): Boolean {
        return isAllowedOn(annotationName, AnnotationTargetType.Parameter)
    }

    private fun isAllowedOn(annotationName: String, targetType: AnnotationTargetType): Boolean {
        if (!targets.containsKey(annotationName)) {
            return true
        }

        return targets[annotationName]?.contains(targetType) == true
    }
}
