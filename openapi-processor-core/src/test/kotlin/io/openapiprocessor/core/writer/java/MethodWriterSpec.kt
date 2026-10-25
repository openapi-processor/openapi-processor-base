/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.collections.shouldNotContainAll
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.builder.api.endpoint
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.converter.mapping.Mappings
import io.openapiprocessor.core.model.datatypes.*
import io.openapiprocessor.core.model.parameters.ParameterBase
import io.openapiprocessor.core.processor.mapping.ResultStyle
import io.openapiprocessor.core.support.*
import io.openapiprocessor.core.support.datatypes.CollectionDataType
import io.openapiprocessor.core.support.datatypes.ObjectDataType
import io.openapiprocessor.core.support.datatypes.propertyDataTypeString
import java.io.StringWriter

class MethodWriterSpec: StringSpec({
    isolationMode = IsolationMode.InstancePerTest

    val apiOptions = ApiOptions()
    val identifier = JavaIdentifier()

    val writer = MethodWriter (
        apiOptions,
        identifier,
        TestStatusAnnotationFactory(),
        TestMappingAnnotationFactory(),
        TestParameterAnnotationFactory(),
        BeanValidationFactory(apiOptions))

    val target = StringWriter()

    fun writer(apiOptions: ApiOptions): MethodWriter {
        return MethodWriter (
            apiOptions,
            identifier,
            TestStatusAnnotationFactory(),
            TestMappingAnnotationFactory(),
            TestParameterAnnotationFactory(),
            BeanValidationFactory(apiOptions))
    }

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
            |    @Status
            |    @Mapping
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
        val jsonImports = writer.write (target, endpoint, endpoint.endpointResponses.first())
        val xmlImports = writer.write (target, endpoint, endpoint.endpointResponses.last())

        // then:
        target.toString () shouldBe
            """    
            |    @Mapping
            |    Collection<String> getFooApplicationJson();
            |    @Mapping
            |    Collection<String> getFooApplicationXml();
            |
            """.trimMargin()

        jsonImports shouldContainAll setOf("java.util.Collection", "java.lang.String")
        jsonImports shouldContainAll MAPPING.imports
        xmlImports shouldContainAll setOf("java.util.Collection", "java.lang.String")
        xmlImports shouldContainAll MAPPING.imports
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
        val jsonImports = writer.write(target, endpoint, endpoint.endpointResponses.first())
        val xmlImports = writer.write(target, endpoint, endpoint.endpointResponses.last())

        // then:
        target.toString () shouldBe
            """    
            |    @Mapping
            |    Collection<String> getFooOperationIdApplicationJson();
            |    @Mapping
            |    Collection<String> getFooOperationIdApplicationXml();
            |
            """.trimMargin()

        jsonImports shouldContainAll setOf("java.util.Collection", "java.lang.String")
        jsonImports shouldContainAll MAPPING.imports
        xmlImports shouldContainAll setOf("java.util.Collection", "java.lang.String")
        xmlImports shouldContainAll MAPPING.imports
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
        val imports = writer.write (target, endpoint, endpoint.endpointResponses.first ())

        // then:
        target.toString () shouldBe
            """    
            |    @Status
            |    @Mapping
            |    void getFoo(@Parameter FooX foo);
            |
            """.trimMargin()

        imports shouldContain "annotation.Status"
        imports shouldContain "annotation.Mapping"
        imports shouldContain "annotation.Parameter"
        imports shouldContain "pkg.FooX"
    }

    "writes import of request parameter data type" {
       val endpoint = endpoint("/foo") {
          parameters {
              query("foo", ObjectDataType("Foo", "model", linkedMapOf(
                  Pair("foo1", propertyDataTypeString()),
                  Pair("foo2", propertyDataTypeString())
              )))
          }
          responses { status("200") { empty() } }
       }

        // when:
        val imports = writer.write (target, endpoint, endpoint.endpointResponses.first ())

        // then:
        imports shouldContain "model.Foo"
    }

    "writes mapped request body parameter with type name" {
        val endpoint = endpoint("/foo") {
            parameters {
                body("body", "application/json", MappedDataType("Foo", "mapped"))
            }
            responses {
                status("204") {
                    response()
                }
            }
        }

        // when:
        val imports = writer.write (target, endpoint, endpoint.endpointResponses.first ())

        // then:
        target.toString () shouldBe
            """    
            |    @Status
            |    @Mapping
            |    void getFoo(@Parameter Foo body);
            |
            """.trimMargin()

        imports shouldContain "mapped.Foo"
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
        val imports = writer.write (target, endpoint, endpoint.endpointResponses.first ())

        // then:
        target.toString () shouldBe
            """    
            |    @Status
            |    @Mapping
            |    void getFoo(@Parameter FooX body);
            |
            """.trimMargin()

        imports shouldContain "pkg.FooX"
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
        val imports = writer.write (target, endpoint, endpoint.endpointResponses.first())

        // then:
        target.toString () shouldBe
            """    
            |    @Mapping
            |    FooX getFoo();
            |
            """.trimMargin()

        imports shouldContainAll MAPPING.imports
        imports shouldContain "pkg.FooX"
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
            |    @Status
            |    @Mapping
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
        val imports = writer.write (target, endpoint, endpoint.endpointResponses.first())

        // then:
        target.toString () shouldBe
            """    
            |    @Mapping
            |    Map<String, Collection<String>> getFoo();
            |
            """.trimMargin()

        imports shouldContainAll MAPPING.imports
        imports shouldContainAll setOf("java.util.Map", "java.lang.String", "java.util.Collection")
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
        val imports = writer.write (target, endpoint, endpoint.endpointResponses.first ())

        // then:
        target.toString () shouldBe
            """
            |    @Status
            |    @Mapping
            |    void getFoo(@Parameter Bar<?> foo);
            |
            """.trimMargin()

        imports shouldContainAll MAPPING.imports
        imports shouldContainAll STATUS.imports
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

        val imports = writer.write (target, endpoint, endpoint.endpointResponses.first ())

        target.toString () shouldBe
            """
            |    @Status
            |    @Mapping
            |    void getFoo();
            |
            """.trimMargin()

        imports shouldContainAll MAPPING.imports
        imports shouldContainAll STATUS.imports
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

        val imports = writer.write (target, endpoint, endpoint.endpointResponses.first ())

        target.toString () shouldBe
            """
            |    @Mapping
            |    String getFoo();
            |
            """.trimMargin()

        imports shouldNotContainAll STATUS.imports
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

        val imports = writer.write (target, endpoint, endpoint.endpointResponses.first ())

        target.toString () shouldBe
            """
            |    @Mapping
            |    Object getFoo();
            |
            """.trimMargin()

        imports shouldNotContainAll STATUS.imports
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

        val imports = writer.write (target, endpoint, endpoint.endpointResponses.first ())

        target.toString () shouldBe
            """
            |    @Status
            |    @Mapping
            |    void getFoo();
            |
            """.trimMargin()

        imports shouldContainAll STATUS.imports
    }

    "writes additional annotation mapping import" {
        val currentOptions = parseOptions(
            """
            |openapi-processor-mapping: v8
            |options:
            |  package-name: pkg
            |map:
            |  types:
            |    - type: Foo @ io.openapiprocessor.Type
            |  parameters:
            |    - name: foo @ io.openapiprocessor.Name
            """.trimMargin())

        val endpoint = endpoint("/foo") {
            parameters {
                query("foo", ObjectDataType(
                    "Foo", "model", linkedMapOf("foo" to propertyDataTypeString()
                )))
            }
            responses { status("204") { empty() } }
        }

        // when:
        val imports = writer(currentOptions)
            .write (target, endpoint, endpoint.endpointResponses.first ())

        // then:
        imports shouldContain "io.openapiprocessor.Type"
        imports shouldContain "io.openapiprocessor.Name"
    }

    "does not write parameter annotation import of a parameter that does not need an annotation" {
        val endpoint = endpoint("/foo") {
            parameters {
                any(object : ParameterBase("foo", StringDataType()) {
                    override val withAnnotation: Boolean
                        get() = false
                })
            }
            responses { status("204") { empty() } }
        }

        // when:
        val imports = writer.write (target, endpoint, endpoint.endpointResponses.first ())

        // then:
        imports shouldNotContain "annotation.Parameter"
    }

    "skips additional annotation mapping import on a parameter if annotation is not allowed on parameter" {
        val currentOptions = parseOptions(
            """
            |openapi-processor-mapping: v15
            |options:
            |  package-name: pkg
            |map:
            |  types:
            |    - type: Foo @ io.openapiprocessor.Type
            |  parameters:
            |    - name: foo @ io.openapiprocessor.Name
            |    
            |annotation-targets:
            |  io.openapiprocessor.Type: []
            |  io.openapiprocessor.Name: []
            """.trimMargin())

        val endpoint = endpoint("/foo") {
            parameters {
                query("foo", ObjectDataType(
                    "Foo", "model", linkedMapOf("foo" to propertyDataTypeString()
                )))
            }
            responses { status("200") { empty() } }
        }

        // when:
        val imports = writer.write (target, endpoint, endpoint.endpointResponses.first ())

        // then:
        imports shouldNotContain "io.openapiprocessor.Type"
        imports shouldNotContain "io.openapiprocessor.Name"
    }
})
