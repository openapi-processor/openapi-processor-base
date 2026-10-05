/*
 * Copyright 2023 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.converter.ApiOptions
import java.io.StringWriter
import java.io.Writer

class StringValuesWriter(
    val options: ApiOptions,
    private val generatedWriter: GeneratedWriter
) {

    fun writeValues(target: Writer) {
        val collectedImports = mutableSetOf<String>()

        val imports = mutableSetOf(
            "${options.beanValidationFormat}.validation.Constraint",
            "${options.beanValidationFormat}.validation.Payload",
            "java.lang.annotation.Documented",
            "java.lang.annotation.ElementType",
            "java.lang.annotation.Retention",
            "java.lang.annotation.RetentionPolicy",
            "java.lang.annotation.Target")

        val pkg = "${options.packageName}.validation"

        val content = StringWriter()
        content.write("""
            package $pkg;
            
            <IMPORTS>

        """.trimIndent())

        content.write("\n")
        imports.sorted().forEach {
            content.write("import ${it};\n")
        }
        content.write("\n")

        content.write("""
            @Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_USE})
            @Retention(RetentionPolicy.RUNTIME)
            @Constraint(validatedBy = ValueValidator.class)
            @Documented
            
        """.trimIndent())

        collectedImports.addAll(generatedWriter.writeAnnotation(content))

        content.write("""
            public @interface Values {
                String message() default "Invalid value. Should be one of values.";
                Class<?>[] groups() default {};
                Class<? extends Payload>[] payload() default {};
                String[] values() default {};
            }
            
            """.trimIndent())

        val (cleanContent, oldImports) = extractImports(content)
        collectedImports.addAll(oldImports)

        val importsX = createImports(pkg, collectedImports)
        target.write(replaceImportsPlaceholder(cleanContent, importsX))
    }

    fun writeValueValidator(target: Writer) {
        val imports = mutableSetOf(
            "${options.beanValidationFormat}.validation.ConstraintValidator",
            "${options.beanValidationFormat}.validation.ConstraintValidatorContext",
            "java.util.Arrays",
            "java.util.Set"
        )
        imports.addAll(generatedWriter.getImports())

        target.write("""
            package ${options.packageName}.validation;
            
        """.trimIndent())

        target.write("\n")
        imports.sorted().forEach {
            target.write("import ${it};\n")
        }
        target.write("\n")

        generatedWriter.writeUse(target)

        target.write("""
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

            """.trimIndent())
    }

    private fun createImports(pkg: String, collectedImports: MutableSet<String>): String {
        return ImportFactory().create(pkg, collectedImports)
    }
}
