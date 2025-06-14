/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.options

class BasePathOptions {
    /**
     * enable/disable base path setup
     */
    var enabled = false

    /**
     * server-url index to use as the base path.
     */
    var serverUrl: Int? = null

    /**
     * properties name for base path.
     */
    var propertiesName: String = "api.properties"
}
