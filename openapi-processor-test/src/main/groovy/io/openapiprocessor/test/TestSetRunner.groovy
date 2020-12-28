/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-test
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.test

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.github.difflib.DiffUtils
import com.github.difflib.UnifiedDiffUtils
import com.github.hauner.openapi.test.TestItems
import com.github.hauner.openapi.test.TestSet

import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Stream

/**
 * used to execute test sets.
 */
class TestSetRunner {

    TestSet testSet

    TestSetRunner(TestSet testSet) {
        this.testSet = testSet
    }

    boolean runOnNativeFileSystem (File folder) {
        def source = testSet.name

        def processor = testSet.processor
        def options = [
            parser: testSet.parser,
            apiPath: "resource:/tests/${source}/inputs/openapi.yaml",
            targetDir: folder.absolutePath
        ]

        def mappingYaml = getResource ("/tests/${source}/inputs/mapping.yaml")
        if(mappingYaml) {
            options.mapping = mappingYaml.text
        } else {
            options.mapping = testSet.defaultOptions
        }

        when:
        processor.run (options)

        then:
        def packageName = testSet.packageName
        def sourcePath = "/tests/${source}"
        def expectedPath = "${sourcePath}/${packageName}"
        def generatedPath = Path.of (folder.absolutePath).resolve (packageName)

        def expectedFiles = collectRelativeOutputPaths (sourcePath, packageName)
            .sort ()
        def generatedFiles = collectPaths (generatedPath)
            .sort ()

        assert expectedFiles == generatedFiles

        def success = true
        expectedFiles.each {
            def expected = "${expectedPath}/$it"
            def generated = generatedPath.resolve (it)

            success &= printUnifiedDiff (expected, generated)
        }

        success
    }

    boolean runOnCustomFileSystem (FileSystem fs) {
        def source = testSet.name

        Path root = Files.createDirectory (fs.getPath ("source"))

        def path = "/tests/${source}"
        copy (path, collectAbsoluteInputPaths (path), root)
        copy (path, collectAbsoluteOutputPaths (path), root)

        Path api = root.resolve ('inputs/openapi.yaml')
        Path target = fs.getPath ('target')

        def processor = testSet.processor
        def options = [
            parser: 'OPENAPI4J', // swagger-parser does not work with fs
            apiPath: api.toUri ().toURL ().toString (),
            targetDir: target.toUri ().toURL ().toString ()
        ]

        def mappingYaml = root.resolve ('inputs/mapping.yaml')
        if(Files.exists (mappingYaml)) {
            options.mapping = mappingYaml.toUri ().toURL ().toString ()
        } else {
            options.mapping = testSet.defaultOptions
        }

        def packageName = testSet.packageName
        def expectedPath = root.resolve (packageName)
        def generatedPath = target.resolve (packageName)

        when:
        processor.run (options)

        then:
        def expectedFiles = collectPaths (expectedPath)
        def generatedFiles = collectPaths (generatedPath)
        assert expectedFiles == generatedFiles

        def success = true
        expectedFiles.each {
            def expected = expectedPath.resolve (it)
            def generated = generatedPath.resolve (it)

            success &= printUnifiedDiff (expected, generated)
        }

        success
    }

    /**
     * copy paths file system <=> file system
     */
    private static void copy (Path source, Path target) {
        Stream<Path> paths = Files.walk (source)
            .filter ({f -> !Files.isDirectory (f)})

        paths.forEach { p ->
            Path relativePath = source.relativize (p)
            Path targetPath = target.resolve (relativePath.toString ())
            Files.createDirectories (targetPath.getParent ())

            InputStream src = Files.newInputStream (p)
            OutputStream dst = Files.newOutputStream (targetPath)
            src.transferTo (dst)
        }

        paths.close ()
    }

    /**
     * copy paths resources <=> file system
     */
    private void copy (String source, List<String> sources, Path target) {
        for (String p : sources) {
            String relativePath = p.substring (source.size () + 1)

            Path targetPath = target.resolve (relativePath.toString ())
            Files.createDirectories (targetPath.getParent ())

            InputStream src = getResource (p)
            OutputStream dst = Files.newOutputStream (targetPath)
            src.transferTo (dst)
        }
    }

    /**
     * collect paths in file system.
     *
     * will convert all paths to use "/" as path separator
     */
    private static List<String> collectPaths(Path source) {
        List<String> files = []

        def found = Files.walk (source)
            .filter ({ f ->
                !Files.isDirectory (f)
            })

        found.forEach ({f ->
                files << source.relativize (f).toString ()
            })
        found.close ()

        files.collect {
            it.replace ('\\', '/')
        }
    }

    /**
     * collect input paths
     */
    private List<String> collectAbsoluteInputPaths (String path) {
        collectAbsoluteResourcePaths (path, "inputs.yaml")
    }

    /**
     * collect output paths
     */
    private List<String> collectAbsoluteOutputPaths (String path) {
        collectAbsoluteResourcePaths (path, "generated.yaml")
    }

    /**
     * collect output paths, relative to packageName
     */
    private List<String> collectRelativeOutputPaths (String path, String packageName) {
        collectRelativeResourcePaths (path, "generated.yaml").collect {
            it.substring (packageName.size () + 1)
        }
    }

    /**
     * collect absolute paths from output.yaml in resources
     */
    private List<String> collectAbsoluteResourcePaths (String path, String itemsYaml) {
        collectRelativeResourcePaths (path, itemsYaml).collect {
            "${path}/${it}".toString ()
        }
    }

    /**
     * collect paths from output.yaml in resources
     */
    private List<String> collectRelativeResourcePaths (String path, String itemsYaml) {
        def source = getResource ("${path}/${itemsYaml}")
        if (!source) {
            println "ERROR: missing '${path}/${itemsYaml}' configuration file!"
        }

        def mapper = createYamlParser ()
        def sourceItems = mapper.readValue (source.text, TestItems)
        sourceItems.items
    }

    /**
     * unified diff resources <=> file system
     *
     * @return true if delta
     */
    private boolean printUnifiedDiff (String expected, Path generated) {
        def expectedLines = getResource (expected).readLines ()

        def patch = DiffUtils.diff (
            expectedLines,
            generated.readLines ())

        def diff = UnifiedDiffUtils.generateUnifiedDiff (
            getResource (expected).text,
            generated.toString (),
            expectedLines,
            patch,
            4
        )

        diff.each {
            println it
        }

        return !patch.deltas.isEmpty ()
    }

    /**
     * unified diff file system <=> file system
     */
    private static void printUnifiedDiff (Path expected, Path generated) {
        def patch = DiffUtils.diff (
            expected.readLines (),
            generated.readLines ())

        def diff = UnifiedDiffUtils.generateUnifiedDiff (
            expected.toString (),
            generated.toString (),
            expected.readLines (),
            patch,
            2
        )

        diff.each {
            println it
        }
    }

    private InputStream getResource (String path) {
        this.class.getResourceAsStream (path)
    }

    private static ObjectMapper createYamlParser () {
        new ObjectMapper (new YAMLFactory ())
            .configure (DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

}
