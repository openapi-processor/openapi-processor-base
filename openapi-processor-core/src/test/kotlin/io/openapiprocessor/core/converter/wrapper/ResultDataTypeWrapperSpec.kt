/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.wrapper

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.openapiprocessor.core.converter.MappingFinder
import io.openapiprocessor.core.converter.SchemaInfo
import io.openapiprocessor.core.model.datatypes.StringDataType
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.parser.NullSchema
import io.openapiprocessor.core.parser.RefResolver
import io.openapiprocessor.core.support.parseOptions
import io.openapiprocessor.core.writer.java.JavaIdentifier

class ResultDataTypeWrapperSpec: StringSpec({
    val resolver = mockk<RefResolver>()

    "does use plain schema result if there is a plain mapping" {
        val options = parseOptions(mapping =
            """
            |map:
            |  paths:
            |    /jsonl:
            |      result: plain
            """)

        val wrapper = ResultDataTypeWrapper(options, JavaIdentifier(), MappingFinder(options))

        val info = SchemaInfo(
            SchemaInfo.Endpoint("/jsonl", HttpMethod.GET),
            "",
            "",
            NullSchema,
            resolver
        )
        val dataType = StringDataType()

        val result = wrapper.wrap(dataType, info)

        result.getTypeName().shouldBe("String")
        result.getPackageName().shouldBe("java.lang")
    }

    "does use result wrapper if there is a result mapping" {
        val options = parseOptions(mapping =
            """
            |map:
            |  paths:
            |    /jsonl:
            |      result: io.result.Result
            """)

        val wrapper = ResultDataTypeWrapper(options, JavaIdentifier(), MappingFinder(options))

        val info = SchemaInfo(
            SchemaInfo.Endpoint("/jsonl", HttpMethod.GET),
            "",
            "",
            NullSchema,
            resolver
        )
        val dataType = StringDataType()

        val result = wrapper.wrap(dataType, info)

        result.getTypeName().shouldBe("Result<String>")
        result.getPackageName().shouldBe("io.result")
    }

    "does replace result if there is a plain => target type mapping" {
        val options = parseOptions(mapping =
            """
            |map:
            |  paths:
            |    /jsonl:
            |      result: plain => io.stream.Response
            """)

        val wrapper = ResultDataTypeWrapper(options, JavaIdentifier(), MappingFinder(options))

        val info = SchemaInfo(
            SchemaInfo.Endpoint("/jsonl", HttpMethod.GET),
            "",
            "",
            NullSchema,
            resolver
        )
        val dataType = StringDataType()

        val result = wrapper.wrap(dataType, info)

        result.getTypeName().shouldBe("Response")
        result.getPackageName().shouldBe("io.stream")
    }
})
