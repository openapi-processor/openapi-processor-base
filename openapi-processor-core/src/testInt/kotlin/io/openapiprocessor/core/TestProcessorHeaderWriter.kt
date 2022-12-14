/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core

import io.openapiprocessor.core.writer.java.SimpleWriter
import java.io.Writer

/**
 * Writer for a simple header of the generated interfaces & classes.
 */
class TestHeaderWriter: SimpleWriter {

    override fun write(target: Writer) {
        target.write(HEADER)
    }
}

private const val HEADER: String = """
/*
 * This class is auto generated by https://github.com/hauner/openapi-processor-core.
 * TEST ONLY.
 */

"""
