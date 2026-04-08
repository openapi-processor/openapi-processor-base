/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping

import io.openapiprocessor.core.processor.SupportedTypes
import io.openapiprocessor.core.processor.TargetTypes

/**
 * The mapping YAML schema.
 */
data class Mapping(
    /**
     * general options
     */
    val options: Options = Options(),

    /**
     * the type mappings
     */
    val map: Map = Map(),

    /**
     * bean-validation
     */
    val beanValidation: SupportedTypes = mapOf(),

    /**
     * annotation-targets
     */
    val annotationTargets: TargetTypes = mapOf(),

    /**
     * compatibility options
     */
    val compatibility: Compatibility = Compatibility(),

    /**
     * logging options
     */
    val logging: Logging = Logging()
)
