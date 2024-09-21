/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core

import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainAll
import io.openapiprocessor.test.Collector
import io.openapiprocessor.test.Diff
import io.openapiprocessor.test.ResourceReader
import io.openapiprocessor.test.TestItemsReader
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

class ProcessorEndToEndRemoteSpec: StringSpec({
    val repo = "https://raw.githubusercontent.com/openapi-processor/openapi-processor-base/main/openapi-processor-core"

    val pkg = "generated"
    val mapping = """
        openapi-processor-mapping: v4
    
        options:
          package-name: $pkg
          format-code: false
    """.trimIndent()

    val resourceReader = ResourceReader(ProcessorEndToEndRemoteSpec::class.java)
    val itemsReader = TestItemsReader(resourceReader)

    /**
     * get the expected files (from outputs.yaml) and strips the prefix.
     *
     * @param path the resource path of the test
     * @param stripPrefix prefix to strip
     * @return the expected files
     */

    fun getExpectedFiles(path: String, stripPrefix: String): Set<String> {
        val items = itemsReader.read(path, "outputs.yaml")

        val wanted = items.items.map {
            it.substring (stripPrefix.length + 1)
        }

        val result = TreeSet<String> ()
        result.addAll (wanted)
        return result
    }

    fun resolveModelFiles(files: Set<String>, replacement: String): Set<String> {
        return files.map { it.replaceFirst("<model>", replacement) }.toSet()
    }

    fun resolveModelFile(file: String, replacement: String): String {
        return file.replaceFirst("<model>", replacement)
    }

    fun getExpectedFile(path: String): Path {
        return Paths.get(resourceReader.getResourceUrl(path).toURI())
    }

    "processes remote openapi with ref's" {
        forAll(
            row("INTERNAL", "ref-into-another-file", "openapi30.yaml"),
            row("INTERNAL", "ref-into-another-file", "openapi31.yaml"),
            row("SWAGGER", "ref-into-another-file", "openapi30.yaml"),
            row("OPENAPI4J", "ref-into-another-file", "openapi30.yaml")
        ) { parser, source, api ->
            val folder = tempdir()

            val options = mutableMapOf(
                "apiPath" to "$repo/src/testInt/resources/tests/$source/inputs/$api",
                "targetDir" to folder.canonicalPath,
                "parser" to parser,
                "mapping" to mapping
            )

            val processor = TestProcessor()
            processor.run(options)

            val sourcePath = "/tests/$source"
            val expectedPath = "$sourcePath/outputs"
            val generatedPath = Path.of (folder.canonicalPath).resolve (pkg)

            val expectedFiles = getExpectedFiles(sourcePath, "outputs")
            val expectedFileNames = resolveModelFiles(expectedFiles, "model")
            val generatedFiles = Collector.collectPaths (generatedPath)

            generatedFiles.shouldContainAll(expectedFileNames)

            var success = true
            expectedFiles.forEach {
                val expected = getExpectedFile(resolveModelFile("$expectedPath/$it", "model/default"))
                val generated = generatedPath.resolve (resolveModelFile(it, "model"))

                val hasDiff = !Diff.printUnifiedDiff (expected, generated)
                success = success && hasDiff
            }

            success.shouldBeTrue()
        }
    }
})
