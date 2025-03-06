/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.kotest.core.spec.style.StringSpec
import io.kotest.datatest.withData
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeEmpty
import io.mockk.every
import io.mockk.mockk
import io.openapiprocessor.core.builder.api.endpoint
import io.openapiprocessor.core.model.Documentation
import io.openapiprocessor.core.model.datatypes.*
import io.openapiprocessor.core.model.parameters.ParameterBase
import io.openapiprocessor.core.support.apiConverter
import io.openapiprocessor.core.support.datatypes.ObjectDataType
import io.openapiprocessor.core.support.datatypes.propertyDataTypeString
import io.openapiprocessor.core.support.parseApi
import io.openapiprocessor.core.support.parseOptions

class JavaDocWriterSpec: StringSpec({

    lateinit var writer: JavaDocWriter

    beforeTest {
        writer = JavaDocWriter(JavaIdentifier())
    }

    "converts endpoint without documentation to empty string" {
        val endpoint = endpoint("/foo") {
            responses {
                status("204") {
                    response()
                }
            }
        }

        val html = writer.convert(endpoint, endpoint.endpointResponses.first())

        html.shouldBeEmpty()
    }

    "converts summary to javadoc comment" {
        val summary = "plain text summary"

        val endpoint = endpoint("/foo") {
            summary(summary)
            responses {
                status("204") {
                    response()
                }
            }
        }

        val html = writer.convert(endpoint, endpoint.endpointResponses.first())

        html shouldBe """
            |    /**
            |     * plain text summary
            |     */
            |
            """.trimMargin()
    }

    "converts description to javadoc comment" {
        val description = "*markdown* description with **text**"

        val endpoint = endpoint("/foo") {
            description(description)
            responses {
                status("204") {
                    response()
                }
            }
        }

        val html = writer.convert(endpoint, endpoint.endpointResponses.first())

        html shouldBe """
            |    /**
            |     * <em>markdown</em> description with <strong>text</strong>
            |     */
            |
            """.trimMargin()
    }

    "converts endpoint summary & description to javadoc comment" {
        val summary = "plain text summary"
        val description = "*markdown* description with **text**"

        val endpoint = endpoint("/foo") {
            summary(summary)
            description(description)
            responses {
                status("204") {
                    response()
                }
            }
        }

        val html = writer.convert(endpoint, endpoint.endpointResponses.first())

        html shouldBe """
            |    /**
            |     * plain text summary
            |     * <em>markdown</em> description with <strong>text</strong>
            |     */
            |
            """.trimMargin()
    }

    "converts endpoint parameter description to javadoc @param" {
        val description = "*markdown* description with **text**"

        val endpoint = endpoint("/foo") {
            description("any")
            parameters {
                any(object : ParameterBase("foo", StringDataType(),
                    true, false, description) {})
            }
            responses {
                status("204") {
                    response()
                }
            }
        }

        val html = writer.convert(endpoint, endpoint.endpointResponses.first())

        html shouldBe """
            |    /**
            |     * any
            |     *
            |     * @param foo <em>markdown</em> description with <strong>text</strong>
            |     */
            |
            """.trimMargin()
    }

    "converts endpoint response description to javadoc @return" {
        val description = "*markdown* description with **text**"

        val endpoint = endpoint("/foo") {
            description("any")
            responses {
                status("204") {
                    response {
                        description(description)
                    }
                }
            }
        }

        val html = writer.convert(endpoint, endpoint.endpointResponses.first())

        html shouldBe """
            |    /**
            |     * any
            |     *
            |     * @return <em>markdown</em> description with <strong>text</strong>
            |     */
            |
            """.trimMargin()
    }

    "converts complex description javadoc" {
        val description = """
            *markdown* description with **text**
        
            - one list item
            - second list item
        
            ```
            code block
            ```

            """.trimIndent()

        val endpoint = endpoint("/foo") {
            description(description)
            responses {
                status("204") {
                    response()
                }
            }
        }

        val html = writer.convert(endpoint, endpoint.endpointResponses.first())

        html shouldBe """
            |    /**
            |     * <em>markdown</em> description with <strong>text</strong>
            |     * <ul>
            |     * <li>one list item</li>
            |     * <li>second list item</li>
            |     * </ul>
            |     * <pre><code>code block
            |     * </code></pre>
            |     */
            |
            """.trimMargin()
    }

    "converts schema without description to empty string" {
        val datatype = mockk<ModelDataType>()
        every { datatype.documentation } returns null

        val html = writer.convertForPojo(datatype)

        html.shouldBeEmpty()
    }

    "converts schema description to javadoc comment" {
        val description = "*markdown* description with **text**"

        val datatype = mockk<ModelDataType>()
        every { datatype.documentation } returns Documentation(description = description)

        val html = writer.convertForPojo(datatype)

        html shouldBe """
            |/**
            | * <em>markdown</em> description with <strong>text</strong>
            | */
            |
            """.trimMargin()
    }

    "converts property schema without description to empty string" {
        val datatype = ObjectDataType( "Foo", "pkg", linkedMapOf(
            Pair("bar", propertyDataTypeString())
        ))

        val html = writer.convertForPojo(datatype)

        html.shouldBeEmpty()
    }

    data class Type(val dt: DataType)
    val description = "*markdown* description with **text**"

    withData(
        nameFn = {"converts property schema description to javadoc comment: ${it.dt.getName()}"},

        Type(IntegerDataType(documentation = Documentation(description = description))),
        Type(LongDataType(documentation = Documentation(description = description))),
        Type(FloatDataType(documentation = Documentation(description = description))),
        Type(DoubleDataType(documentation = Documentation(description = description))),
        Type(BooleanDataType(documentation = Documentation(description = description))),
        Type(StringDataType(documentation = Documentation(description = description))),
        Type(LocalDateDataType(documentation = Documentation(description = description))),
        Type(OffsetDateTimeDataType(documentation = Documentation(description = description)))
    ) { (type: DataType) ->

        val html = writer.convert(type)

        html shouldBe """
            |    /**
            |     * <em>markdown</em> description with <strong>text</strong>
            |     */
            |
            """.trimMargin()
    }

    "record properties are javadoc @params of the record" {
        val options = parseOptions(
            """
            |openapi-processor-mapping: v9
            |
            |options:
            |  package-name: pkg
            |  javadoc: true
            |  model-type: record
            """.trimMargin())

        val openApi = parseApi(
            """
            |openapi: 3.1.0
            |info:
            |  title: API
            |  version: 1.0.0
            |
            |paths:
            |  /foo:
            |    get:
            |      responses:
            |        '200':
            |          description: response description
            |          content:
            |            application/json:
            |              schema:
            |                ${'$'}ref: '#/components/schemas/Foo'
            |
            |components:
            |  schemas:
            |    Foo:
            |      type: object
            |      description: Foo object
            |      properties:
            |        fooA:
            |          type: string
            |          description: this a parameter fooA
            |        fooB:
            |          type: string
            |          description: this a parameter fooB
            """.trimMargin())

        val api = apiConverter(options).convert(openApi)
        val dto = api.getDataTypes().getModelDataTypes().first()

        val doc = writer.convertForRecord(dto)

        val expected =
            """
            /**
             * Foo object
             *
             * @param fooA this a parameter fooA
             * @param fooB this a parameter fooB
             */

            """.trimIndent()

        doc shouldBeEqual expected
    }

    "converts enum schema description to javadoc comment" {
        val description = "*markdown* description with **text**"

        val datatype = mockk<StringEnumDataType>()
        every { datatype.documentation } returns Documentation(description = description)

        val html = writer.convertForDataType(datatype)

        html shouldBe """
            |/**
            | * <em>markdown</em> description with <strong>text</strong>
            | */
            |
            """.trimMargin()
    }
})
