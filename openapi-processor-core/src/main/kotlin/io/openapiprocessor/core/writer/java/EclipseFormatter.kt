package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.writer.SourceFormatter
import org.eclipse.jdt.core.JavaCore
import org.eclipse.jdt.core.ToolFactory
import org.eclipse.jdt.core.formatter.CodeFormatter
import org.eclipse.jface.text.Document
import java.io.StringReader
import java.util.*
import java.util.stream.Collectors
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants as Formatter

class EclipseFormatter: SourceFormatter {
    private lateinit var formatter: CodeFormatter

    init {
        initFormatter()
    }

    override fun format(raw: String): String {
        try {
            val textEdit = formatter.format(
                CodeFormatter.K_COMPILATION_UNIT + CodeFormatter.F_INCLUDE_COMMENTS,
                raw,
                0,
                raw.length,
                0,
                "\n"
            )

            val document = Document(raw)
            textEdit.apply(document)

            return correctEndOfFile(document.get())
        } catch (e: Exception) {
            throw FormattingException(raw, e)
        }
    }

    private fun correctEndOfFile(formatted: String): String {
        val index = formatted.lastIndexOf("}")

        return StringBuilder()
            .append(formatted.substring(0, index))
            .append("}\n")
            .toString()
    }

    private fun initFormatter() {
        formatter = ToolFactory.createCodeFormatter(getOptions())
    }

    private fun getOptions(): Map<String, String> {
        val options = mutableMapOf<String, String>()

        // options.putAll(JavaCore.getDefaultOptions())
        options.putAll(getJavaOptions())

        //        options.putAll(convertOptions(loadStyleOptions()))
        //        options[DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR] = JavaCore.SPACE
        //        options[DefaultCodeFormatterConstants.FORMATTER_LINE_SPLIT] = "80"
        //        options[DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_PARAMETERS_IN_METHOD_DECLARATION] = "4"
        //        options[DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_PARAMETERS_IN_METHOD_DECLARATION] =
        //            DefaultCodeFormatterConstants.createAlignmentValue(
        //                false,
        //                DefaultCodeFormatterConstants.WRAP_ONE_PER_LINE,
        //                DefaultCodeFormatterConstants.INDENT_BY_ONE)
        //
        //        options[DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_METHOD_DECLARATION] =
        //            DefaultCodeFormatterConstants.createAlignmentValue(
        //                false,
        //                DefaultCodeFormatterConstants.WRAP_COMPACT,
        //                DefaultCodeFormatterConstants.INDENT_BY_ONE)
        //
                // FORMATTER_ALIGNMENT_FOR_METHOD_DECLARATION

        return options
    }

    @Suppress("UNCHECKED_CAST")
    private fun getJavaOptions(): Map<String, String> {
        return Formatter.getJavaConventionsSettings() as Map<String, String>
    }

    private fun loadStyleOptions(): Properties {
        val content = this.javaClass.getResource("/google-style.properties")?.readText()
        val reader = StringReader(content)
        val properties = Properties()
        properties.load(reader)
        return properties
    }

    private fun convertOptions(prop: Properties): HashMap<String, String> {
        return prop.entries
            .stream()
            .collect(
                Collectors.toMap(
                    { e -> e.toString() },
                    { v -> v.toString() },
                    { _, next -> next })
                { HashMap() }
            )
    }
}
