/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2.parser.antlr

import io.openapiprocessor.core.processor.mapping.v2.parser.Mapping
import io.openapiprocessor.core.processor.mapping.v2.parser.MappingType


class MappingExtractor: MappingBaseListener(), Mapping {
    override var kind: Mapping.Kind? = null
    override var sourceType: String? = null
    override var sourceFormat: String? = null
    override var annotationType: String? = null
    override var annotationParameters = LinkedHashMap<String, String>()
    override var targetType: String? = null
    override var targetGenericTypes: MutableList<String> = mutableListOf()
    override var targetGenericTypes2: List<MappingType> = mutableListOf()

    private class Type(val targetType: String) {
        val targetGenericTypes: MutableList<Type> = mutableListOf()
    }

    private var typeStack = ArrayDeque<Type>()

    override fun enterType(ctx: MappingParser.TypeContext) {
        kind = Mapping.Kind.TYPE
    }

    override fun enterMap(ctx: MappingParser.MapContext) {
        kind = Mapping.Kind.MAP
    }

    override fun enterContent(ctx: MappingParser.ContentContext) {
        kind = Mapping.Kind.MAP
    }

    override fun enterAnnotate(ctx: MappingParser.AnnotateContext) {
        kind = Mapping.Kind.ANNOTATE
    }

    override fun enterPlainType(ctx: MappingParser.PlainTypeContext) {
        targetType = ctx.text
    }

    override fun enterSourceIdentifier(ctx: MappingParser.SourceIdentifierContext) {
        sourceType = ctx.text
    }

    override fun enterFormatIdentifier(ctx: MappingParser.FormatIdentifierContext) {
        sourceFormat = ctx.text
    }

    override fun enterQualifiedTargetType(ctx: MappingParser.QualifiedTargetTypeContext) {
        targetType = ctx.start.text
        typeStack.addLast(Type(targetType!!))
    }

    override fun exitQualifiedTargetType(ctx: MappingParser.QualifiedTargetTypeContext?) {
        val last = typeStack.last()

        fun x(type: Type): MappingType {
            val tt = type.targetType
            val gt = type.targetGenericTypes
                .map { x(it) }

            return MappingType(tt, gt)
        }

        targetGenericTypes2 = x (last).targetGenericTypes
    }

    override fun enterContentType(ctx: MappingParser.ContentTypeContext) {
        sourceType = ctx.start.text
    }

    override fun enterGenericParameter(ctx: MappingParser.GenericParameterContext) {
        val genericType = ctx.start.text
        targetGenericTypes.add(genericType)

        val last = typeStack.last()
        val type = Type(genericType!!)
        last.targetGenericTypes.add(type)
        typeStack.addLast(type)
    }

    override fun exitGenericParameter(ctx: MappingParser.GenericParameterContext?) {
        typeStack.removeLast()
    }

    override fun enterAnnotationType(ctx: MappingParser.AnnotationTypeContext) {
        annotationType = ctx.start.text
    }

    override fun enterAnnotationParameterUnnamed(ctx: MappingParser.AnnotationParameterUnnamedContext) {
        annotationParameters.put("", ctx.text)
    }

    override fun enterAnnotationParameterNamed(ctx: MappingParser.AnnotationParameterNamedContext) {
        annotationParameters.put(ctx.getChild(0).text, ctx.getChild(2).text)
    }
}
