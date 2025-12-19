/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.openapiprocessor.core.model.DataTypes
import io.openapiprocessor.core.model.datatypes.ObjectDataType
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.support.getSchemaInfo
import io.openapiprocessor.core.support.parseApiBody
import io.openapiprocessor.core.support.parseOptions
import io.openapiprocessor.core.support.ref
import io.openapiprocessor.core.writer.java.JavaIdentifier

class DataTypeConverterResponseSpec: StringSpec({
    isolationMode = IsolationMode.InstancePerTest

    val dataTypes = DataTypes()
    val identifier = JavaIdentifier()

    "single endpoint with identical results do not have a marker interface" {
        val options = parseOptions(mapping =
            """
            |map: {}
            """)

        val openApi = parseApiBody("""
            paths:
              /foo:
                get:
                  responses:
                    '200':
                       description: success
                       content:
                         application/json:
                             schema:
                               $ref: '#/components/schemas/Foo'
                    '202':
                       description: another success
                       content:
                         application/json:
                             schema:
                               $ref: '#/components/schemas/Foo'
            
            components:
              schemas:
            
                Foo:
                  description: a Foo
                  type: object
                  properties:
                    foo:
                      type: string
            """)

        val converter = DataTypeConverter(options, identifier)

        val schemaInfo200 = openApi.getSchemaInfo(
            "Foo",
            "/foo",
            HttpMethod.GET,
            "200",
            "application/json",
            false)

        val schemaInfo202 = openApi.getSchemaInfo(
            "Foo",
            "/foo",
            HttpMethod.GET,
            "202",
            "application/json",
            false)

        converter.convert(schemaInfo200, dataTypes)
        converter.convert(schemaInfo202, dataTypes)

        val dataType = dataTypes.find("Foo")
        dataType.shouldBeInstanceOf<ObjectDataType>()
        dataType.implementsDataTypes.shouldBeEmpty()
    }

    "single endpoint with different results do have a marker interface" {
        val options = parseOptions(mapping =
            """
            |map: {}
            """)

        val openApi = parseApiBody("""
            paths:
              /foo:
                get:
                  responses:
                    '200':
                       description: success
                       content:
                         application/json:
                             schema:
                               $ref: '#/components/schemas/Foo'
                    '202':
                       description: another success
                       content:
                         application/json:
                             schema:
                               $ref: '#/components/schemas/Bar'
            
            components:
              schemas:
            
                Foo:
                  description: a Foo
                  type: object
                  properties:
                    foo:
                      type: string
                      
                Bar:
                  description: a Bar
                  type: object
                  properties:
                    foo:
                      type: string
            """)

        val converter = DataTypeConverter(options, identifier)

        val schemaInfo200 = openApi.getSchemaInfo(
            "Foo",
            "/foo",
            HttpMethod.GET,
            "200",
            "application/json",
            true,
            "GetFooApplicationJsonResponse")

        val schemaInfo202 = openApi.getSchemaInfo(
            "Bar",
            "/foo",
            HttpMethod.GET,
            "202",
            "application/json",
            true,
            "GetFooApplicationJsonResponse")

        converter.convert(schemaInfo200, dataTypes)
        converter.convert(schemaInfo202, dataTypes)

        val dataType = dataTypes.find("Foo")
        dataType.shouldBeInstanceOf<ObjectDataType>()
        dataType.implementsDataTypes.size shouldBe 1
        dataType.implementsDataTypes.first().getName().shouldBe("GetFooApplicationJsonResponse")
    }

    "multiple endpoints with different result do have multiple marker interfaces" {
         val options = parseOptions(mapping =
             """
             |map: {}
             """)

         val openApi = parseApiBody("""
             paths:
               /foo:
                 get:
                   responses:
                     '200':
                        description: success
                        content:
                          application/json:
                              schema:
                                $ref: '#/components/schemas/Foo'
                     '202':
                        description: another success
                        content:
                          application/json:
                              schema:
                                $ref: '#/components/schemas/Bar'
             
               /bar:
                 get:
                   responses:
                     '200':
                        description: success
                        content:
                          application/json:
                              schema:
                                $ref: '#/components/schemas/Foo'
                     '202':
                        description: another success
                        content:
                          application/json:
                              schema:
                                $ref: '#/components/schemas/Bar'
             
             components:
               schemas:
             
                 Foo:
                   description: a Foo
                   type: object
                   properties:
                     foo:
                       type: string
                       
                 Bar:
                   description: a Bar
                   type: object
                   properties:
                     foo:
                       type: string
             """)

        val converter = DataTypeConverter(options, identifier)

        val schemaInfo200 = openApi.getSchemaInfo(
            "Foo",
            "/foo",
            HttpMethod.GET,
            "200",
            "application/json",
            true,
            "GetFooApplicationJsonResponse")

        val schemaInfo202 = openApi.getSchemaInfo(
            "Bar",
            "/foo",
            HttpMethod.GET,
            "202",
            "application/json",
            true,
            "GetFooApplicationJsonResponse")

        val schemaInfoBar200 = openApi.getSchemaInfo(
            "Foo",
            "/bar",
            HttpMethod.GET,
            "200",
            "application/json",
            true,
            "GetBarApplicationJsonResponse")

        val schemaInfoBar202 = openApi.getSchemaInfo(
            "Bar",
            "/bar",
            HttpMethod.GET,
            "202",
            "application/json",
            true,
            "GetBarApplicationJsonResponse")

        converter.convert(schemaInfo200, dataTypes)
        converter.convert(schemaInfo202, dataTypes)
        converter.convert(schemaInfoBar200, dataTypes)
        converter.convert(schemaInfoBar202, dataTypes)

        val dataType = dataTypes.find("Foo")
        dataType.shouldBeInstanceOf<ObjectDataType>()
        dataType.implementsDataTypes.size shouldBe 2
        dataType.implementsDataTypes.elementAt(0).getName().shouldBe("GetFooApplicationJsonResponse")
        dataType.implementsDataTypes.elementAt(1).getName().shouldBe("GetBarApplicationJsonResponse")

        val dataTypeBar = dataTypes.find("Bar")
        dataTypeBar.shouldBeInstanceOf<ObjectDataType>()
        dataTypeBar.implementsDataTypes.size shouldBe 2
        dataTypeBar.implementsDataTypes.elementAt(0).getName().shouldBe("GetFooApplicationJsonResponse")
        dataTypeBar.implementsDataTypes.elementAt(1).getName().shouldBe("GetBarApplicationJsonResponse")
     }

})
