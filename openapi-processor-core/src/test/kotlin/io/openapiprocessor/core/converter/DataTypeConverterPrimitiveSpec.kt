/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import io.openapiprocessor.core.model.DataTypes
import io.openapiprocessor.core.model.datatypes.MappedCollectionDataTypePrimitive
import io.openapiprocessor.core.model.datatypes.MappedDataTypePrimitive
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.parser.RefResolver
import io.openapiprocessor.core.parser.Schema
import io.openapiprocessor.core.support.getParameterSchemaInfo
import io.openapiprocessor.core.support.parseApi
import io.openapiprocessor.core.support.parseOptions
import io.openapiprocessor.core.writer.java.JavaIdentifier

class DataTypeConverterPrimitiveSpec: StringSpec({
    isolationMode = IsolationMode.InstancePerTest

    val dataTypes = DataTypes()
    val identifier = JavaIdentifier()

    "ignores unknown primitive data type format" {
        val any = SchemaInfo.Endpoint("/any", HttpMethod.GET)
        val converter = DataTypeConverter(ApiOptions(), identifier, MappingFinderX(ApiOptions()))
        val resolver = mockk<RefResolver>()

        forAll(
            row("string","unknown", "String"),
            row("integer","unknown", "Integer"),
            row("number","unknown", "Float"),
            row("boolean","unknown", "Boolean")
        ) { type, format, dataTypeName ->
            val schema = mockk<Schema>(relaxed = true)
            every { schema.getRef() } returns null
            every { schema.getType() } returns type
            every { schema.getFormat() } returns format

            // when:
            val info = SchemaInfo(any, "foo", schema = schema, resolver = resolver)
            val datatype = converter.convert(info, DataTypes())

            // then:
            datatype.getTypeName() shouldBe dataTypeName
        }
    }

    "maps data type to primitive type" {
        val options = parseOptions(mapping =
            """
            |map:
            |  types:
            |    - type: string:byte => byte
            """)

        val openApi = parseApi(body =
            """
            |paths:
            |  /foo:
            |    get:
            |      parameters:
            |        - in: query
            |          name: foo
            |          schema:
            |            type: string
            |            format: byte
            |      responses:
            |        '204':
            |          description: none
            """)

        val schemaInfo = openApi.getParameterSchemaInfo("/foo", HttpMethod.GET, "foo")

        // when:
        val converter = DataTypeConverter(options, identifier)
        val datatype = converter.convert(schemaInfo, dataTypes)

        // then:
        datatype.shouldBeInstanceOf<MappedDataTypePrimitive>()
        datatype.getTypeName() shouldBe "byte"
        datatype.getPackageName() shouldBe ""
        datatype.getImports().shouldBeEmpty()
    }

    "maps data type to primitive type array" {
        val options = parseOptions(mapping =
            """
            |map:
            |  types:
            |    - type: string:bytes => byte[]
            """)

        val openApi = parseApi(body =
            """
            |paths:
            |  /foo:
            |    get:
            |      parameters:
            |        - in: query
            |          name: foo
            |          schema:
            |            type: string
            |            format: bytes
            |      responses:
            |        '204':
            |          description: none
            """)

        val schemaInfo = openApi.getParameterSchemaInfo("/foo", HttpMethod.GET, "foo")

        // when:
        val converter = DataTypeConverter(options, identifier)
        val datatype = converter.convert(schemaInfo, dataTypes)

        // then:
        datatype.shouldBeInstanceOf<MappedCollectionDataTypePrimitive>()
        datatype.getTypeName() shouldBe "byte[]"
        datatype.getPackageName() shouldBe ""
        datatype.getImports().shouldBeEmpty()
    }
})
