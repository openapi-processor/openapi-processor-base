/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.mockk.mockk
import io.openapiprocessor.core.model.Documentation
import io.openapiprocessor.core.model.datatypes.DataTypeName
import io.openapiprocessor.core.model.datatypes.ObjectDataType
import io.openapiprocessor.core.model.datatypes.PropertyDataType
import io.openapiprocessor.core.model.datatypes.StringDataType
import io.openapiprocessor.core.parser.ContentType
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.parser.HttpStatus
import io.openapiprocessor.core.parser.Response
import io.openapiprocessor.core.model.Response as ModelResponse


class ContentTypeInterfaceCollectorSpec: StringSpec({

    val foo = ObjectDataType(
            DataTypeName("Foo"),
            "pkg",
            linkedMapOf(
                "foo" to PropertyDataType(
                    readOnly = false,
                    writeOnly = false,
                    dataType = StringDataType(),
                    documentation = Documentation())))

    val oof = ObjectDataType(
            DataTypeName("Oof"),
            "pkg",
            linkedMapOf(
                "oof" to PropertyDataType(
                    readOnly = false,
                    writeOnly = false,
                    dataType = StringDataType(),
                    documentation = Documentation())))

    val bar = ObjectDataType(
            DataTypeName("Bar"),
            "pkg",
            linkedMapOf(
                "bar" to PropertyDataType(
                    readOnly = false,
                    writeOnly = false,
                    dataType = StringDataType(),
                    documentation = Documentation())))

    "returns empty result on empty inputs" {
        val collector = ContentTypeInterfaceCollector("/foo", HttpMethod.GET)

        val cti = collector.collectContentTypeInterfaces(emptyMap(), emptyMap())

        cti.shouldBeEmpty()
    }

    "returns empty result with identical response data types" {
        val collector = ContentTypeInterfaceCollector("/foo", HttpMethod.GET)

        val ctr = mutableMapOf<ContentType, Map<HttpStatus, Response>>()
        ctr["application/json"] = mapOf(
            "200" to mockk<Response>(),
            "201" to mockk<Response>()
        )

        val srr = mutableMapOf<HttpStatus, List<ModelResponse>>()
        srr["200"] = listOf(ModelResponse("application/json", foo))
        srr["201"] = listOf(ModelResponse("application/json", foo))

        val cti = collector.collectContentTypeInterfaces(ctr, srr)

        cti.shouldBeEmpty()
    }

    "returns interface result with different response data types" {
        val collector = ContentTypeInterfaceCollector("/foo", HttpMethod.GET)

        val ctr = mutableMapOf<ContentType, Map<HttpStatus, Response>>()
        ctr["application/json"] = mapOf(
            "200" to mockk<Response>(),
            "201" to mockk<Response>(),
        )

        val srr = mutableMapOf<HttpStatus, List<ModelResponse>>()
        srr["200"] = listOf(ModelResponse("application/json", foo))
        srr["201"] = listOf(ModelResponse("application/json", bar))

        val cti = collector.collectContentTypeInterfaces(ctr, srr)

        cti shouldHaveSize 1

        cti["application/json"].shouldNotBeNull()
    }

    "returns interface result with different response data types including errors" {
        val collector = ContentTypeInterfaceCollector("/foo", HttpMethod.GET)

        val ctr = mutableMapOf<ContentType, Map<HttpStatus, Response>>()
        ctr["application/json"] = mapOf(
            "200" to mockk<Response>(),
            "202" to mockk<Response>(),
            "400" to mockk<Response>(),
            "401" to mockk<Response>(),
        )

        val srr = mutableMapOf<HttpStatus, List<ModelResponse>>()
        srr["200"] = listOf(ModelResponse("application/json", foo))
        srr["201"] = listOf(ModelResponse("application/json", bar))
        srr["400"] = listOf(ModelResponse("application/json", oof))
        srr["401"] = listOf(ModelResponse("application/json", oof))

        val cti = collector.collectContentTypeInterfaces(ctr, srr)

        cti shouldHaveSize 1

        cti["application/json"].shouldNotBeNull()
    }

    "returns interface result with different response data types, with primitve" {
        val collector = ContentTypeInterfaceCollector("/foo", HttpMethod.GET)

        val ctr = mutableMapOf<ContentType, Map<HttpStatus, Response>>()
        ctr["application/json"] = mapOf(
            "200" to mockk<Response>(),
            "201" to mockk<Response>(),
        )

        val srr = mutableMapOf<HttpStatus, List<ModelResponse>>()
        srr["200"] = listOf(ModelResponse("application/json", foo))
        srr["201"] = listOf(ModelResponse("application/json", StringDataType()))

        val cti = collector.collectContentTypeInterfaces(ctr, srr)

        cti.shouldBeEmpty()
    }
})
