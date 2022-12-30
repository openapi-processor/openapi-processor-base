/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.converter.mapping.Annotation
import io.openapiprocessor.core.converter.mapping.AnnotationTypeMapping
import io.openapiprocessor.core.converter.mapping.ParameterAnnotationTypeMapping

class MappingFinderAnnotationSpec: StringSpec({

    "find type annotation mapping" {
        val finder = MappingFinder(listOf(
            AnnotationTypeMapping(
                "Foo", null,
                Annotation("annotation.Bar"))
        ))

        val mapping = finder.findTypeAnnotations("Foo")

        mapping.size shouldBe 1
        mapping.first().sourceTypeName shouldBe "Foo"
    }

    "find type:format annotation mapping" {
        val finder = MappingFinder(listOf(
            AnnotationTypeMapping(
                "string", "uuid",
                Annotation("annotation.Foo"))
        ))

        val mapping = finder.findTypeAnnotations("string:uuid")

        mapping.size shouldBe 1
        mapping.first().sourceTypeName shouldBe "string"
        mapping.first().sourceTypeFormat shouldBe "uuid"
    }

    "find parameter annotation mapping" {
        val finder = MappingFinder(
            listOf(
                ParameterAnnotationTypeMapping(
                    AnnotationTypeMapping(
                        "Foo",
                        null,
                        Annotation("annotation.Bar")))
        ))

        val mapping = finder.findParameterAnnotations("/any", null, "Foo")

        mapping.size shouldBe 1
        mapping.first().sourceTypeName shouldBe "Foo"
    }

    "find parameter type:format annotation mapping" {
        val finder = MappingFinder(listOf(
            ParameterAnnotationTypeMapping(
                AnnotationTypeMapping(
                    "string",
                    "uuid",
                Annotation("annotation.Foo")))
        ))

        val mapping = finder.findParameterAnnotations("/any", null, "string:uuid")

        mapping.size shouldBe 1
        mapping.first().sourceTypeName shouldBe "string"
        mapping.first().sourceTypeFormat shouldBe "uuid"
    }
})

