/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.converter.mapping.Annotation
import io.openapiprocessor.core.converter.mapping.AnnotationNameMappingDefault
import io.openapiprocessor.core.converter.mapping.AnnotationTypeMappingDefault
import io.openapiprocessor.core.model.datatypes.ArrayDataType
import io.openapiprocessor.core.model.datatypes.StringDataType
import io.openapiprocessor.core.support.datatypes.ObjectDataType
import io.openapiprocessor.core.support.datatypes.propertyDataTypeString

class MappingFinderAnnotationSpec: StringSpec({

    "find global annotation type mapping" {
        val finder = MappingFinder(listOf(
            AnnotationTypeMappingDefault(
                "Foo", null,
                Annotation("annotation.Bar"))
        ))

        val mapping = finder.findTypeAnnotations("Foo")

        mapping.size shouldBe 1
        mapping.first().sourceTypeName shouldBe "Foo"
    }

    "find 'object' annotation type mapping for model data type" {
        val finder = MappingFinder(listOf(
            AnnotationTypeMappingDefault(
                "object", null,
                Annotation("annotation.Bar"))
        ))

        val dataType = ObjectDataType("Foo", "pkg", linkedMapOf(
            Pair("foo", propertyDataTypeString())
        ))

        val mapping = finder.findTypeAnnotations(dataType.getTypeName(), true)

        mapping.size shouldBe 1
        mapping.first().sourceTypeName shouldBe "object"
    }

    "ignore 'object' annotation type mapping for non model data types" {
        val finder = MappingFinder(listOf(
            AnnotationTypeMappingDefault(
                "object", null,
                Annotation("annotation.Bar"))
        ))

        val mappingSimple = finder.findTypeAnnotations(StringDataType().getTypeName())
        mappingSimple.shouldBeEmpty()

        val mappingCollection = finder.findTypeAnnotations(ArrayDataType(StringDataType()).getTypeName())
        mappingCollection.shouldBeEmpty()
    }

    "find type:format annotation type mapping" {
        val finder = MappingFinder(listOf(
            AnnotationTypeMappingDefault(
                "string", "uuid",
                Annotation("annotation.Foo"))
        ))

        val mapping = finder.findTypeAnnotations("string:uuid")

        mapping.size shouldBe 1
        mapping.first().sourceTypeName shouldBe "string"
        mapping.first().sourceTypeFormat shouldBe "uuid"
    }

    "find parameter annotation type mapping" {
        val finder = MappingFinder(listOf(
                AnnotationTypeMappingDefault(
                    "Foo",
                    null,
                    Annotation("annotation.Bar")))
        )

        val mapping = finder.findParameterTypeAnnotations("/any", null, "Foo")

        mapping.size shouldBe 1
        mapping.first().sourceTypeName shouldBe "Foo"
    }

    "find parameter type:format annotation type mapping" {
        val finder = MappingFinder(listOf(
                AnnotationTypeMappingDefault(
                    "string",
                    "uuid",
                Annotation("annotation.Foo")))
        )

        val mapping = finder.findParameterTypeAnnotations("/any", null, "string:uuid")

        mapping.size shouldBe 1
        mapping.first().sourceTypeName shouldBe "string"
        mapping.first().sourceTypeFormat shouldBe "uuid"
    }

    "find parameter annotation name mapping" {
        val finder = MappingFinder(listOf(
                AnnotationNameMappingDefault(
                    "foo",
                    Annotation("annotation.Bar")))
        )

        val mapping = finder.findParameterNameAnnotations("/any", null, "foo")

        mapping.size shouldBe 1
        mapping.first().parameterName shouldBe "foo"
    }

})

