/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.openapiprocessor.core.converter.mapping.TargetType
import io.openapiprocessor.core.converter.mapping.TypeMapping
import io.openapiprocessor.core.model.DataTypes
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.model.datatypes.*
import io.openapiprocessor.core.support.getBodySchemaInfo
import io.openapiprocessor.core.support.getSchemaInfo
import io.openapiprocessor.core.support.parse
import io.openapiprocessor.core.writer.java.JavaIdentifier

class DataTypeConverterSuffixSpec: StringSpec({
    val identifier = JavaIdentifier()

    "adds suffix to model data type name" {
        val dataTypes = DataTypes()
        val options = ApiOptions()
        options.modelNameSuffix = "Suffix"

        val openApi = parse("""
           openapi: 3.0.2
           info:
             title: API
             version: 1.0.0
           
           paths:
             /foo:
               post:
                 requestBody:
                   content:
                     application/json:
                       schema:
                         ${'$'}ref: '#/components/schemas/Foo'
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
                     type: string
                 
        """.trimIndent())

        val schemaInfo = openApi.getBodySchemaInfo("Foo",
            "/foo", HttpMethod.POST, "application/json")

        // when:
        val converter = DataTypeConverter(options, identifier)
        val datatype = converter.convert(schemaInfo, dataTypes)

        // then:
        dataTypes.find("Foo") shouldBeSameInstanceAs datatype

        datatype.shouldBeInstanceOf<ObjectDataType>()
        datatype.getName().shouldBe("Foo")
        datatype.getTypeName().shouldBe("FooSuffix")
    }

    "adds suffix to model enum data type name" {
        val dataTypes = DataTypes()
        val options = ApiOptions()
        options.modelNameSuffix = "Suffix"

        val openApi = parse("""
           openapi: 3.0.2
           info:
             title: API
             version: 1.0.0
           
           paths:
             /foo:
               post:
                 requestBody:
                   content:
                     application/json:
                       schema:
                         ${'$'}ref: '#/components/schemas/Bar'
                 responses:
                   '204':
                     description: empty
           
           components:
             schemas:
           
                Bar:
                  type: string
                  enum:
                    - bar-1
                    - bar-2
                 
        """.trimIndent())

        val schemaInfo = openApi.getBodySchemaInfo("Bar",
            "/foo", HttpMethod.POST, "application/json")

        // when:
        val converter = DataTypeConverter(options, identifier)
        val datatype = converter.convert(schemaInfo, dataTypes)

        // then:
        dataTypes.find("Bar") shouldBeSameInstanceAs datatype

        datatype.shouldBeInstanceOf<StringEnumDataType>()
        datatype.getName().shouldBe("Bar")
        datatype.getTypeName().shouldBe("BarSuffix")
    }

    "adds suffix to allOf model data type name" {
        val dataTypes = DataTypes()
        val options = ApiOptions()
        options.modelNameSuffix = "Suffix"

        val openApi = parse("""
openapi: 3.0.2
info:
  title: merge allOf into same object
  version: 1.0.0

paths:
  /foo:
    get:
      responses:
        '200':
          description: allOf object
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
                 
        """.trimIndent())

        val schemaInfo = openApi.getSchemaInfo("Foo",
            "/foo", HttpMethod.GET, "200", "application/json")

        // when:
        val converter = DataTypeConverter(options, identifier)
        val datatype = converter.convert(schemaInfo, dataTypes)

        // then:
        dataTypes.find("Foo") shouldBeSameInstanceAs datatype

        datatype.shouldBeInstanceOf<AllOfObjectDataType>()
        datatype.getName().shouldBe("Foo")
        datatype.getTypeName().shouldBe("FooSuffix")
    }

    "adds suffix to array model data type name" {
        val dataTypes = DataTypes()
        val options = ApiOptions()
        options.modelNameSuffix = "Suffix"

        val openApi = parse("""
openapi: 3.0.2
info:
  title: array
  version: 1.0.0

paths:
  /foo:
    get:
      responses:
        '200':
          description: array
          content:
            application/json:
              schema:
                type: array
                items: 
                  ${'$'}ref: '#/components/schemas/Foo'

components:
 schemas:

    Foo:
      type: object
      properties:
        bar:
          type: string          

        """.trimIndent())

        val schemaInfo = openApi.getSchemaInfo("FooResponse200",
            "/foo", HttpMethod.GET, "200", "application/json")

        // when:
        val converter = DataTypeConverter(options, identifier)
        val datatype = converter.convert(schemaInfo, dataTypes)

        // then:
        datatype.shouldBeInstanceOf<ArrayDataType>()
        datatype.getName().shouldBe("Foo[]")
        datatype.getTypeName().shouldBe("FooSuffix[]")
    }

    "adds suffix to mapped data type generics" {
        val options = ApiOptions()
        options.packageName = "model"
        options.modelNameSuffix = "Suffix"

        // when:
        val converter = DataTypeConverter(options, identifier)
        val datatype = converter.createAdditionalParameterDataType(
            TypeMapping(
                null,
                null,
                "other.Foo",
                listOf(TargetType("model.Bar"))
            )
        )

        // then:
        datatype.shouldBeInstanceOf<MappedDataType>()
        datatype.getName().shouldBe("Foo<Bar>")
        datatype.getTypeName().shouldBe("Foo<BarSuffix>")
    }
})
