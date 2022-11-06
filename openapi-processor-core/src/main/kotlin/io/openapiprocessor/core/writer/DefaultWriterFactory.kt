/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer

import io.openapiprocessor.core.support.toURI
import io.openapiprocessor.core.writer.java.PathWriter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.BufferedWriter
import java.io.Writer
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class DefaultWriterFactory(private val targetDir: String?, val packageName: String): WriterFactory {
    private var log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    private lateinit var paths: Map<String, Path>

    init {
        init()
    }

    override fun createWriter(packageName: String, className: String): Writer {
        return createWriter(paths[packageName]!!, className)
    }

    private fun createWriter(packagePath: Path, className: String): Writer {
        return BufferedWriter(PathWriter(packagePath.resolve("${className}.java")))
    }

    private fun init() {
        val pkgPaths = HashMap<String, Path>()

        log.debug ("initializing target folders")

        val (apiName, apiPath) = initTargetPackage("api")
        pkgPaths[apiName] = apiPath
        log.debug ("initialized target folder: {}", apiPath.toAbsolutePath ().toString ())

        val (modelName, modelPath) = initTargetPackage("model")
        pkgPaths[modelName] = modelPath
        log.debug ("initialized target folder: {}", modelPath.toAbsolutePath ().toString ())

        val (supportName, supportPath) = initTargetPackage("support")
        pkgPaths[supportName] = supportPath
        log.debug ("initialized target folder: {}", supportPath.toAbsolutePath ().toString ())

        paths = pkgPaths
    }

    private fun initTargetPackage(subPackageName: String): Pair<String, Path> {
        val rootPackageFolder = packageName.replace(".", "/")

        val apiPackage = packageName.plus(".$subPackageName")
        val apiFolder = listOf(rootPackageFolder, subPackageName).joinToString("/")
        val apiPath = createTargetPackage(apiFolder)

        return Pair(apiPackage, apiPath)
    }

    private fun createTargetPackage(apiPkg: String): Path {
        val pkg = listOf(targetDir, apiPkg).joinToString("/")

        val target = Paths.get (toURI(pkg))
        Files.createDirectories(target)
        return target
    }
}
