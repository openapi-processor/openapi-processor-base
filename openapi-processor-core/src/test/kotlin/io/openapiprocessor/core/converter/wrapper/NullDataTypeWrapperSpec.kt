/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.wrapper

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.mockk.mockk
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.converter.MappingFinder
import io.openapiprocessor.core.converter.SchemaInfo
import io.openapiprocessor.core.model.datatypes.StringDataType
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.parser.NullSchema.Companion.nullSchema
import io.openapiprocessor.core.parser.RefResolver
import io.openapiprocessor.core.support.parseOptions

class NullDataTypeWrapperSpec : StringSpec({
    val resolver = mockk<RefResolver>()
    val any = SchemaInfo.Endpoint("/any", HttpMethod.GET)

    "does not wrap datatype if there is no null mapping" {
        val options = ApiOptions()

        val wrapper = NullDataTypeWrapper(options, MappingFinder(options))

        val info = SchemaInfo(
            SchemaInfo.Endpoint("/any", HttpMethod.GET), "", "", nullSchema, resolver
        )
        val dataType = StringDataType()

        wrapper.wrap(dataType, info).shouldBeSameInstanceAs(dataType)
    }

    "does wrap datatype if there is an endpoint null mapping" {
        val options = parseOptions(mapping =
            """
            |map:
            |  paths:
            |    /any:
            |      null: org.openapitools.jackson.nullable.JsonNullable
            """)

        val wrapper = NullDataTypeWrapper(options, MappingFinder(options))

        val info = SchemaInfo(
            SchemaInfo.Endpoint("/any", HttpMethod.GET), "", "", nullSchema, resolver
        )
        val dataType = StringDataType()

        val result = wrapper.wrap(dataType, info)
        result.getTypeName().shouldBe("JsonNullable<String>")
        result.getPackageName().shouldBe("org.openapitools.jackson.nullable")
    }

    "does wrap datatype if there is an endpoint method null mapping" {
        val options = parseOptions(mapping =
            """
            |map:
            |  paths:
            |    /any:
            |      patch:
            |       null: org.openapitools.jackson.nullable.JsonNullable
            """)

        val wrapper = NullDataTypeWrapper(options, MappingFinder(options))

        val info = SchemaInfo(
            SchemaInfo.Endpoint("/any", HttpMethod.PATCH), "", "", nullSchema, resolver
        )
        val dataType = StringDataType()

        val result = wrapper.wrap(dataType, info)
        result.getTypeName().shouldBe("JsonNullable<String>")
        result.getPackageName().shouldBe("org.openapitools.jackson.nullable")
    }
})
