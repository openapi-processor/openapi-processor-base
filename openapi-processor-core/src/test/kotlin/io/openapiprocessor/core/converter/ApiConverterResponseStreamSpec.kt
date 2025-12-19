/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.framework.FrameworkBase
import io.openapiprocessor.core.model.Api
import io.openapiprocessor.core.support.parseApi
import io.openapiprocessor.core.support.parseOptionsMapping
import io.openapiprocessor.core.writer.java.JavaIdentifier

class ApiConverterResponseStreamSpec: StringSpec({
    isolationMode = IsolationMode.InstancePerTest

    "generates itemSchema model" {
        val options = parseOptionsMapping("""
            |map:
            |  paths:
            |    /jsonl:
            |      result: plain => io.stream.Response
            """)

        val openApi = parseApi ("""
            openapi: 3.2.0
            info:
              title: item schema
              version: 1.0.0
            
            paths:
              /jsonl:
                get:
                  tags:
                    - jsonl
                  summary: stream objects
                  description: endpoint sends a stream response
                  parameters:
                    - name: source
                      description: query, required, string
                      in: query
                      required: true
                      schema:
                        type: string
                  responses:
                    '200':
                      description: jsonl stream
                      content:
                        application/jsonl:
                          itemSchema:
                            type: object
                            properties:
                              foo:
                                type: string
                
            """)

        val api: Api = ApiConverter (options, JavaIdentifier(), FrameworkBase())
            .convert(openApi)

        api.getDataTypes().getModelDataTypes().size shouldBe 1
     }
})
