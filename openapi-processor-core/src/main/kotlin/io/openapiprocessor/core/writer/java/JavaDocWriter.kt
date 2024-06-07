/*
 * Copyright © 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.model.Endpoint
import io.openapiprocessor.core.model.EndpointResponse
import io.openapiprocessor.core.model.datatypes.DataType
import io.openapiprocessor.core.model.datatypes.ModelDataType
import io.openapiprocessor.core.writer.Identifier
import org.commonmark.node.Document
import org.commonmark.node.Node
import org.commonmark.node.Paragraph
import org.commonmark.parser.Parser
import org.commonmark.renderer.NodeRenderer
import org.commonmark.renderer.html.CoreHtmlNodeRenderer
import org.commonmark.renderer.html.HtmlNodeRendererContext
import org.commonmark.renderer.html.HtmlRenderer

/**
 * Do not wrap the top level items in (unwanted) paragraphs.
 */
class SkipParentWrapperParagraphsRenderer(val context: HtmlNodeRendererContext)
    : CoreHtmlNodeRenderer(context), NodeRenderer {

    override fun getNodeTypes(): Set<Class<out Node>> {
        return setOf(Paragraph::class.java)
    }

    override fun render(node: Node) {
        if (node.parent is Document) {
            visitChildren(node)
        } else {
            visit(node as Paragraph)
        }
    }
}

/**
 * create javadoc from OpenAPI descriptions.
 */
open class JavaDocWriter(val identifier: Identifier) {

    val parser: Parser = Parser
        .builder()
        .build()

    val renderer: HtmlRenderer = HtmlRenderer
        .builder()
        .nodeRendererFactory { context -> SkipParentWrapperParagraphsRenderer(context) }
        .build()

    fun convert(endpoint: Endpoint, endpointResponse: EndpointResponse): String {
        var comment = ""

        if (endpoint.summary != null) {
            comment += endpoint.summary + "\n"
        }

        if (endpoint.description != null) {
            comment += convert(endpoint.description)
        }

        if (endpoint.parameters.isNotEmpty() || endpointResponse.description != null)
            comment += "\n"

        endpoint.parameters.forEach {
            comment += convert(it.description, "@param ${identifier.toCamelCase (it.name)}")
        }

        endpoint.requestBodies.forEach {
            comment += convert(it.description, "@param ${identifier.toCamelCase (it.name)}")
        }

        val response = convert(endpointResponse.description, "@return")
        if (response.isNotEmpty()) {
            comment += response
            comment += "\n"
        }

        return indent(wrap(comment))
    }

    fun convert(dataType: ModelDataType): String {
        var comment = ""

        if (dataType.documentation?.description != null) {
            comment += convert(dataType.documentation!!.description) + "\n"
        }

        return wrap(comment)
    }

    fun convert(dataType: DataType): String {
        var comment = ""

        if (dataType.documentation?.description != null) {
            comment += convert(dataType.documentation!!.description) + "\n"
        }

        return indent(wrap(comment))
    }

    private fun wrap(comment: String): String {
        val temp = comment
            .dropLastWhile { it == '\n' }

        if (temp.isEmpty())
            return ""

        val javadoc = temp
            .lineSequence()
            .map {
                " * $it".trimEnd()
            }
            .joinToString(
                "\n",
                "/**\n",
                "\n */\n")

        return javadoc
    }

    private fun indent(javadoc: String): String {
        if (javadoc.isEmpty())
            return ""

        val indented = javadoc
            .lineSequence()
            .map {
                return@map if (it.isEmpty())
                    it
                else
                    "    $it"
            }
            .joinToString("\n")

        return indented
    }

    private fun convert(description: String?, intro: String? = null): String {
        if (description.isNullOrEmpty())
            return ""

        val doc = parser.parse(description)

        var result = ""
        if (intro != null) {
            result = intro
            result += " "
        }

        result += renderer.render(doc) + "\n"

        return result
    }

}
