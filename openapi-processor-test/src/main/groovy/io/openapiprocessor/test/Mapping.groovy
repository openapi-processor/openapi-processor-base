/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.test

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory

import java.nio.file.Files
import java.nio.file.Path

class Mapping {
    private ObjectMapper mapper
    private String mappingYaml

    Mapping(String mappingYaml) {
        this.mapper = createMapper()
        this.mappingYaml = mappingYaml
    }

    static Mapping createMapping(Path mappingPath, String defaultOptions) {
        if (Files.exists(mappingPath)) {
            return new Mapping(mappingPath.toUri().toURL().text)
        } else {
            return new Mapping(defaultOptions)
        }
    }

    String getYaml() {
        return mappingYaml
    }

    void setModelType(String modelType) {
        def mapping = getMapping()
        mapping['options']['model-type'] = modelType
        mappingYaml = mapper.writeValueAsString(mapping)
    }

    String getPackageName() {
        def mapping = getMapping()
        return mapping['options']['package-name'] as String
    }

    private Map<String, ?> getMapping() {
        mapper.readValue(mappingYaml, Map<String,?>)
    }

    private static ObjectMapper createMapper() {
        return new ObjectMapper(new YAMLFactory())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setPropertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE)
    }
}
