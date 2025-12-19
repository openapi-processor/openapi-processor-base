/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.openapiprocessor.core.model.DataTypes
import io.openapiprocessor.core.model.datatypes.ObjectDataType
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.support.getBodySchemaInfo
import io.openapiprocessor.core.support.parseApiBody
import io.openapiprocessor.core.support.parseOptions
import io.openapiprocessor.core.writer.java.JavaIdentifier

class DataTypeConverterNullSpec: StringSpec({

    val dataTypes = DataTypes()
    val identifier = JavaIdentifier()

    "wraps object property in a null wrapper if a null mappings exists" {
        val options = parseOptions(mapping =
            """
            |map:
            |  paths:
            |    /foo:
            |      null: org.openapitools.jackson.nullable.JsonNullable
            """)

        val openApi = parseApiBody($$"""
            paths:
              /foo:
                patch:
                  requestBody:
                    content:
                      application/json:
                        schema:
                          $ref: '#/components/schemas/Foo'
                  responses:
                    '204':
                      description: empty
            
            components:
              schemas:
            
                Foo:
                  description: a Foo
                  type: object
                  properties:
                    foo:
                      type: [string, null]
            """
        )

        val schemaInfo = openApi.getBodySchemaInfo("Foo",
            "/foo", HttpMethod.PATCH, "application/json")

        // when:
        val converter = DataTypeConverter(options, identifier)
        val datatype = converter.convert(schemaInfo, dataTypes)

        // then:
        datatype.shouldBeInstanceOf<ObjectDataType>()
        val fooDataType = datatype.getObjectProperty("foo")
        fooDataType.getTypeName().shouldBe("JsonNullable<String>")
    }
})
