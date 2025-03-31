/*
 * Copyright © 2019-2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2

/**
 * a http method (get, post, ...) entry of a path in the mapping yaml
 */
class PathMethod(

    /**
     * path should be excluded
     */
    val exclude: Boolean = false,

    /**
     * path limited result mapping
     */
    val result: String?,

    /**
     * controller method return type, e.g. **success** response or **all** responses
     */
    val resultStyle: ResultStyle? = null,

    /**
     * single mapping, i.e. Mono<>
     */
    val single: String?,

    /**
     * multi mapping, i.e. Flux<>
     */
    val multi: String?,

    /**
     * null wrapper, e.g. JsonNullable<>
     */
    val `null`: String?,

    /**
     * path/method limited type mappings
     */
    val types: List<Type> = emptyList(),

    /**
     * path/method limited schema mappings
     */
    val schemas: List<Type> = emptyList(),

    /**
     * path/method limited parameter mappings
     */
    val parameters: List<Parameter> = emptyList(),

    /**
     * path/method limited response mappings
     */
    val responses: List<Response> = emptyList()

)
