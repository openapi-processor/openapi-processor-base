/*
 * Copyright 2024 https://github.com/openapi-processor-base/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping.steps

import io.openapiprocessor.core.converter.mapping.MappingQuery

class MethodsStep(val query: MappingQuery): MappingStep {
    private val steps: MutableCollection<MappingStep> = ArrayList()

    override fun isMatch(): Boolean {
        return steps.any { it.isMatch() }
    }

    override fun add(step: MappingStep): MappingStep {
        val found = steps.find { it.isEqual(step) }
        if(found != null) {
            return found
        }

        steps.add(step)
        return step
    }

    override fun isEqual(step: MappingStep): Boolean {
        if (step !is MethodsStep) {
            return false
        }

        return query.method == step.query.method
    }

    override fun log(indent: String) {
        TODO("Not yet implemented")
    }
}
