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
open class DefaultWriterFactory(val options: ApiOptions): WriterFactory, InitWriterTarget {
    private var log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    private var packagePaths: MutableMap<String, Path> = HashMap()
    private lateinit var resourcesPath: Path

    override fun createWriter(packageName: String, className: String): Writer {
        if (packagePaths[packageName] == null) {
            val pkg = packageName.substring(options.packageName.length + 1)
            val (name, path) = initTargetPackage(pkg)
            packagePaths[name] = path
        }

        return createWriter(packagePaths.getValue(packageName), className)
    }

    private fun createWriter(packagePath: Path, className: String): Writer {
        return BufferedWriter(PathWriter(packagePath.resolve("${className}.java")))
    }

    override fun createResourceWriter(resourceName: String): Writer {
        return BufferedWriter(PathWriter(resourcesPath.resolve(resourceName)))
    }

    override fun init() {
        log.debug ("initializing target folders")
        if (options.targetDirOptions.clear) {
            clearTargetDir()
        }

        val (apiName, apiPath) = initTargetPackage("api")
        packagePaths[apiName] = apiPath
        log.debug ("initialized target folder: {}", apiPath.toAbsolutePath ().toString ())

        // should be dto or resources
        val (modelName, modelPath) = initTargetPackage("model")
        packagePaths[modelName] = modelPath
        log.debug ("initialized target folder: {}", modelPath.toAbsolutePath ().toString ())

        val (supportName, supportPath) = initTargetPackage("support")
        packagePaths[supportName] = supportPath
        log.debug ("initialized target folder: {}", supportPath.toAbsolutePath ().toString ())

        if (options.beanValidation) {
            val (validationName, validationPath) = initTargetPackage("validation")
            packagePaths[validationName] = validationPath
            log.debug("initialized target folder: {}", validationPath.toAbsolutePath().toString())
        }

        if (options.targetDirOptions.standardLayout) {
            resourcesPath = initTargetResources()
            log.debug("initialized target folder: {}", resourcesPath.toAbsolutePath().toString())
        }

        packagePaths.putAll(initAdditionalPackages(options))
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

    private fun initTargetResources(): Path {
        val items = mutableListOf(options.targetDir, "resources")
        val path = items.joinToString("/")
        val target = Paths.get (toURI(path))
        Files.createDirectories(target)
        return target
    }

    protected fun initTargetPackage(subPackageName: String): Pair<String, Path> {
        val rootPackageFolder = options.packageName.replace(".", "/")
        val subPackageFolder = subPackageName.replace(".", "/")

        val targetPackage = options.packageName.plus(".$subPackageName")
        val packageFolder = listOf(rootPackageFolder, subPackageFolder).joinToString("/")
        val packagePath = createTargetPackage(packageFolder)

        return Pair(targetPackage, packagePath)
    }

    private fun createTargetPackage(apiPkg: String): Path {
        val items = mutableListOf(options.targetDir)
        if (options.targetDirOptions.layout.isStandard()) {
            items.add("java")
        }
        items.add(apiPkg)

        val pkg = items.joinToString("/")
        val target = Paths.get (toURI(pkg))
        Files.createDirectories(target)
        return target
    }
}
