/*
 * Copyright 2024 https://github.com/openapi-processor-base/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping.steps

class GlobalsStep: ItemsStep() {

    override fun isEqual(step: MappingStep): Boolean {
        return step is GlobalsStep
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

        log(prefix, "global")
        steps.forEach {
            it.log("$indent  ")
        }
    }
}
