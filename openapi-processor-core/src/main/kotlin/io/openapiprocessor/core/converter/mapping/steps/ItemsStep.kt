/*
 * Copyright 2024 https://github.com/openapi-processor-base/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping.steps

abstract class ItemsStep: MappingStep {
    protected val steps: MutableCollection<MappingStep> = ArrayList()

    override fun isMatch(): Boolean {
        return steps.any { it.isMatch() }
    }
}
