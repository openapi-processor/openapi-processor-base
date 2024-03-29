/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-test
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.test.stream

import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.net.URLConnection
import java.net.URLStreamHandler
import java.net.spi.URLStreamHandlerProvider

/**
 * Simple in-memory protocol for "loading" openapi.yaml content from memory via URL.
 */
class MemoryStreamHandlerProvider: URLStreamHandlerProvider() {

    override fun createURLStreamHandler(protocol: String?): URLStreamHandler? {
        if ("memory" != protocol) {
            return null
        }

        return object : URLStreamHandler() {

            override fun openConnection(url: URL): URLConnection {
                if (("memory" != url.protocol)) {
                    throw IOException ("unknown protocol: ${url.protocol}")
                }

                return object : URLConnection(url) {
                    private var data: ByteArray? = null

                    override fun connect() {
                        connected = true
                        data = Memory.get(url.path)
                    }

                    override fun getContentLengthLong(): Long {
                        if (data == null)
                            return -1

                        return data!!.size.toLong()
                    }

                    override fun getInputStream(): InputStream {
                        if (!connected)
                            connect()

                        return ByteArrayInputStream(data)
                    }
                }
            }

        }
    }
}
