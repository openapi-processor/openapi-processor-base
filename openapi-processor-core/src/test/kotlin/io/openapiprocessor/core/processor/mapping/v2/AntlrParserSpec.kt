/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import io.openapiprocessor.core.processor.mapping.v2.parser.Mapping
import io.openapiprocessor.core.processor.mapping.v2.parser.antlr.MappingException
import io.openapiprocessor.core.processor.mapping.v2.parser.antlr.parseMapping


class AntlrParserSpec: StringSpec({

    "qualified java target type" {
        val source = "io.oap.TargetType<java.lang.String>"

        val mapping = parseMapping(source)
        mapping.kind shouldBe Mapping.Kind.TYPE
        mapping.sourceType.shouldBeNull()
        mapping.targetType shouldBe "io.oap.TargetType"
        mapping.targetGenericTypes shouldBe listOf("java.lang.String")
        mapping.annotationType.shouldBeNull()
        mapping.annotationParameters.shouldBeEmpty()
    }

    "map source type to fully qualified java target type" {
        val source = "SourceType => io.oap.TargetType"

        val mapping = parseMapping(source)
        mapping.kind shouldBe Mapping.Kind.MAP
        mapping.sourceType shouldBe "SourceType"
        mapping.targetType shouldBe "io.oap.TargetType"
        mapping.targetGenericTypes.shouldBeEmpty()
    }

    // only for string, number, integer
    "map source type with format to fully qualified java target type" {
        val source = "string:format => io.oap.TargetType"

        val mapping = parseMapping(source)
        mapping.kind shouldBe Mapping.Kind.MAP
        mapping.sourceType shouldBe "string"
        mapping.sourceFormat shouldBe "format"
        mapping.targetType shouldBe "io.oap.TargetType"
        mapping.targetGenericTypes.shouldBeEmpty()
        mapping.annotationType.shouldBeNull()
        mapping.annotationParameters.shouldBeEmpty()
    }

    "map source type to fully qualified java target type with generic parameters" {
        val source = "SourceType => io.oap.TargetType <java.lang.String, java.lang.Integer>"

        val mapping = parseMapping(source)
        mapping.kind shouldBe Mapping.Kind.MAP
        mapping.sourceType shouldBe "SourceType"
        mapping.targetType shouldBe "io.oap.TargetType"
        mapping.targetGenericTypes shouldBe listOf("java.lang.String", "java.lang.Integer")
        mapping.annotationType.shouldBeNull()
        mapping.annotationParameters.shouldBeEmpty()
    }

    "map source type to fully qualified java target type with generics parameter using {package-name}" {
        val source = "SourceType => io.oap.TargetType<{package-name}.Foo>"

        val mapping = parseMapping(source)
        mapping.kind shouldBe Mapping.Kind.MAP
        mapping.sourceType shouldBe "SourceType"
        mapping.targetType shouldBe "io.oap.TargetType"
        mapping.targetGenericTypes shouldBe listOf("{package-name}.Foo")
        mapping.annotationType.shouldBeNull()
        mapping.annotationParameters.shouldBeEmpty()
    }

    "map source type to fully qualified java target type with annotation" {
        val source = "SourceType => io.oap.Annotation io.oap.TargetType"

        val mapping = parseMapping(source)
        mapping.kind shouldBe Mapping.Kind.MAP
        mapping.sourceType shouldBe "SourceType"
        mapping.targetType shouldBe "io.oap.TargetType"
        mapping.targetGenericTypes.shouldBeEmpty()
        mapping.annotationType shouldBe "io.oap.Annotation"
        mapping.annotationParameters.shouldBeEmpty()
    }

    "map source type to fully qualified java target type with annotation & default simple parameter" {
        val source = "SourceType => io.oap.Annotation(42) io.oap.TargetType"

        val mapping = parseMapping(source)
        mapping.kind shouldBe Mapping.Kind.MAP
        mapping.sourceType shouldBe "SourceType"
        mapping.targetType shouldBe "io.oap.TargetType"
        mapping.targetGenericTypes.shouldBeEmpty()
        mapping.annotationType shouldBe "io.oap.Annotation"
        mapping.annotationParameters.size shouldBe 1
        mapping.annotationParameters[""]!!.value shouldBe "42"
    }

    "map source type to fully qualified java target type with annotation & default string parameter" {
        val source = """SourceType => io.oap.Annotation("42") io.oap.TargetType"""

        val mapping = parseMapping(source)
        mapping.kind shouldBe Mapping.Kind.MAP
        mapping.sourceType shouldBe "SourceType"
        mapping.targetType shouldBe "io.oap.TargetType"
        mapping.targetGenericTypes.shouldBeEmpty()
        mapping.annotationType shouldBe "io.oap.Annotation"
        mapping.annotationParameters.size shouldBe 1
        mapping.annotationParameters[""]!!.value shouldBe """"42""""
    }

    "map source type to fully qualified java target type with annotation & multiple named parameters" {
        val source = """SourceType => io.oap.Annotation(a = 42, bb = "foo") io.oap.TargetType"""

        val mapping = parseMapping(source)
        mapping.kind shouldBe Mapping.Kind.MAP
        mapping.sourceType shouldBe "SourceType"
        mapping.targetType shouldBe "io.oap.TargetType"
        mapping.targetGenericTypes.shouldBeEmpty()
        mapping.annotationType shouldBe "io.oap.Annotation"
        mapping.annotationParameters.size shouldBe 2
        mapping.annotationParameters["a"]!!.value shouldBe "42"
        mapping.annotationParameters["bb"]!!.value shouldBe """"foo""""
    }

    "map source type to fully qualified java target type with annotation & boolean parameter" {
        val source = """SourceType => io.oap.Annotation(true) io.oap.TargetType"""

        val mapping = parseMapping(source)
        mapping.kind shouldBe Mapping.Kind.MAP
        mapping.sourceType shouldBe "SourceType"
        mapping.targetType shouldBe "io.oap.TargetType"
        mapping.targetGenericTypes.shouldBeEmpty()
        mapping.annotationType shouldBe "io.oap.Annotation"
        mapping.annotationParameters.size shouldBe 1
        mapping.annotationParameters[""]!!.value shouldBe "true"
    }

    "map source type to fully qualified java target type with annotation & named boolean parameter" {
        val source = """SourceType => io.oap.Annotation(value = true) io.oap.TargetType"""

        val mapping = parseMapping(source)
        mapping.kind shouldBe Mapping.Kind.MAP
        mapping.sourceType shouldBe "SourceType"
        mapping.targetType shouldBe "io.oap.TargetType"
        mapping.targetGenericTypes.shouldBeEmpty()
        mapping.annotationType shouldBe "io.oap.Annotation"
        mapping.annotationParameters.size shouldBe 1
        mapping.annotationParameters["value"]!!.value shouldBe "true"
    }

    // the parser does not detect exact number format
    val anyNumber = "0b0B_0x0X_0123456789_abcdef_ABCDEF_fFlL_."

    "map source type to fully qualified java target type with annotation & number parameter" {
        val source = """SourceType => io.oap.Annotation($anyNumber) io.oap.TargetType"""

        val mapping = parseMapping(source)
        mapping.kind shouldBe Mapping.Kind.MAP
        mapping.sourceType shouldBe "SourceType"
        mapping.targetType shouldBe "io.oap.TargetType"
        mapping.targetGenericTypes.shouldBeEmpty()
        mapping.annotationType shouldBe "io.oap.Annotation"
        mapping.annotationParameters.size shouldBe 1
        mapping.annotationParameters[""]!!.value shouldBe anyNumber
    }

    "map source type to fully qualified java target type with annotation & named number parameter" {
        val source = """SourceType => io.oap.Annotation(value = $anyNumber) io.oap.TargetType"""

        val mapping = parseMapping(source)
        mapping.kind shouldBe Mapping.Kind.MAP
        mapping.sourceType shouldBe "SourceType"
        mapping.targetType shouldBe "io.oap.TargetType"
        mapping.targetGenericTypes.shouldBeEmpty()
        mapping.annotationType shouldBe "io.oap.Annotation"
        mapping.annotationParameters.size shouldBe 1
        mapping.annotationParameters["value"]!!.value shouldBe anyNumber
    }

    "map content type to fully qualified java target type with annotation" {
        val source = "application/vnd.foo-bar => io.oap.Annotation io.oap.TargetType"

        val mapping = parseMapping(source)
        mapping.kind shouldBe Mapping.Kind.MAP
        mapping.sourceType shouldBe "application/vnd.foo-bar"
        mapping.targetType shouldBe "io.oap.TargetType"
        mapping.targetGenericTypes.shouldBeEmpty()
        mapping.annotationType shouldBe "io.oap.Annotation"
        mapping.annotationParameters.shouldBeEmpty()
    }

    "map source type to fully qualified java annotation" {
        val source = """SourceType @ io.oap.Annotation"""

        val mapping = parseMapping(source)
        mapping.kind shouldBe Mapping.Kind.ANNOTATE
        mapping.sourceType shouldBe "SourceType"
        mapping.targetType.shouldBeNull()
        mapping.targetGenericTypes.shouldBeEmpty()
        mapping.annotationType shouldBe "io.oap.Annotation"
        mapping.annotationParameters.shouldBeEmpty()
    }

    "annotate source type with fully qualified java annotation and class parameter" {
        val source = """SourceType @ io.oap.Annotation (value = io.oap.Foo.class)"""

        val mapping = parseMapping(source)
        mapping.kind shouldBe Mapping.Kind.ANNOTATE
        mapping.sourceType shouldBe "SourceType"
        mapping.targetType.shouldBeNull()
        mapping.targetGenericTypes.shouldBeEmpty()
        mapping.annotationType shouldBe "io.oap.Annotation"
        mapping.annotationParameters["value"]!!.value shouldBe "Foo.class"
        mapping.annotationParameters["value"]!!.import shouldBe "io.oap.Foo"
    }

    // because of the simple "name" rule, it will not error on this.
    "reports parsing error".config(false) {
        val source = """SourceType =X io.oap.TargetType"""

        val ex = shouldThrow<MappingException> {
            parseMapping(source)
        }

        ex.message shouldStartWith "failed to parse mapping:"
    }

    "java target type with multiple generics levels" {
        val source = "kotlin.collections.Map<java.lang.String, kotlin.collections.Collection<java.lang.String>>"

        val mapping = parseMapping(source)
        mapping.kind shouldBe Mapping.Kind.TYPE
        mapping.sourceType.shouldBeNull()
        mapping.targetType shouldBe "kotlin.collections.Map"

        mapping.targetGenericTypes2.size shouldBe 2
        val level1First = mapping.targetGenericTypes2[0]
        level1First.targetType shouldBe "java.lang.String"
        val level1Second = mapping.targetGenericTypes2[1]
        level1Second.targetType shouldBe "kotlin.collections.Collection"

        level1Second.targetGenericTypes.size shouldBe 1
        val level2First = mapping.targetGenericTypes2[0]
        level2First.targetType shouldBe "java.lang.String"
    }

    "map array type to java array" {
        val source = "array => plain"

        val mapping = parseMapping(source)
        mapping.kind shouldBe Mapping.Kind.MAP
        mapping.sourceType shouldBe "array"
        mapping.targetType shouldBe "plain"
    }

    "primitive byte" {
        val type = "byte"

        val mapping = parseMapping(type)
        mapping.kind shouldBe Mapping.Kind.TYPE
        mapping.targetTypePrimitive.shouldBeTrue()
        mapping.targetTypePrimitiveArray.shouldBeFalse()
        mapping.sourceType.shouldBeNull()
        mapping.targetType shouldBe "byte"
    }

    "primitive byte[]" {
        val type = "byte[]"

        val mapping = parseMapping(type)
        mapping.kind shouldBe Mapping.Kind.TYPE
        mapping.targetTypePrimitive.shouldBeTrue()
        mapping.targetTypePrimitiveArray.shouldBeTrue()
        mapping.sourceType.shouldBeNull()
        mapping.targetType shouldBe "byte"
    }

    "java primitive target type" {
        val source = "string:binary => char"

        val mapping = parseMapping(source)
        mapping.kind shouldBe Mapping.Kind.MAP
        mapping.sourceType shouldBe "string"
        mapping.sourceFormat shouldBe "binary"
        mapping.targetType shouldBe "char"
        mapping.targetTypePrimitive.shouldBeTrue()
        mapping.targetTypePrimitiveArray.shouldBeFalse()
    }

    "java primitive target type array" {
        val source = "string:binary => byte[]"

        val mapping = parseMapping(source)
        mapping.kind shouldBe Mapping.Kind.MAP
        mapping.sourceType shouldBe "string"
        mapping.sourceFormat shouldBe "binary"
        mapping.targetType shouldBe "byte"
        mapping.targetTypePrimitive.shouldBeTrue()
        mapping.targetTypePrimitiveArray.shouldBeTrue()
    }

    "java primitive target type with format equal to primitive" {
        val source = "string:byte => byte"

        val mapping = parseMapping(source)
        mapping.kind shouldBe Mapping.Kind.MAP
        mapping.sourceType shouldBe "string"
        mapping.sourceFormat shouldBe "byte"
        mapping.targetType shouldBe "byte"
        mapping.targetTypePrimitive.shouldBeTrue()
        mapping.targetTypePrimitiveArray.shouldBeFalse()
    }

    "annotate source type with fully qualified java annotation and nested type parameter" {
        val source = """integer:year @ com.fasterxml.jackson.annotation.JsonFormat(shape = JsonFormat.Shape.NUMBER, pattern = "yyyy")"""

        val mapping = parseMapping(source)
        mapping.kind shouldBe Mapping.Kind.ANNOTATE
        mapping.sourceType shouldBe "integer"
        mapping.sourceFormat shouldBe "year"
        mapping.targetType.shouldBeNull()
        mapping.targetGenericTypes.shouldBeEmpty()
        mapping.annotationType shouldBe "com.fasterxml.jackson.annotation.JsonFormat"
        val shape = mapping.annotationParameters["shape"]!!
        shape.value shouldBe "JsonFormat.Shape.NUMBER"
        shape.import.shouldBeNull()
        mapping.annotationParameters["pattern"]!!.value shouldBe """"yyyy""""
    }

    "annotate source type with fully qualified java annotation and qualified type parameter" {
        val source = """integer:year @ com.fasterxml.jackson.annotation.JsonFormat(shape = com.fasterxml.jackson.annotation.JsonFormat.Shape.NUMBER, pattern = "yyyy")"""

        val mapping = parseMapping(source)
        mapping.kind shouldBe Mapping.Kind.ANNOTATE
        mapping.sourceType shouldBe "integer"
        mapping.sourceFormat shouldBe "year"
        mapping.targetType.shouldBeNull()
        mapping.targetGenericTypes.shouldBeEmpty()
        mapping.annotationType shouldBe "com.fasterxml.jackson.annotation.JsonFormat"
        val shape = mapping.annotationParameters["shape"]!!
        shape.value shouldBe "JsonFormat.Shape.NUMBER"
        shape.import shouldBe "com.fasterxml.jackson.annotation.JsonFormat"
        mapping.annotationParameters["pattern"]!!.value shouldBe """"yyyy""""
    }

    "map implements source type to fully qualified java target type" {
        val source = "SourceType =+ io.oap.TargetType"

        val mapping = parseMapping(source)
        mapping.kind shouldBe Mapping.Kind.IMPLEMENT
        mapping.sourceType shouldBe "SourceType"
        mapping.targetType shouldBe "io.oap.TargetType"
        mapping.targetGenericTypes.shouldBeEmpty()
    }

    "map implements source type to fully qualified java target type, long" {
        val source = "SourceType implement io.oap.TargetType"

        val mapping = parseMapping(source)
        mapping.kind shouldBe Mapping.Kind.IMPLEMENT
        mapping.sourceType shouldBe "SourceType"
        mapping.targetType shouldBe "io.oap.TargetType"
        mapping.targetGenericTypes.shouldBeEmpty()
    }

    "map simple string" {
        val source = "name"

        val mapping = parseMapping(source)
        mapping.kind shouldBe Mapping.Kind.TYPE
        mapping.sourceType shouldBe source
        mapping.targetType shouldBe null
    }

    "map simple string with dash" {
        val source = "name-with-dash"

        val mapping = parseMapping(source)
        mapping.kind shouldBe Mapping.Kind.TYPE
        mapping.sourceType shouldBe source
        mapping.targetType shouldBe null
    }

    "map simple string with space" {
        val source = "name with space"

        val mapping = parseMapping(source)
        mapping.kind shouldBe Mapping.Kind.TYPE
        mapping.sourceType shouldBe source
        mapping.targetType shouldBe null
    }

    "map source type to fully qualified java target type with single wildcard generic parameters" {
        val source = "SourceType => io.oap.TargetType <?>"

        val mapping = parseMapping(source)
        mapping.kind shouldBe Mapping.Kind.MAP
        mapping.sourceType shouldBe "SourceType"
        mapping.targetType shouldBe "io.oap.TargetType"
        mapping.targetGenericTypes shouldBe listOf("?")
        mapping.annotationType.shouldBeNull()
        mapping.annotationParameters.shouldBeEmpty()
    }

    "map source type to fully qualified java target type with wildcard generic parameters" {
        val source = "SourceType => io.oap.TargetType <java.lang.String, ?>"

        val mapping = parseMapping(source)
        mapping.kind shouldBe Mapping.Kind.MAP
        mapping.sourceType shouldBe "SourceType"
        mapping.targetType shouldBe "io.oap.TargetType"
        mapping.targetGenericTypes shouldBe listOf("java.lang.String", "?")
        mapping.annotationType.shouldBeNull()
        mapping.annotationParameters.shouldBeEmpty()
    }
})
