/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.test

import groovy.transform.CompileStatic

import javax.tools.SimpleJavaFileObject

@CompileStatic
class MemoryFile extends SimpleJavaFileObject {
    MemoryFile(String name, Kind kind) {
        super(URI.create("string:///${name}"), kind)
    }

    OutputStream openOutputStream() {
        return new ByteArrayOutputStream()
    }
}
