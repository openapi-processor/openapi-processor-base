/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.test

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory

class TestItemsReader {
    private ResourceReader resource

    TestItemsReader(ResourceReader resource) {
        this.resource = resource
    }

    /**
     * check existence of items yaml
     *
     * @param resource path
     * @param name of the items yaml file
     * @return true if it exists, else false
     */
    boolean exists(String path, String name) {
        return getResource ("${path}/${name}") != null
    }

    /**
     * read test items yaml
     *
     * @param resource path
     * @param name of the items yaml file
     * @return content of yaml
     */
    TestItems read (String path, String name) {
        def source = getResource ("${path}/${name}")
        if (!source) {
            println "ERROR: missing '${path}/${name}' configuration file!"
        }

        def mapper = createYamlParser ()
        mapper.readValue (source.text, TestItems)
    }

    private InputStream getResource (String path) {
        resource.getResource (path)
    }

    private static ObjectMapper createYamlParser () {
        new ObjectMapper (new YAMLFactory ()).configure (DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }
}
