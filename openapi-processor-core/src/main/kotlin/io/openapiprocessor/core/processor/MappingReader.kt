/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.net.MalformedURLException
import java.net.URL
import io.openapiprocessor.core.processor.mapping.Mapping as MappingV2
import io.openapiprocessor.core.processor.mapping.Parameter as ParameterV2
import io.openapiprocessor.core.processor.mapping.ParameterDeserializer as ParameterDeserializerV2
import io.openapiprocessor.core.processor.mapping.version.Mapping as Version

/**
 *  Reader for mapping YAML.
 */
class MappingReader(private val validator: MappingValidator = MappingValidator()) {
    var log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    fun read(typeMappings: String?): MappingV2? {
        if (typeMappings.isNullOrEmpty()) {
            return null
        }

        val mapping: String = when {
            isUrl (typeMappings) -> {
                URL (typeMappings).readText()
            }
            isFileName (typeMappings) -> {
                File (typeMappings).readText()
            }
            else -> {
                typeMappings
            }
        }

        val versionMapper = createVersionParser ()
        val version = versionMapper.readValue (mapping, Version::class.java)
        if (version.version == null) {
            log.error("the mapping is missing the openapi-processor-* version identifier")
            log.error("see https://openapiprocessor.io/spring/mapping/structure.html")
        }

        validate(mapping, version.getSafeVersion())

        val mapper = createV2Parser()
        return mapper.readValue (mapping, MappingV2::class.java)
    }

    private fun validate(mapping: String, version: String) {
        val output = validator.validate(mapping, version)
        if (output.isValid)
            return

        log.warn("mapping is not valid!")
        val error = output.error
        if(error != null) {
            log.warn(error)
        }

        output.errors?.forEach {
            log.warn("{} at {}", it.error, it.instanceLocation.ifEmpty { "/" })
        }
    }

    private fun createV2Parser(): ObjectMapper {
        val module = SimpleModule()
        module.addDeserializer (ParameterV2::class.java, ParameterDeserializerV2 ())

        val kotlinModule = KotlinModule.Builder()
            .configure(KotlinFeature.NullIsSameAsDefault, true)
            .build ()

        return YAMLMapper.builder(YAMLFactory())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true)
            .build()
            .setPropertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE)
            .registerModules(kotlinModule, module)
    }

    private fun createVersionParser(): ObjectMapper {
        val kotlinModule = KotlinModule.Builder()
            .build ()

        return ObjectMapper (YAMLFactory ())
            .configure (DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setPropertyNamingStrategy (PropertyNamingStrategies.KEBAB_CASE)
            .registerModule (kotlinModule)
    }

    private fun isFileName(name: String): Boolean {
        return name.endsWith (".yaml") || name.endsWith (".yml")
    }

    private fun isUrl (source: String): Boolean {
        return try {
            URL (source)
            true
        } catch (ignore: MalformedURLException) {
            false
        }
    }

}
