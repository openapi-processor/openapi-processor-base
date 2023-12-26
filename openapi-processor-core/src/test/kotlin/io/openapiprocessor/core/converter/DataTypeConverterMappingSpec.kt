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
import io.openapiprocessor.core.converter.mapping.AddParameterTypeMapping
import io.openapiprocessor.core.converter.mapping.TargetType
import io.openapiprocessor.core.converter.mapping.TypeMapping
import io.openapiprocessor.core.model.DataTypes
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.model.datatypes.*
import io.openapiprocessor.core.parser.ParserType
import io.openapiprocessor.core.support.getParameterSchemaInfo
import io.openapiprocessor.core.support.getSchemaInfo
import io.openapiprocessor.core.support.parse

class DataTypeConverterMappingSpec: StringSpec({
    isolationMode = IsolationMode.InstancePerTest

    val dataTypes = DataTypes()

    "mapped object data type has source data type" {
        val openApi = parse("""
           openapi: 3.0.2
           info:
             title: API
             version: 1.0.0
           
           paths:
             /foo:
               get:
                 responses:
                   '200':
                     description: empty
                     content:
                       application/json:
                         schema:
                           description: a Foo
                           type: object
                           properties:
                             foo:
                               type: string
        """.trimIndent(), ParserType.INTERNAL)


        val options = ApiOptions()
        options.typeMappings = listOf(
            TypeMapping("Foo", null, "package.Bar")
        )

        val schemaInfo = openApi.getSchemaInfo("Foo",
            "/foo", HttpMethod.GET, "200", "application/json")

        // when:
        val converter = DataTypeConverter(options)
        val datatype = converter.convert(schemaInfo, dataTypes)

        // then:
        datatype.shouldBeInstanceOf<MappedSourceDataType>()
        datatype.sourceDataType.shouldBeInstanceOf<ObjectDataType>()
    }

    "mapped composed object has source data type" {
        val openApi = parse("""
            openapi: 3.0.3
            info:
              title: merge allOf into same object
              version: 1.0.0
            
            paths:
              /composed:
                get:
                  responses:
                    '200':
                      description: create result from allOff object
                      content:
                        application/json:
                          schema:
                            allOf:
                              - type: object
                                properties:
                                  prop1:
                                    type: string
                              - type: object
                                properties:
                                  prop2:
                                    type: string
                              - type: object
                                properties:
                                  prop2:
                                    type: string            
        """.trimIndent(), ParserType.INTERNAL)

        val options = ApiOptions()
        options.typeMappings = listOf(
            TypeMapping("Foo", null, "package.Bar")
        )

        val schemaInfo = openApi.getSchemaInfo("Foo",
            "/composed", HttpMethod.GET, "200", "application/json")

        // when:
        val converter = DataTypeConverter(options)
        val datatype = converter.convert(schemaInfo, dataTypes)

        // then:
        datatype.shouldBeInstanceOf<MappedSourceDataType>()
        datatype.sourceDataType.shouldBeInstanceOf<AllOfObjectDataType>()
    }

    "mapped array has source data type" {
        val openApi = parse("""
            openapi: 3.0.2
            info:
              title: test template
              version: 1.0.0

            paths:

              /array:
                get:
                  responses:
                    '200':
                      description: the foo result
                      content:
                        application/json:
                          schema:
                            type: array
                            items:
                              type: object
                              properties:
                                bar:
                                  type: string
        """.trimIndent(), ParserType.INTERNAL)

        val options = ApiOptions()
        options.typeMappings = listOf(
            TypeMapping("array", null, "java.util.List"),
            TypeMapping("ArrayFoo", null, "package.Bar"),
        )

        val schemaInfo = openApi.getSchemaInfo("Foo",
            "/array", HttpMethod.GET, "200", "application/json")

        // when:
        val converter = DataTypeConverter(options)
        val datatype = converter.convert(schemaInfo, dataTypes)

        // then:
        datatype.shouldBeInstanceOf<MappedCollectionDataType>()
        datatype.sourceDataType.shouldBeInstanceOf<ArrayDataType>()
        datatype.item.shouldBeInstanceOf<MappedDataType>()
        (datatype.item as MappedDataType).sourceDataType.shouldBeInstanceOf<ObjectDataType>()
    }

    "mapped data type has nested generics" {
        val openApi = parse("""
            openapi: 3.0.2
            info:
              title: nested generics
              version: 1.0.0

            paths:
              /foo:
                get:
                  responses:
                    '200':
                      description: OK
                      content:
                        '*/*':
                          schema:
                            type: object
                            additionalProperties:
                              type: array
                              items:
                                type: string
        """.trimIndent(), ParserType.INTERNAL)

        val options = ApiOptions()
        options.typeMappings = listOf(
            TypeMapping("Dictionary", null,
                "java.util.Map",
                genericTypes = listOf(
                    TargetType("java.lang.String"),
                    TargetType("java.util.List",
                        genericTypes = listOf(
                            TargetType("java.lang.String")
                        ))
                )),
        )

        val schemaInfo = openApi.getSchemaInfo("Dictionary",
            "/foo", HttpMethod.GET, "200", "*/*")

        // when:
        val converter = DataTypeConverter(options)
        val datatype = converter.convert(schemaInfo, dataTypes)

        // then:
        datatype.shouldBeInstanceOf<MappedDataType>()
        datatype.getTypeName() shouldBe "Map<String, List<String>>"
    }

    "additional parameter has no source type" {
        val options = ApiOptions()
        options.typeMappings = listOf(
            AddParameterTypeMapping("add",
                TypeMapping(null, null, "additional.Parameter")
        ))

        // see ApiConvert.createAdditionalParameter()
        val tm = options.typeMappings.first().getChildMappings().first() as TypeMapping

        // when:
        val converter = DataTypeConverter(options)
        val datatype = converter.createAdditionalParameterDataType(tm)

        // then:
        datatype.shouldBeInstanceOf<MappedDataType>()
        datatype.getTypeName().shouldBe("Parameter")
        datatype.sourceDataType.shouldBeNull()
    }

    "mapped data type has wildcard generic parameter" {
        val openApi = parse("""
            openapi: 3.1.0
            info:
              title: wildcard generic parameter
              version: 1.0.0

            paths:
              /foo:
                get:
                  parameters:
                    - in: query
                      name: foo
                      schema:
                        type: string
                  responses:
                    '204':
                      description: none
        """.trimIndent(), ParserType.INTERNAL)

        val options = ApiOptions()
        options.typeMappings = listOf(
            TypeMapping("string", null, "bar.Bar",
                genericTypes = listOf(
                    TargetType("?")
                ))
        )

        val schemaInfo = openApi.getParameterSchemaInfo("/foo", HttpMethod.GET, "foo")

        // when:
        val converter = DataTypeConverter(options)
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
