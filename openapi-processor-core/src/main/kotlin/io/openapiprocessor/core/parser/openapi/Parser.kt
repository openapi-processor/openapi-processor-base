/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi

import io.openapiparser.OpenApiParser
import io.openapiparser.OpenApiResult
import io.openapiparser.ValidationErrorTextBuilder
import io.openapiprocessor.core.support.toURI
import io.openapiprocessor.jackson.JacksonConverter
import io.openapiprocessor.jsonschema.reader.UriReader
import io.openapiprocessor.jsonschema.schema.*
import io.openapiprocessor.jsonschema.validator.Validator
import io.openapiprocessor.jsonschema.validator.ValidatorSettings
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import io.openapiparser.model.v30.OpenApi as OpenApi30
import io.openapiparser.model.v31.OpenApi as OpenApi31
import io.openapiprocessor.core.parser.OpenApi as ParserOpenApi
import io.openapiprocessor.core.parser.openapi.v30.OpenApi as ParserOpenApi30
import io.openapiprocessor.core.parser.openapi.v31.OpenApi as ParserOpenApi31

/**
 * openapi-parser
 */
class Parser {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass.name)


    fun parse(apiPath: String): ParserOpenApi {
        val reader = UriReader ()
        val converter = JacksonConverter ()
        val loader = DocumentLoader (reader, converter)

        val documents = DocumentStore ()
        val settings = Resolver.Settings (SchemaVersion.Draft4)
        val resolver = Resolver (documents, loader, settings)

        val parser = OpenApiParser(resolver)

        val apiUri = toURI(apiPath)
        val result = parser.parse(apiUri)

        return when (result.version) {
            OpenApiResult.Version.V30 -> {
                val store = SchemaStore (loader)
                store.registerDraft4()

                val validator = Validator(ValidatorSettings().setOutput(Output.BASIC))
                val valid = result.validate(validator, store)

                if (!valid) {
                    val builder = ValidationErrorTextBuilder()

                    log.warn("OpenAPI description '{}' does not pass schema validation:", apiPath)
                    for (error in result.validationErrors) {
                        log.warn(builder.getText(error))
                    }
                }

                val model = result.getModel(OpenApi30::class.java)
                ParserOpenApi30(model)
            }
            OpenApiResult.Version.V31 -> {
                ParserOpenApi31(result.getModel(OpenApi31::class.java))
            }
            else -> {
                TODO() // unsupported openapi version
            }
        }
    }
}
