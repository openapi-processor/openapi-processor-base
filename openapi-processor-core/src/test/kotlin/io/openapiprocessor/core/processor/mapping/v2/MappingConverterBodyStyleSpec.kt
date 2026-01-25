/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.support.MappingConstants.VERSION
import io.openapiprocessor.core.converter.MappingFinderQuery
import io.openapiprocessor.core.converter.mapping.steps.EndpointsStep
import io.openapiprocessor.core.converter.mapping.steps.GlobalsStep
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.processor.MappingReader

class MappingConverterBodyStyleSpec: StringSpec({
    val reader = MappingReader()

    "read global body style, null" {
        val yaml = """
           |openapi-processor-mapping: $VERSION
           |
           |options:
           |  package-name: io.openapiprocessor.somewhere
           | 
           |map: {}
           """.trimMargin()

        // when:
        val mapping = reader.read (yaml) as Mapping
        val mappings = MappingConverter(mapping).convert().globalMappings

        // then:
        val bodyStyle = mappings.getBodyStyle(GlobalsStep())

        bodyStyle.shouldBeNull()
    }

    "read global body style, object" {
        val yaml = """
           |openapi-processor-mapping: $VERSION
           |
           |options:
           |  package-name: io.openapiprocessor.somewhere
           | 
           |map:
           |  body-style: object
           """.trimMargin()

        // when:
        val mapping = reader.read (yaml) as Mapping
        val mappings = MappingConverter(mapping).convert().globalMappings

        // then:
        val bodyStyle = mappings.getBodyStyle(GlobalsStep())!!

        bodyStyle.shouldBe(BodyStyle.OBJECT)
    }

    "read global body style, destructure" {
        val yaml = """
           |openapi-processor-mapping: $VERSION
           |
           |options:
           |  package-name: io.openapiprocessor.somewhere
           | 
           |map:
           |  body-style: destructure
           """.trimMargin()

        // when:
        val mapping = reader.read (yaml) as Mapping
        val mappings = MappingConverter(mapping).convert().globalMappings

        // then:
        val bodyStyle = mappings.getBodyStyle(GlobalsStep())!!

        bodyStyle.shouldBe(BodyStyle.DESTRUCTURE)
    }

    "read endpoint body style" {
        val yaml = """
           |openapi-processor-mapping: $VERSION
           |
           |options:
           |  package-name: io.openapiprocessor.somewhere
           | 
           |map:
           |  paths:
           |    /foo:
           |      body-style: destructure
           |
           |      post:
           |        body-style: object
           |        
           """.trimMargin()

        // when:
        val mapping = reader.read (yaml) as Mapping
        val mappings = MappingConverter(mapping).convert().endpointMappings

        // then:
        val bodyQuery = MappingFinderQuery(path = "/foo", method = HttpMethod.PUT)
        val bodyStyle = mappings["/foo"]!!.getBodyStyle(bodyQuery, EndpointsStep(bodyQuery))!!
        bodyStyle shouldBe BodyStyle.DESTRUCTURE

        val bodyQueryPost = MappingFinderQuery(path = "/foo", method = HttpMethod.POST)
        val bodyStylePost = mappings["/foo"]!!.getBodyStyle(bodyQueryPost, EndpointsStep(bodyQueryPost))!!
        bodyStylePost shouldBe BodyStyle.OBJECT
    }
})
