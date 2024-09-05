/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.every
import io.openapiprocessor.core.builder.api.`interface`
import io.openapiprocessor.core.builder.api.itf
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.converter.mapping.SimpleParameterValue
import io.openapiprocessor.core.extractBody
import io.openapiprocessor.core.extractImports
import io.openapiprocessor.core.framework.AnnotationType
import io.openapiprocessor.core.framework.FrameworkAnnotations
import io.openapiprocessor.core.model.Annotation
import io.openapiprocessor.core.model.Endpoint
import io.openapiprocessor.core.model.datatypes.*
import io.openapiprocessor.core.model.parameters.AdditionalParameter
import io.openapiprocessor.core.model.parameters.Parameter
import io.openapiprocessor.core.model.parameters.ParameterBase
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.support.datatypes.ObjectDataType
import io.openapiprocessor.core.support.datatypes.propertyDataTypeString
import io.openapiprocessor.core.support.parseOptions
import java.io.StringWriter
import java.io.Writer
import io.mockk.mockk as stub


class InterfaceWriterSpec: StringSpec({
    isolationMode = IsolationMode.InstancePerTest

    val options = ApiOptions()
    val generatedWriter = SimpleGeneratedWriter(options)
    val methodWriter: MethodWriter = stub(relaxed = true)
    val annotations: FrameworkAnnotations = stub(relaxed = true)

    lateinit var writer: InterfaceWriter
    val target = StringWriter()

    beforeTest {
        writer = InterfaceWriter(options, generatedWriter, methodWriter, annotations)
    }

    fun writer(options: ApiOptions = ApiOptions()): InterfaceWriter {
        return InterfaceWriter(options, generatedWriter, methodWriter, annotations)
    }

    "writes mapping import" {
        every { annotations.getAnnotation(any<HttpMethod>()) } returns Annotation("annotation.Mapping")

        val itf = `interface` {
            endpoint("/foo") {
                responses { status("200") }
            }
        }

        // when:
        writer().write(target, itf)

        // then:
        extractImports(target) shouldContain "import annotation.Mapping;"
    }

    "writes mapping parameter import" {
        every { annotations.getAnnotation(any<HttpMethod>()) } returns Annotation(
            "annotation.Mapping", linkedMapOf(
                "parameter" to SimpleParameterValue("Parameter", "parameter.Parameter")
            )
        )

        val itf = `interface` {
            endpoint("/foo") {
                responses { status("200") }
            }
        }

        // when:
        writer.write(target, itf)

        // then:
        extractImports(target) shouldContain "import annotation.Mapping;"
        extractImports(target) shouldContain "import parameter.Parameter;"
    }

    "writes multiple mapping imports" {
        every { annotations.getAnnotation(any<HttpMethod>()) } returnsMany listOf(
            Annotation("annotation.MappingA"),
            Annotation("annotation.MappingB"),
            Annotation("annotation.MappingC"))

        val itf = `interface` {
            endpoint("/foo", HttpMethod.GET) {
                responses { status("200") }
            }
            endpoint("/foo", HttpMethod.PUT) {
                responses { status("200") }
            }
            endpoint("/foo", HttpMethod.POST) {
                responses { status("200") }
            }
        }

        // when:
        writer.write(target, itf)

        // then:
        val imports = extractImports(target)
        imports shouldContain "import annotation.MappingA;"
        imports shouldContain "import annotation.MappingB;"
        imports shouldContain "import annotation.MappingC;"
    }

    "writes result wrapper data type import" {
        val itf = `interface` {
            endpoint("/foo") {
                responses {
                    status("200") {
                        response(dataType = ResultDataType("ResultWrapper", "http", NoneDataType()))
                    }
                }
            }
        }

        // when:
        writer.write(target, itf)

        // then:
        val imports = extractImports(target)
        imports shouldContain "import http.ResultWrapper;"
    }

    "writes parameter annotation import" {
        every { annotations.getAnnotation(any<HttpMethod>()) } returns Annotation("annotation.Mapping")
        every { annotations.getAnnotation(any<Parameter>()) } returns Annotation("annotation.Parameter")

        val itf = `interface` {
            endpoint("/foo") {
                parameters {
                    query("bar", StringDataType())
                }

                responses { status("200") }
            }
        }

        // when:
        writer.write(target, itf)

        // then:
        val imports = extractImports(target)
        imports shouldContain "import annotation.Parameter;"
    }

    "writes parameter @NotNull validation annotation import" {
        options.beanValidation = true

        every { annotations.getAnnotation(any<HttpMethod>()) } returns Annotation("annotation.Mapping")
        every { annotations.getAnnotation(any<Parameter>()) } returns Annotation("annotation.Parameter")

        val itf = `interface` {
            endpoint("/foo") {
                parameters {
                    query("bar", StringDataType()) {
                        required()
                    }
                }

                responses { status("200") }
            }
        }

        // when:
        writer.write(target, itf)

        // then:
        val imports = extractImports(target)
        imports shouldContain "import javax.validation.constraints.NotNull;"
    }

    "does not write parameter annotation import of a parameter that does not need an annotation" {
        val itf = `interface` {
            endpoint("/foo") {
                parameters {
                    any(object : ParameterBase("foo", StringDataType()) {
                        override val withAnnotation: Boolean
                            get() = false
                    })
                }
                responses { status("200") }
            }
        }

        // when:
        writer.write(target, itf)

        // then:
        val imports = extractImports(target)
        imports shouldNotContain "import annotation.Parameter;"
    }

    "writes import of request parameter data type" {
        val itf = `interface` {
            endpoint("/foo") {
                parameters {
                    query("foo", ObjectDataType("Foo", "model", linkedMapOf(
                        Pair("foo1", propertyDataTypeString()),
                        Pair("foo2", propertyDataTypeString())
                    )))
                }
                responses { status("200") }
            }
        }

        // when:
        writer.write(target, itf)

        // then:
        val imports = extractImports(target)
        imports shouldContain "import model.Foo;"
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

        val itf = itf {
            endpoint("/foo") {
                parameters {
                    query("foo", ObjectDataType(
                        "Foo", "model", linkedMapOf("foo" to propertyDataTypeString()
                    )))
                }
                responses { status("200") }
            }
        }

        // when:
        writer(currentOptions).write(target, itf)

        // then:
        val imports = extractImports(target)
        imports shouldContain "import io.openapiprocessor.Type;"
        imports shouldContain "import io.openapiprocessor.Name;"
    }

    "writes additional parameter annotation import" {
        every { annotations.getAnnotation(any<Parameter>()) } returns Annotation("annotation.Parameter")

        val itf = `interface` {
            endpoint ("/foo") {
                parameters {
                    any(AdditionalParameter(
                        "bar",
                        StringDataType(),
                        AnnotationDataType("Bar", "bar", linkedMapOf())
                    ))
                }
            }
        }

        // when:
        writer.write(target, itf)

        // then:
        val imports = extractImports(target)
        imports shouldContain "import bar.Bar;"
    }

    "writes request body annotation import" {
        every { annotations.getAnnotation(any<Parameter>()) } returns Annotation("annotation.Body")

        val itf = `interface` {
            endpoint("/foo") {
                parameters {
                    body("body", "text/plain", StringDataType())
                }
                responses { status("200") }
            }
        }

        // when:
        writer.write(target, itf)

        // then:
        val imports = extractImports(target)
        imports shouldContain "import annotation.Body;"
    }

    "writes import of request body data type" {
        val itf = `interface` {
            endpoint("/foo") {
                parameters {
                    body("body", "plain/text",
                        MappedDataType("Foo", "mapped"))
                }
                responses { status("200") }
            }
        }

        // when:
        writer.write(target, itf)

        // then:
        val imports = extractImports(target)
        imports shouldContain "import mapped.Foo;"
    }

    "writes model import" {
        val itf = `interface` {
            endpoint("/foo") {
                responses {
                    status("200") {
                        response("application/json",
                            ObjectDataType("Foo", "model"))
                    }
                }
            }
        }

        // when:
        writer.write(target, itf)

        // then:
        val imports = extractImports(target)
        imports shouldContain "import model.Foo;"
    }

    "writes multiple response model import" {
        val itf = `interface` {
            endpoint("/foo") {
                responses {
                    status("200") {
                        response("text/html",
                            ObjectDataType("Foo", "foo"))
                        response("text/plain",
                            ObjectDataType("Bar", "bar"))
                    }
                }
            }
        }

        // when:
        writer.write(target, itf)

        // then:
        val imports = extractImports(target)
        imports shouldContain "import foo.Foo;"
        imports shouldContain "import bar.Bar;"
    }

    "writes @Deprecated import" {
        writer = InterfaceWriter(options, generatedWriter, methodWriter, annotations,
            importFilter = NullImportFilter())

        val itf = `interface` {
            endpoint("/foo") {
                deprecated()

                responses {
                    status("200")
                }
            }
        }

        // when:
        writer.write(target, itf)

        // then:
        val imports = extractImports(target)
        imports shouldContain "import java.lang.Deprecated;"
    }

    "sorts imports alphabetically" {
        every { annotations.getAnnotation(any<HttpMethod>()) } returnsMany listOf(
            Annotation("annotation.MappingC"),
            Annotation("annotation.MappingA"),
            Annotation("annotation.MappingB"))

        val itf = `interface` {
            endpoint("/foo") {
                responses { status("200") }
            }
            endpoint("/bar") {
                responses { status("200") }
            }
            endpoint("/foobar") {
                responses { status("200") }
            }
        }

        // when:
        writer.write(target, itf)

        // then:
        val imports = mutableListOf<String>()
        imports.addAll(extractImports(target))
        imports.remove("import io.openapiprocessor.generated.support.Generated;")

        imports shouldBe listOf(
            "import annotation.MappingA;",
            "import annotation.MappingB;",
            "import annotation.MappingC;",
            )
    }

    "filters unnecessary 'java.lang' imports" {
        val itf = `interface` {
            endpoint("/foo") {
                responses {
                    status("200") {
                        response("plain/text", StringDataType())
                    }
                }
            }
        }

        // when:
        writer.write(target, itf)

        // then:
        val imports = extractImports(target)
        imports shouldNotContain  "import java.lang.String;"
    }

    "writes methods" {
        every { methodWriter.write(any(), any(), any()) } answers {
            arg<Writer>(0).write("${arg<Endpoint>(1).path}\n")
        }

        val itf = `interface` {
            endpoint("/foo") {
                responses {
                    status("200") {
                        response("text/plain", StringDataType())
                    }
                }
            }
            endpoint("/bar") {
                responses {
                    status("200") {
                        response("text/plain", StringDataType())
                    }
                }
            }
        }

        // when:
        writer.write(target, itf)

        // then:
        val interfaceBody = extractBody(target)
        interfaceBody shouldBe listOf(
            "",
            "/foo",
            "",
            "/bar",
            "")
    }

    "generates interface with valid java class name" {
        val itf = `interface`("foo-bar", "api") {}

        writer.write(target, itf)

        target.toString() shouldContain "interface FooBar"
    }

    "write server uri as path prefix" {
        every { annotations.getAnnotation(any<AnnotationType>()) } returns Annotation("annotation.Prefix")

        val itf = itf("foo-bar", "api", path = "/prefix") {
            endpoint("/foo") {
                responses {
                    status("200") {
                        response("text/plain", StringDataType())
                    }
                }
            }
        }

        options.serverPrefix = true
        writer.write(target, itf)

        target.toString() shouldContain "import annotation.Prefix;"

        target.toString() shouldContain """
            |@Prefix("/prefix")
            |public interface FooBarApi"""
            .trimMargin()
    }
})
