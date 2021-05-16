/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-test
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.test.stream

import java.net.spi.URLStreamHandlerProvider

/**
 * Simple in-memory protocol for "loading" openapi.yaml content from memory via URL.
 */
class MemoryStreamHandlerProvider extends URLStreamHandlerProvider {

    @Override
    URLStreamHandler createURLStreamHandler (String protocol) {
        if ('memory' != protocol) {
            return null
        }

        new URLStreamHandler () {

            @Override
            protected URLConnection openConnection (URL url) throws IOException {
                if (('memory' != url.protocol)) {
                    throw new IOException ("unknown protocol: ${url.protocol}")
                }

                return new URLConnection (url) {

                    private byte[] data

                    @Override
                    void connect () throws IOException {
                        data = getData ()
                        connected = true
                    }

                    @Override
                    long getContentLengthLong () {
                        return data.length
                    }

                    @Override
                    InputStream getInputStream () throws IOException {
                        return new ByteArrayInputStream (data)
                    }

                    private byte[] getData () {
                        Memory.get (url.path)
                    }

                }
            }

        }

    }

}
