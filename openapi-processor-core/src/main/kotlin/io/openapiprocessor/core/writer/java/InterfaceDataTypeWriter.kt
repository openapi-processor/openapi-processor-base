/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.model.datatypes.InterfaceDataType
import java.io.Writer

class InterfaceDataTypeWriter(
    private val apiOptions: ApiOptions,
    private val generatedWriter: GeneratedWriter,
    private val javadocWriter: JavaDocWriter
) {

    fun write(target: Writer, dataType: InterfaceDataType) {
        target.write("package ${dataType.getPackageName()};\n\n")

        val imports = collectImports()
        imports.forEach {
            target.write("import ${it};\n")
        }
        target.write("\n")

        if (apiOptions.javadoc) {
            target.write(javadocWriter.create(dataType))
        }

        generatedWriter.writeUse(target)
        target.write("public interface ${dataType.getTypeName()} {\n")
        target.write ("}\n")
    }

    private fun collectImports(): Set<String> {
        val imports = mutableSetOf<String>()
        imports.addAll(generatedWriter.getImports())
        return imports
    }
}
