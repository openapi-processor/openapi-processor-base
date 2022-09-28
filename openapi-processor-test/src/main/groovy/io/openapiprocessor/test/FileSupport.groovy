/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-test
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.test

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.github.difflib.DiffUtils
import com.github.difflib.UnifiedDiffUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Stream

class FileSupport {
    private static final Logger log = LoggerFactory.getLogger (FileSupport)

    private Class resourceBase
    private String inputs
    private String generated

    FileSupport(Class resourceBase, String inputs = "inputs.yaml", String generated = "generated.yaml") {
        this.resourceBase = resourceBase
        this.inputs = inputs
        this.generated = generated
    }

    /**
     * copy paths: file system => file system
     *
     * @param source source folder
     * @param target target folder
     */
    static void copy (Path source, Path target) {
        Stream<Path> paths = Files.walk (source)
            .filter ({f -> !Files.isDirectory (f)})

        paths.forEach { p ->
            try {
                Path relativePath = source.relativize (p)
                Path targetPath = target.resolve (relativePath.toString ())
                Files.createDirectories (targetPath.getParent ())

                InputStream src = Files.newInputStream (p)
                OutputStream dst = Files.newOutputStream (targetPath)
                src.transferTo (dst)
            } catch (Exception ex) {
                log.error ("failed to copy {}", p.toString (), ex)
            }
        }

        paths.close ()
    }

    /**
     * copy paths: resources <=> file system
     *
     * @param source source prefix
     * @param sources source files
     * @param target target folder
     */
    void copy (String source, List<String> sources, Path target) {
        for (String p : sources) {
            try {
                String relativePath = p.substring (source.size () + 1)

                Path targetPath = target.resolve (relativePath.toString ())
                Files.createDirectories (targetPath.getParent ())

                InputStream src = getResource (p)
                OutputStream dst = Files.newOutputStream (targetPath)
                src.transferTo (dst)
            } catch (Exception ex) {
                log.error ("failed to copy {}", p, ex)
            }
        }
    }

    /**
     * collect paths in file system.
     *
     * will convert all paths to use "/" as path separator
     */
    static List<String> collectPaths(Path source) {
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
    List<String> collectAbsoluteInputPaths (String path) {
        collectAbsoluteResourcePaths (path, inputs)
    }

    /**
     * collect output paths
     */
    List<String> collectAbsoluteOutputPaths (String path) {
        collectAbsoluteResourcePaths (path, generated)
    }

    /**
     * collect output paths, relative to packageName
     */
    List<String> collectRelativeOutputPaths (String path, String packageName) {
        def testItems = readTestItems (path, generated)

        def items = []
        testItems.items.forEach {
            items.add (it.substring (packageName.size () + 1))
        }

        items
    }

    /**
     * collect absolute paths from items listed in the test items yaml file
     */
    List<String> collectAbsoluteResourcePaths (String path, String itemsYaml) {
        collectRelativeResourcePaths (path, itemsYaml).collect {
            "${path}/${it}".toString ()
        }
    }

    /**
     * collect paths from test items yaml file (input.yaml/generated.yaml)
     */
    List<String> collectRelativeResourcePaths (String path, String itemsYaml) {
        def testItems = readTestItems (path, itemsYaml)
        testItems.items
    }

    /**
     * get the generated files.
     *
     * @param path path of the generated files
     * @return the generated files
     */
    static SortedSet<String> getGeneratedFiles (Path path) {
        def result = new TreeSet<String> ()
        result.addAll (collectPaths (path))
        result
    }

    /**
     *get the expected files (from generated.yaml) and strip package name.
     *
     * @param path the resource path of the test
     * @param packageName stripped package name
     * @return the expected files
     */
    SortedSet<String> getExpectedFiles (String path, String packageName) {
        def items = readTestItems (path, generated)

        def wanted = items.items.collect {
            it.substring (packageName.size () + 1)
        }

        def result = new TreeSet<String> ()
        result.addAll (wanted)
        result
    }

    /**
     * read expected files and strip package name.
     *
     * @param path the resource path of the test
     * @param packageName stripped package name
     * @return the expected files
    TestItems getExpectedFiles (String path, String packageName) {
        def items = readTestItems (path, generated)

        def wanted = items.items.collect {
            it.substring (packageName.size () + 1)
        }

        def ignore = items.ignore.collect {
            it.substring (packageName.size () + 1)
        }

        def expected = new TestItems ()
        expected.items = wanted.sort ()
        expected.ignore = ignore.sort ()
        expected
    }
    */

    /**
     * read test items yaml
     *
     * @param resource path
     * @param name of test items yaml file
     * @return content of yaml
     */
    TestItems readTestItems (String path, String itemsYaml) {
        def source = getResource ("${path}/${itemsYaml}")
        if (!source) {
            println "ERROR: missing '${path}/${itemsYaml}' configuration file!"
        }

        def mapper = createYamlParser ()
        mapper.readValue (source.text, TestItems)
    }

    /**
     * unified diff resources <=> file system
     *
     * @return true if there is a difference
     */
    boolean printUnifiedDiff (String expected, Path generated) {
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
    static void printUnifiedDiff (Path expected, Path generated) {
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

    InputStream getResource (String path) {
        resourceBase.getResourceAsStream (path)
    }

    private static ObjectMapper createYamlParser () {
        new ObjectMapper (new YAMLFactory ())
            .configure (DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

}
