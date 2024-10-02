/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.support

import io.openapiprocessor.core.converter.MappingFinderQuery
import io.openapiprocessor.core.converter.mapping.matcher.*
import io.openapiprocessor.core.parser.HttpMethod

fun typeMatcher(
    path: String? = null,
    method: HttpMethod? = null,
    name: String? = null,
    type: String? = null,
    format: String? = null,
    contentType: String? = null,
    primitive: Boolean = false,
    array: Boolean = false
): TypeMatcher {
    return TypeMatcher(MappingFinderQuery(
        path,
        method,
        name,
        type,
        format,
        contentType,
        primitive,
        array))
}

fun annotationTypeMatcher(
    path: String? = null,
    method: HttpMethod? = null,
    name: String? = null,
    type: String? = null,
    format: String? = null,
    contentType: String? = null,
    primitive: Boolean = false,
    array: Boolean = false
): AnnotationTypeMatcher {
    return AnnotationTypeMatcher(MappingFinderQuery(
        path,
        method,
        name,
        type,
        format,
        contentType,
        primitive,
        array
    ))
}

fun parameterNameMatcher(
    path: String? = null,
    method: HttpMethod? = null,
    name: String? = null,
    type: String? = null,
    format: String? = null,
    contentType: String? = null,
    primitive: Boolean = false,
    array: Boolean = false
): ParameterNameTypeMatcher {
    return ParameterNameTypeMatcher(MappingFinderQuery(
        path,
        method,
        name,
        type,
        format,
        contentType,
        primitive,
        array))
}

fun addParameterTypeMatcher(): AddParameterTypeMatcher {
    return AddParameterTypeMatcher()
}

fun annotationParameterNameTypeMatcher(
    path: String? = null,
    method: HttpMethod? = null,
    name: String? = null,
    type: String? = null,
    format: String? = null,
    contentType: String? = null,
    primitive: Boolean = false,
    array: Boolean = false
): AnnotationParameterNameTypeMatcher {
    return AnnotationParameterNameTypeMatcher(MappingFinderQuery(
        path,
        method,
        name,
        type,
        format,
        contentType,
        primitive,
        array
    ))
}

fun responseTypeMatcher(
    path: String? = null,
    method: HttpMethod? = null,
    name: String? = null,
    type: String? = null,
    format: String? = null,
    contentType: String? = null,
    primitive: Boolean = false,
    array: Boolean = false
): ContentTypeMatcher {
    return ContentTypeMatcher(MappingFinderQuery(
        path,
        method,
        name,
        type,
        format,
        contentType,
        primitive,
        array))
}
