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
import io.openapiprocessor.core.processor.mapping.v2.ResultStyle
import io.openapiprocessor.core.support.LF
import io.openapiprocessor.core.support.capitalizeFirstChar
import io.openapiprocessor.core.support.indent
import io.openapiprocessor.core.writer.Identifier
import io.openapiprocessor.core.writer.java.MappingAnnotationWriter as CoreMappingAnnotationWriter
import io.openapiprocessor.core.writer.java.ParameterAnnotationWriter as CoreParameterAnnotationWriter
import io.openapiprocessor.core.writer.java.StatusAnnotationWriter as CoreStatusAnnotationWriter
import java.io.StringWriter
import java.io.Writer

/**
 * Writer for Java interface methods, i.e. endpoints.
 */
open class MethodWriter(
    private val apiOptions: ApiOptions,
    private val identifier: Identifier,
    private val statusAnnotationWriter: CoreStatusAnnotationWriter,
    private val mappingAnnotationWriter: CoreMappingAnnotationWriter,
    private val parameterAnnotationWriter: CoreParameterAnnotationWriter,
    private val beanValidationFactory: BeanValidationFactory,
    private val javadocFactory: JavaDocFactory = JavaDocFactory(identifier)
) {
    private val annotationWriter = AnnotationWriter()

    fun write(target: Writer, endpoint: Endpoint, endpointResponse: EndpointResponse) {
        if (apiOptions.javadoc) {
            target.write(createJavadoc(endpoint, endpointResponse).indent())
            target.write(LF)
        }

        if (endpoint.deprecated) {
            target.write(createDeprecated().indent())
            target.write(LF)
        }

        if (shouldAddStatus(endpointResponse, endpoint)) {
            val status = createStatus(endpoint, endpointResponse)
            target.write(status.indent())
            target.write(LF)
        }

        val annotations = createMappingAnnotations(endpoint, endpointResponse)
        annotations.forEach {
            target.write(it.indent())
            target.write(LF)
        }

        target.write(createResult(endpoint, endpointResponse).indent())
        target.write(" ")
        target.write(createMethodName(endpoint, endpointResponse))
        target.write("(")
        val parameters = createParameters(endpoint)
        parameters.forEachIndexed { index, it ->
            target.write(formatParameter(index, it, parameters.lastIndex))
        }
        target.write(");")
        target.write(LF)
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

    private fun createDeprecated(): String {
        return DEPRECATED.annotationName
    }

    private fun shouldAddStatus(endpointResponse: EndpointResponse, endpoint: Endpoint): Boolean {
        return MappingFinder(apiOptions).getResultStatusOption(MappingFinderQuery(endpoint))
            && endpointResponse.hasSingleResponse(getResultStyle(endpoint))
    }

    private fun createStatus(endpoint: Endpoint, endpointResponse: EndpointResponse): String {
        val annotation = StringWriter()
        statusAnnotationWriter.write(annotation, endpoint, endpointResponse)
        return annotation.toString ()
    }

    private fun createMappingAnnotations(endpoint: Endpoint, endpointResponse: EndpointResponse): List<String> {
        return mappingAnnotationWriter.create(endpoint, endpointResponse)
    }

    private fun createResult(endpoint: Endpoint, endpointResponse: EndpointResponse): String {
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

    private fun createParameters(endpoint: Endpoint): List<String> {
        val parameters = mutableListOf<String>()

        endpoint.parameters.forEach {

            val dataTypeValue = if (apiOptions.beanValidation) {
                val info = beanValidationFactory.validate(it.dataType, it.required)
                info.inout.dataTypeValue
            } else {
                it.dataType.getTypeName()
            }

            val parameter = "${createParameterAnnotation(endpoint, it)} $dataTypeValue ${identifier.toIdentifier (it.name)}"
                 .trim()

            parameters.add(parameter)
        }

        if (endpoint.requestBodies.isNotEmpty()) {
            val body = endpoint.getRequestBody()

            val dataTypeValue = if (apiOptions.beanValidation) {
                val info = beanValidationFactory.validate(body.dataType, body.required)
                info.inout.dataTypeValue
            } else {
                body.dataType.getTypeName()
            }

            val param = "${createParameterAnnotation(endpoint, body)} $dataTypeValue ${identifier.toIdentifier(body.name)}"
            parameters.add (param.trim())
        }

        return parameters
    }

    private fun createParameterAnnotation(endpoint: Endpoint, parameter: Parameter): String {
        val target = StringWriter()
        if (parameter.deprecated) {
            target.write("@Deprecated ")
        }

        parameterAnnotationWriter.write(target, parameter)
        addAnnotations(endpoint, parameter, target)

        if (parameter is AdditionalParameter && parameter.annotationDataType != null) {
            target.write(" @${parameter.annotationDataType.getName()}")

            val annotationParameters = parameter.annotationDataType.getParameters()
            if (annotationParameters != null) {
                val parameters = mutableListOf<String>()

                annotationParameters.forEach {
                    if (it.key == "") {
                        parameters.add(it.value.value)
                    } else {
                        parameters.add("${it.key} = ${it.value.value}")
                    }
                }

                if (parameters.isNotEmpty()) {
                    target.write("(")
                    target.write(parameters.joinToString(", "))
                    target.write(")")
                }
            }
        }

        return target.toString()
    }

    private fun addAnnotations(endpoint: Endpoint, parameter: Parameter, target: StringWriter) {
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

        mappingAnnotations.forEach {
            target.write(" ")
            annotationWriter.write(target, Annotation(it.type, it.parameters))
        }
    }
}
