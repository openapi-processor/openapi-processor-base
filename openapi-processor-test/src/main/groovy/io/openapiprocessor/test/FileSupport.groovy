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
     * get the expected files (from generated.yaml) and strips the prefix.
     *
     * @param path the resource path of the test
     * @param stripPrefix prefix to strip
     * @return the expected files
     */
    SortedSet<String> getExpectedFiles (String path, String stripPrefix) {
        def items = readTestItems (path, generated)

        def wanted = items.items.collect {
            it.substring (stripPrefix.size () + 1)
        }

        def result = new TreeSet<String> ()
        result.addAll (wanted)
        result
    }

    /**
     * check existence test items yaml
     *
     * @param resource path
     * @param name of test items yaml file
     * @return true if it exists, else false
     */
    boolean checkTestItems (String path, String itemsYaml) {
        getResource ("${path}/${itemsYaml}") != null
    }

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
     * unified diff resources <=> file system.
     *
     * @param expected resource path
     * @param generated file system path
     *
     * @return true if there is a difference
     */
    boolean printUnifiedDiff (String expected, Path generated) {
        def expectedStream = getResource (expected)
        if (expectedStream == null) {
            log.error ("failed to find {}", expected)
            return true
        }

        def expectedLines = expectedStream.readLines ()

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

        if (!patch.deltas.isEmpty()) {
            println "$expected"
        }
        diff.each {
            println it
        }

        return !patch.deltas.isEmpty ()
    }

    InputStream getResource (String path) {
        resourceBase.getResourceAsStream (path)
    }

    private static ObjectMapper createYamlParser () {
        new ObjectMapper (new YAMLFactory ())
            .configure (DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

}
