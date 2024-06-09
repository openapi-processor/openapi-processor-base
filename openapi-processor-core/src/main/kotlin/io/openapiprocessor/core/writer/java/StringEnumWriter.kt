/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.model.datatypes.DataType
import io.openapiprocessor.core.model.datatypes.StringEnumDataType
import io.openapiprocessor.core.writer.Identifier
import java.io.Writer

/**
 * Writer for String enum.
 */
open class StringEnumWriter(
    private val apiOptions: ApiOptions,
    private val identifier: Identifier,
    private val generatedWriter: GeneratedWriter
) {

    fun write(target: Writer, dataType: StringEnumDataType) {
        target.write("package ${dataType.getPackageName()};\n\n")

        val imports = collectImports (dataType.getPackageName(), dataType)
        imports.forEach {
            target.write("import ${it};\n")
        }
        if(imports.isNotEmpty()) {
            target.write("\n")
        }

        generatedWriter.writeUse(target)
        target.write("\n")
        target.write("public enum ${dataType.getTypeName()}")
        if (isSupplier()) {
            target.write(" implements Supplier<String>")
        }
        target.write(" {\n")

        val values = mutableListOf<String>()
        dataType.values.forEach {
            values.add ("    ${identifier.toEnum (it)}(\"${it}\")")
        }
        target.write (values.joinToString (",\n") + ";\n\n")
        target.write("    private final String value;\n\n")

        target.write (
            """
            |    ${dataType.getTypeName()}(String value) {
            |        this.value = value;
            |    }
            |
            |
            """.trimMargin())

        if(isSupplier()) {
            target.write(
                """
                |    @JsonValue
                |    public String get() {
                |        return this.value;
                |    }
                |
                |
                """.trimMargin())

        } else {
            // default
            target.write(
                """
                |    @JsonValue
                |    public String getValue() {
                |        return this.value;
                |    }
                |
                |
                """.trimMargin())
        }

        target.write(
            """
            |    @JsonCreator
            |    public static ${dataType.getTypeName()} fromValue(String value) {
            |        for (${dataType.getTypeName()} val : ${dataType.getTypeName()}.values()) {
            |            if (val.value.equals(value)) {
            |                return val;
            |            }
            |        }
            |        throw new IllegalArgumentException(value);
            |    }
            |
            """.trimMargin())

        target.write ("}\n")
    }

    private fun collectImports(packageName: String, dataType: DataType): List<String> {
        val imports = mutableSetOf<String>()
        imports.add("com.fasterxml.jackson.annotation.JsonCreator")
        imports.add("com.fasterxml.jackson.annotation.JsonValue")
        imports.addAll(generatedWriter.getImports())
        imports.addAll(dataType.referencedImports)
        if (isSupplier()) {
            imports.add ("java.util.function.Supplier")
        }

        return DefaultImportFilter ()
            .filter(packageName, imports)
            .sorted()
    }

    private fun isSupplier(): Boolean {
        return apiOptions.enumType == "framework"
    }
}
