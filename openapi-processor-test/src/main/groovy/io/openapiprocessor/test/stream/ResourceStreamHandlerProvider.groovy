/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-test
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.test.stream

import java.net.spi.URLStreamHandlerProvider

/**
 * Simple resource protocol for loading integration test files from the resources via URL.
 */
class ResourceStreamHandlerProvider extends URLStreamHandlerProvider {

    @Override
    URLStreamHandler createURLStreamHandler (String protocol) {
        if ('resource' != protocol) {
            return null
        }

        new URLStreamHandler () {

            @Override
            protected URLConnection openConnection (URL url) throws IOException {
                URL resource = this.class.getResource (url.getPath())
                if (!resource) {
                    return null
                }

                resource.openConnection()
            }

        }

    }
}
