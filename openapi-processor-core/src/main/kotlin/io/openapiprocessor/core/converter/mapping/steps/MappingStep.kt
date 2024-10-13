/*
 * Copyright 2024 https://github.com/openapi-processor-base/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping.steps

interface MappingStep {
    fun isMatch(): Boolean
    fun hasMappings(): Boolean
    fun add(step: MappingStep): MappingStep
    fun isEqual(step: MappingStep): Boolean
    fun log(indent: String = "")
}

const val MATCH = "+  {}"
const val NO_MATCH = "-  {}"
