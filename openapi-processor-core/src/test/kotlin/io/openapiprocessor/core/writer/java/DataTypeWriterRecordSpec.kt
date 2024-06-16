/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldStartWith
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.converter.JsonPropertyAnnotationMode
import io.openapiprocessor.core.converter.mapping.Annotation
import io.openapiprocessor.core.converter.mapping.AnnotationNameMappingDefault
import io.openapiprocessor.core.converter.mapping.ExtensionMapping
import io.openapiprocessor.core.extractImports
import io.openapiprocessor.core.model.datatypes.*
import io.openapiprocessor.core.support.datatypes.ListDataType
import io.openapiprocessor.core.support.datatypes.propertyDataType
import io.openapiprocessor.core.support.datatypes.propertyDataTypeString
import java.io.StringWriter
import java.util.*
import io.openapiprocessor.core.support.datatypes.ObjectDataType as ObjectDataTypeId

class DataTypeWriterRecordSpec: StringSpec({
    this.isolationMode = IsolationMode.InstancePerTest

    val options = ApiOptions()
    val generatedWriter = SimpleGeneratedWriter(options)
    val writer = DataTypeWriterRecord(options, JavaIdentifier(), generatedWriter, BeanValidationFactory(options))
    val target = StringWriter()

    "writes 'package'" {
        val pkg = "io.openapiprocessor.generated"
        val dataType = ObjectDataTypeId ("Book", pkg)

        // when:
        writer.write (target, dataType)

        // then:
        target.toString () shouldStartWith
            """
            |package $pkg;
            """.trimMargin()
    }

    data class IdType(val id: String, val type: String)

    for (td in listOf(
        IdType("Isbn", "Isbn"),
        IdType("Isbn", "IsbnX")
    )) {
        "writes imports of nested types: ${td.id} - ${td.type}" {
            val pkg = "external"

            val dataType = ObjectDataTypeId ("Book", "object", linkedMapOf(
                "isbn" to propertyDataType(ObjectDataType(
                    DataTypeName (td.id, td.type), pkg
                ))
            ))

            // when:
            writer.write (target, dataType)

            // then:
            val result = extractImports (target)
            result shouldContain "import $pkg.${td.type};"
        }
    }

    "writes import of generic list type" {
        val dataType = ObjectDataTypeId ("Book", "pkg", linkedMapOf(
            "authors" to propertyDataType(ListDataType(StringDataType()))))

        // when:
        writer.write (target, dataType)

        // then:
        val result = extractImports (target)
        result shouldContain "import java.util.List;"
    }

    for (td in listOf(
        IdType("Bar", "Bar"),
        IdType("Bar", "BarX")
    )) {
        "writes import of generic object list type: ${td.id} - ${td.type}" {
            val dataType = ObjectDataTypeId ("Foo", "object", linkedMapOf(
                "bars" to propertyDataType(
                    ListDataType(ObjectDataType(DataTypeName (td.id, td.type), "other"))))
            )

            // when:
            writer.write (target, dataType)

            // then:
            val result = extractImports (target)
            result shouldContain "import other.${td.type};"
        }
    }

    for (td in listOf(
        IdType("Bar", "Bar"),
        IdType("Bar", "BarX")
    )) {
        "writes class: ${td.id} - ${td.type}" {
            val pkg = "io.openapiprocessor.test"
            val dataType = ObjectDataType (DataTypeName(td.id, td.type), pkg, linkedMapOf())

            // when:
            writer.write (target, dataType)

            // then:
            target.toString () shouldContain
                """
                |@Generated
                |public record ${td.type}""".trimMargin()
        }
    }

    "writes simple properties" {
        val pkg = "io.openapiprocessor.test"

        val dataType = ObjectDataTypeId ("Foo", pkg, linkedMapOf(
            "barA" to propertyDataType (StringDataType ()),
            "barB" to propertyDataType (StringDataType ())
        ))

        // when:
        writer.write (target, dataType)

        // then:
        target.toString () shouldContain "    String barA,"
        target.toString () shouldContain "    String barB"
    }

    for (td in listOf(
        IdType("Bar", "Bar"),
        IdType("Bar", "BarX")
    )) {
        "writes object property: ${td.id} - ${td.type}" {
            val pkg = "io.openapiprocessor.test"

            val dataType = ObjectDataTypeId ("Foo", pkg, linkedMapOf(
                "bar" to propertyDataType(ObjectDataType(DataTypeName(td.id, td.type), "other")))
            )

            // when:
            writer.write (target, dataType)

            // then:
            target.toString() shouldContain "    ${td.type} bar"
        }
    }

    "writes deprecated class" {
        val pkg = "io.openapiprocessor.test"

        val dataType = ObjectDataTypeId ("Bar", pkg, linkedMapOf(), deprecated = true)

        // when:
        writer.write (target, dataType)

        // then:
        target.toString () shouldContain
            """
            |@Deprecated
            |@Generated
            |public record Bar""".trimMargin()
    }

    "writes deprecated property" {
        val pkg = "io.openapiprocessor.test"

        val dataType = ObjectDataTypeId ("Foo", pkg, linkedMapOf(
            "bar" to propertyDataType(StringDataType(deprecated = true))
        ))

        // when:
        writer.write (target, dataType)

        // then:
        target.toString () shouldContain
            """
            |    @Deprecated
            |    @JsonProperty("bar")
            |    String bar
            """.trimMargin()
    }

    "writes properties with valid java identifiers" {
        val pkg = "io.openapiprocessor.test"

        val dataType = ObjectDataTypeId ("Foo", pkg, linkedMapOf(
            "a-foo" to propertyDataTypeString(),
            "b-foo" to propertyDataTypeString()
        ))

        // when:
        writer.write (target, dataType)

        // then:
        target.toString () shouldContain "String aFoo"
        target.toString () shouldContain "String bFoo"
    }

    "writes imports of @JsonProperty" {
        val pkg = "external"

        val dataType = ObjectDataTypeId ("Foo", pkg, linkedMapOf(
            "a-foo" to propertyDataTypeString(),
            "b-foo" to propertyDataTypeString()
        ))

        // when:
        writer.write (target, dataType)

        // then:
        val result = extractImports (target)
        result shouldContain "import com.fasterxml.jackson.annotation.JsonProperty;"
    }

    "writes properties with @JsonProperty annotation" {
        val pkg = "io.openapiprocessor.test"

        val dataType = ObjectDataTypeId ("Foo", pkg, linkedMapOf(
            "a-foo" to propertyDataTypeString(),
            "b-foo" to propertyDataTypeString()
        ))

        // when:
        writer.write (target, dataType)

        // then:
        target.toString () shouldContain
            """
            |    @JsonProperty("a-foo")
            |    String aFoo,
            |
            |    @JsonProperty("b-foo")
            |    String bFoo
            """.trimMargin()
    }

    "writes @Generated annotation import" {
        val dataType = ObjectDataTypeId("Foo", "pkg", linkedMapOf(
            Pair("foo", propertyDataTypeString())
        ))

        // when:
        writer.write(target, dataType)

        // then:
        val imports = extractImports(target)
        imports shouldContain "import io.openapiprocessor.generated.support.Generated;"
    }

    "writes @Generated annotation" {
        val dataType = ObjectDataTypeId("Foo", "pkg", linkedMapOf(
            Pair("foo", propertyDataTypeString())
        ))

        // when:
        writer.write(target, dataType)

        // then:
        target.toString() shouldContain
            """    
            |@Generated
            |public record Foo
            """.trimMargin()
    }

    "writes @NotNull import for required property" {
        options.beanValidation = true

        val dataType = ObjectDataTypeId("Foo", "pkg", linkedMapOf(
            Pair("foo", propertyDataTypeString())
        ), DataTypeConstraints(required = listOf("foo")), false)

        // when:
        writer.write(target, dataType)

        // then:
        val imports = extractImports(target)
        imports shouldContain "import javax.validation.constraints.NotNull;"
    }

    "writes json nullable with value" {
        val dataType = ObjectDataTypeId ("Foo", "pkg", linkedMapOf(
            "foo" to propertyDataType(NullDataType("JsonNullable", "pkg", StringDataType(),
                "JsonNullable.undefined()" /* not available for records */))
        ))

        // when:
        writer.write(target, dataType)

        // then:
        target.toString() shouldContain
            """
            |public record Foo(
            |    @JsonProperty("foo")
            |    JsonNullable<String> foo
            |) {}
            """.trimMargin()
    }

    "writes additional property annotation from extension mapping" {
        options.typeMappings = listOf(
            ExtensionMapping("x-foo", listOf(
                AnnotationNameMappingDefault(
                    "ext", annotation = Annotation("annotation.Extension", linkedMapOf())
                )
            )),
            ExtensionMapping("x-bar", listOf(
                AnnotationNameMappingDefault(
                    "barA", annotation = Annotation("annotation.BarA", linkedMapOf())
                ),
                AnnotationNameMappingDefault(
                    "barB", annotation = Annotation("annotation.BarB", linkedMapOf())
                )
            ))
        )

        val dataType = io.openapiprocessor.core.support.datatypes.ObjectDataType(
            "Foo", "pkg", linkedMapOf(
                "foo" to propertyDataType(
                    StringDataType(), mapOf(
                        "x-foo" to "ext",
                        "x-bar" to listOf("barA", "barB")
                    )
                )
            )
        )

        // when:
        writer.write(target, dataType)

        // then:
        val t1 = target.toString()
        val t2 =
            """package pkg;
            |
            |import annotation.BarA;
            |import annotation.BarB;
            |import annotation.Extension;
            |import com.fasterxml.jackson.annotation.JsonProperty;
            |import io.openapiprocessor.generated.support.Generated;
            |
            |@Generated
            |public record Foo(
            |    @Extension
            |    @BarA
            |    @BarB
            |    @JsonProperty("foo")
            |    String foo
            |) {}
            |
            """.trimMargin()

        t1 shouldBeEqual t2
        Arrays.mismatch(t1.toByteArray(), t2.toByteArray()).shouldBe(-1)
    }

    "does not add @JsonProperty annotation if OpenAPI property name = java property name" {
        options.jsonPropertyAnnotation = JsonPropertyAnnotationMode.Auto

        val dataType = io.openapiprocessor.core.support.datatypes.ObjectDataType(
            "Foo", "pkg", linkedMapOf(
                Pair("foo", propertyDataTypeString())
            )
        )

        // when:
        writer.write(target, dataType)

        target.toString() shouldContain
            """package pkg;
            |
            |import io.openapiprocessor.generated.support.Generated;
            |
            |@Generated
            |public record Foo(
            |    String foo
            |) {}
            |
            """.trimMargin()
    }

    "does add @JsonProperty annotation if OpenAPI property name != java property name" {
        options.jsonPropertyAnnotation = JsonPropertyAnnotationMode.Auto

        val dataType = io.openapiprocessor.core.support.datatypes.ObjectDataType(
            "Foo", "pkg", linkedMapOf(
                Pair("1foo", propertyDataTypeString())
            )
        )

        // when:
        writer.write(target, dataType)

        target.toString() shouldContain
            """package pkg;
            |
            |import com.fasterxml.jackson.annotation.JsonProperty;
            |import io.openapiprocessor.generated.support.Generated;
            |
            |@Generated
            |public record Foo(
            |    @JsonProperty("1foo")
            |    String foo
            |) {}
            |
            """.trimMargin()
    }

    "does not add @JsonProperty annotation if OpenAPI property name != java property name" {
        options.jsonPropertyAnnotation = JsonPropertyAnnotationMode.Never

        val dataType = io.openapiprocessor.core.support.datatypes.ObjectDataType(
            "Foo", "pkg", linkedMapOf(
                Pair("1foo", propertyDataTypeString())
            )
        )

        // when:
        writer.write(target, dataType)

        target.toString() shouldContain
            """package pkg;
            |
            |import io.openapiprocessor.generated.support.Generated;
            |
            |@Generated
            |public record Foo(
            |    String foo
            |) {}
            |
            """.trimMargin()
    }

    "does add @JsonProperty annotation if OpenAPI property is read only" {
        options.jsonPropertyAnnotation = JsonPropertyAnnotationMode.Auto

        val dataType = io.openapiprocessor.core.support.datatypes.ObjectDataType(
            "Foo", "pkg", linkedMapOf(
                Pair("foo", propertyDataTypeString(readOnly = true))
            )
        )

        // when:
        writer.write(target, dataType)

        target.toString() shouldContain
            """package pkg;
            |
            |import com.fasterxml.jackson.annotation.JsonProperty;
            |import io.openapiprocessor.generated.support.Generated;
            |
            |@Generated
            |public record Foo(
            |    @JsonProperty(value = "foo", access = JsonProperty.Access.READ_ONLY)
            |    String foo
            |) {}
            |
            """.trimMargin()
    }
})

