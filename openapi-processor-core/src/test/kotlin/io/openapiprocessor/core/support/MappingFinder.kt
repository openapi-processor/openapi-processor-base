/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.support

import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.converter.MappingFinder
import io.openapiprocessor.core.converter.mapping.MappingData

fun mappingFinder(): MappingFinder {
    val data = MappingData()
    return MappingFinder(ApiOptions().apply {
        globalMappings = data.globalMappings
        endpointMappings = data.endpointMappings
        extensionMappings = data.extensionMappings
    })
}

fun mappingFinder(options: ApiOptions): MappingFinder {
    return MappingFinder(options)
}
