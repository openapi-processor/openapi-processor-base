/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.converter.options.TargetDirLayout
import io.openapiprocessor.core.support.text
import io.openapiprocessor.core.tempFolder
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createFile

@Suppress("BlockingMethodInNonBlockingContext")
class DefaultWriterFactorySpec : StringSpec({
    isolationMode = IsolationMode.InstancePerTest

    val target = tempFolder()
    val options = ApiOptions()

    beforeTest {
        options.packageName = "io.openapiprocessor"
        options.targetDir = listOf(target.toString(), "java", "src").joinToString(File.separator)
        options.beanValidation = false
    }

    "initializes 'classic' package folders" {
        options.targetDir = listOf(target.toString()).joinToString(File.separator)
        options.targetDirOptions.layout = TargetDirLayout.CLASSIC

        val writerFactory = DefaultWriterFactory(options)
        writerFactory.init()

        val api = options.getSourceDir("api")
        val model = options.getSourceDir("model")
        val support = options.getSourceDir("support")
        val validation = options.getSourceDir("validation")

        Files.exists(api) shouldBe true
        Files.isDirectory(api) shouldBe true
        Files.exists(model) shouldBe true
        Files.isDirectory(model) shouldBe true
        Files.exists(support) shouldBe true
        Files.isDirectory(support) shouldBe true
        Files.exists(validation) shouldBe false
        Files.isDirectory(validation) shouldBe false
    }

    "initializes 'standard' package folders" {
        options.targetDir = listOf(target.toString()).joinToString(File.separator)
        options.targetDirOptions.layout = TargetDirLayout.STANDARD

        val writerFactory = DefaultWriterFactory(options)
        writerFactory.init()

        val api = options.getSourceDir("api")
        val model = options.getSourceDir("model")
        val support = options.getSourceDir("support")
        val validation = options.getSourceDir("validation")
        val resources = options.getResourceDir()

        Files.exists(api) shouldBe true
        Files.isDirectory(api) shouldBe true
        Files.exists(model) shouldBe true
        Files.isDirectory(model) shouldBe true
        Files.exists(support) shouldBe true
        Files.isDirectory(support) shouldBe true
        Files.exists(validation) shouldBe false
        Files.isDirectory(validation) shouldBe false
        Files.exists(resources) shouldBe true
        Files.isDirectory(resources) shouldBe true
    }

    "initializes package folders with validation" {
        options.beanValidation = true
        val writerFactory = DefaultWriterFactory(options)
        writerFactory.init()

        val api = options.getSourceDir("api")
        val model = options.getSourceDir("model")
        val support = options.getSourceDir("support")
        val validation = options.getSourceDir("validation")

        Files.exists(api) shouldBe true
        Files.isDirectory(api) shouldBe true
        Files.exists(model) shouldBe true
        Files.isDirectory(model) shouldBe true
        Files.exists(support) shouldBe true
        Files.isDirectory(support) shouldBe true
        Files.exists(validation) shouldBe true
        Files.isDirectory(validation) shouldBe true
    }

    "does not fail if target folder structure already exists" {
        Files.createDirectories(options.getSourceDir("api"))
        Files.createDirectories(options.getSourceDir("model"))
        Files.createDirectories(options.getSourceDir("support"))
        Files.createDirectories(options.getSourceDir("validation"))

        shouldNotThrowAny {
            DefaultWriterFactory(options).init()
        }
    }

    "writes @Generated source file to support package" {
        fun textOfSupport(name: String): String {
            return options.getSourcePath("support", name).text
        }

        val factory = DefaultWriterFactory(options)
        factory.init()

        val writer = factory.createWriter("${options.packageName}.support", "Generated")
        writer.write("public @interface Generated {}\n")
        writer.close()

        textOfSupport("Generated.java") shouldBe "public @interface Generated {}\n"
    }

    "writes interface source file to api package" {
        fun textOfSupport(name: String): String {
            return options.getSourcePath("api", name).text
        }

        val factory = DefaultWriterFactory(options)
        factory.init()

        val writer = factory.createWriter("${options.packageName}.api", "Api")
        writer.write("public interface Api {}\n")
        writer.close()

        textOfSupport("Api.java") shouldBe "public interface Api {}\n"
    }

    "writes model source file to model package" {
        fun textOfSupport(name: String): String {
            return options.getSourcePath("model", name).text
        }

        val factory = DefaultWriterFactory(options)
        factory.init()

        val writer = factory.createWriter("${options.packageName}.model", "Model")
        writer.write("public class Model {}\n")
        writer.close()

        textOfSupport("Model.java") shouldBe "public class Model {}\n"
    }

    "writes file to resource folder" {
        fun textOfSupport(name: String): String {
            return options.getResourcePath(name).text
        }

        options.targetDir = target.toString()
        options.targetDirOptions.layout = TargetDirLayout.STANDARD
        val factory = DefaultWriterFactory(options)
        factory.init()

        val writer = factory.createResourceWriter("api.properties")
        writer.write("api\n")
        writer.close()

        textOfSupport("api.properties") shouldBe "api\n"
    }

    "deletes target directory to clear old files" {
        val api = options.getSourceDir("api")
        val model = options.getSourceDir("model")
        val support = options.getSourceDir("support")
        val validation = options.getSourceDir("validation")
        val resource = options.getResourceDir()

        Files.createDirectories(api)
        Files.createDirectories(model)
        Files.createDirectories(support)
        Files.createDirectories(validation)
        Files.createDirectories(resource)

        api.resolve("Old.java").createFile()
        model.resolve("Old.java").createFile()
        support.resolve("Old.java").createFile()
        validation.resolve("Old.java").createFile()
        resource.resolve("old.properties").createFile()

        Files.exists(api.resolve("Old.java")) shouldBe true
        Files.exists(model.resolve("Old.java")) shouldBe true
        Files.exists(support.resolve("Old.java")) shouldBe true
        Files.exists(validation.resolve("Old.java")) shouldBe true
        Files.exists(resource.resolve("old.properties")) shouldBe true

        val factory = DefaultWriterFactory(options)
        factory.init()

        Files.exists(api.resolve("Old.java")) shouldBe false
        Files.exists(model.resolve("Old.java")) shouldBe false
        Files.exists(support.resolve("Old.java")) shouldBe false
        Files.exists(validation.resolve("Old.java")) shouldBe false
        Files.exists(resource.resolve("old.properties")) shouldBe false
    }

    "skips deleting of target directory if clearTargetDir option is false" {
        options.targetDirOptions.clear = false

        val api = options.getSourceDir("api")
        val model = options.getSourceDir("model")
        val support = options.getSourceDir("support")
        val validation = options.getSourceDir("validation")
        val resource = options.getResourceDir()

        Files.createDirectories(api)
        Files.createDirectories(model)
        Files.createDirectories(support)
        Files.createDirectories(validation)
        Files.createDirectories(resource)

        api.resolve("Old.java").createFile()
        model.resolve("Old.java").createFile()
        support.resolve("Old.java").createFile()
        validation.resolve("Old.java").createFile()
        resource.resolve("old.properties").createFile()

        val factory = DefaultWriterFactory(options)
        factory.init()

        Files.exists(api.resolve("Old.java")) shouldBe true
        Files.exists(model.resolve("Old.java")) shouldBe true
        Files.exists(support.resolve("Old.java")) shouldBe true
        Files.exists(validation.resolve("Old.java")) shouldBe true
        Files.exists(resource.resolve("old.properties")) shouldBe true
    }

    "initializes additional package folders" {
        val writerFactory = object : DefaultWriterFactory(options) {
            override fun initAdditionalPackages(options: ApiOptions): Map<String, Path> {
                val pkgPaths = HashMap<String, Path>()
                val (name, path) = initTargetPackage("foo/bar")
                pkgPaths[name] = path
                return pkgPaths
            }
        }
        writerFactory.init()

        val additional = options.getSourceDir("foo/bar")

        Files.exists(additional) shouldBe true
        Files.isDirectory(additional) shouldBe true
    }
})


private fun ApiOptions.getSourcePath(pkg: String, name: String): Path {
    return getSourceDir(pkg)
        .resolve(name)
}

private fun ApiOptions.getResourcePath(name: String): Path {
    return getResourceDir()
        .resolve(name)
}

private fun ApiOptions.getSourceDir(pkg: String): Path {
    val items = mutableListOf(targetDir)
    if (TargetDirLayout.isStandard(targetDirOptions.layout)) {
        items.add("java")
    }
    items.add(packageName.replace(".", File.separator))
    items.add(pkg)

    return Path.of(items.joinToString(File.separator))
}

private fun ApiOptions.getResourceDir(): Path {
    val items = mutableListOf(targetDir)
    if (TargetDirLayout.isStandard(targetDirOptions.layout)) {
        items.add("resources")
    }

    return Path.of(items.joinToString(File.separator))
}
