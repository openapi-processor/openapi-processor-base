/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer

import io.openapiprocessor.core.converter.ApiOptions
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
open class DefaultWriterFactory(val options: ApiOptions): WriterFactory, InitWriterTarget
{
    private var log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    private lateinit var paths: Map<String, Path>

    override fun createWriter(packageName: String, className: String): Writer {
        return createWriter(paths.getValue(packageName), className)
    }

    private fun createWriter(packagePath: Path, className: String): Writer {
        return BufferedWriter(PathWriter(packagePath.resolve("${className}.java")))
    }

    override fun init() {
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

        if (options.beanValidation) {
            val (validationName, validationPath) = initTargetPackage("validation")
            pkgPaths[validationName] = validationPath
            log.debug("initialized target folder: {}", validationPath.toAbsolutePath().toString())
        }

        pkgPaths.putAll(initAdditionalPackages(options))

        paths = pkgPaths
    }

    open fun initAdditionalPackages(options: ApiOptions): Map<String, Path> {
        return emptyMap()
    }

    @OptIn(ExperimentalPathApi::class)
    private fun clearTargetDir() {
        try {
            Path.of(toURI(options.targetDir!!)).deleteRecursively()
        } catch (ex: IOException) {
            log.error("failed to clean target directory: {}", options.targetDir, ex)
        }
    }

    protected fun initTargetPackage(subPackageName: String): Pair<String, Path> {
        val rootPackageFolder = options.packageName.replace(".", "/")

        val apiPackage = options.packageName.plus(".$subPackageName")
        val apiFolder = listOf(rootPackageFolder, subPackageName).joinToString("/")
        val apiPath = createTargetPackage(apiFolder)

        return Pair(apiPackage, apiPath)
    }

    private fun createTargetPackage(apiPkg: String): Path {
        val pkg = listOf(options.targetDir, apiPkg).joinToString("/")

        val target = Paths.get (toURI(pkg))
        Files.createDirectories(target)
        return target
    }
}
