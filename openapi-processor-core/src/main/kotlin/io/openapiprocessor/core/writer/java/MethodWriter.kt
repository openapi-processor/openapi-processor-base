/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.converter.MappingFinder
import io.openapiprocessor.core.converter.MappingFinderQuery
import io.openapiprocessor.core.model.Annotation
import io.openapiprocessor.core.model.Endpoint
import io.openapiprocessor.core.model.EndpointResponse
import io.openapiprocessor.core.model.parameters.AdditionalParameter
import io.openapiprocessor.core.model.parameters.Parameter
import io.openapiprocessor.core.processor.mapping.ResultStyle
import io.openapiprocessor.core.support.LF
import io.openapiprocessor.core.support.capitalizeFirstChar
import io.openapiprocessor.core.support.indent
import io.openapiprocessor.core.writer.Identifier
import io.openapiprocessor.core.writer.java.MappingAnnotationFactory as CoreMappingAnnotationFactory
import io.openapiprocessor.core.writer.java.ParameterAnnotationFactory as CoreParameterAnnotationWriter
import io.openapiprocessor.core.writer.java.StatusAnnotationFactory as CoreStatusAnnotationFactory
import java.io.StringWriter
import java.io.Writer

/**
 * Writer for Java interface methods, i.e., endpoints.
 */
open class MethodWriter(
    private val apiOptions: ApiOptions,
    private val identifier: Identifier,
    private val statusAnnotationFactory: CoreStatusAnnotationFactory,
    private val mappingAnnotationFactory: CoreMappingAnnotationFactory,
    private val parameterAnnotationFactory: CoreParameterAnnotationWriter,
    private val beanValidationFactory: BeanValidationFactory,
    private val javadocFactory: JavaDocFactory = JavaDocFactory(identifier)
) {
    private val annotationWriter = AnnotationWriter()

    fun write(target: Writer, endpoint: Endpoint, endpointResponse: EndpointResponse): Set<String> {
        val collectedImports = mutableSetOf<String>()

        if (apiOptions.javadoc) {
            target.write(createJavadoc(endpoint, endpointResponse).indent())
            target.write(LF)
        }

        if (endpoint.deprecated) {
            collectedImports.addAll(DEPRECATED.imports)
            target.write(DEPRECATED.annotationName.indent())
            target.write(LF)
        }

        if (shouldAddStatus(endpointResponse, endpoint)) {
            createStatusAnnotations(endpoint, endpointResponse).forEach { (annotation, imports) ->
                collectedImports.addAll(imports)
                target.write(annotation.indent())
                target.write(LF)
            }
        }

        createMappingAnnotations(endpoint, endpointResponse).forEach { (annotation, imports) ->
            collectedImports.addAll(imports)
            target.write(annotation.indent())
            target.write(LF)
        }

        val resultData = createResult(endpoint, endpointResponse)
        collectedImports.addAll(resultData.imports)
        target.write(resultData.text.indent())
        target.write(" ")

        target.write(createMethodName(endpoint, endpointResponse))
        target.write("(")

        val parameters = createParameters(endpoint)
        parameters.forEachIndexed { index, (text, imports) ->
            collectedImports.addAll(imports)
            target.write(formatParameter(index, text, parameters.lastIndex))
        }
        target.write(");")
        target.write(LF)

        return collectedImports
    }

    private fun createJavadoc(endpoint: Endpoint, endpointResponse: EndpointResponse): String {
        return javadocFactory.create(endpoint, endpointResponse)
    }

    private fun formatParameter(index: Int, parameter: String, lastIndex: Int): String {
        val formatted = StringBuilder()

        if (lastIndex == 0) {
            // one parameter on the same line
            formatted.append(parameter)

        } else {
            // each parameter on a new line
            formatted.append(LF)
            formatted.append(parameter.indent(3))

            if(index < lastIndex) {
                formatted.append(",")
            }
        }

        return formatted.toString()
    }

    private fun shouldAddStatus(endpointResponse: EndpointResponse, endpoint: Endpoint): Boolean {
        return MappingFinder(apiOptions).getResultStatusOption(MappingFinderQuery(endpoint))
            && endpointResponse.hasSingleResponse(getResultStyle(endpoint))
    }

    private fun createStatusAnnotations(endpoint: Endpoint, endpointResponse: EndpointResponse): List<CodeSnippet> {
        return statusAnnotationFactory.create(endpoint, endpointResponse)
    }

    private fun createMappingAnnotations(endpoint: Endpoint, endpointResponse: EndpointResponse): List<CodeSnippet> {
        return mappingAnnotationFactory.create(endpoint, endpointResponse)
    }

    private fun createResult(endpoint: Endpoint, endpointResponse: EndpointResponse): CodeSnippet {
        return endpointResponse.getResponseType(getResultStyle(endpoint))
    }

    private fun getResultStyle(endpoint: Endpoint): ResultStyle {
        return MappingFinder(apiOptions).findResultStyleMapping(MappingFinderQuery(endpoint))
    }

    private fun createMethodName(endpoint: Endpoint, endpointResponse: EndpointResponse): String {
        val tokens: MutableList<String>

        if (endpoint.operationId != null) {
            tokens = mutableListOf(endpoint.operationId)
        } else {
            tokens = mutableListOf(endpoint.method.method)
            tokens += endpoint.path
                .split('/')
                .filter { it.isNotEmpty() }
                .toMutableList()
        }

        if (endpoint.hasMultipleEndpointResponses()) {
            tokens += endpointResponse.contentType.split('/')
        }

        val camel = tokens.map { identifier.toCamelCase(it) }
        val head = camel.first()
        val tail = camel.subList(1, camel.count())
            .joinToString("") { it.capitalizeFirstChar() }

        if(endpoint.parameters.isEmpty() || endpoint.requestBodies.isNotEmpty()) {
            return head + identifier.toMethodTail(tail)
        }

        return head + tail
    }

    private fun createParameters(endpoint: Endpoint): List<CodeSnippet> {
        val parameters = mutableListOf<CodeSnippet>()

        endpoint.parameters.forEach {
            val collectedImports = mutableSetOf<String>()
            collectedImports.addAll(it.dataType.getImports())

            val dataTypeValue = if (apiOptions.beanValidation) {
                val info = beanValidationFactory.validate(it.dataType, it.required)
                collectedImports.addAll(info.inout.imports)
                info.inout.dataTypeValue
            } else {
                it.dataType.getTypeName()
            }

            val target = StringWriter()
            val annotationData = createParameterAnnotation(endpoint, it)
            if (annotationData != null) {
                collectedImports.addAll(annotationData.imports)
                target.write(annotationData.text)
                target.write(" ")
            }

            target.write("$dataTypeValue ${identifier.toIdentifier(it.name)}")

            parameters.add(CodeSnippet(target.toString(), collectedImports))
        }

        if (endpoint.requestBodies.isNotEmpty()) {
            val collectedImports = mutableSetOf<String>()

            val body = endpoint.getRequestBody()
            collectedImports.addAll(body.dataTypeImports)

            val dataTypeValue = if (apiOptions.beanValidation) {
                val info = beanValidationFactory.validate(body.dataType, body.required)
                collectedImports.addAll(info.inout.imports)
                info.inout.dataTypeValue
            } else {
                body.dataType.getTypeName()
            }

            val target = StringWriter()
            val annotationData = createParameterAnnotation(endpoint, body)
            if (annotationData != null) {
                collectedImports.addAll(annotationData.imports)
                target.write(annotationData.text)
                target.write(" ")
            }

            target.write("$dataTypeValue ${identifier.toIdentifier(body.name)}")

            parameters.add(CodeSnippet(target.toString(), collectedImports))
        }

        return parameters
    }

    private fun createParameterAnnotation(endpoint: Endpoint, parameter: Parameter): CodeSnippet? {
        val collectedImports = mutableSetOf<String>()
        val parts = mutableListOf<String>()

        if (parameter.deprecated) {
            parts.add(DEPRECATED.annotationName)
            collectedImports.addAll(DEPRECATED.imports)
        }

        parameterAnnotationFactory.create(parameter).forEach { (text, imports) ->
            collectedImports.addAll(imports)
            parts.add(text)
        }

        createAnnotations(endpoint, parameter).forEach { (text, imports) ->
            collectedImports.addAll(imports)
            parts.add(text)
        }

        if (parameter is AdditionalParameter && parameter.annotationDataType != null) {
            val additionalData = createAdditionalParameterAnnotation(parameter)
            collectedImports.addAll(additionalData.imports)
            parts.add(additionalData.text)
        }

        if (parts.isEmpty()) {
            return null
        }

        val target = StringWriter()
        target.write(parts.joinToString(" "))

        return CodeSnippet(target.toString(), collectedImports)
    }

    private fun createAdditionalParameterAnnotation(parameter: AdditionalParameter): CodeSnippet {
        val collectedImports = mutableSetOf<String>()

        val target = StringWriter()

        target.write("@${parameter.annotationDataType!!.getName()}")

        val annotationParameters = parameter.annotationDataType.getParameters()
        if (annotationParameters != null) {
            collectedImports.addAll(parameter.annotationDataType.getImports())
            collectedImports.addAll(parameter.annotationDataType.referencedImports)

            val parameters = mutableListOf<String>()

            annotationParameters.forEach {
                if (it.key == "") {
                    parameters.add(it.value.value)
                } else {
                    parameters.add("${it.key} = ${it.value.value}")
                }

                val valueImport = it.value.import
                if (valueImport != null)
                collectedImports.add(valueImport)
            }

            if (parameters.isNotEmpty()) {
                target.write("(")
                target.write(parameters.joinToString(", "))
                target.write(")")
            }
        }

        return CodeSnippet(target.toString(), collectedImports)
    }

    private fun createAnnotations(endpoint: Endpoint, parameter: Parameter): List<CodeSnippet> {
        val mappingFinder = MappingFinder(apiOptions)

        val mappingAnnotations = mutableListOf<io.openapiprocessor.core.converter.mapping.Annotation>()

        mappingAnnotations.addAll(
            mappingFinder
                .findAnnotationParameterTypeMappings(MappingFinderQuery(endpoint, parameter))
                .map { it.annotation })

        mappingAnnotations.addAll(
            mappingFinder
                .findAnnotationParameterNameTypeMapping(MappingFinderQuery(endpoint, parameter))
                .map { it.annotation })

        return mappingAnnotations
            .filter {
                apiOptions.annotationTargets.isAllowedOnParameter(it.type)
            }
            .map {
                val annotation = Annotation(it.type, it.parameters)

                val target = StringWriter()
                annotationWriter.write(target, annotation)

                CodeSnippet(target.toString(), annotation.imports + annotation.referencedImports)
            }
    }
}
