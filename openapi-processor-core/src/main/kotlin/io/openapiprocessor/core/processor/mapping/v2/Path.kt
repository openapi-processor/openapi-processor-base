/*
 * Copyright 2020 the original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.openapiprocessor.core.processor.mapping.v2

/**
 * a "paths:" entry in the mapping yaml
 *
 *  @author Martin Hauner
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
