/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

interface MappingSettings {
    val globalMappings: Mappings
    val endpointMappings: Map<String /* path */, EndpointMappings>
    val extensionMappings: Map<String /* x- */, ExtensionMappings>
}
