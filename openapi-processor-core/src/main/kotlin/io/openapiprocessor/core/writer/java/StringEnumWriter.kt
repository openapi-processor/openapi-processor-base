/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.model.datatypes.DataType
import io.openapiprocessor.core.model.datatypes.StringEnumDataType
import io.openapiprocessor.core.support.LF
import io.openapiprocessor.core.writer.Identifier
import java.io.StringWriter
import java.io.Writer

/**
 * Writer for String enum.
 */
open class StringEnumWriter(
    private val apiOptions: ApiOptions,
    private val identifier: Identifier,
    private val generatedWriter: GeneratedWriter,
    private val jacksonAnnotations: JacksonAnnotations = JacksonAnnotations(apiOptions),
    private val javadocFactory: JavaDocFactory = JavaDocFactory(identifier)
) {

    fun write(target: Writer, dataType: StringEnumDataType) {
        val collectedImports = mutableSetOf<String>()
        collectedImports.addAll(dataType.referencedImports)

        val content = StringWriter()
        content.write("package ${dataType.getPackageName()};\n\n")
        content.write("<IMPORTS>\n\n")

        writeJavaDoc(content, dataType)

        collectedImports.addAll(generatedWriter.writeAnnotation(content))
        content.write("public enum ${dataType.getTypeName()}")
        if (isSupplier()) {
            collectedImports.add("java.util.function.Supplier")
            content.write(" implements Supplier<String>")
        }
        content.write(" {\n")

        val values = mutableListOf<String>()
        dataType.values.forEach {
            values.add ("    ${identifier.toEnum (it)}(\"${it}\")")
        }
        content.write (values.joinToString (",\n") + ";\n\n")
        content.write("    private final String value;\n\n")

        content.write (
            """
            |    ${dataType.getTypeName()}(String value) {
            |        this.value = value;
            |    }
            |
            |
            """.trimMargin())

        if(isSupplier()) {
            collectedImports.addAll(jacksonAnnotations.jsonValue.imports)
            content.write(
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
            collectedImports.addAll(jacksonAnnotations.jsonValue.imports)
            content.write(
                """
                |    @JsonValue
                |    public String getValue() {
                |        return this.value;
                |    }
                |
                |
                """.trimMargin())
        }

        collectedImports.addAll(jacksonAnnotations.jsonCreator.imports)
        content.write(
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

        content.write ("}\n")

        val imports = createImports(dataType, collectedImports)
        target.write(replaceImportsPlaceholder(content.toString(), imports))
    }

    private fun createImports(dataType: StringEnumDataType, collectedImports: MutableSet<String>): String =
        ImportFactory().create(dataType.getPackageName(), collectedImports)

    private fun isSupplier(): Boolean {
        return apiOptions.enumType == "framework"
    }

    private fun writeJavaDoc(target: Writer, dataType: DataType) {
        if (apiOptions.javadoc) {
            target.write(javadocFactory.createForDataType(dataType))
            target.write(LF)
        }
    }
}
