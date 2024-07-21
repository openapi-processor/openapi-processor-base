/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

/**
 * Mapping of a content type to a java type.
 */
class ContentTypeMapping(
    /**
     * The content type of this mapping. Must match 1:1 with what is written in the api.
     */
    val contentType: String,

    /**
     * Type mapping valid only for responses with {@link #contentType}.
     */
    val mapping: TypeMapping

): Mapping
