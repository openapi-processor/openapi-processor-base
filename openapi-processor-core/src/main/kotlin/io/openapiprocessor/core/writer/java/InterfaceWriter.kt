/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.converter.MappingFinder
import io.openapiprocessor.core.converter.MappingFinderQuery
import io.openapiprocessor.core.framework.FrameworkAnnotations
import io.openapiprocessor.core.model.Endpoint
import io.openapiprocessor.core.model.EndpointResponse
import io.openapiprocessor.core.model.Interface
import io.openapiprocessor.core.model.parameters.AdditionalParameter
import io.openapiprocessor.core.model.parameters.Parameter
import java.io.StringWriter
import java.io.Writer

/**
 * Writer for Java interfaces.
 */
class InterfaceWriter(
    private val apiOptions: ApiOptions,
    private val generatedWriter: GeneratedWriter,
    private val methodWriter: MethodWriter,
    private val annotations: FrameworkAnnotations,
    private val validationAnnotations: BeanValidationFactory = BeanValidationFactory(apiOptions),
    private val importFilter: ImportFilter = DefaultImportFilter(),
) {
    fun write(target: Writer, itf: Interface) {
        val collectedImports = mutableSetOf<String>()

        val content = StringWriter()
        content.write ("package ${itf.getPackageName()};\n\n")
        content.write("<IMPORTS>\n\n")

        collectedImports.addAll(collectImports (itf.getPackageName(), itf.endpoints))
        collectedImports.forEach {
            content.write("import ${it};\n")
        }
        content.write("\n")

        collectedImports.addAll(generatedWriter.writeAnnotation(content))

        content.write("public interface ${itf.getInterfaceName()} {\n\n")

        itf.endpoints.forEach { ep ->
            val annotation = annotations.getAnnotation (ep.method)
            collectedImports.addAll(annotation.imports)
            collectedImports.addAll(annotation.referencedImports)

            ep.endpointResponses.forEach { er ->
                methodWriter.write(content, ep, er)
                content.write("\n")
            }
        }

        content.write ("}\n")

        val (cleanContent, oldImports) = extractImports(content)
        collectedImports.addAll(oldImports)

        val imports = createImports(itf, collectedImports)
        target.write(replaceImportsPlaceholder(cleanContent, imports))
    }

    private fun createImports(itf: Interface, collectedImports: MutableSet<String>): String =
        ImportFactory().create(itf.getPackageName(), collectedImports)

    private fun collectImports(packageName: String, endpoints: List<Endpoint>): List<String> {
        val imports: MutableSet<String> = mutableSetOf()

        endpoints.forEach { ep ->

            ep.parameters.forEach { p ->
                addImports(ep, p, imports)
            }

            ep.requestBodies.forEach { b ->
                addImports(ep, b, imports)
            }

            ep.endpointResponses.forEach { r ->
                addImports(ep, r, imports)
            }
        }

        return importFilter
            .filter(packageName, imports)
            .sorted ()
    }

    private fun addImports(endpoint: Endpoint, parameter: Parameter, imports: MutableSet<String>) {
        if (apiOptions.beanValidation) {
            val info = validationAnnotations.validate(parameter.dataType, parameter.required)
            imports.addAll(info.inout.imports)
        }

        if (parameter.withAnnotation) {
            imports.addAll(annotations.getAnnotation(parameter).imports)
        }

        if (parameter is AdditionalParameter && parameter.annotationDataType != null) {
            imports.addAll(parameter.annotationDataType.getImports())
        }

        imports.addAll(getParameterAnnotationsMappingImports(endpoint, parameter))
        imports.addAll(parameter.dataTypeImports)
    }

    private fun getParameterAnnotationsMappingImports(endpoint: Endpoint, parameter: Parameter): Set<String> {
        val mappingFinder = MappingFinder(apiOptions)
        val query = MappingFinderQuery(endpoint, parameter)

        val mappingAnnotations = mutableSetOf<String>()

        // type mappings
        mappingAnnotations.addAll(
            mappingFinder
                .findAnnotationTypeMappings(query)
                .map { it.annotation.type })

        // parameter type mappings
        mappingAnnotations.addAll(
            mappingFinder
                .findAnnotationParameterTypeMappings(query)
                .map { it.annotation.type })

        // parameter name type mappings
        mappingAnnotations.addAll(
            mappingFinder
                .findAnnotationParameterNameTypeMapping(query)
                .map { it.annotation.type })

        return mappingAnnotations
            .filter { apiOptions.annotationTargets.isAllowedOnParameter(it) }
            .toSet()
    }

    private fun addImports(endpoint: Endpoint, response: EndpointResponse, imports: MutableSet<String>) {
        val mappingFinder = MappingFinder(apiOptions)
        val query = MappingFinderQuery(endpoint)
        val resultStyle = mappingFinder.findResultStyleMapping(query)
        val resultStatus = mappingFinder.getResultStatusOption(query)

        val responseImports = response.getResponseImports(resultStyle)
        if (responseImports.isNotEmpty()) {
            imports.addAll(responseImports)
        }

        if (resultStatus && response.hasSingleResponse(resultStyle)) {
            val annotation = annotations.getAnnotation(response)
            imports.addAll(annotation.imports)
            imports.addAll(annotation.referencedImports)
        }
    }
}
