/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.openapiprocessor.core.model.DataTypes
import io.openapiprocessor.core.model.datatypes.*
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.support.getParameterSchemaInfo
import io.openapiprocessor.core.support.getSchemaInfo
import io.openapiprocessor.core.support.parseApi
import io.openapiprocessor.core.support.parseOptions
import io.openapiprocessor.core.writer.java.JavaIdentifier

class DataTypeConverterMappingSpec: StringSpec({
    isolationMode = IsolationMode.InstancePerTest

    val dataTypes = DataTypes()
    val identifier = JavaIdentifier()

    "mapped object data type has source data type" {
        val options = parseOptions(mapping =
            """
            |map:
            |  types:
            |    - type: Foo => package.Bar
            """)

        val openApi = parseApi(body =
            """
            |paths:
            |  /foo:
            |    get:
            |      responses:
            |        '200':
            |           description: empty
            |           content:
            |             application/json:
            |               schema:
            |                 description: a Foo
            |                 type: object
            |                 properties:
            |                   foo:
            |                     type: string
            """)

        val schemaInfo = openApi.getSchemaInfo(
            "Foo", "/foo", HttpMethod.GET, "200", "application/json")

        // when:
        val converter = DataTypeConverter(options, identifier)
        val datatype = converter.convert(schemaInfo, dataTypes)

        // then:
        datatype.shouldBeInstanceOf<SourceDataType>()
        datatype.sourceDataType.shouldBeInstanceOf<ObjectDataType>()
    }

    "mapped composed object has source data type" {
        val options = parseOptions(mapping =
            """
            |map:
            |  types:
            |    - type: Foo => package.Bar
            """)

        val openApi = parseApi(body =
            """
            |paths:
            |  /composed:
            |    get:
            |      responses:
            |        '200':
            |          description: create result from allOff object
            |          content:
            |            application/json:
            |              schema:
            |                allOf:
            |                  - type: object
            |                    properties:
            |                      prop1:
            |                        type: string
            |                  - type: object
            |                    properties:
            |                      prop2:
            |                        type: string
            |                  - type: object
            |                    properties:
            |                      prop2:
            |                        type: string            
            """)

        val schemaInfo = openApi.getSchemaInfo("Foo",
            "/composed", HttpMethod.GET, "200", "application/json")

        // when:
        val converter = DataTypeConverter(options, identifier)
        val datatype = converter.convert(schemaInfo, dataTypes)

        // then:
        datatype.shouldBeInstanceOf<SourceDataType>()
        datatype.sourceDataType.shouldBeInstanceOf<AllOfObjectDataType>()
    }

    "mapped array has source data type" {
        val options = parseOptions(mapping =
            """
            |map:
            |  types:
            |    - type: array => java.util.List
            |    - type: FooArrayItem => package.Bar
            """)

        val openApi = parseApi(body =
            """
            |paths:
            |  /array:
            |    get:
            |      responses:
            |        '200':
            |          description: the foo result
            |          content:
            |            application/json:
            |              schema:
            |                type: array
            |                items:
            |                  type: object
            |                  properties:
            |                    bar:
            |                      type: string
            """)

        val schemaInfo = openApi.getSchemaInfo("Foo",
            "/array", HttpMethod.GET, "200", "application/json")

        // when:
        val converter = DataTypeConverter(options, identifier)
        val datatype = converter.convert(schemaInfo, dataTypes)

        // then:
        datatype.shouldBeInstanceOf<MappedCollectionDataType>()
        datatype.sourceDataType.shouldBeInstanceOf<ArrayDataType>()
        datatype.item.shouldBeInstanceOf<MappedDataType>()
        (datatype.item as MappedDataType).sourceDataType.shouldBeInstanceOf<ObjectDataType>()
    }

    "mapped data type has nested generics" {
        val options = parseOptions(mapping =
            """
            |map:
            |  types:
            |    - type: Dictionary => java.util.Map
            |      generics:
            |        - java.lang.String
            |        - java.util.List<java.lang.String>
            |    - type: ArrayFoo => package.Bar
            """)

        val openApi = parseApi(body =
            """
            |paths:
            |  /foo:
            |    get:
            |      responses:
            |        '200':
            |          description: OK
            |          content:
            |            '*/*':
            |              schema:
            |                type: object
            |                additionalProperties:
            |                  type: array
            |                  items:
            |                    type: string
            """)

        val schemaInfo = openApi.getSchemaInfo("Dictionary",
            "/foo", HttpMethod.GET, "200", "*/*")

        // when:
        val converter = DataTypeConverter(options, identifier)
        val datatype = converter.convert(schemaInfo, dataTypes)

        // then:
        datatype.shouldBeInstanceOf<MappedDataType>()
        datatype.getTypeName() shouldBe "Map<String, List<String>>"
    }

    "additional parameter has no source type" {
        val options = parseOptions(mapping =
            """
            |map:
            |  parameters:
            |    - add: add => additional.Parameter
            """)

        val tm = options.globalMappings.findAddParameterTypeMappings { _ -> true }.first().mapping

        // when:
        val converter = DataTypeConverter(options, identifier)
        val datatype = converter.createAdditionalParameterDataType(tm)

        // then:
        datatype.shouldBeInstanceOf<MappedDataType>()
        datatype.getTypeName().shouldBe("Parameter")
        datatype.sourceDataType.shouldBeNull()
    }

    "mapped data type has wildcard generic parameter" {
        val options = parseOptions(mapping =
            """
            |map:
            |  types:
            |    - type: string => bar.Bar<?>
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
            |      responses:
            |        '204':
            |          description: none
            """)

        val schemaInfo = openApi.getParameterSchemaInfo("/foo", HttpMethod.GET, "foo")

        // when:
        val converter = DataTypeConverter(options, identifier)
        val datatype = converter.convert(schemaInfo, dataTypes)

        // then:
        datatype.shouldBeInstanceOf<MappedDataType>()
        datatype.getTypeName() shouldBe "Bar<?>"
        datatype.getPackageName() shouldBe "bar"
        datatype.getImports() shouldHaveSingleElement "bar.Bar"
        datatype.genericTypes.shouldHaveSize(1)
        val generic = datatype.genericTypes.first()
        generic.getImports().shouldBeEmpty()
        generic.getPackageName() shouldBe ""
    }
})
