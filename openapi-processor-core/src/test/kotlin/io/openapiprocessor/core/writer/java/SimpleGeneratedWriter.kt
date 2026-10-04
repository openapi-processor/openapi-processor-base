/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.converter.ApiOptions
import java.io.Writer

class SimpleGeneratedWriter(private val options: ApiOptions) : GeneratedWriter {

    @Deprecated("use writeAnnotation instead.")
    override fun getImports(): Set<String> {
        return setOf("${options.packageName}.support.Generated")
    }

    @Deprecated("use writeAnnotation instead.")
    override fun writeUse(target: Writer) {
        target.write("@Generated\n")
    }

    override fun writeAnnotation(target: Writer): Set<String> {
        target.write("@Generated\n")
        return setOf("${options.packageName}.support.Generated")
    }

    override fun writeSource(target: Writer) {
        target.write("""
            package ${options.packageName}.support.Generated;

            import java.lang.annotation.*;
            import static java.lang.annotation.ElementType.*;
            import static java.lang.annotation.RetentionPolicy.*;

            @Documented
            @Retention(CLASS)
            @Target({TYPE, METHOD})
            public @interface Generated {
            }
            """.trimIndent())
    }
}
