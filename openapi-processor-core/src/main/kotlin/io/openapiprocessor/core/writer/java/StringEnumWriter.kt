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
        // Review: probably not the best place for validation
        if (dataType.xEnumNames.size > 0 && dataType.values.size != dataType.xEnumNames.size) {
            throw IllegalStateException("Enum names size mismatch")
        }
        val enumNames = if (dataType.xEnumNames.size > 0) dataType.xEnumNames
            else MutableList(dataType.values.size) { "" }
        dataType.values.zip(enumNames).forEach { pair ->
            val enumValue = pair.component1()
            val enumName = pair.component2()
            if (enumName == "") {
                values.add("    ${identifier.toEnum(enumValue)}(\"${enumValue}\")")
            } else {
                values.add("    ${identifier.toEnum(enumName)}(\"${enumValue}\")")
            }
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
        imports.add ("com.fasterxml.jackson.annotation.JsonCreator")
        imports.add ("com.fasterxml.jackson.annotation.JsonValue")
        imports.add(generatedWriter.getImport())
        imports.addAll (dataType.referencedImports)
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
