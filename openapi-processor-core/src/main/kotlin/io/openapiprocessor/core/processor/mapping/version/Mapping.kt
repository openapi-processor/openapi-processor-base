/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.version

import com.fasterxml.jackson.databind.annotation.JsonDeserialize

// todo rename to MappingVersion
@JsonDeserialize(using = VersionDeserializer::class)
data class Mapping(
    val name: String? = null,
    val version: String? = null
) {
    fun getSafeVersion(): String {
        if (version != null)
            return version

        return "v1"
    }
}
