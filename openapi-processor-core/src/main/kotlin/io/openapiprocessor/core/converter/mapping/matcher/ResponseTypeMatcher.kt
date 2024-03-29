/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping.matcher

import io.openapiprocessor.core.converter.mapping.MappingSchema
import io.openapiprocessor.core.converter.mapping.ContentTypeMapping

/**
 * [io.openapiprocessor.core.converter.mapping.MappingFinder] matcher for response type mappings.
 */
class ResponseTypeMatcher(private val schema: MappingSchema): (ContentTypeMapping) -> Boolean {

    override fun invoke(mapping: ContentTypeMapping): Boolean {
        return mapping.contentType == schema.getContentType()
    }

}
