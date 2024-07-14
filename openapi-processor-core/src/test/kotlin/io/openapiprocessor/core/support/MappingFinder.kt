/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.support

import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.converter.MappingFinderX
import io.openapiprocessor.core.converter.mapping.MappingData

fun mappingFinder(): MappingFinderX {
    return MappingFinderX(MappingData())
}

fun mappingFinder(options: ApiOptions): MappingFinderX {
    return MappingFinderX(MappingData(
        globalMappings = options.globalMappings,
        endpointMappings = options.endpointMappings,
        extensionMappings = options.extensionMappings
    ))
}
