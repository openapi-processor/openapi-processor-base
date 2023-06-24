/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-test
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.test.stream

/**
 * in-memory content by path.
 */
class Memory {
    companion object {
        private val contents: MutableMap<String, ByteArray> = mutableMapOf()

        fun get(path: String): ByteArray? {
            return contents[path]
        }

        fun add(path: String, data: String) {
            add(path, data.toByteArray())
        }

        fun add(path: String, data: ByteArray) {
            contents[path] = data
        }

        fun clear() {
            contents.clear()
        }
    }
}
