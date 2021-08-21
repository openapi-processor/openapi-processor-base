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
                        connected = true
                        data = Memory.get (url.path)
                    }

                    @Override
                    long getContentLengthLong () {
                        return data.length
                    }

                    @Override
                    InputStream getInputStream () throws IOException {
                        if(!connected)
                            connect ()

                        return new ByteArrayInputStream (data)
                    }

                }
            }

        }

    }

}
