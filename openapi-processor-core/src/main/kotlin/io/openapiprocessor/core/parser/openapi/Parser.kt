/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi

import io.openapiparser.*
import io.openapiparser.jackson.JacksonConverter
import io.openapiparser.reader.UriReader
import io.openapiparser.schema.DocumentStore
import io.openapiparser.schema.Resolver
import io.openapiparser.schema.SchemaStore
import io.openapiparser.validator.result.*
import io.openapiprocessor.core.support.toURI
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
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
        val resolver = Resolver(UriReader(), JacksonConverter(), DocumentStore())
        val store = SchemaStore(resolver)

        val parser = OpenApiParser(resolver)

        val apiUri = toURI(apiPath)
        val result = parser.parse(apiUri)

        return when (result.version) {
            OpenApiResult.Version.V30 -> {
                store.loadDraft4()
                val validator = io.openapiparser.validator.Validator()
                val valid = result.validate(validator, store)
                if (!valid) {
                    val collector = MessageCollector(result.validationMessages)
                    val messages: LinkedList<Message> = collector.collect()
                    val builder = MessageTextBuilder()

                    log.warn("OpenAPI description '{}' does not pass schema validation:", apiPath)
                    for (message in messages) {
                        log.warn(builder.getText(message))
                    }
                }

                ParserOpenApi30(result.getModel(OpenApi30::class.java))
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
