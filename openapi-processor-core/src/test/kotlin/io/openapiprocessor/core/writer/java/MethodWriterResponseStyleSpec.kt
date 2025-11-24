/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.openapiprocessor.core.builder.api.endpoint
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.model.datatypes.ResultDataType
import io.openapiprocessor.core.model.datatypes.StringDataType
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.support.TestStatusAnnotationWriter
import io.openapiprocessor.core.support.TestMappingAnnotationFactory
import io.openapiprocessor.core.support.TestParameterAnnotationWriter
import io.openapiprocessor.core.support.parseOptions
import java.io.StringWriter

class MethodWriterResponseStyleSpec: FreeSpec() {

    fun writer(options: ApiOptions = ApiOptions()): MethodWriter {
        return MethodWriter(
            options,
            JavaIdentifier(),
            TestStatusAnnotationWriter(),
            TestMappingAnnotationFactory(),
            TestParameterAnnotationWriter(),
            mockk<BeanValidationFactory>(),
            mockk<JavaDocFactory>()
        )
    }

    val target = StringWriter()

    init {
        isolationMode = IsolationMode.InstancePerTest

        "with response style all" - {

            "writes method with 'Object' response when it has multiple result content types (200, default)" {
                val options = parseOptions(mapping =
                    """
                    |map:
                    |  result-style: all
                    """)

                val endpoint = endpoint("/foo", HttpMethod.GET) {
                    responses {
                        status("200") {
                            response("application/json", StringDataType())
                        }
                        status("default") {
                            response("text/plain", StringDataType())
                        }
                    }
                }

                // when:
                writer(options).write(target, endpoint, endpoint.endpointResponses.first())

                // then:
                target.toString() shouldBe
                    """    
                    |    @CoreMapping
                    |    Object getFoo();
                    |
                    """.trimMargin()
            }

            "writes method with success response type when it has only empty error responses" {
                val options = parseOptions(mapping =
                    """
                    |map:
                    |  result-style: all
                    """)

                val endpoint = endpoint("/foo", HttpMethod.GET) {
                    responses {
                        status("200") {
                            response("application/json", StringDataType())
                        }
                        status("400") {
                            empty()
                        }
                        status("500") {
                            empty()
                        }
                    }
                }

                // when:
                writer(options).write (target, endpoint, endpoint.endpointResponses.first ())

                // then:
                target.toString() shouldBe
                    """    
                    |    @CoreMapping
                    |    String getFoo();
                    |
                    """.trimMargin()
            }

            "writes method with '?' response when it has multiple result contents types & wrapper result type" {
                val options = parseOptions(mapping =
                    """
                    |map:
                    |  result-style: all
                    """)

                val endpoint = endpoint("/foo", HttpMethod.GET) {
                    responses {
                        status("200") {
                            response("application/json", ResultDataType(
                                    "ResultWrapper",
                                    "http",
                                    StringDataType()
                                )
                            )
                        }
                        status("default") {
                            response("text/plain", ResultDataType(
                                    "ResultWrapper",
                                    "http",
                                    StringDataType()
                                )
                            )
                        }
                    }
                }

                // when:
                writer(options).write(target, endpoint, endpoint.endpointResponses.first())

                // then:
                target.toString() shouldBe
                    """    
                    |    @CoreMapping
                    |    ResultWrapper<?> getFoo();
                    |
                    """.trimMargin()
            }

        }

        "with response style success (default)" - {

            "writes method with success response if it has multiple result content types (200, default)" {
                    val endpoint = endpoint("/foo", HttpMethod.GET) {
                    responses {
                        status("200") {
                            response("application/json", StringDataType())
                        }
                        status("default") {
                            response("text/plain", StringDataType())
                        }
                    }
                }

                // when:
                writer().write(target, endpoint, endpoint.endpointResponses.first())

                // then:
                target.toString() shouldBe
                    """    
                    |    @CoreMapping
                    |    String getFoo();
                    |
                    """.trimMargin()
            }

            "writes method with success response when it has multiple result contents types & wrapper result type" {
                val endpoint = endpoint("/foo", HttpMethod.GET) {
                    responses {
                        status("200") {
                            response("application/json", ResultDataType(
                                    "ResultWrapper",
                                    "http",
                                    StringDataType()
                                )
                            )
                        }
                        status("default") {
                            response("text/plain", ResultDataType(
                                    "ResultWrapper",
                                    "http",
                                    StringDataType()
                                )
                            )
                        }
                    }
                }

                // when:
                writer().write(target, endpoint, endpoint.endpointResponses.first())

                // then:
                target.toString() shouldBe
                    """    
                    |    @CoreMapping
                    |    ResultWrapper<String> getFoo();
                    |
                    """.trimMargin()
            }

        }

    }
}
