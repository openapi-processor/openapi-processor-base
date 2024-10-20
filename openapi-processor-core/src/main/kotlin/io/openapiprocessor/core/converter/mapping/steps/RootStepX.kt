/*
 * Copyright 2024 https://github.com/openapi-processor-base/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping.steps

class RootStepX(val message: String = "", val extension: String) : ItemsStep() {

    override fun log(indent: String) {
        log("{} '{}'", message, extension)
        if (!hasMappings()) {
            log("$indent  $NO_MATCH", "no mappings")
            return
        }

        steps.filter { it.hasMappings() }
            .forEach { it.log("$indent  ") }
    }

    override fun isEqual(step: MappingStep): Boolean {
        return false
    }
}
