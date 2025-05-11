/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.openapiprocessor.core.converter.mapping.*
import io.openapiprocessor.core.converter.mapping.steps.MappingStep
import io.openapiprocessor.core.converter.mapping.steps.RootStep
import io.openapiprocessor.core.converter.mapping.steps.RootStepX
import io.openapiprocessor.core.processor.mapping.v2.ResultStyle

class MappingFinder(val options: ApiOptions) {

    private val repository = MappingRepository(
        options.globalMappings,
        options.endpointMappings,
        options.extensionMappings
    )

    // path/method
    fun getResultTypeMapping(query: MappingQuery): ResultTypeMapping? {
        val step = rootStep("looking for result type mapping of", query)
        try {
            return getResultTypeMapping(query, step)
        } finally {
            step.log()
        }
    }

    private fun getResultTypeMapping(query: MappingQuery, step: MappingStep): ResultTypeMapping? {
        val epMapping = repository.getEndpointResultTypeMapping(query, step)
        if (epMapping != null) {
            return epMapping
        }

        val gMapping = repository.getGlobalResultTypeMapping(step)
        if(gMapping != null) {
            return gMapping
        }

        return null
    }

    fun findResultStyleMapping(query: MappingQuery): ResultStyle {
        val step = rootStep("looking for result style mapping of", query)
        try {
            return findResultStyleMapping(query, step)
        } finally {
            step.log()
        }
    }

    private fun findResultStyleMapping(query: MappingQuery, step: MappingStep): ResultStyle {
        val epMapping = repository.getEndpointResultStyleMapping(query, step)
        if (epMapping != null) {
            return epMapping
        }

        val gMapping = repository.getGlobalResultStyleMapping(step)
        if(gMapping != null) {
            return gMapping
        }

        return ResultStyle.SUCCESS
    }

    fun getResultStatusOption(query: MappingQuery): Boolean {
        val step = rootStep("looking for result status mapping of", query)
        try {
            return getResultStatusOption(query, step)
        } finally {
            step.log()
        }
    }

    private fun getResultStatusOption(query: MappingQuery, step: MappingStep): Boolean {
        val epMapping = repository.getEndpointResultStatusOption(query, step)
        if (epMapping != null) {
            return epMapping
        }

        val gMapping = repository.getGlobalResultStatusOption(step)
        if(gMapping != null) {
            return gMapping
        }

        return false
    }

    // path/method
    fun getSingleTypeMapping(query: MappingQuery): TypeMapping? {
        val step = rootStep("looking for single type style mapping of", query)
        try {
            return getSingleTypeMapping(query, step)
        } finally {
            step.log()
        }
    }

    private fun getSingleTypeMapping(query: MappingQuery, step: MappingStep): TypeMapping? {
        val epMapping = repository.getEndpointSingleTypeMapping(query, step)
        if (epMapping != null) {
            return epMapping
        }

        val gMapping = repository.getGlobalSingleTypeMapping(step)
        if(gMapping != null) {
            return gMapping
        }

        return null
    }

    // path/method
    fun getMultiTypeMapping(query: MappingQuery): TypeMapping? {
        val step = rootStep("looking for multi type mapping of", query)
        try {
            return getMultiTypeMapping(query, step)
        } finally {
            step.log()
        }
    }

    private fun getMultiTypeMapping(query: MappingQuery, step: MappingStep): TypeMapping? {
        val epMapping = repository.getEndpointMultiTypeMapping(query, step)
        if (epMapping != null) {
            return epMapping
        }

        val gMapping = repository.getGlobalMultiTypeMapping(step)
        if(gMapping != null) {
            return gMapping
        }

        return null
    }

    /**
     * find any type mapping. The mappings are checked in the following order and the first match wins:
     *
     * - endpoint parameter type
     * - endpoint parameter name
     * - endpoint response type
     * - endpoint type
     * - global parameter type
     * - global parameter name
     * - global response type
     * - global type
     */
    fun findAnyTypeMapping(query: MappingQuery): TypeMapping? {
        val step = rootStep("looking for any type mapping of", query)
        try {
            return findAnyTypeMapping(query, step)
        } finally {
            step.log()
        }
    }

    private fun findAnyTypeMapping(query: MappingQuery, step: MappingStep): TypeMapping? {
        val eppMapping = repository.findEndpointParameterTypeMapping(query, step)
        if (eppMapping != null) {
            return eppMapping
        }

        val eppnMapping = repository.findEndpointParameterNameTypeMapping(query, step)
        if (eppnMapping != null) {
            return eppnMapping.mapping
        }

        val eprMapping = repository.findEndpointContentTypeMapping(query, step)
        if (eprMapping != null) {
            return eprMapping.mapping
        }

        val eptMapping = repository.findEndpointTypeMapping(query, step)
        if (eptMapping != null) {
            return eptMapping
        }

        val gpMapping = repository.findGlobalParameterTypeMapping(query, step)
        if (gpMapping != null) {
            return gpMapping
        }

        val gpnMapping = repository.findGlobalParameterNameTypeMapping(query, step)
        if (gpnMapping != null) {
            return gpnMapping.mapping
        }

        val grMapping = repository.findGlobalContentTypeMapping(query, step)
        if (grMapping != null) {
            return grMapping.mapping
        }

        val gtMapping = repository.findGlobalTypeMapping(query, step)
        if (gtMapping != null) {
            return gtMapping
        }

        return null
    }

    // path/method/name/format/type
    fun findTypeMapping(query: MappingQuery): TypeMapping? {
        val step = rootStep("looking for type mapping of", query)
        try {
            return findTypeMapping(query, step)
        } finally {
            step.log()
        }
    }

    private fun findTypeMapping(query: MappingQuery, step: MappingStep): TypeMapping? {
        val epMapping = repository.findEndpointTypeMapping(query, step)
        if (epMapping != null) {
            return epMapping
        }

        val gMapping = repository.findGlobalTypeMapping(query, step)
        if(gMapping != null) {
            return gMapping
        }

        return null
    }

    fun findAnnotationTypeMappings(sourceName: String, allowObject: Boolean = false): List<AnnotationTypeMapping> {
        val (type, format) = splitTypeName(sourceName)
        return findAnnotationTypeMappings(
            MappingFinderQuery(
                type = type,
                format = format,
                allowObject = allowObject)
        )
    }

    fun findAnnotationTypeMappings(query: MappingQuery): List<AnnotationTypeMapping> {
        val step = rootStep("looking for annotation type mapping of", query)
        try {
            return findAnnotationTypeMappings(query, step)
        } finally {
            step.log()
        }
    }

    private fun findAnnotationTypeMappings(query: MappingQuery, step: MappingStep): List<AnnotationTypeMapping> {
        val epMapping = repository.findEndpointAnnotationTypeMapping(query, step)
        if (epMapping.isNotEmpty()) {
            return epMapping
        }

        return repository.findGlobalAnnotationTypeMapping(query, step)
    }

    fun findAnnotationSchemaTypeMappings(sourceName: String): List<AnnotationTypeMapping> {
        val (type, format) = splitTypeName(sourceName)
        return findAnnotationSchemaTypeMappings(
            MappingFinderQuery(
                type = type,
                format = format)
        )
    }

    private fun findAnnotationSchemaTypeMappings(query: MappingQuery): List<AnnotationTypeMapping> {
        val step = rootStep("looking for annotation schema type mapping of", query)
        try {
            return findAnnotationSchemaTypeMappings(query, step)
        } finally {
            step.log()
        }
    }

    private fun findAnnotationSchemaTypeMappings(query: MappingQuery, step: MappingStep): List<AnnotationTypeMapping> {
        return repository.findGlobalAnnotationSchemaTypeMapping(query, step)
    }

    fun findParameterTypeMapping(query: MappingQuery): TypeMapping? {
        val step = rootStep("looking for parameter type mapping of", query)
        try {
            return findParameterTypeMapping(query, step)
        } finally {
            step.log()
        }
    }

    private fun findParameterTypeMapping(query: MappingQuery, step: MappingStep): TypeMapping? {
        val epMapping = repository.findEndpointParameterTypeMapping(query, step)
        if (epMapping != null) {
            return epMapping
        }

        val gMapping = repository.findGlobalParameterTypeMapping(query, step)
        if (gMapping != null) {
            return gMapping
        }

        return null
    }

    fun findAnnotationParameterTypeMappings(query: MappingQuery): List<AnnotationTypeMapping> {
        val step = rootStep("looking for annotation parameter type mapping of", query)
        try {
            return findAnnotationParameterTypeMappings(query, step)
        } finally {
            step.log()
        }
    }

    private fun findAnnotationParameterTypeMappings(query: MappingQuery, step: MappingStep): List<AnnotationTypeMapping> {
        val eppMapping = repository.findEndpointAnnotationParameterTypeMappings(query, step)
        if (eppMapping.isNotEmpty()) {
            return eppMapping
        }

        val epMapping = repository.findEndpointAnnotationTypeMapping(query, step)
        if (epMapping.isNotEmpty()) {
            return epMapping
        }

        val pMapping = repository.findGlobalAnnotationParameterTypeMappings(query, step)
        if (pMapping.isNotEmpty()) {
            return pMapping
        }

        return repository.findGlobalAnnotationTypeMapping(query, step)
    }

    fun findParameterNameTypeMapping(query: MappingQuery): NameTypeMapping? {
        val step = rootStep("looking for parameter name type mapping of", query)
        try {
            return findParameterNameTypeMapping(query, step)
        } finally {
            step.log()
        }
    }

    private fun findParameterNameTypeMapping(query: MappingQuery, step: MappingStep): NameTypeMapping? {
        val epMapping = repository.findEndpointParameterNameTypeMapping(query, step)
        if (epMapping != null) {
            return epMapping
        }

        val gMapping = repository.findGlobalParameterNameTypeMapping(query, step)
        if(gMapping != null) {
            return gMapping
        }

        return null
    }

    fun findAnnotationParameterNameTypeMapping(query: MappingQuery): List<AnnotationNameMapping> {
        val step = rootStep("looking for annotation parameter name type mapping of", query)
        try {
            return findAnnotationParameterNameTypeMapping(query, step)
        } finally {
            step.log()
        }
    }

    private fun findAnnotationParameterNameTypeMapping(query: MappingQuery, step: MappingStep): List<AnnotationNameMapping> {
        val epMapping = repository.findEndpointAnnotationParameterNameTypeMapping(query, step)
        if (epMapping.isNotEmpty()) {
            return epMapping
        }

        return repository.findGlobalAnnotationParameterNameTypeMapping(query, step)
    }

    fun findAddParameterTypeMappings(query: MappingQuery): List<AddParameterTypeMapping> {
        val step = rootStep("looking for additional parameter type mapping of", query)
        try {
            return findAddParameterTypeMappings(query, step)
        } finally {
            step.log()
        }
    }

    private fun findAddParameterTypeMappings(query: MappingQuery, step: MappingStep): List<AddParameterTypeMapping> {
        val epMapping = repository.findEndpointAddParameterTypeMappings(query, step)
        if (epMapping.isNotEmpty()) {
            return epMapping
        }

        return repository.findGlobalAddParameterTypeMappings(step)
    }

    fun findDropParameterTypeMappings(query: MappingQuery): List<DropParameterTypeMapping> {
        val step = rootStep("looking for drop parameter type mapping of", query)
        try {
            return findDropParameterTypeMappings(query, step)
        } finally {
            step.log()
        }
    }

    private fun findDropParameterTypeMappings(query: MappingQuery, step: MappingStep): List<DropParameterTypeMapping> {
        val epMapping = repository.findEndpointDropParameterTypeMappings(query, step)
        if (epMapping.isNotEmpty()) {
            return epMapping
        }

        return repository.findGlobalDropParameterTypeMappings(step)
    }

    fun findContentTypeMapping(query: MappingQuery): ContentTypeMapping? {
        val step = rootStep("looking for content type type mapping of", query)
        try {
            return findContentTypeMapping(query, step)
        } finally {
            step.log()
        }
    }

    private fun findContentTypeMapping(query: MappingQuery, step: MappingStep): ContentTypeMapping? {
        val epMapping = repository.findEndpointContentTypeMapping(query, step)
        if (epMapping != null) {
            return epMapping
        }

        val gMapping = repository.findGlobalContentTypeMapping(query, step)
        if(gMapping != null) {
            return gMapping
        }

        return null
    }

    fun findNullTypeMapping(query: MappingQuery): NullTypeMapping? {
        val step = rootStep("looking for null type mapping of", query)
        try {
            return findNullTypeMapping(query, step)
        } finally {
            step.log()
        }
    }

    private fun findNullTypeMapping(query: MappingQuery, step: MappingStep): NullTypeMapping? {
        return repository.getEndpointNullTypeMapping(query, step)
    }

    fun findExtensionAnnotations(extension: String, vararg values: String): List<AnnotationNameMapping> {
        return findExtensionAnnotations(extension, values.asList())
    }

    fun findExtensionAnnotations(extension: String, values: List<String>): List<AnnotationNameMapping> {
        val step = rootStep("looking for annotation extension type mapping", extension)
        try {
            return findExtensionAnnotations(extension, values, step)
        } finally {
            step.log()
        }
    }

    private fun findExtensionAnnotations(extension: String, values: List<String>, step: MappingStep): List<AnnotationNameMapping> {
        return values
            .map { repository.findExtensionAnnotations(extension, it, step) }
            .flatten()
    }

    fun isEndpointExcluded(query: MappingQuery): Boolean {
        val step = rootStep("looking for exclude mapping of", query)
        try {
            return isEndpointExcluded(query, step)
        } finally {
            step.log()
        }
    }

    private fun isEndpointExcluded(query: MappingQuery, step: MappingStep): Boolean {
        return repository.isEndpointExcluded(query, step)
    }

    private fun rootStep(message: String, query: MappingQuery): MappingStep {
        return RootStep(message, query)
    }

    private fun rootStep(message: String, extension: String): MappingStep {
        return RootStepX(message, extension)
    }
}
