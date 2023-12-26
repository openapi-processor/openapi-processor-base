/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import io.openapiprocessor.core.processor.mapping.MappingVersion

/**
 * *the* v2 Schema of the mapping yaml
 */
data class Mapping(

    /**
     * mapping format version
     */
    @JsonProperty("openapi-processor-mapping")
    @JsonAlias("openapi-processor-spring") // deprecated
    val version: String,

    /**
     * general options
     */
    val options: Options = Options(),

    /**
     * the type mappings
     */
    val map: Map = Map(),

    /**
     * the extension mappings
     */
    @JsonFormat(with = [JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY])
    val extensions: LinkedHashMap<String, List<Type>> = LinkedHashMap()

): MappingVersion {
    override val v2: Boolean = true
}
