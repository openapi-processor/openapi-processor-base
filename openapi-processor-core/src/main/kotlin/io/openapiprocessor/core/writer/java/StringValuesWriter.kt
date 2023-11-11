/*
 * Copyright 2023 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.converter.ApiOptions
import java.io.Writer

class StringValuesWriter(val options: ApiOptions) {

    fun writeValues(target: Writer) {
        target.write("""
            package ${options.packageName}.validation;

            import ${options.beanValidationFormat}.validation.Constraint;
            import ${options.beanValidationFormat}.validation.Payload;
            import java.lang.annotation.*;
    
            @Target({ElementType.FIELD, ElementType.PARAMETER})
            @Retention(RetentionPolicy.RUNTIME)
            @Constraint(validatedBy = ValueValidator.class)
            @Documented
            public @interface Values {
                String message() default "Invalid value. Should be one of values.";
                Class<?>[] groups() default {};
                Class<? extends Payload>[] payload() default {};
                String[] values() default {};
            }
            
            """.trimIndent())
    }

    fun writeValueValidator(target: Writer) {
        target.write("""
            package ${options.packageName}.validation;

            import jakarta.validation.ConstraintValidator;
            import jakarta.validation.ConstraintValidatorContext;
            
            import java.util.Arrays;

            public class ValueValidator implements ConstraintValidator<Values, String> {
                private String[] values;

                @Override
                public void initialize (Values constraintAnnotation) {
                    values = constraintAnnotation.values();
                }

                @Override
                public boolean isValid (String value, ConstraintValidatorContext context) {
                    return value != null && Arrays.asList(values).contains(value);
                }
            }

            """.trimIndent())
    }
}
