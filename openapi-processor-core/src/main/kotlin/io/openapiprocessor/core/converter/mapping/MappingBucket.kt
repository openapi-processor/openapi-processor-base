/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

interface MappingBucket {
    /**
     * filter the mapping(s) by the given filter.
     *
     * @param filter matching conditions
     */
    fun filter(filter: MappingMatcher): List<Mapping>
}
