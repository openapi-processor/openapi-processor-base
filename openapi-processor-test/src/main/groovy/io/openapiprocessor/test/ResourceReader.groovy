/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.test

import groovy.transform.CompileStatic

@CompileStatic
class ResourceReader {
    private Class resourceBase

    ResourceReader(Class resourceBase) {
        this.resourceBase = resourceBase
    }

    InputStream getResource (String path) {
        resourceBase.getResourceAsStream (path)
    }

    /** get absolute resource uri */
    URL getResourceUrl (String path) {
        resourceBase.getResource (path)
    }
}
