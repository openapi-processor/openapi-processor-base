/*
 * Copyright 2026 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.version

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import java.io.IOException

class VersionDeserializer : JsonDeserializer<Mapping>() {

    @Throws(IOException::class)
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Mapping {
        val node = p.codec.readTree<JsonNode>(p)

        val versionKey = node.fieldNames()
            .asSequence()
            .firstOrNull { it.startsWith("openapi-processor-") }
            ?: throw IOException("Missing 'openapi-processor-*' key!")

        val name = versionKey.removePrefix("openapi-processor-")
        val version = node[versionKey]?.asText()

        return Mapping(name = name, version = version)
    }
}
