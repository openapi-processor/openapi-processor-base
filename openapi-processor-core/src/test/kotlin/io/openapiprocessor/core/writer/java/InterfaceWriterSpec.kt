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
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.every
import io.openapiprocessor.core.builder.api.`interface`
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.converter.mapping.SimpleParameterValue
import io.openapiprocessor.core.extractBody
import io.openapiprocessor.core.extractImports
import io.openapiprocessor.core.framework.FrameworkAnnotations
import io.openapiprocessor.core.model.Annotation
import io.openapiprocessor.core.model.Endpoint
import io.openapiprocessor.core.model.datatypes.StringDataType
import io.openapiprocessor.core.openapi.HttpMethod
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
        writer = InterfaceWriter(generatedWriter, methodWriter, annotations)
    }

    fun writer(): InterfaceWriter {
        return InterfaceWriter(generatedWriter, methodWriter, annotations)
    }

    "writes imports from collected method imports" {
        val expectedImports = setOf(
            "one.imported.Class",
            "another.imported.Class")

        every { methodWriter.write(any(),any(), any()) } returns expectedImports

        val itf = `interface` {
            endpoint("/foo") {
                responses { status("200") { empty() } }
            }
        }

        // when:
        writer().write(target, itf)

        // then:
        val actualImports = extractImports(target)
        actualImports shouldContainAll expectedImports.map { "import $it;" }
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

    // todo move to spring/micronaut
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
            emptySet()
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
})
