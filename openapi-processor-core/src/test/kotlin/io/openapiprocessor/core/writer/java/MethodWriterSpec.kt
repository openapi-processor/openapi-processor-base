/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.builder.api.endpoint
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.converter.mapping.Mappings
import io.openapiprocessor.core.model.datatypes.*
import io.openapiprocessor.core.model.parameters.ParameterBase
import io.openapiprocessor.core.processor.mapping.v2.ResultStyle
import io.openapiprocessor.core.support.TestStatusAnnotationWriter
import io.openapiprocessor.core.support.TestMappingAnnotationWriter
import io.openapiprocessor.core.support.TestParameterAnnotationWriter
import io.openapiprocessor.core.support.datatypes.CollectionDataType
import java.io.StringWriter

class MethodWriterSpec: StringSpec({
    isolationMode = IsolationMode.InstancePerTest

    val apiOptions = ApiOptions()
    val identifier = JavaIdentifier()

    val writer = MethodWriter (
        apiOptions,
        identifier,
        TestStatusAnnotationWriter(),
        TestMappingAnnotationWriter(),
        TestParameterAnnotationWriter(),
        BeanValidationFactory(apiOptions))

    val target = StringWriter()

    "writes parameter validation annotation" {
        apiOptions.beanValidation = true

        val endpoint = endpoint("/foo") {
            parameters {
                any(object : ParameterBase("foo", StringDataType(), true) {
                })
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
            |    void getFoo(@Parameter @NotNull String foo);
            |
            """.trimMargin()
    }

    "writes multi content response methods with media-type postfix" {
        val endpoint = endpoint("/foo") {
            responses {
                status("200") {
                    response (
                        "application/json",
                        CollectionDataType(StringDataType())
                    )
                    response(
                        "application/xml",
                        CollectionDataType(StringDataType())
                    )
                }
            }
        }

        // when:
        writer.write (target, endpoint, endpoint.endpointResponses.first())
        writer.write (target, endpoint, endpoint.endpointResponses.last())

        // then:
        target.toString () shouldBe
            """    
            |    @CoreMapping
            |    Collection<String> getFooApplicationJson();
            |    @CoreMapping
            |    Collection<String> getFooApplicationXml();
            |
            """.trimMargin()
    }

    "writes multi content response methods with media-type postfix on operationId" {
        val endpoint = endpoint("/foo") {
            operationId = "get_foo_operation_id"
            responses {
                status("200") {
                    response (
                        "application/json",
                        CollectionDataType(StringDataType())
                    )
                    response(
                        "application/xml",
                        CollectionDataType(StringDataType())
                    )
                }
            }
        }

        // when:
        writer.write(target, endpoint, endpoint.endpointResponses.first())
        writer.write(target, endpoint, endpoint.endpointResponses.last())

        // then:
        target.toString () shouldBe
            """    
            |    @CoreMapping
            |    Collection<String> getFooOperationIdApplicationJson();
            |    @CoreMapping
            |    Collection<String> getFooOperationIdApplicationXml();
            |
            """.trimMargin()
    }

    "writes parameter with type name" {
        val endpoint = endpoint("/foo") {
            parameters {
                any(object : ParameterBase("foo", ObjectDataType(
                    DataTypeName("Foo", "FooX"), "pkg", linkedMapOf()), true) {
                })
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
            |    void getFoo(@Parameter FooX foo);
            |
            """.trimMargin()
    }

    "writes request body parameter with type name" {
        val endpoint = endpoint("/foo") {
            parameters {
                body("body", "application/json",
                    ObjectDataType(DataTypeName("Foo", "FooX"), "pkg",
                        linkedMapOf()))
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
            |    void getFoo(@Parameter FooX body);
            |
            """.trimMargin()
    }

    "writes single response methods with type name" {
        val endpoint = endpoint("/foo") {
            responses {
                status("200") {
                    response (
                        "application/json",
                        ObjectDataType(DataTypeName("Foo", "FooX"), "pkg", linkedMapOf())
                    )
                }
            }
        }

        // when:
        writer.write (target, endpoint, endpoint.endpointResponses.first())

        // then:
        target.toString () shouldBe
            """    
            |    @CoreMapping
            |    FooX getFoo();
            |
            """.trimMargin()
    }

    "writes parameter with nested generics" {
        val endpoint = endpoint("/foo") {
            parameters {
                any(object : ParameterBase("foo", MappedDataType(
                    "Map",
                    "java.util",
                    listOf(
                        GenericDataType(DataTypeName("String"), "java.lang"),
                        GenericDataType(DataTypeName("Collection"), "java.util", listOf(
                            GenericDataType(DataTypeName("String"), "java.lang" )
                        ))
                    )
                )) {})
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
            |    void getFoo(@Parameter Map<String, Collection<String>> foo);
            |
            """.trimMargin()
    }

    "writes single response methods with nested generics" {
        val endpoint = endpoint("/foo") {
            responses {
                status("200") {
                    response (
                        "application/json", MappedDataType(
                            "Map",
                            "java.util",
                            listOf(
                                GenericDataType(DataTypeName("String"), "java.lang"),
                                GenericDataType(DataTypeName("Collection"), "java.util", listOf(
                                    GenericDataType(DataTypeName("String"), "java.lang" )
                                ))
                            )
                        )
                    )
                }
            }
        }

        // when:
        writer.write (target, endpoint, endpoint.endpointResponses.first())

        // then:
        target.toString () shouldBe
            """    
            |    @CoreMapping
            |    Map<String, Collection<String>> getFoo();
            |
            """.trimMargin()
    }

    "writes generic wildcard parameter from mapping" {
        val endpoint = endpoint("/foo") {
            parameters {
                any(object : ParameterBase("foo", MappedDataType(
                    "Bar", "bar", listOf(GenericDataType(DataTypeName("?"), ""))
                )) {})
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
            |    void getFoo(@Parameter Bar<?> foo);
            |
            """.trimMargin()
    }

    "writes success status annotation" {
        apiOptions.globalMappings = Mappings(
            resultStyle = ResultStyle.SUCCESS,
            resultStatus = true)

        val endpoint = endpoint("/foo") {
            responses {
                status("204") {
                    empty()
                }
                status("500") {
                    response(
                        "application/json",
                        StringDataType()
                    )
                }
            }
        }

        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        target.toString () shouldBe
            """
            |    @Status
            |    @CoreMapping
            |    void getFoo();
            |
            """.trimMargin()
    }

    "do not write success status annotation for default 200 status" {
        apiOptions.globalMappings = Mappings(
            resultStyle = ResultStyle.SUCCESS,
            resultStatus = true)

        val endpoint = endpoint("/foo") {
            responses {
                status("200") {
                    response (
                        "application/json",
                        StringDataType()
                    )
                }
                status("500") {
                    response(
                        "application/json",
                        StringDataType()
                    )
                }
            }
        }

        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        target.toString () shouldBe
            """
            |    @CoreMapping
            |    String getFoo();
            |
            """.trimMargin()
    }

    "does not write success status annotation with result style all" {
        apiOptions.globalMappings = Mappings(
            resultStyle = ResultStyle.ALL,
            resultStatus = true)

        val endpoint = endpoint("/foo") {
            responses {
                status("204") {
                    empty()
                }
                status("500") {
                    response(
                        "application/json",
                        StringDataType()
                    )
                }
            }
        }

        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        target.toString () shouldBe
            """
            |    @CoreMapping
            |    Object getFoo();
            |
            """.trimMargin()
    }

    "write success status annotation with result style all and no errors" {
        apiOptions.globalMappings = Mappings(
            resultStyle = ResultStyle.ALL,
            resultStatus = true)

        val endpoint = endpoint("/foo") {
            responses {
                status("204") {
                    empty()
                }
            }
        }

        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        target.toString () shouldBe
            """
            |    @Status
            |    @CoreMapping
            |    void getFoo();
            |
            """.trimMargin()
    }

})
