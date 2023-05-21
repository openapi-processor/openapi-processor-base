/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor

import io.openapiprocessor.jackson.JacksonConverter
import io.openapiprocessor.jsonschema.ouput.OutputConverter
import io.openapiprocessor.jsonschema.ouput.OutputUnit
import io.openapiprocessor.jsonschema.ouput.OutputUnitFlag
import io.openapiprocessor.jsonschema.reader.UriReader
import io.openapiprocessor.jsonschema.schema.*
import io.openapiprocessor.jsonschema.validator.Validator
import io.openapiprocessor.jsonschema.validator.ValidatorSettings
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URI


/**
 * validate the given mapping.yaml with the mapping.yaml json schema.
 */
open class MappingValidator {
    val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    fun validate(mapping: String, version: String): OutputUnit {
        return try {
            val reader = UriReader()
            val converter = JacksonConverter()
            val loader = DocumentLoader(reader, converter)

            val store = SchemaStore(loader)
            store.register(getSchemaUri(version), getSchema(version))

            val schema = store.getSchema(getSchemaUri(version))
            val instance = JsonInstance(converter.convert(mapping))

            val settings = ValidatorSettings().setOutput(Output.BASIC)
            val validator = Validator(settings)

            val step = validator.validate(schema, instance)

            val output = OutputConverter(Output.BASIC)
            output.convert(step)

        } catch (ex: Exception) {
            log.error("failed to validate mapping!", ex)
            OutputUnitFlag(false)
        }
    }

    private fun getSchemaUri(version: String): URI {
        return URI("https://openapiprocessor.io/schemas/mapping/mapping-v${version}.json")
    }

    private fun getSchema(version: String): String {
        return "/mapping/$version/mapping.yaml.json"
    }
}
