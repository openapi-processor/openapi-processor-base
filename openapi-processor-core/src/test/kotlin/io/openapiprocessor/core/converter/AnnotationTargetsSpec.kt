/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue

class AnnotationTargetsSpec : FreeSpec({

    "missing annotation is allowed on all types" {
        val targets = AnnotationTargets()
        targets.isAllowedOnType("an.Annotation").shouldBeTrue()
    }

    "annotation is allowed on type" {
        val targets = AnnotationTargets()
        targets.add("an.Annotation", AnnotationTargetType.TYPE)
        targets.isAllowedOnType("an.Annotation").shouldBeTrue()
    }

    "annotation is NOT allowed on type" {
        val targets = AnnotationTargets()
        val types = AnnotationTargetType.entries - setOf(AnnotationTargetType.TYPE)
        targets.add("an.Annotation", *types.toTypedArray())
        targets.isAllowedOnType("an.Annotation").shouldBeFalse()
    }

    "annotation is allowed on field" {
        val targets = AnnotationTargets()
        targets.add("an.Annotation", AnnotationTargetType.FIELD)
        targets.isAllowedOnField("an.Annotation").shouldBeTrue()
    }

    "annotation is NOT allowed on field" {
        val targets = AnnotationTargets()
        val types = AnnotationTargetType.entries - setOf(AnnotationTargetType.FIELD)
        targets.add("an.Annotation", *types.toTypedArray())
        targets.isAllowedOnField("an.Annotation").shouldBeFalse()
    }

    "annotation is allowed on method" {
        val targets = AnnotationTargets()
        targets.add("an.Annotation", AnnotationTargetType.METHOD)
        targets.isAllowedOnMethod("an.Annotation").shouldBeTrue()
    }

    "annotation is NOT allowed on method" {
        val targets = AnnotationTargets()
        val types = AnnotationTargetType.entries - setOf(AnnotationTargetType.METHOD)
        targets.add("some.Annotation", *types.toTypedArray())
        targets.isAllowedOnMethod("some.Annotation").shouldBeFalse()
    }

    "annotation is allowed on parameter" {
        val targets = AnnotationTargets()
        targets.add("an.Annotation", AnnotationTargetType.PARAMETER)
        targets.isAllowedOnParameter("an.Annotation").shouldBeTrue()
    }

    "annotation is NOT allowed on parameter" {
        val targets = AnnotationTargets()
        val types = AnnotationTargetType.entries - setOf(AnnotationTargetType.PARAMETER)
        targets.add("an.Annotation", *types.toTypedArray())
        targets.isAllowedOnParameter("an.Annotation").shouldBeFalse()
    }

})
