/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-test
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.test.stream

/**
 * in-memory content by path.
 */
class Memory {
    private static final Map<String, byte[]> contents = new HashMap<> ()

    static byte[] get (String path) {
        contents.get (path)
    }

    static void add (String path, String data) {
        add (path, data.getBytes ("UTF-8"))
    }

    static void add (String path, byte[] data) {
        contents.put (path, data)
    }

    static clear() {
        contents.clear ()
    }

}
