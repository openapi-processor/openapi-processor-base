/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.model.datatypes.InterfaceDataType
import java.io.StringWriter
import java.io.Writer

class InterfaceDataTypeWriter(
    private val apiOptions: ApiOptions,
    private val generatedWriter: GeneratedWriter,
    private val javadocFactory: JavaDocFactory
) {

    fun write(target: Writer, dataType: InterfaceDataType) {
        val collectedImports = mutableSetOf<String>()

        val content = StringWriter()
        content.write("package ${dataType.getPackageName()};\n\n")
        content.write("<IMPORTS>\n\n")

        if (apiOptions.javadoc) {
            content.write(javadocFactory.create(dataType))
        }

        collectedImports.addAll(generatedWriter.writeAnnotation(content))
        content.write("public interface ${dataType.getTypeName()} {\n")
        content.write ("}\n")

        val imports = createImports(dataType, collectedImports)
        target.write(replaceImportsPlaceholder(content.toString(), imports))
    }

    private fun createImports(dataType: InterfaceDataType, collectedImports: MutableSet<String>): String =
        ImportFactory().create(dataType.getPackageName(), collectedImports)
}
