/*
 * Copyright 2026 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.model.Annotation
import io.openapiprocessor.core.model.datatypes.PropertyDataType

interface JsonAnnotationFactory {
    @Deprecated(message = "use methods")
    val jsonProperty: Annotation
    val jsonCreator: Annotation
    val jsonValue: Annotation

    /** get property annotations imports */
    fun createPropertyImports(propDataType: PropertyDataType): Collection<String>
    /** get property annotations */
    fun createPropertyAnnotations(propertyName: String, propDataType: PropertyDataType): Collection<String>
}
