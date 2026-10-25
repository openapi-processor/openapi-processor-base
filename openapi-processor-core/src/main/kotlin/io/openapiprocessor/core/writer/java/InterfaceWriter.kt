/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.framework.FrameworkAnnotations
import io.openapiprocessor.core.model.Interface
import java.io.StringWriter
import java.io.Writer

/**
 * Writer for Java interfaces.
 */
class InterfaceWriter(
    private val generatedWriter: GeneratedWriter,
    private val methodWriter: MethodWriter,
    private val annotations: FrameworkAnnotations
) {
    fun write(target: Writer, itf: Interface) {
        val collectedImports = mutableSetOf<String>()

        val content = StringWriter()
        content.write ("package ${itf.getPackageName()};\n\n")
        content.write("<IMPORTS>\n\n")

        collectedImports.addAll(generatedWriter.writeAnnotation(content))

        content.write("public interface ${itf.getInterfaceName()} {\n\n")

        itf.endpoints.forEach { ep ->
            val annotation = annotations.getAnnotation (ep.method)
            collectedImports.addAll(annotation.imports)
            collectedImports.addAll(annotation.referencedImports)

            ep.endpointResponses.forEach { er ->
                val methodImports = methodWriter.write(content, ep, er)
                collectedImports.addAll(methodImports)
                content.write("\n")
            }
        }

        content.write ("}\n")

        val imports = createImports(itf, collectedImports)
        target.write(replaceImportsPlaceholder(content, imports))
    }

    private fun createImports(itf: Interface, collectedImports: MutableSet<String>): String =
        ImportFactory().create(itf.getPackageName(), collectedImports)
}
