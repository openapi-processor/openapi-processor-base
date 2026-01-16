/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.builder.api.endpoint
import io.openapiprocessor.core.model.datatypes.*
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.processor.mapping.v2.ResultStyle

class EndpointResponseSpec: StringSpec({

    "anyOf/oneOf always uses multi response" {
        val rw = ResponseWithStatus(
            "200",
            Response(
                "application/json",
                AnyOneOfObjectDataType("Foo", "pkg", "anyOf")))

        val er = EndpointResponse(
            rw,
            emptySet(),
            listOf(rw)
        )

        er.getResponseType(ResultStyle.ALL) shouldBe "Object"
        er.getResponseImports(ResultStyle.ALL) shouldBe emptySet()
        er.getResponseType(ResultStyle.SUCCESS) shouldBe "Object"
        er.getResponseImports(ResultStyle.SUCCESS) shouldBe emptySet()
    }

    "result style all with errors uses multi response" {
        val rw = ResponseWithStatus(
            "200",
            Response(
                "application/json",
                StringDataType()))

        val er = EndpointResponse(
            rw,
            setOf(ResponseWithStatus(
                "500", Response("text/plain", StringDataType())
            )),
            listOf(rw)
        )

        er.getResponseType(ResultStyle.ALL) shouldBe "Object"
        er.getResponseImports(ResultStyle.ALL) shouldBe emptySet()
    }

    "result style all without errors uses single response" {
        val rw = ResponseWithStatus(
            "200",
            Response(
                "", ObjectDataType(
                    DataTypeName("Foo"),
                    "pkg",
                    linkedMapOf(
                        "bar" to PropertyDataType(
                            readOnly = false,
                            writeOnly = false,
                            dataType = StringDataType(),
                            documentation = Documentation())))))

        val er = EndpointResponse(
            rw,
            emptySet(),
            listOf(rw))

        er.getResponseType(ResultStyle.ALL) shouldBe "Foo"
        er.getResponseImports(ResultStyle.ALL) shouldBe setOf("pkg.Foo")
    }

    "result style success uses single response" {
        val rw = ResponseWithStatus(
            "200",
            Response(
                "", ObjectDataType(
                    DataTypeName("Foo"),
                    "pkg",
                    linkedMapOf(
                        "bar" to PropertyDataType(
                            readOnly = false,
                            writeOnly = false,
                            dataType = StringDataType(),
                            documentation = Documentation())))))

        val er = EndpointResponse(
            rw,
            setOf(
                ResponseWithStatus(
                    "404",
                    Response("text/plain", StringDataType())
            )),
            listOf(rw)
        )

        er.getResponseType(ResultStyle.SUCCESS) shouldBe "Foo"
        er.getResponseImports(ResultStyle.SUCCESS) shouldBe setOf("pkg.Foo")
    }

    "result style SUCCESS has single response" {
        val rw = ResponseWithStatus(
            "201",
            Response("application/json", StringDataType()))

        val er = EndpointResponse(
            rw,
            setOf(ResponseWithStatus(
                "500", Response("text/plain", StringDataType())
            )),
            listOf(rw)
        )

        er.hasSingleResponse(ResultStyle.SUCCESS) shouldBe true
    }

    "result style SUCCESS has single response 200" {
        val rw = ResponseWithStatus(
            "200",
            Response("application/json", StringDataType()))

        val er = EndpointResponse(
            rw,
            setOf(ResponseWithStatus(
                "500", Response("text/plain", StringDataType())
            )),
            listOf(rw)
        )

        er.hasSingleResponse(ResultStyle.SUCCESS) shouldBe false
    }

    "result style ALL has no single response" {
        val rw = ResponseWithStatus(
            "201",
            Response("application/json", StringDataType()))

        val er = EndpointResponse(
            rw,
            setOf(ResponseWithStatus(
                "500", Response("text/plain", StringDataType())
            )),
            listOf(rw)
        )

        er.hasSingleResponse(ResultStyle.ALL) shouldBe false
    }

    "accepts 1xx as success" {
        val ep = endpoint("/foo", HttpMethod.GET) {
            responses {
                status("100") {
                    empty()
                }
            }
        }

        val result = ep.endpointResponses

        result shouldHaveSize 1
        result.first().getResponseType(ResultStyle.SUCCESS) shouldBe "void"
    }

    "accepts 3xx as success" {
        val ep = endpoint("/foo", HttpMethod.GET) {
            responses {
                status("302") {
                    empty()
                }
            }
        }

        val result = ep.endpointResponses

        result shouldHaveSize 1
        result.first().getResponseType(ResultStyle.SUCCESS) shouldBe "void"
    }
})
