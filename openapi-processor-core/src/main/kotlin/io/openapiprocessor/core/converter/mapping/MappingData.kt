/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

data class MappingData(
    override val globalMappings: Mappings = Mappings(),
    override val endpointMappings: Map<String /* path */, EndpointMappings> = emptyMap(),
    override val extensionMappings: Map<String /* x- */, ExtensionMappings> = emptyMap()
): MappingSettings {}
