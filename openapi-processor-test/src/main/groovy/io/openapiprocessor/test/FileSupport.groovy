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

import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Stream

class FileSupport {

    private Class resourceBase

    FileSupport(Class resourceBase) {
        this.resourceBase = resourceBase
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
     * copy paths: resources <=> file system
     *
     * @param source source prefix
     * @param sources source files
     * @param target target folder
     */
    void copy (String source, List<String> sources, Path target) {
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
        collectAbsoluteResourcePaths (path, "inputs.yaml")
    }

    /**
     * collect output paths
     */
    List<String> collectAbsoluteOutputPaths (String path) {
        collectAbsoluteResourcePaths (path, "generated.yaml")
    }

    /**
     * collect output paths, relative to packageName
     */
    List<String> collectRelativeOutputPaths (String path, String packageName) {
        collectRelativeResourcePaths (path, "generated.yaml").collect {
            it.substring (packageName.size () + 1)
        }
    }

    /**
     * collect absolute paths from output.yaml in resources
     */
    List<String> collectAbsoluteResourcePaths (String path, String itemsYaml) {
        collectRelativeResourcePaths (path, itemsYaml).collect {
            "${path}/${it}".toString ()
        }
    }

    /**
     * collect paths from output.yaml in resources
     */
    List<String> collectRelativeResourcePaths (String path, String itemsYaml) {
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
