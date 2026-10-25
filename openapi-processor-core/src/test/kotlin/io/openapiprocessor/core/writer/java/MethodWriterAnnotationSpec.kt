/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.builder.api.endpoint
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.model.datatypes.*
import io.openapiprocessor.core.model.parameters.AdditionalParameter
import io.openapiprocessor.core.model.parameters.ParameterBase
import io.openapiprocessor.core.support.TestMappingAnnotationFactory
import io.openapiprocessor.core.support.TestParameterAnnotationFactory
import io.openapiprocessor.core.support.TestStatusAnnotationFactory
import io.openapiprocessor.core.support.parseOptions
import java.io.StringWriter

class MethodWriterAnnotationSpec: StringSpec ({
    isolationMode = IsolationMode.InstancePerTest

    fun writer(
        options: ApiOptions,
        mappingAnnotationFactory: MappingAnnotationFactory = TestMappingAnnotationFactory())
    : MethodWriter {
        return MethodWriter(
            options,
            JavaIdentifier(),
            TestStatusAnnotationFactory(),
            mappingAnnotationFactory,
            TestParameterAnnotationFactory(),
            BeanValidationFactory(options)
        )
    }

    val target = StringWriter()

    "writes mapping annotation" {
        val options = ApiOptions()

        val endpoint = endpoint("/foo") {
            responses {
                status("204") {
                    response()
                }
            }
        }

        val imports = writer(options).write (target, endpoint, endpoint.endpointResponses.first ())

        target.toString () shouldBe
            """
            |    @Status
            |    @Mapping
            |    void getFoo();
            |
            """.trimMargin()

        imports shouldContain "annotation.Mapping"
    }

    "writes @Deprecated annotation & provides imports" {
        val options = ApiOptions()

        val endpoint = endpoint ("/foo") {
            deprecated()
            responses {
                status("204") {
                    response()
                }
            }
        }

        val imports = writer(options).write (target, endpoint, endpoint.endpointResponses.first ())

        target.toString () shouldBe
            """
            |    @Deprecated
            |    @Status
            |    @Mapping
            |    void getFoo();
            |
            """.trimMargin()

        imports  shouldContainAll DEPRECATED.imports
    }

    "writes additional parameter annotation" {
        val options = ApiOptions()

        val endpoint = endpoint("/foo") {
            parameters {
                any(AdditionalParameter(
                        "bar",
                        StringDataType(),
                        AnnotationDataType("Bar", "bar", linkedMapOf())
                    )
                )
            }
            responses {
                status("204") { empty() }
            }
        }

        // when:
        val imports = writer(options).write (target, endpoint, endpoint.endpointResponses.first ())

        // then:
        target.toString () shouldBe
            """    
            |    @Status
            |    @Mapping
            |    void getFoo(@Parameter @Bar String bar);
            |
            """.trimMargin()

        // then:
        imports shouldContain "bar.Bar"
        imports shouldContain "annotation.Status"
        imports shouldContain "annotation.Mapping"
        imports shouldContain "annotation.Parameter"
        imports shouldContain "java.lang.String"
    }

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
        val imports = writer(options).write (target, endpoint, endpoint.endpointResponses.first ())

        // then:
        target.toString () shouldBe
            """    
            |    @Status
            |    @Mapping
            |    void getFoo(@Parameter @Bar(bar = rab) Foo foo);
            |
            """.trimMargin()

        imports shouldContain "pkg.Foo"
        imports shouldContain "annotation.Bar"
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
            |    @Status
            |    @Mapping
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
            |    @Status
            |    @Mapping
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
            |    @Status
            |    @Mapping
            |    void getFoo(@Parameter @Bar(bar = rab) MappedFoo foo);
            |
            """.trimMargin()
    }

    "skips additional parameter annotation if annotation is not allowed on parameter" {
        val options = parseOptions(
            """
            |openapi-processor-mapping: v15
            |options:
            |  package-name: pkg
            |
            |
            |map:
            |  types:
            |    - type: Foo @ lombok.Builder
            |    
            |annotation-targets:
            | lombok.Builder: ['type', 'method']
            """.trimMargin())

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

        writer(options).write (target, endpoint, endpoint.endpointResponses.first ())

        target.toString () shouldBe
            """    
            |    @Status
            |    @Mapping
            |    void getFoo(@Parameter Foo foo);
            |
            """.trimMargin()
    }

    "writes parameter @NotNull validation annotation" {
        val options = ApiOptions()
        options.beanValidation = true

        val endpoint = endpoint("/foo") {
            parameters {
                query("bar", StringDataType()) {
                    required()
                }
            }
            responses { status("204") { empty() } }
        }

        // when:
        val imports = writer(options).write (target, endpoint, endpoint.endpointResponses.first ())

        // then:
        target.toString () shouldBe
            """    
            |    @Status
            |    @Mapping
            |    void getFoo(@Parameter @NotNull String bar);
            |
            """.trimMargin()

        imports shouldContain "javax.validation.constraints.NotNull"
    }
})
