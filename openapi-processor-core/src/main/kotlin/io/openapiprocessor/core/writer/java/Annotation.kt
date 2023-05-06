/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.converter.mapping.ParameterValue

class Annotation(
    val qualifiedName: String,
    val parameters: LinkedHashMap<String, ParameterValue> = linkedMapOf()
) {
    val typeName: String
        get() {
            return qualifiedName.substring(qualifiedName.lastIndexOf('.') + 1)
        }

    val packageName: String
        get() {
            return qualifiedName.substring(0, qualifiedName.lastIndexOf('.') + 1)
        }

    val import = qualifiedName
}
