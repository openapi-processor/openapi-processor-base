/*
 * Copyright 2024 https://github.com/openapi-processor-base/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping.steps

class StringStep(val mapping: String, val match: Boolean): MappingStepBase() {

    override fun isMatch(): Boolean {
        return match
    }

    override fun hasMappings(): Boolean {
        return true
    }

    override fun add(step: MappingStep): MappingStep {
        throw NotImplementedError("nop")
    }

    override fun isEqual(step: MappingStep): Boolean {
        return false
    }

    override fun log(indent: String) {
        if (isMatch()) {
            log("$indent$MATCH", mapping)
        } else {
            log("$indent$NO_MATCH", mapping)
        }
    }
}
