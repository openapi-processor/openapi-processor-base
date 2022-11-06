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
import io.openapiprocessor.core.support.text
import io.openapiprocessor.core.tempFolder
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

@Suppress("BlockingMethodInNonBlockingContext")
class DefaultWriterFactorySpec : StringSpec({
    isolationMode = IsolationMode.InstancePerTest

    val target = tempFolder()
    val options = ApiOptions()

    beforeTest {
        options.packageName = "io.openapiprocessor"
        options.targetDir = listOf(target.toString(), "java", "src").joinToString(File.separator)
    }

    "initializes package folders" {
        DefaultWriterFactory(options.targetDir, options.packageName)

        val api = options.getSourceDir("api")
        val model = options.getSourceDir("model")
        val support = options.getSourceDir("support")

        Files.exists(api) shouldBe true
        Files.isDirectory(api) shouldBe true
        Files.exists(model) shouldBe true
        Files.isDirectory(model) shouldBe true
        Files.exists(support) shouldBe true
        Files.isDirectory(support) shouldBe true
    }

    "does not fail if target folder structure already exists" {
        Files.createDirectories(options.getSourceDir("api"))
        Files.createDirectories(options.getSourceDir("model"))
        Files.createDirectories(options.getSourceDir("support"))

        shouldNotThrowAny {
            DefaultWriterFactory(options.targetDir, options.packageName)
        }
    }

    "writes @Generated source file to support package" {
        fun textOfSupport(name: String): String {
            return options.getSourcePath("support", name).text
        }

        val factory = DefaultWriterFactory(options.targetDir.toString(), options.packageName)

        val writer = factory.createWriter("${options.packageName}.support", "Generated")
        writer.write("public @interface Generated {}\n")
        writer.close()

        textOfSupport("Generated.java") shouldBe "public @interface Generated {}\n"
    }

    "writes interface source file to api package" {
        fun textOfSupport(name: String): String {
            return options.getSourcePath("api", name).text
        }

        val factory = DefaultWriterFactory(options.targetDir.toString(), options.packageName)

        val writer = factory.createWriter("${options.packageName}.api", "Api")
        writer.write("public interface Api {}\n")
        writer.close()

        textOfSupport("Api.java") shouldBe "public interface Api {}\n"
    }

    "writes model source file to model package" {
        fun textOfSupport(name: String): String {
            return options.getSourcePath("model", name).text
        }

        val factory = DefaultWriterFactory(options.targetDir.toString(), options.packageName)

        val writer = factory.createWriter("${options.packageName}.model", "Model")
        writer.write("public class Model {}\n")
        writer.close()

        textOfSupport("Model.java") shouldBe "public class Model {}\n"
    }
})


private fun ApiOptions.getSourcePath(pkg: String, name: String): Path {
    return getSourceDir(pkg)
        .resolve(name)
}

private fun ApiOptions.getSourceDir(pkg: String): Path {
    return Path.of(
        listOf(
            targetDir,
            packageName.replace(".", File.separator),
            pkg)
        .joinToString(File.separator))
}
