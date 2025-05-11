/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2

/**
 * a "paths:" entry in the mapping YAML
 */
data class Path(
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
     * add status annotation
     */
    val resultStatus: Boolean? = null,

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
     * path limited type mappings
     */
    val types: List<Type> = emptyList(),

    /**
     * path limited schema mappings
     */
    val schemas: List<Type> = emptyList(),

    /**
     * path limited parameter mappings
     */
    val parameters: List<Parameter> = emptyList(),

    /**
     * path limited response mappings
     */
    val responses: List<Response> = emptyList(),

    /**
     * http method specific mappings
     */
    val get: PathMethod? = null,
    val head: PathMethod? = null,
    val post: PathMethod? = null,
    val put: PathMethod? = null,
    val delete: PathMethod? = null,
    val connect: PathMethod? = null,
    val options: PathMethod? = null,
    val trace: PathMethod? = null,
    val patch: PathMethod? = null
)
