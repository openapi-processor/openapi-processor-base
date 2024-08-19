/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

import io.openapiprocessor.core.parser.HttpMethod

/**
 * container for endpoint specific type/annotation mappings.
 */
class EndpointTypeMapping @JvmOverloads constructor(

    /**
     * full path of the endpoint that is configured by this object.
     */
    val path: String,

    /**
     * http method of this endpoint. If it is not set (i.e. null) the mapping applies to all http
     * methods.
     */
    val method: HttpMethod? = null,

    /**
     * provides type mappings for the endpoint.
     */
    val typeMappings: List<Mapping> = emptyList(),

    /**
     * exclude endpoint.
     */
    val exclude: Boolean = false

): Mapping
