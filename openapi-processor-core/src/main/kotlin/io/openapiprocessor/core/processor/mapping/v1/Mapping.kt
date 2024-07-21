/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v1

import io.openapiprocessor.core.processor.mapping.MappingVersion

/**
 * the *old* Schema of the mapping yaml, replaced by mapping.v2
 */
class Mapping: MappingVersion {
    override val v2: Boolean
        get() = false
}
