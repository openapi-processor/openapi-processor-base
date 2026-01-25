/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2

import io.openapiprocessor.core.converter.mapping.*
import io.openapiprocessor.core.converter.mapping.Annotation
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.processor.BadMappingException
import io.openapiprocessor.core.processor.mapping.v2.parser.Mapping.Kind.ANNOTATE
import io.openapiprocessor.core.processor.mapping.v2.parser.Mapping.Kind.IMPLEMENT
import io.openapiprocessor.core.processor.mapping.v2.parser.MappingType
import io.openapiprocessor.core.processor.mapping.v2.parser.antlr.parseMapping
import java.util.stream.Collectors
import io.openapiprocessor.core.processor.mapping.v2.Map as MapV2
import io.openapiprocessor.core.processor.mapping.v2.Mapping as MappingV2
import io.openapiprocessor.core.processor.mapping.v2.parser.Mapping as ParserMapping

/**
 *  Converter for the type mapping from the mapping YAML. It converts the type mapping information
 *  into the format used by [io.openapiprocessor.core.converter.DataTypeConverter].
 */
class MappingConverter(val mapping: MappingV2) {

    fun convert(): MappingData {
        return MappingData(
            convertGlobalMappings(mapping.map),
            convertPathsMappings(mapping.map.paths),
            convertExtensionMappings(mapping.map.extensions))
    }

    private fun convertGlobalMappings(map: MapV2): Mappings {
        var bodyStyle: BodyStyle? = null
        var resultTypeMapping: ResultTypeMapping? = null
        var resultStyle: ResultStyle? = null
        var resultStatus: Boolean? = null
        var singleTypeMapping: TypeMapping? = null
        var multiTypeMapping: TypeMapping? = null
        val typeMappings = mutableListOf<Mapping>()
        val schemaTypeMappings = mutableListOf<Mapping>()
        val parameterTypeMappings = mutableListOf<Mapping>()
        val responseTypeMappings = mutableListOf<Mapping>()

        if (map.bodyStyle != null) {
            bodyStyle = map.bodyStyle
        }

        if (map.result != null) {
            resultTypeMapping = convertResult(map.result)
        }

        if (map.resultStyle != null) {
            resultStyle = map.resultStyle
        }

        if (map.resultStatus != null) {
            resultStatus = map.resultStatus
        }

        if(map.single != null) {
            singleTypeMapping = convertType("single" , map.single)
        }

        if(map.multi != null) {
            multiTypeMapping = convertType("multi", map.multi)
        }

        map.types.forEach {
            typeMappings.add(convertType(it))
        }

        map.schemas.forEach {
            schemaTypeMappings.add(convertType(it))
        }

        map.parameters.forEach {
            parameterTypeMappings.add (convertParameter (it))
        }

        map.responses.forEach {
            responseTypeMappings.add (convertResponse (it))
        }

        return Mappings(
            bodyStyle,
            resultTypeMapping,
            resultStyle,
            resultStatus,
            singleTypeMapping,
            multiTypeMapping,
            null,
            TypeMappings(typeMappings),
            TypeMappings(schemaTypeMappings),
            TypeMappings(parameterTypeMappings),
            TypeMappings(responseTypeMappings))
    }

    private fun convertPathsMappings(paths: Map<String, Path>): Map<String, EndpointMappings> {
        val endpointMappings = HashMap<String, EndpointMappings>()

        paths.forEach {
            val path: Path = it.value

            val pathMappings = convertPath(path)
            val methodMappings = HashMap<HttpMethod, Mappings>()

            when {
                path.get != null -> {
                    methodMappings[HttpMethod.GET] = convertPathMethod(path.get)
                }
                path.put != null -> {
                    methodMappings[HttpMethod.PUT] = convertPathMethod(path.put)
                }
                path.post != null -> {
                    methodMappings[HttpMethod.POST] = convertPathMethod(path.post)
                }
                path.delete != null -> {
                    methodMappings[HttpMethod.DELETE] = convertPathMethod(path.delete)
                }
                path.options != null -> {
                    methodMappings[HttpMethod.OPTIONS] = convertPathMethod(path.options)
                }
                path.head != null -> {
                    methodMappings[HttpMethod.HEAD] = convertPathMethod(path.head)
                }
                path.patch != null -> {
                    methodMappings[HttpMethod.PATCH] = convertPathMethod(path.patch)
                }
                path.trace != null -> {
                    methodMappings[HttpMethod.TRACE] = convertPathMethod(path.trace)
                }
            }

            endpointMappings[it.key] = EndpointMappings(pathMappings, methodMappings)
        }

        return endpointMappings
    }

    private fun convertExtensionMappings(extensions: Map<String, List<Type>>): Map<String, ExtensionMappings> {
        val extensionMappings = LinkedHashMap<String, ExtensionMappings>()

        extensions.forEach { ext ->
            val valueMappings = mutableMapOf<String /* extension value */, MutableList<AnnotationNameMapping>>()
            ext.value.forEach { value ->
                val xMapping = createExtensionMapping(value)
                valueMappings.computeIfAbsent(xMapping.name) { mutableListOf() }.add(xMapping)
            }
            extensionMappings[ext.key] = ExtensionMappings(valueMappings)
        }

        return extensionMappings
    }

    private fun convertResult (result: String): ResultTypeMapping {
        val mapping = parseMapping(result)

        var targetTypeName = mapping.targetType
        if (targetTypeName == null) {
            targetTypeName = mapping.sourceType
        }

        return ResultTypeMapping(
            mapping.sourceType,
            resolvePackageVariable(targetTypeName!!),
            convertInlineGenerics(mapping.targetGenericTypes2))
    }

    private fun convertNull(value: String): NullTypeMapping {
        val split = value
                .split(" = ")
                .map { it.trim() }

        val type = split.component1()
        var init: String? = null
        if (split.size == 2)
            init = split.component2()

        return NullTypeMapping("null", type, init)
    }

    private fun convertType (from: String, to: String): TypeMapping {
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

        return when (mapping.kind) {
            ANNOTATE -> {
                AnnotationTypeMappingDefault(
                    mapping.sourceType!!,
                    mapping.sourceFormat,
                    Annotation(mapping.annotationType!!, mapping.annotationParameters))
            }
            IMPLEMENT -> {
                InterfaceTypeMapping(
                    mapping.sourceType!!,
                    mapping.targetType!!,
                    genericTypes)
            }
            else -> {
                TypeMapping(
                    mapping.sourceType,
                    mapping.sourceFormat,
                    resolvePackageVariable(mapping.targetType!!),
                    genericTypes,
                    mapping.targetTypePrimitive,
                    mapping.targetTypePrimitiveArray)
            }
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
            // - drop: parameter name
            is UnnecessaryParameter -> {
                createDropParameterTypeMapping(source)
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
            AnnotationNameTypeMappingDefault(mapping.sourceType!!, Annotation(
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

        var annotation: Annotation? = null
        if(mapping.annotationType != null) {
            annotation = Annotation(mapping.annotationType!!, mapping.annotationParameters)
        }

        return AddParameterTypeMapping(mapping.sourceType!!, typeMapping, annotation)
    }

    private fun createDropParameterTypeMapping(source: UnnecessaryParameter): DropParameterTypeMapping {
        val (mapping, _) = parseMapping(source.drop, emptyList())

        return DropParameterTypeMapping(mapping.sourceType!!)
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

    private fun convertPath(source: Path): Mappings {
        var bodyStyle: BodyStyle? = null
        var resultTypeMapping: ResultTypeMapping? = null
        var resultStyle: ResultStyle? = null
        var resultStatus: Boolean? = null
        var singleTypeMapping: TypeMapping? = null
        var multiTypeMapping: TypeMapping? = null
        var nullTypeMapping: NullTypeMapping? = null
        val typeMappings = mutableListOf<Mapping>()
        val schemaTypeMappings = mutableListOf<Mapping>()
        val parameterTypeMappings = mutableListOf<Mapping>()
        val responseTypeMappings = mutableListOf<Mapping>()

        if (source.bodyStyle != null) {
            bodyStyle = source.bodyStyle
        }

        if (source.result != null) {
            resultTypeMapping = convertResult(source.result)
        }

        if (source.resultStyle != null) {
            resultStyle = source.resultStyle
        }

        if (source.resultStatus != null) {
            resultStatus = source.resultStatus
        }

        if(source.single != null) {
            singleTypeMapping = convertType("single" , source.single)
        }

        if(source.multi != null) {
            multiTypeMapping = convertType("multi", source.multi)
        }

        if(source.`null` != null) {
            nullTypeMapping = convertNull(source.`null`)
        }

        source.types.forEach {
            typeMappings.add(convertType(it))
        }

        source.schemas.forEach {
            schemaTypeMappings.add(convertType(it))
        }

        source.parameters.forEach {
            parameterTypeMappings.add (convertParameter (it))
        }

        source.responses.forEach {
            responseTypeMappings.add (convertResponse (it))
        }

        return Mappings(
            bodyStyle,
            resultTypeMapping,
            resultStyle,
            resultStatus,
            singleTypeMapping,
            multiTypeMapping,
            nullTypeMapping,
            TypeMappings(typeMappings),
            TypeMappings(schemaTypeMappings),
            TypeMappings(parameterTypeMappings),
            TypeMappings(responseTypeMappings),
            source.exclude)
    }

    private fun convertPathMethod(source: PathMethod): Mappings {
        var bodyStyle: BodyStyle? = null
        var resultTypeMapping: ResultTypeMapping? = null
        var resultStyle: ResultStyle? = null
        var resultStatus: Boolean? = null
        var singleTypeMapping: TypeMapping? = null
        var multiTypeMapping: TypeMapping? = null
        var nullTypeMapping: NullTypeMapping? = null
        val typeMappings = mutableListOf<Mapping>()
        val schemaTypeMappings = mutableListOf<Mapping>()
        val parameterTypeMappings = mutableListOf<Mapping>()
        val responseTypeMappings = mutableListOf<Mapping>()

        if (source.bodyStyle != null) {
            bodyStyle = source.bodyStyle
        }

        if (source.result != null) {
            resultTypeMapping = convertResult(source.result)
        }

         if (source.resultStyle != null) {
            resultStyle = source.resultStyle
         }

        if (source.resultStatus != null) {
           resultStatus = source.resultStatus
        }

        if(source.single != null) {
            singleTypeMapping = convertType("single" , source.single)
        }

        if(source.multi != null) {
            multiTypeMapping = convertType("multi", source.multi)
        }

        if(source.`null` != null) {
            nullTypeMapping = convertNull(source.`null`)
        }

        source.types.forEach {
            typeMappings.add(convertType(it))
        }

        source.schemas.forEach {
            schemaTypeMappings.add(convertType(it))
        }

        source.parameters.forEach {
            parameterTypeMappings.add (convertParameter (it))
        }

        source.responses.forEach {
            responseTypeMappings.add (convertResponse (it))
        }

        return Mappings(
            bodyStyle,
            resultTypeMapping,
            resultStyle,
            resultStatus,
            singleTypeMapping,
            multiTypeMapping,
            nullTypeMapping,
            TypeMappings(typeMappings),
            TypeMappings(schemaTypeMappings),
            TypeMappings(parameterTypeMappings),
            TypeMappings(responseTypeMappings),
            source.exclude)
    }

    private fun createExtensionMapping(source: Type): AnnotationNameMapping {
        val (mapping, _) = parseMapping(source.type, source.generics)
        if (mapping.kind != ANNOTATE) {
            throw BadMappingException(source.type)
        }

        return AnnotationNameTypeMappingDefault(mapping.sourceType!!, Annotation(
                        mapping.annotationType!!,
                        mapping.annotationParameters))
    }

    private fun resolvePackageVariable(source: String): String {
        return source.replace("{package-name}", mapping.options.packageName)
    }
}
