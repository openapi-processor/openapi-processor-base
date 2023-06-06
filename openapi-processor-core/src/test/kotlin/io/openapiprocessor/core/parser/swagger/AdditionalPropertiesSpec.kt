/*
 * Copyright 2023 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.swagger

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.openapiprocessor.core.model.HttpMethod
import io.openapiprocessor.core.parser.ParserType
import io.openapiprocessor.core.support.getSchemaInfo
import io.openapiprocessor.core.support.parse

class AdditionalPropertiesSpec: StringSpec({
    isolationMode = IsolationMode.InstancePerTest

    "additionalProperties of empty object is empty object" {
        val openApi = parse("""
           openapi: 3.0.2
           info:
             title: API
             version: 1.0.0
           
           paths:
            /values:
              get:
                description: query object dictionary
                responses:
                  '200':
                    description: dictionary response
                    content:
                      application/json:
                        schema:
                          type: object
                          additionalProperties: {}

        """.trimIndent(), ParserType.SWAGGER
        )


        val schemaInfo = openApi.getSchemaInfo("Values",
            "/values", HttpMethod.GET, "200", "application/json")

        val additional = schemaInfo.buildForAdditionalProperties()
        additional.shouldNotBeNull()
    }

    "additionalProperties of boolean object is null" {
        val openApi = parse("""
           openapi: 3.0.2
           info:
             title: API
             version: 1.0.0
           
           paths:
            /values:
              get:
                description: query object dictionary
                responses:
                  '200':
                    description: dictionary response
                    content:
                      application/json:
                        schema:
                          type: object
                          additionalProperties: true

        """.trimIndent(), ParserType.SWAGGER
        )

        val schemaInfo = openApi.getSchemaInfo("Values",
            "/values", HttpMethod.GET, "200", "application/json")

        val additional = schemaInfo.buildForAdditionalProperties()
        additional.shouldBeNull()
    }
})
