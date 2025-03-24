/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.builder.api.endpoint
import io.openapiprocessor.core.model.datatypes.DataTypeName
import io.openapiprocessor.core.model.datatypes.ObjectDataType
import io.openapiprocessor.core.model.datatypes.PropertyDataType
import io.openapiprocessor.core.model.datatypes.StringDataType
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.processor.mapping.v2.ResultStyle

class EndpointSuccessResponseSpec: StringSpec({

    "groups multiple success responses" {
        val ep = endpoint("/foo", HttpMethod.GET) {
            responses {
                status("200") {
                    response("application/json", ObjectDataType(
                        DataTypeName("Foo200Json"), "pkg", linkedMapOf(
                            "foo" to PropertyDataType(
                                readOnly = false,
                                writeOnly = false,
                                dataType = StringDataType(),
                                documentation = Documentation()
                            )))) {}
                    response("application/xml", ObjectDataType(
                        DataTypeName("Foo200Xml"), "pkg", linkedMapOf(
                            "foo" to PropertyDataType(
                                readOnly = false,
                                writeOnly = false,
                                dataType = StringDataType(),
                                documentation = Documentation()
                            )))) {}
                }
                status("202") {
                    response("application/json", ObjectDataType(
                        DataTypeName("Foo202Json"), "pkg", linkedMapOf(
                            "foo" to PropertyDataType(
                                readOnly = false,
                                writeOnly = false,
                                dataType = StringDataType(),
                                documentation = Documentation()
                            )))) {}
                }
                status("204") {
                    empty()
                }
                status("default") {
                    response("text/plain", StringDataType()) {}
                }
            }
        }

        val result = ep.endpointResponses

        result shouldHaveSize 2

        result[0].contentType shouldBe "application/json"
        result[0].getResponseType(ResultStyle.SUCCESS) shouldBe "Object"

        result[1].contentType shouldBe "application/xml"
        result[1].getResponseType(ResultStyle.SUCCESS) shouldBe "Foo200Xml"
    }

    "groups multiple success responses - no content type" {
        val ep = endpoint("/foo", HttpMethod.GET) {
            responses {
                status("200") {
                    empty()
                }
                status("202") {
                    empty()
                }
                status("204") {
                    empty()
                }
                status("default") {
                    response("text/plain", StringDataType()) {}
                }
            }
        }

        val result = ep.endpointResponses

        result shouldHaveSize 1
        result.first().getResponseType(ResultStyle.SUCCESS) shouldBe "void"
    }

    "groups multiple success responses - same content type" {
        val ep = endpoint("/foo", HttpMethod.GET) {
            responses {
                status("201") {
                    response("text/plain", StringDataType()) {}
                }
                status("202") {
                    response("text/plain", StringDataType()) {}
                }
            }
        }

        val result = ep.endpointResponses

        result shouldHaveSize 1
        result.first().getResponseType(ResultStyle.SUCCESS) shouldBe "String"
    }
})
