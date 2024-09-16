/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.test

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory

class TestItemsReader {
    private Class resourceBase

    TestItemsReader(Class resourceBase) {
        this.resourceBase = resourceBase
    }

    /**
     * read test items yaml
     *
     * @param resource path
     * @param name of test items yaml file
     * @return content of yaml
     */
    TestItems read (String path, String itemsYaml) {
        def source = getResource ("${path}/${itemsYaml}")
        if (!source) {
            println "ERROR: missing '${path}/${itemsYaml}' configuration file!"
        }

        def mapper = createYamlParser ()
        mapper.readValue (source.text, TestItems)
    }

    InputStream getResource (String path) {
        resourceBase.getResourceAsStream (path)
    }

    private static ObjectMapper createYamlParser () {
        new ObjectMapper (new YAMLFactory ()).configure (DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }
}
