/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.builder.api.endpoint
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.converter.mapping.*
import io.openapiprocessor.core.converter.mapping.Annotation
import io.openapiprocessor.core.model.datatypes.DataTypeName
import io.openapiprocessor.core.model.datatypes.MappedDataType
import io.openapiprocessor.core.model.datatypes.ObjectDataType
import io.openapiprocessor.core.model.parameters.ParameterBase
import io.openapiprocessor.core.processor.MappingConverter
import io.openapiprocessor.core.processor.MappingReader
import io.openapiprocessor.core.support.TestMappingAnnotationWriter
import io.openapiprocessor.core.support.TestParameterAnnotationWriter
import java.io.StringWriter

class MethodWriterAnnotationSpec: StringSpec ({
    isolationMode = IsolationMode.InstancePerTest

    val apiOptions = ApiOptions()
    val identifier = JavaIdentifier()
    val reader = MappingReader()
    val converter = MappingConverter()

    val writer = MethodWriter (
        apiOptions,
        identifier,
        TestMappingAnnotationWriter(),
        TestParameterAnnotationWriter(),
        BeanValidationFactory(apiOptions))

    val target = StringWriter()


    "writes additional parameter annotation from annotation mapping" {
        apiOptions.typeMappings = listOf(
            AnnotationTypeMappingDefault(
                "Foo", annotation = Annotation(
                    "io.openapiprocessor.Bar", linkedMapOf("bar" to SimpleParameterValue("rab")))
            ))

        val endpoint = endpoint("/foo") {
            parameters {
                any(object : ParameterBase("foo", ObjectDataType(
                    DataTypeName("Foo"), "pkg"), true) {})
            }
            responses {
                status("204") {
                    response()
                }
            }
        }

        // when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        // then:
        target.toString () shouldBe
            """    
            |    @CoreMapping
            |    void getFoo(@Parameter @Bar(bar = rab) Foo foo);
            |
            """.trimMargin()
    }

    "writes additional parameter annotation from path annotation mapping" {
        apiOptions.typeMappings = listOf(
            EndpointTypeMapping(
                "/foo", null, listOf(
                    AnnotationTypeMappingDefault("Foo", annotation = Annotation(
                            "io.openapiprocessor.Bar", linkedMapOf("bar" to SimpleParameterValue("rab")))))
            ))

        val endpoint = endpoint("/foo") {
            parameters {
                any(object : ParameterBase("foo", ObjectDataType(
                    DataTypeName("Foo"), "pkg"), true) {})
            }
            responses {
                status("204") {
                    response()
                }
            }
        }

        // when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        // then:
        target.toString () shouldBe
            """    
            |    @CoreMapping
            |    void getFoo(@Parameter @Bar(bar = rab) Foo foo);
            |
            """.trimMargin()
    }

    "writes additional parameter annotation from path annotation name mapping" {
        val yaml = """
           |openapi-processor-mapping: v6
           |options:
           |  package-name: io.openapiprocessor
           |map:
           |  parameters:
           |    - name: foo @ io.openapiprocessor.Bar(bar = "rab")
           """.trimMargin()

        apiOptions.typeMappings = converter.convert(reader.read(yaml))

        val endpoint = endpoint("/foo") {
            parameters {
                any(object : ParameterBase("foo", ObjectDataType(
                    DataTypeName("Foo"), "pkg"), true) {})
            }
            responses {
                status("204") {
                    response()
                }
            }
        }

        // when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        // then:
        target.toString () shouldBe
            """    
            |    @CoreMapping
            |    void getFoo(@Parameter @Bar(bar = "rab") Foo foo);
            |
            """.trimMargin()
    }

    "writes additional parameter annotation on mapped data type from annotation mapping" {
        apiOptions.typeMappings = listOf(
            AnnotationTypeMappingDefault("Foo", annotation = Annotation(
                "io.openapiprocessor.Bar", linkedMapOf("bar" to SimpleParameterValue("rab")))
            )
        )

        val endpoint = endpoint("/foo") {
            parameters {
                any(object : ParameterBase(
                    "foo", MappedDataType(
                        "MappedFoo", "pkg",
                        sourceDataType = ObjectDataType(
                            DataTypeName("Foo"), "pkg"
                        )
                    ),
                    true
                ) {})
            }
            responses {
                status("204") {
                    response()
                }
            }
        }

        // when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        // then:
        target.toString () shouldBe
            """    
            |    @CoreMapping
            |    void getFoo(@Parameter @Bar(bar = rab) MappedFoo foo);
            |
            """.trimMargin()
    }
})
