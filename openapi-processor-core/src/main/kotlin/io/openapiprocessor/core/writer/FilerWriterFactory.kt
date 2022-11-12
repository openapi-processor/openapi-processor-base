/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer

import java.io.BufferedWriter
import java.io.Writer
import javax.annotation.processing.Filer

/**
 * Writer factory for annotation processor.
 */
class FilerWriterFactory(private val filer: Filer): WriterFactory {

    override fun createWriter(packageName: String, className: String): Writer {
        val fileObject = filer.createSourceFile("$packageName.$className")
        return BufferedWriter(fileObject.openWriter())
    }
}

const val PROJECT_ROOT = "io.openapiprocessor.project.root"
