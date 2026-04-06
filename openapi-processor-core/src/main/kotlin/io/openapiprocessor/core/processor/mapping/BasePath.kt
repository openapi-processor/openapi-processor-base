/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping

data class BasePath(
    /**
     * server-url index to use as the base path.
     */
    val serverUrl: String = "false",

    /**
     * profile name for the base path.
     */
    val propertiesName: String = "api.properties"
)
