/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-test
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.test.stream

import java.io.IOException
import java.net.URL
import java.net.URLConnection
import java.net.URLStreamHandler
import java.net.spi.URLStreamHandlerProvider

/**
 * Simple resource protocol for loading integration test files from the resources via URL.
 */
class ResourceStreamHandlerProvider : URLStreamHandlerProvider() {

    override fun createURLStreamHandler(protocol: String?): URLStreamHandler? {
        if ("resource" != protocol) {
            return null
        }

        return object : URLStreamHandler() {
            override fun openConnection(url: URL): URLConnection {
                val resource = this::class.java.getResource (url.path)
                if (resource == null) {
                    throw IOException ("unknown resource: ${url.protocol}")
                }

                return resource.openConnection()
            }
        }
    }
}
