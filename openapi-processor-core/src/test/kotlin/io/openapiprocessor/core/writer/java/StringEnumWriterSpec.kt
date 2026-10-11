/*
 * Copyright 2016 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.mockk.verify
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.model.Documentation
import io.openapiprocessor.core.model.datatypes.DataTypeName
import io.openapiprocessor.core.model.datatypes.StringEnumDataType
import java.io.StringWriter

class StringEnumWriterSpec : StringSpec({
    val options = ApiOptions()

    "looks up jackson annotations" {
        val jackson = mockk<JacksonAnnotations>(relaxed = true)

        val writer = StringEnumWriter(
            options,
            JavaIdentifier(),
            SimpleGeneratedWriter(options),
            jacksonAnnotations = jackson)
        val target = StringWriter()

        val dataType = StringEnumDataType(
            DataTypeName("Foo"),
            "pkg",
            listOf(),
            null,
            false,
            Documentation(null, "description"))

        writer.write(target, dataType)

        verify { jackson.createCreatorAnnotation() }
        verify { jackson.createValueAnnotation() }
    }

    "writes enum" {
        val writer = StringEnumWriter(
            options,
            JavaIdentifier(),
            SimpleGeneratedWriter(options))
        val target = StringWriter()

        val dataType = StringEnumDataType(
            DataTypeName("Foo"),
            "pkg",
            listOf("foo", "bar"))

        writer.write(target, dataType)

        target.toString() shouldBe """
            package pkg;

            import com.fasterxml.jackson.annotation.JsonCreator;
            import com.fasterxml.jackson.annotation.JsonValue;
            import io.openapiprocessor.generated.support.Generated;

            @Generated
            public enum Foo {
                FOO("foo"),
                BAR("bar");

                private final String value;

                Foo(String value) {
                    this.value = value;
                }

                @JsonValue
                public String getValue() {
                    return this.value;
                }

                @JsonCreator
                public static Foo fromValue(String value) {
                    for (Foo val : Foo.values()) {
                        if (val.value.equals(value)) {
                            return val;
                        }
                    }
                    throw new IllegalArgumentException(value);
                }
            }

        """.trimIndent()
    }

    "writes enum implementing Supplier" {
        val opt = ApiOptions()
        opt.enumType = "framework"
        val writer = StringEnumWriter(
            opt,
            JavaIdentifier(),
            SimpleGeneratedWriter(opt))
        val target = StringWriter()

        val dataType = StringEnumDataType(
            DataTypeName("Foo"),
            "pkg",
            listOf("foo", "bar"))

        writer.write(target, dataType)

        target.toString() shouldBe """
            package pkg;

            import com.fasterxml.jackson.annotation.JsonCreator;
            import com.fasterxml.jackson.annotation.JsonValue;
            import io.openapiprocessor.generated.support.Generated;
            import java.util.function.Supplier;

            @Generated
            public enum Foo implements Supplier<String> {
                FOO("foo"),
                BAR("bar");

                private final String value;

                Foo(String value) {
                    this.value = value;
                }

                @JsonValue
                public String get() {
                    return this.value;
                }

                @JsonCreator
                public static Foo fromValue(String value) {
                    for (Foo val : Foo.values()) {
                        if (val.value.equals(value)) {
                            return val;
                        }
                    }
                    throw new IllegalArgumentException(value);
                }
            }

        """.trimIndent()
    }
})
