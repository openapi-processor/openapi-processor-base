/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.converter.MappingFinder
import io.openapiprocessor.core.converter.resultStyle
import io.openapiprocessor.core.framework.FrameworkAnnotations
import io.openapiprocessor.core.model.Endpoint
import io.openapiprocessor.core.model.EndpointResponse
import io.openapiprocessor.core.model.Interface
import io.openapiprocessor.core.model.parameters.AdditionalParameter
import io.openapiprocessor.core.model.parameters.Parameter
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
    private val importFilter: ImportFilter = DefaultImportFilter()
) {
    fun write(target: Writer, itf: Interface) {
        target.write ("package ${itf.getPackageName()};\n\n")

        val imports: List<String> = collectImports (itf.getPackageName(), itf.endpoints)
        imports.forEach {
            target.write("import ${it};\n")
        }
        target.write("\n")

        generatedWriter.writeUse(target)
        target.write("public interface ${itf.getInterfaceName()} {\n\n")

        itf.endpoints.forEach { ep ->
            ep.endpointResponses.forEach { er ->
                methodWriter.write(target, ep, er)
                target.write("\n")
            }
        }

        target.write ("}\n")
    }

    private fun collectImports(packageName: String, endpoints: List<Endpoint>): List<String> {
        val imports: MutableSet<String> = mutableSetOf()

        imports.addAll(generatedWriter.getImports())

        endpoints.forEach { ep ->
            val annotation = annotations.getAnnotation (ep.method)
            imports.addAll(annotation.imports)
            imports.addAll(annotation.referencedImports)

            if (ep.deprecated) {
                imports.add (java.lang.Deprecated::class.java.canonicalName)
            }

            ep.parameters.forEach { p ->
                addImports(ep, p, imports)
            }

            ep.requestBodies.forEach { b ->
                addImports(ep, b, imports)
            }

            ep.endpointResponses.forEach { r ->
                addImports(r, imports)
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

        imports.addAll(getMappingAnnotationsImports(endpoint, parameter))
        imports.addAll(parameter.dataTypeImports)
    }

    private fun getMappingAnnotationsImports(endpoint: Endpoint, parameter: Parameter): Set<String> {
        val mappingFinder = MappingFinder(apiOptions.typeMappings)

        val mappingAnnotations = mutableSetOf<String>()

        mappingAnnotations.addAll(
            mappingFinder
                .findParameterTypeAnnotations(endpoint.path, endpoint.method, parameter.dataType.getSourceName())
                .map { it.annotation.type })

        mappingAnnotations.addAll(
            mappingFinder
                .findParameterNameAnnotations(endpoint.path, endpoint.method, parameter.name)
                .map { it.annotation.type })

        return mappingAnnotations
    }

    private fun addImports(response: EndpointResponse, imports: MutableSet<String>) {
        val responseImports: MutableSet<String> = response.getResponseImports(
                    apiOptions.resultStyle).toMutableSet()

        if (responseImports.isNotEmpty()) {
            imports.addAll(responseImports)
        }
    }
}
