/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.openapiprocessor.core.converter.mapping.steps.MappingStepBase
import io.openapiprocessor.core.converter.mapping.steps.Target
import io.openapiprocessor.core.converter.options.TargetDirLayout
import io.openapiprocessor.core.processor.MappingConverter
import io.openapiprocessor.core.processor.MappingReader
import io.openapiprocessor.core.processor.mapping.MappingVersion
import io.openapiprocessor.core.processor.mapping.v2.Options
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import io.openapiprocessor.core.processor.mapping.v1.Mapping as MappingV1
import io.openapiprocessor.core.processor.mapping.v2.Mapping as MappingV2

/**
 * creates [ApiOptions] from processor options and mapping.yaml.
 */
class OptionsConverter(private val checkObsoleteProcessorOptions: Boolean = false) {
    var log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    fun convertOptions(processorOptions: Map<String, Any>): ApiOptions {
        val options = ApiOptions()

        checkDeprecatedMapOptions(processorOptions, options)

        if (processorOptions.containsKey("targetDir")) {
            options.targetDir = processorOptions["targetDir"].toString()
        } else {
            log.warn("required option 'targetDir' is missing!")
        }

        if (processorOptions.containsKey("mapping")) {
            readMapping(processorOptions["mapping"].toString(), options)
        } else {
            log.warn("required option 'mapping' is missing!")
        }

        return options
    }

    private fun readMapping(mappingSource: String, options: ApiOptions) {
        try {
            val mapping: MappingVersion? = MappingReader().read(mappingSource)
            if (mapping == null) {
                log.warn("missing 'mapping.yaml' configuration!")
                return
            }

            when (mapping) {
                is MappingV1 -> {
                    log.error("please update the mapping.yaml")
                    log.error("this project is using an old mapping format that is no longer supported.")
                    log.error(" - 2024.4 is the last version that supports this mapping format.")
                }

                is MappingV2 -> {
                    with(mapping.options) {
                        options.targetDirOptions.clear = targetDir.clear ?: clearTargetDir
                        options.targetDirOptions.layout = TargetDirLayout.from(targetDir.layout)
                    }

                    options.packageName = mapping.options.packageName
                    with(mapping.options.packageNames) {
                        options.packageOptions.base = base
                        options.packageOptions.location = location

                        // sync base package name
                        if (options.packageOptions.base != null) {
                            options.packageName = options.packageOptions.base!!
                        }
                        else {
                            options.packageOptions.base = mapping.options.packageName
                        }
                    }

                    options.modelType = mapping.options.modelType
                    options.modelAccessors = mapping.options.modelAccessors
                    options.enumType = mapping.options.enumType
                    options.modelNameSuffix = mapping.options.modelNameSuffix

                    val (enable, format) = checkBeanValidation(mapping.options)
                    options.beanValidation = enable
                    options.beanValidationFormat = format

                    val (enablePathPrefix, pathPrefixServerIndex) = checkServerUrl(mapping.options)
                    options.basePathOptions.enabled = enablePathPrefix
                    options.basePathOptions.serverUrl = pathPrefixServerIndex
                    options.basePathOptions.propertiesName = mapping.options.basePath.propertiesName
                    // to write the base path resource, we need standard layout
                    if (options.basePathOptions.enabled && !options.targetDirOptions.standardLayout) {
                        log.warn("base-path is enabled, forcing target-dir.layout = standard")
                        options.targetDirOptions.layout = TargetDirLayout.STANDARD
                    }

                    options.javadoc = mapping.options.javadoc
                    options.oneOfInterface = mapping.options.oneOfInterface
                    options.responseInterface = mapping.options.responseInterface

                    val (enableFormatCode, formatCodeFormatter) = checkFormatter(mapping.options)
                    options.formatCode = enableFormatCode
                    options.formatCodeFormatter = formatCodeFormatter

                    options.generatedAnnotation = mapping.options.generatedAnnotation
                    options.generatedDate = mapping.options.generatedDate
                    options.jsonPropertyAnnotation = JsonPropertyAnnotationMode.findBy(
                        mapping.options.jsonPropertyAnnotation)

                    with(mapping.compatibility) {
                        options.beanValidationValidOnReactive = beanValidationValidOnReactive
                        options.identifierWordBreakFromDigitToLetter = identifierWordBreakFromDigitToLetter
                        options.identifierPrefixInvalidEnumStart = identifierPrefixInvalidEnumStart
                    }

                    options.beanValidationAditionalSupportedTypes = mapping.beanValidation

                    val mappings = MappingConverter().convert(mapping)
                    options.globalMappings = mappings.globalMappings
                    options.endpointMappings = mappings.endpointMappings
                    options.extensionMappings = mappings.extensionMappings

                    if (options.packageName == "io.openapiprocessor.generated") {
                        log.warn("is 'options.package-name' set in mapping? found default: '{}'.", options.packageName)
                    }

                    with(mapping) {
                        options.loggingOptions.mapping = logging.mapping
                        if (logging.mappingTarget == "stdout") {
                            options.loggingOptions.mappingTarget = Target.STDOUT
                        }
                        MappingStepBase.options.set(options.loggingOptions)
                    }
                }
            }
        } catch (t: Throwable) {
            throw InvalidMappingException("failed to parse 'mapping.yaml' configuration!", t)
        }
    }

    private fun checkBeanValidation(options: Options): Pair<Boolean, String?> {
        return when (options.beanValidation) {
            "true" -> Pair(true, "javax")
            "javax" -> Pair(true, "javax")
            "jakarta" -> Pair(true, "jakarta")
            else -> Pair(false, null)
        }
    }

    private fun checkFormatter(options: Options): Pair<Boolean, String?> {
        return when (options.formatCode) {
            "true" -> Pair(true, "google")
            "google" -> Pair(true, "google")
            "eclipse" -> Pair(true, "eclipse")
            else -> Pair(false, null)
        }
    }

    private fun checkServerUrl(options: Options): Pair<Boolean, Int?> {
        return when (options.basePath.serverUrl) {
            "false" -> Pair(false, null)
            "true" -> Pair(true, 0)
            else -> Pair(true, options.basePath.serverUrl.toInt())
        }
    }

    private fun checkDeprecatedMapOptions(processorOptions: Map<String, *>, options: ApiOptions) {
        if (!checkObsoleteProcessorOptions)
            return

        if (processorOptions.containsKey("packageName")) {
            options.packageName = processorOptions["packageName"].toString()
            log.warn("'options.package-name' should be set in the mapping yaml!")
        }

        if (processorOptions.containsKey("beanValidation")) {
            options.beanValidation = processorOptions["beanValidation"] as Boolean
            log.warn("options.bean-validation' should be set in the mapping yaml!")
        }

        if (processorOptions.containsKey("typeMappings")) {
            readMapping(processorOptions["typeMappings"].toString(), options)
            log.warn("'typeMappings' option is deprecated, please use 'mapping'!")
        }
    }
}

