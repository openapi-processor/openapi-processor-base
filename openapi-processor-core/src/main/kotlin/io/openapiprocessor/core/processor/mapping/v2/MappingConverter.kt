/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2

import io.openapiprocessor.core.converter.mapping.*
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.processor.BadMappingException
import io.openapiprocessor.core.processor.mapping.v2.parser.Mapping as ParserMapping
import io.openapiprocessor.core.processor.mapping.v2.parser.Mapping.Kind.ANNOTATE
import io.openapiprocessor.core.processor.mapping.v2.parser.MappingType
import io.openapiprocessor.core.processor.mapping.v2.parser.antlr.parseMapping
import java.util.stream.Collectors
import io.openapiprocessor.core.processor.mapping.v2.Mapping as MappingV2

/**
 *  Converter for the type mapping from the mapping yaml. It converts the type mapping information
 *  into the format used by [io.openapiprocessor.core.converter.DataTypeConverter].
 */
class MappingConverter(val mapping: MappingV2) {

    fun convert(): List<Mapping> {
        val result = ArrayList<Mapping>()

        if(mapping.map.result != null) {
            result.add(convertResult(mapping.map.result))
        }

        if(mapping.map.resultStyle != null) {
            result.add(convertResultStyleOption(mapping.map.resultStyle))
        }

        if(mapping.map.single != null) {
            result.add(convertType("single" , mapping.map.single))
        }

        if(mapping.map.multi != null) {
            result.add(convertType("multi", mapping.map.multi))
        }

        //if(mapping.map.`null` != null) {
        //    result.add(convertNull(mapping.map.`null`))
        //}

        mapping.map.types.forEach {
            result.add(convertType(it))
        }

        mapping.map.parameters.forEach {
            result.add (convertParameter (it))
        }

        mapping.map.responses.forEach {
            result.add (convertResponse (it))
        }

        mapping.map.paths.forEach {
            result.add(convertPath (it.key, it.value))
            result.addAll(convertPathMethods(it.key, it.value))
        }

        mapping.map.extensions.forEach {
            result.add(convertExtension (it.key, it.value))
        }

        return result
    }

    private fun convertResultStyleOption(value: ResultStyle): Mapping {
        return ResultStyleOptionMapping(value)
    }

    private fun convertResult (result: String): Mapping {
        val mapping = parseMapping(result)
        return ResultTypeMapping(resolvePackageVariable(mapping.targetType!!))
    }

    private fun convertNull(value: String): Mapping {
        val split = value
                .split(" = ")
                .map { it.trim() }

        val type = split.component1()
        var init: String? = null
        if (split.size == 2)
            init = split.component2()

        return NullTypeMapping("null", type, init)
    }

    private fun convertType (from: String, to: String): Mapping {
        val mapping = parseMapping(to)
        return TypeMapping(
            from,
            null,
            resolvePackageVariable(mapping.targetType!!),
            emptyList(),
            mapping.targetTypePrimitive,
            mapping.targetTypePrimitiveArray)
    }

    private fun convertType(source: Type): Mapping {
        val (mapping, genericTypes) = parseMapping(source.type, source.generics)

        return if (mapping.kind == ANNOTATE) {
            AnnotationTypeMappingDefault(
                mapping.sourceType!!,
                mapping.sourceFormat,
                Annotation(mapping.annotationType!!, mapping.annotationParameters)
            )
        } else {
            TypeMapping(
                mapping.sourceType,
                mapping.sourceFormat,
                resolvePackageVariable(mapping.targetType!!),
                genericTypes,
                mapping.targetTypePrimitive,
                mapping.targetTypePrimitiveArray
            )
        }
    }

    private fun convertParameter(source: Parameter): Mapping {
        // parameters:
        return when (source) {
            // - name: parameter name => target type
            // - name: parameter name @ annotation
            is RequestParameter -> {
                createParameterTypeMapping(source)
            }
            // - add: parameter name => annotation target type
            is AdditionalParameter -> {
                createAddParameterTypeMapping(source)
            }
            // - type: OpenAPI type => target type
            // - type: OpenAPI type @ annotation
            is Type -> {
                convertType(source)
            }
            else -> {
                throw Exception("unknown parameter mapping $source")
            }
        }
    }

    private fun createParameterTypeMapping(source: RequestParameter): Mapping {
        val (mapping, genericTypes) = parseMapping(source.name, source.generics)

        return if (mapping.kind == ANNOTATE) {
            AnnotationNameMappingDefault(mapping.sourceType!!, Annotation(
                mapping.annotationType!!,
                mapping.annotationParameters)
                )
        } else {
            NameTypeMapping(mapping.sourceType!!, TypeMapping(
                null,
                null,
                resolvePackageVariable(mapping.targetType!!),
                genericTypes,
                mapping.targetTypePrimitive,
                mapping.targetTypePrimitiveArray
            ))
        }
    }

    private fun createAddParameterTypeMapping(source: AdditionalParameter): AddParameterTypeMapping {
        val (mapping, genericTypes) = parseMapping(source.add, source.generics)

        val typeMapping = TypeMapping(
            null,
            null,
            resolvePackageVariable(mapping.targetType!!),
            genericTypes,
            mapping.targetTypePrimitive,
            mapping.targetTypePrimitiveArray
        )

        var annotation: io.openapiprocessor.core.converter.mapping.Annotation? = null
        if(mapping.annotationType != null) {
            annotation = Annotation(
                mapping.annotationType!!, mapping.annotationParameters)
        }

        return AddParameterTypeMapping(mapping.sourceType!!, typeMapping, annotation)
    }

    private fun convertResponse(source: Response): Mapping {
        val (mapping, genericTypes) = parseMapping(source.content, source.generics)

        val typeMapping = TypeMapping(
            null,
            null,
            resolvePackageVariable(mapping.targetType!!),
            genericTypes,
            mapping.targetTypePrimitive,
            mapping.targetTypePrimitiveArray
        )

        return ContentTypeMapping (mapping.sourceType!!, typeMapping)
    }

    data class ParsedMapping(
        val mapping: ParserMapping,
        val genericTypes: List<TargetType>,
        val genericNames: List<String>)

    private fun parseMapping(mapping: String, generics: List<String>?): ParsedMapping {
        val parsedMapping = parseMapping(mapping)

        var targetGenericTypes = convertInlineGenerics(parsedMapping.targetGenericTypes2)
        if (targetGenericTypes.isEmpty() && generics != null) {
            targetGenericTypes = convertExplicitGenerics(generics)
        }

        // obsolete, names only
        val targetGenericTypeNames = parsedMapping.targetGenericTypes.toMutableList()
        if (targetGenericTypeNames.isEmpty() && generics != null) {
            targetGenericTypeNames.addAll(generics)
        }

        return ParsedMapping(parsedMapping, targetGenericTypes, targetGenericTypeNames)
    }

    private fun convertExplicitGenerics(generics: List<String>): List<TargetType> =
        generics
            .stream()
            .map {
                val genericMapping = parseMapping(it)
                TargetType(
                    resolvePackageVariable(genericMapping.targetType!!),
                    convertInlineGenerics(genericMapping.targetGenericTypes2)
                )
            }
            .collect(Collectors.toList())

    private fun convertInlineGenerics(targetGenericTypes: List<MappingType>): List<TargetType> {
        return targetGenericTypes
            .stream()
            .map {
                TargetType(
                    resolvePackageVariable(it.targetType),
                    convertInlineGenerics(it.targetGenericTypes)
                )
            }
            .collect(Collectors.toList())
    }

    private fun convertPath(path: String, source: Path): Mapping {
        val result = ArrayList<Mapping>()

        if(source.result != null) {
            result.add(convertResult(source.result))
        }

        if(source.single != null) {
            result.add(convertType("single" , source.single))
        }

        if(source.multi != null) {
            result.add(convertType("multi", source.multi))
        }

        if(source.`null` != null) {
            result.add(convertNull(source.`null`))
        }

        source.types.forEach {
            result.add(convertType(it))
        }

        source.parameters.forEach {
            result.add (convertParameter (it))
        }

        source.responses.forEach {
            result.add (convertResponse (it))
        }

        return EndpointTypeMapping(path, null, result, source.exclude)
    }

    private fun convertPathMethods(path: String, source: Path): List<Mapping> {
        val result = ArrayList<Mapping>()

        if (source.get != null) {
            result.add(convertPathMethod(path, HttpMethod.GET, source.get))
        }

        if (source.put != null) {
            result.add(convertPathMethod(path, HttpMethod.PUT, source.put))
        }

        if (source.post != null) {
            result.add(convertPathMethod(path, HttpMethod.POST, source.post))
        }

        if (source.delete != null) {
            result.add(convertPathMethod(path, HttpMethod.DELETE, source.delete))
        }

        if (source.options != null) {
            result.add(convertPathMethod(path, HttpMethod.OPTIONS, source.options))
        }

        if (source.head != null) {
            result.add(convertPathMethod(path, HttpMethod.HEAD, source.head))
        }

        if (source.patch != null) {
            result.add(convertPathMethod(path, HttpMethod.PATCH, source.patch))
        }

        if (source.trace != null) {
            result.add(convertPathMethod(path, HttpMethod.TRACE, source.trace))
        }

        return result
    }

    private fun convertPathMethod(path: String, method: HttpMethod, source: PathMethod): Mapping {
        val result = ArrayList<Mapping>()

        if(source.result != null) {
            result.add(convertResult(source.result))
        }

        if(source.single != null) {
            result.add(convertType("single" , source.single))
        }

        if(source.multi != null) {
            result.add(convertType("multi", source.multi))
        }

        if(source.`null` != null) {
            result.add(convertNull(source.`null`))
        }

        source.types.forEach {
            result.add(convertType(it))
        }

        source.parameters.forEach {
            result.add (convertParameter (it))
        }

        source.responses.forEach {
            result.add (convertResponse (it))
        }

        return EndpointTypeMapping(path, method, result, source.exclude)
    }

    private fun convertExtension(extension: String, values: List<Type>): Mapping {
        val mappings = values.map {
            createExtensionMapping(it)
        }

        return ExtensionMapping(extension, mappings)
    }

    private fun createExtensionMapping(source: Type): Mapping {
        val (mapping, genericTypes) = parseMapping(source.type, source.generics)
        if (mapping.kind != ANNOTATE) {
            throw BadMappingException(source.type)
        }

        return AnnotationNameMappingDefault(mapping.sourceType!!, Annotation(
                        mapping.annotationType!!,
                        mapping.annotationParameters))
    }

    private fun resolvePackageVariable(source: String): String {
        return source.replace("{package-name}", mapping.options.packageName)
    }
}
