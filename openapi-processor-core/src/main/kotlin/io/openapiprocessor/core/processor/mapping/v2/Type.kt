/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2

import com.fasterxml.jackson.annotation.JsonCreator

/**
 * a "type:" entry in the "types:" list of the mapping YAML
 */

data class Type @JsonCreator @JvmOverloads constructor(
    /**
     * the mapping from source to target, i.e., a mapping string like:
     *
     * source type => target type
     * source type @ target type
     * source type =+ target type
     */
    val type: String,

    /**
     * (optional) generic parameters of {@link #type} target
     */
    val generics: List<String>? = null

) : Parameter
