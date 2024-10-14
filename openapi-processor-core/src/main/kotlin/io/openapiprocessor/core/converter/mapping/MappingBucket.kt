/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

import io.openapiprocessor.core.converter.mapping.steps.MappingStep

fun interface MappingBucket {
    /**
     * filter the mapping(s) by the given filter.
     *
     * @param filter matching conditions
     * @param step mapping trace collector
     */
    fun filter(filter: MappingMatcher, step: MappingStep): List<Mapping>
}
