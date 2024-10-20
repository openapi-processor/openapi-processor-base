/*
 * Copyright 2024 https://github.com/openapi-processor-base/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping.steps

import io.openapiprocessor.core.converter.mapping.MappingQuery

class EndpointsStep(val query: MappingQuery): ItemsStep() {

    override fun isEqual(step: MappingStep): Boolean {
        if (step !is EndpointsStep) {
            return false
        }

        return query.path == step.query.path
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

        log(prefix, query.path)
        steps.forEach {
            it.log("$indent  ")
        }
    }
}
