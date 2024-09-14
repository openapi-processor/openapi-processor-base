/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2

data class BasePath(
    /**
     * server-url index to use as base path.
     */
    val serverUrl: String = "false",

    /**
     * profile name for base path.
     */
    val profileName: String = "api.properties"
)
