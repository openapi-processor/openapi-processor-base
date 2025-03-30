/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.model.DataTypes
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.model.datatypes.ModelDataType
import io.openapiprocessor.core.support.getBodySchemaInfo
import io.openapiprocessor.core.support.parse
import io.openapiprocessor.core.writer.java.JavaIdentifier

class DataTypeConverterOneOfSpec: StringSpec({
    val identifier = JavaIdentifier()

    "creates interface and implementing model classes for oneOf with objects" {
        val dataTypes = DataTypes()
        val options = ApiOptions()
        options.oneOfInterface = true

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
                  type: object
                  properties:
                    myProperties:
                      ${'$'}ref: '#/components/schemas/GenericProperties'
                      
                GenericProperties:
                  oneOf:
                    - ${'$'}ref: '#/components/schemas/SpecificPropertiesOne'
                    - ${'$'}ref: '#/components/schemas/SpecificPropertiesTwo'
                
                SpecificPropertiesOne:
                  type: object
                  properties:
                    foo:
                      type: string
                      maxLength: 200
                
                SpecificPropertiesTwo:
                  type: object
                  properties:
                    bar:
                      type: string
                      maxLength: 100
                 
        """.trimIndent())

        val schemaInfo = openApi.getBodySchemaInfo("Foo",
            "/foo", HttpMethod.POST, "application/json")

        // when:
        val converter = DataTypeConverter(options, identifier)
        converter.convert(schemaInfo, dataTypes)

        // then:
        val ifDataType = dataTypes.getInterfaceDataTypes().first()
        ifDataType.getName() shouldBe "GenericProperties"
        ifDataType.items.size shouldBe 2
        ifDataType.items.forEach {
            (it as ModelDataType).implementsDataTypes shouldContainExactly listOf(ifDataType)
        }
    }

})
