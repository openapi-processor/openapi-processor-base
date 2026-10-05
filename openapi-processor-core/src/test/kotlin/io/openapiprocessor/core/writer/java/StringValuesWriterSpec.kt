/*
 * Copyright 2026 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.converter.ApiOptions
import java.io.StringWriter

class StringValuesWriterSpec: StringSpec({
    val options = ApiOptions()

    "writes values annotation" {
        options.beanValidation = true
        options.beanValidationFormat = "jakarta"
        val writer = StringValuesWriter(options, SimpleGeneratedWriter(options))

        val target = StringWriter()
        writer.writeValues(target)

        target.toString() shouldBe """
            package io.openapiprocessor.generated.validation;
            
            import io.openapiprocessor.generated.support.Generated;
            import jakarta.validation.Constraint;
            import jakarta.validation.Payload;
            import java.lang.annotation.Documented;
            import java.lang.annotation.ElementType;
            import java.lang.annotation.Retention;
            import java.lang.annotation.RetentionPolicy;
            import java.lang.annotation.Target;

            @Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_USE})
            @Retention(RetentionPolicy.RUNTIME)
            @Constraint(validatedBy = ValueValidator.class)
            @Documented
            @Generated
            public @interface Values {
                String message() default "Invalid value. Should be one of values.";
                Class<?>[] groups() default {};
                Class<? extends Payload>[] payload() default {};
                String[] values() default {};
            }

        """.trimIndent()
    }

    "writes values validator" {
        options.beanValidation = true
        options.beanValidationFormat = "jakarta"
        val writer = StringValuesWriter(options, SimpleGeneratedWriter(options))

        val target = StringWriter()
        writer.writeValueValidator(target)

        target.toString() shouldBe """
            package io.openapiprocessor.generated.validation;
            
            import io.openapiprocessor.generated.support.Generated;
            import jakarta.validation.ConstraintValidator;
            import jakarta.validation.ConstraintValidatorContext;
            import java.util.Arrays;
            import java.util.Set;
            
            @Generated
            public class ValueValidator implements ConstraintValidator<Values, String> {
                private Set<String> values;

                @Override
                public void initialize (Values constraintAnnotation) {
                    values = Set.copyOf(Arrays.asList(constraintAnnotation.values()));
                }

                @Override
                public boolean isValid (String value, ConstraintValidatorContext context) {
                    return value != null && values.contains(value);
                }
            }

        """.trimIndent()
    }
})
