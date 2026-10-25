/*
 * Copyright © 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.support

import io.openapiprocessor.core.model.parameters.Parameter
import io.openapiprocessor.core.writer.java.CodeSnippet
import io.openapiprocessor.core.writer.java.ParameterAnnotationFactory
import java.io.Writer

class TestParameterAnnotationFactory: ParameterAnnotationFactory {

    override fun write(target: Writer, parameter: Parameter) {
        target.write ("@Parameter")
    }

    override fun create(parameter: Parameter): List<CodeSnippet> {
        if (!parameter.withAnnotation) {
            return emptyList()
        }

        return listOf(CodeSnippet("@Parameter", setOf("annotation.Parameter")))
    }
}
