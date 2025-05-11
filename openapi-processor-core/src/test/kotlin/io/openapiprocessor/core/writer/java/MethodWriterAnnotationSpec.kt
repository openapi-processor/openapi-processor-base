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
import io.openapiprocessor.core.model.datatypes.DataTypeName
import io.openapiprocessor.core.model.datatypes.MappedDataType
import io.openapiprocessor.core.model.datatypes.ObjectDataType
import io.openapiprocessor.core.model.parameters.ParameterBase
import io.openapiprocessor.core.support.TestStatusAnnotationWriter
import io.openapiprocessor.core.support.TestMappingAnnotationWriter
import io.openapiprocessor.core.support.TestParameterAnnotationWriter
import io.openapiprocessor.core.support.parseOptions
import java.io.StringWriter

class MethodWriterAnnotationSpec: StringSpec ({
    isolationMode = IsolationMode.InstancePerTest

    fun writer(options: ApiOptions): MethodWriter {
        return MethodWriter(
            options,
            JavaIdentifier(),
            TestStatusAnnotationWriter(),
            TestMappingAnnotationWriter(),
            TestParameterAnnotationWriter(),
            BeanValidationFactory(options)
        )
    }

    val target = StringWriter()


    "writes additional parameter annotation from annotation mapping" {
        val options = parseOptions(mapping =
            """
            |map:
            |  types:
            |    - type: Foo @ annotation.Bar(bar = rab)
            """)

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
        writer(options).write (target, endpoint, endpoint.endpointResponses.first ())

        // then:
        target.toString () shouldBe
            """    
            |    @CoreMapping
            |    void getFoo(@Parameter @Bar(bar = rab) Foo foo);
            |
            """.trimMargin()
    }

    "writes additional parameter annotation from path annotation mapping" {
        val options = parseOptions(mapping =
            """
            |map:
            |  paths:
            |    /foo:
            |      types:
            |        - type: Foo @ annotation.Bar(bar = rab)
            """)

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
        writer(options).write (target, endpoint, endpoint.endpointResponses.first ())

        // then:
        target.toString () shouldBe
            """    
            |    @CoreMapping
            |    void getFoo(@Parameter @Bar(bar = rab) Foo foo);
            |
            """.trimMargin()
    }

    "writes additional parameter annotation from path annotation name mapping" {
        val options = parseOptions(mapping =
            """
            |map:
            |  parameters:
            |    - name: foo @ annotation.Bar(bar = rab)
            """)

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
        writer(options).write (target, endpoint, endpoint.endpointResponses.first ())

        // then:
        target.toString () shouldBe
            """    
            |    @CoreMapping
            |    void getFoo(@Parameter @Bar(bar = rab) Foo foo);
            |
            """.trimMargin()
    }

    "writes additional parameter annotation on mapped data type from annotation mapping" {
        val options = parseOptions(mapping =
            """
            |map:
            |  types:
            |    - type: Foo @ annotation.Bar(bar = rab)
            """)

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
        writer(options).write (target, endpoint, endpoint.endpointResponses.first ())

        // then:
        target.toString () shouldBe
            """    
            |    @CoreMapping
            |    void getFoo(@Parameter @Bar(bar = rab) MappedFoo foo);
            |
            """.trimMargin()
    }
})
