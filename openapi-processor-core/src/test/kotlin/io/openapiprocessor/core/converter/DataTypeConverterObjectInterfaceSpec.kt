/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.model.DataTypes
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.support.getSchemaInfo
import io.openapiprocessor.core.support.parseApiBody
import io.openapiprocessor.core.support.parseOptions
import io.openapiprocessor.core.writer.java.JavaIdentifier

class DataTypeConverterObjectInterfaceSpec: StringSpec({
    isolationMode = IsolationMode.InstancePerTest

    val dataTypes = DataTypes()
    val identifier = JavaIdentifier()

    "adds interface to ObjectDataType from global mapping" {
        val options = parseOptions(mapping =
            """
            |map:
            |  types:
            |    - type: Foo =+ java.io.Serializable
            """.trimIndent()
        )

        val openApi = parseApiBody(
            $$"""
            paths:
              /foo:
                get:
                  responses:
                    '200':
                      description: ok
                      content:
                        application/json:
                          schema:
                            $ref: '#/components/schemas/Foo'
            components:
              schemas:
                Foo:
                  type: object
                  properties:
                    foo:
                      type: string
            """.trimIndent()
        )

        val schemaInfo = openApi.getSchemaInfo(
            "Foo",
            "/foo",
            HttpMethod.GET,
            "200",
            "application/json",
            false)

        val converter = DataTypeConverter(options, identifier)
        converter.convert(schemaInfo, dataTypes)

        val modelDataTypes = dataTypes.getModelDataTypes()
        modelDataTypes.size shouldBe 1

        val fooDataType = modelDataTypes.first()
        val implements = fooDataType.implementsDataTypes
        val serialize = implements.first()

        serialize.getName() shouldBe "Serializable"
        serialize.getPackageName() shouldBe "java.io"
    }

    "adds interface to allOf composed ObjectDataType from global mapping" {
        val options = parseOptions(mapping =
            """
            |map:
            |  types:
            |    - type: FooBar =+ java.io.Serializable
            """.trimIndent()
        )

        val openApi = parseApiBody(
            $$"""
            paths:
              /foo:
                get:
                  responses:
                    '200':
                      description: ok
                      content:
                        application/json:
                          schema:
                            $ref: '#/components/schemas/FooBar'
            components:
              schemas:
                FooBar:
                  allOf:
                    - type: object
                      properties:
                        foo:
                          type: string
                    - type: object
                      properties:
                        bar:
                          type: string
            """.trimIndent()
        )

        val schemaInfo = openApi.getSchemaInfo(
            "FooBar",
            "/foo",
            HttpMethod.GET,
            "200",
            "application/json",
            false)

        val converter = DataTypeConverter(options, identifier)
        converter.convert(schemaInfo, dataTypes)

        val modelDataTypes = dataTypes.getModelDataTypes()
        modelDataTypes.size shouldBe 1

        val fooDataType = modelDataTypes.first()
        val implements = fooDataType.implementsDataTypes
        val serialize = implements.first()

        serialize.getName() shouldBe "Serializable"
        serialize.getPackageName() shouldBe "java.io"
    }
})
