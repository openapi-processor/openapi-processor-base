/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2

import com.fasterxml.jackson.annotation.JsonFormat

/**
 * the "map:" entry in the mapping YAML
 */
data class Map(

    /**
     * global result mapping
     */
    val result: String? = null,

    /**
     * controller method return type, e.g. **success** response or **all** responses
     */
    val resultStyle: ResultStyle? = null,

    /**
     * add response status annotation
     */
    val resultStatus: Boolean? = null,

    /**
     * single mapping, e.g. Mono<>
     */
    val single: String? = null,

    /**
     * multi mapping, e.g. Flux<>
     */
    val multi: String? = null,

    /**
     * null wrapper, e.g. JsonNullable<>
     */
    //val `null`: String? = null,

    /**
     * global type mappings
     */
    val types: List<Type> = emptyList(),

    /**
     * global schema mappings
     */
    val schemas: List<Type> = emptyList(),

    /**
     * global parameter mappings
     */
    val parameters: List<Parameter> = emptyList(),

    /**
     * global response mappings
     */
    val responses: List<Response> = emptyList(),

    /**
     * endpoint mappings
     *
     * the LinkedHashMap preserves order
     */
    val paths: LinkedHashMap<String, Path> = LinkedHashMap(),

    /**
     * the extension mappings
     */
    @JsonFormat(with = [JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY])
    val extensions: LinkedHashMap<String, List<Type>> = LinkedHashMap()
)
