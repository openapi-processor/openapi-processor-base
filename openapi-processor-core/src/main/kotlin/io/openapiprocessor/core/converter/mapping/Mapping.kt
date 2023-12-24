/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

/**
 * marker interface for type mappings.
 */
interface Mapping {
    /**
     * Returns the nested child mappings. If the Mapping does not have children it returns a singleton list
     * of itself.
     *
     * @return the nested mappings.
     */
    fun getChildMappings(): List<Mapping> {
        return listOf(this)
    }
}
