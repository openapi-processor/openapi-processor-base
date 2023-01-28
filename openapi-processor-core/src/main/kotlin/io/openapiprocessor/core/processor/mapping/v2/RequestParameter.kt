/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2

/**
 * a "parameters:" request parameter entry in the mapping yaml
 */
data class RequestParameter(
    /**
     * the mapping from parameter name to target, ie a mapping string like:
     *
     * foo => mapping.Bar
     */
    val name: String,

    /**
     * (optional) generic parameters of {@link #name} target
     */
    val generics: List<String>?

): Parameter
