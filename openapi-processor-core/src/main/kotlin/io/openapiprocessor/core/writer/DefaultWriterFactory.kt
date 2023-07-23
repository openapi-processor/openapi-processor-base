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
import java.io.IOException
import java.io.Writer
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.deleteRecursively

/**
 * Writer factory for local file system. Must be initialized via [InitWriterTarget].
 */
class DefaultWriterFactory: WriterFactory, InitWriterTarget {
    private var log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    private lateinit var targetDir: String
    private lateinit var packageName: String
    private lateinit var paths: Map<String, Path>

    override fun createWriter(packageName: String, className: String): Writer {
        return createWriter(paths.getValue(packageName), className)
    }

    private fun createWriter(packagePath: Path, className: String): Writer {
        return BufferedWriter(PathWriter(packagePath.resolve("${className}.java")))
    }

    override fun init(targetDir: String, packageName: String) {
        this.targetDir = targetDir
        this.packageName = packageName
        val pkgPaths = HashMap<String, Path>()

        log.debug ("initializing target folders")
        clearTargetDir()

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

    @OptIn(ExperimentalPathApi::class)
    private fun clearTargetDir() {
        try {
            Path.of(targetDir).deleteRecursively()
        } catch (ex: IOException) {
            log.error("failed to clean target directory: {}", targetDir, ex)
        }
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
