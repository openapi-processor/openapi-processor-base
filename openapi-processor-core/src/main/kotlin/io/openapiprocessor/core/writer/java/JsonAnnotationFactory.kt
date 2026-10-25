/*
 * Copyright 2026 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.model.datatypes.PropertyDataType

interface JsonAnnotationFactory {
    /** get property annotations imports */
    fun createPropertyImports(propDataType: PropertyDataType): Collection<String>
    /** get property annotations */
    @Deprecated(
        message = "use createPropertyAnnotation(...)",
        replaceWith = ReplaceWith("createPropertyAnnotation(propertyName, propDataType).text")
    )
    fun createPropertyAnnotations(propertyName: String, propDataType: PropertyDataType): Collection<String>
    /** get property annotation */
    fun createPropertyAnnotation(propertyName: String, propDataType: PropertyDataType): CodeSnippet
    /** get creator annotation */
    fun createCreatorAnnotation(): CodeSnippet
    /** get value annotation */
    fun createValueAnnotation(): CodeSnippet
}
