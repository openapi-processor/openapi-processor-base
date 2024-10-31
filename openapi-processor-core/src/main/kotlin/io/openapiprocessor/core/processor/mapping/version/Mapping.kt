/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.version

import com.fasterxml.jackson.annotation.JsonProperty

data class Mapping(
        @JsonProperty("openapi-processor-mapping")
        val version: String? = null,

        @JsonProperty("openapi-processor-spring")
        val versionObsolete: String? = null
) {

    fun isV2(): Boolean {
        return getSafeVersion().substring(1).toDouble() > 1.0
    }

    fun isDeprecatedVersionKey (): Boolean {
        return versionObsolete != null
    }

    fun getSafeVersion(): String {
        if (version != null)
            return version

        if (versionObsolete != null)
            return versionObsolete

        return "v1"
    }

}
