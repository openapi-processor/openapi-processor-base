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
import io.openapiprocessor.jsonschema.schema.DocumentLoader
import io.openapiprocessor.jsonschema.schema.DocumentStore
import io.openapiprocessor.jsonschema.schema.Output
import io.openapiprocessor.jsonschema.schema.SchemaStore
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
        val parser = OpenApiParser(documents, loader)

        val baseUri = toURI(apiPath)
        val result = parser.parse(baseUri)

        return when (result.version) {
            OpenApiResult.Version.V31 -> {
                validate31(loader, apiPath, result)
                createApi31(result)
            }
            OpenApiResult.Version.V30 -> {
                validate30(loader, apiPath, result)
                createApi30(result)
            }
        }
    }

    private fun createApi30(result: OpenApiResult): ParserOpenApi {
        val model = result.getModel(OpenApi30::class.java)
        return ParserOpenApi30(model)
    }

    private fun createApi31(result: OpenApiResult): ParserOpenApi {
        val model = result.getModel(OpenApi31::class.java)
        return ParserOpenApi31(model)
    }

    private fun validate30(loader: DocumentLoader, apiPath: String, result: OpenApiResult) {
        val store = SchemaStore(loader)
        store.registerDraft4()
        validate(apiPath, result, store)
    }

    private fun validate31(loader: DocumentLoader, apiPath: String, result: OpenApiResult) {
        val store = SchemaStore(loader)
        store.registerDraft202012()
        validate(apiPath, result, store)
    }

    private fun validate(apiPath: String, result: OpenApiResult, store: SchemaStore) {
        val validator = Validator(ValidatorSettings().setOutput(Output.BASIC))
        val valid = result.validate(validator, store)

        if (!valid) {
            val builder = ValidationErrorTextBuilder()

            log.warn("OpenAPI description '{}' does not pass schema validation:", apiPath)
            for (error in result.validationErrors) {
                log.warn(builder.getText(error))
            }
        }
    }
}
