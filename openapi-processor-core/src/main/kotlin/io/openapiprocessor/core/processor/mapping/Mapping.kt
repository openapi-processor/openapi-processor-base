/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping

import io.openapiprocessor.core.processor.SupportedTypes
import io.openapiprocessor.core.processor.TargetTypes

/**
 * *the* v2 Schema of the mapping YAML
 */
data class Mapping(

    /**
     * mapping format version, todo do we need that at all??
     */
//    @JsonProperty("openapi-processor-mapping")
//    @JsonAlias("openapi-processor-spring") // deprecated
//    val version: String,

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
