/*
 * Copyright © 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.support

import io.openapiprocessor.core.model.parameters.Parameter
import io.openapiprocessor.core.writer.java.CodeSnippet
import io.openapiprocessor.core.writer.java.ParameterAnnotationFactory

class TestParameterAnnotationFactory: ParameterAnnotationFactory {

    override fun create(parameter: Parameter): List<CodeSnippet> {
        if (!parameter.withAnnotation) {
            return emptyList()
        }

        return listOf(CodeSnippet("@Parameter", setOf("annotation.Parameter")))
    }
}
