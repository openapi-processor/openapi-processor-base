/*
 * Copyright 2023 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.converter.ApiOptions
import java.io.Writer

class StringValuesWriter(
    val options: ApiOptions,
    private val generatedWriter: GeneratedWriter
) {

    fun writeValues(target: Writer) {
        val imports = listOf(
            generatedWriter.getImport(),
            "${options.beanValidationFormat}.validation.Constraint",
            "${options.beanValidationFormat}.validation.Payload",
            "java.lang.annotation.*"
        ).sorted()

        target.write("""
            package ${options.packageName}.validation;
            
        """.trimIndent())

        target.write("\n")
        imports.forEach {
            target.write("import ${it};\n")
        }
        target.write("\n")

        target.write("""
            @Target({ElementType.FIELD, ElementType.PARAMETER})
            @Retention(RetentionPolicy.RUNTIME)
            @Constraint(validatedBy = ValueValidator.class)
            @Documented
            
        """.trimIndent())

        generatedWriter.writeUse(target)
        target.write("\n")

        target.write("""
            public @interface Values {
                String message() default "Invalid value. Should be one of values.";
                Class<?>[] groups() default {};
                Class<? extends Payload>[] payload() default {};
                String[] values() default {};
            }
            
            """.trimIndent())
    }

    fun writeValueValidator(target: Writer) {
        val imports = listOf(
            generatedWriter.getImport(),
            "${options.beanValidationFormat}.validation.ConstraintValidator",
            "${options.beanValidationFormat}.validation.ConstraintValidatorContext",
            "java.util.Arrays"
        ).sorted()

        target.write("""
            package ${options.packageName}.validation;
            
        """.trimIndent())

        target.write("\n")
        imports.forEach {
            target.write("import ${it};\n")
        }
        target.write("\n")

        generatedWriter.writeUse(target)
        target.write("\n")

        target.write("""
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
