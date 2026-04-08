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
import io.openapiprocessor.core.processor.mapping.Mapping
import io.openapiprocessor.core.processor.mapping.Parameter
import io.openapiprocessor.core.processor.mapping.ParameterDeserializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.net.MalformedURLException
import java.net.URL

/**
 *  Reader for mapping YAML.
 */
class MappingReader(private val validator: MappingValidator = MappingValidator()) {
    var log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    fun read(typeMappings: String?): Mapping? {
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

        validate(mapping)

        val mapper = createParser()
        return mapper.readValue (mapping, Mapping::class.java)
    }

    private fun validate(mapping: String) {
        val output = validator.validate(mapping)
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

    private fun createParser(): ObjectMapper {
        val module = SimpleModule()
        module.addDeserializer (Parameter::class.java, ParameterDeserializer())

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
