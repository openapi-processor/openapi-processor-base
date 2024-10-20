/*
 * Copyright 2024 https://github.com/openapi-processor-base/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping.steps

import io.openapiprocessor.core.converter.mapping.MappingQuery

class MethodsStep(val query: MappingQuery): ItemsStep() {

    override fun isEqual(step: MappingStep): Boolean {
        if (step !is MethodsStep) {
            return false
        }

        return query.method == step.query.method
    }

    override fun log(indent: String) {
        if (!hasMappings()) {
            return
        }

        val prefix = if (isMatch()) {
            "$indent$MATCH"
        } else {
            "$indent$NO_MATCH"
        }

        log(prefix, "methods")
        steps.forEach {
            it.log("$indent  ")
        }
    }
}
