/*
 * Copyright 2019-2020 the original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.hauner.openapi.test

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.github.difflib.DiffUtils
import com.github.difflib.UnifiedDiffUtils
import org.junit.Rule
import org.junit.rules.TemporaryFolder

import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Stream

import static org.junit.Assert.assertEquals

abstract class ProcessorTestBase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    TestSet testSet

    ProcessorTestBase (TestSet testSet) {
        this.testSet = testSet
    }

    protected runOnNativeFileSystem () {
        def source = testSet.name

        def processor = testSet.processor
        def options = [
            parser: testSet.parser,
            apiPath: "resource:/tests/${source}/openapi.yaml",
            targetDir: folder.root
        ]

        def mappingYaml = getResource ("/tests/${source}/mapping.yaml")
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
        def generatedPath = Path.of (folder.root.toString()).resolve (packageName)

        def expectedFiles = collectRelativeOutputPaths (sourcePath, packageName)
            .sort ()
        def generatedFiles = collectPaths (generatedPath)
            .sort ()

        assert expectedFiles == generatedFiles

        expectedFiles.each {
            def expected = "${expectedPath}/$it"
            def generated = generatedPath.resolve (it)

            printUnifiedDiff (expected, generated)
            assertEquals(
                // ignore cr (ie. crlf vs lf)
                getResource (expected).text.replace('\r',''),
                generated.text.replace('\r','')
            )
        }
    }

    protected void runOnCustomFileSystem (FileSystem fs) {
        def source = testSet.name

        Path root = Files.createDirectory (fs.getPath ("source"))

        copy ("/tests/${source}", collectAbsoluteInputPaths ("/tests/${source}"), root)
        copy ("/tests/${source}", collectAbsoluteOutputPaths ("/tests/${source}"), root)

        Path api = root.resolve ('openapi.yaml')
        Path target = fs.getPath ('target')

        def processor = testSet.processor
        def options = [
            parser: 'OPENAPI4J', // swagger-parser does not work with fs
            apiPath: api.toUri ().toURL ().toString (),
            targetDir: target.toUri ().toURL ().toString ()
        ]

        def mappingYaml = root.resolve ('mapping.yaml')
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

        expectedFiles.each {
            def expected = expectedPath.resolve (it)
            def generated = generatedPath.resolve (it)

            printUnifiedDiff (expected, generated)
            assertEquals(
                // ignore cr (ie. crlf vs lf)
                expected.text.replace('\r',''),
                generated.text.replace('\r','')
            )
        }
    }

    /**
     * copy paths file system <=> file system
     */
    protected static void copy (Path source, Path target) {
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
    protected void copy (String source, List<String> sources, Path target) {
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
     * collect paths in file system
     */
    protected static List<String> collectPaths(Path source) {
        def files = []

        def found = Files.walk (source)
            .filter ({ f ->
                !Files.isDirectory (f)
            })

        found.forEach ({f ->
                files << source.relativize (f).toString ()
            })
        found.close ()

        return files
    }

    /**
     * collect input paths
     */
    protected List<String> collectAbsoluteInputPaths (String path) {
        collectAbsoluteResourcePaths (path, "inputs.yaml")
    }

    /**
     * collect output paths
     */
    protected List<String> collectAbsoluteOutputPaths (String path) {
        collectAbsoluteResourcePaths (path, "outputs.yaml")
    }

    /**
     * collect output paths, relative to packageName
     */
    protected List<String> collectRelativeOutputPaths (String path, String packageName) {
        collectRelativeResourcePaths (path, "outputs.yaml").collect {
            it.substring (packageName.size () + 1)
        }
    }

    /**
     * collect absolute paths from output.yaml in resources
     */
    protected List<String> collectAbsoluteResourcePaths (String path, String itemsYaml) {
        collectRelativeResourcePaths (path, itemsYaml).collect {
            "${path}/${it}".toString ()
        }
    }

    /**
     * collect paths from output.yaml in resources
     */
    protected List<String> collectRelativeResourcePaths (String path, String itemsYaml) {
        def source = getResource ("${path}/${itemsYaml}").text
        def mapper = createYamlParser ()
        def sourceItems = mapper.readValue (source, TestItems)
        sourceItems.items
    }

    /**
     * unified diff resources <=> file system
     */
    protected void printUnifiedDiff (String expected, Path generated) {
        def expectedLines = getResource (expected).readLines ()

        def patch = DiffUtils.diff (
            expectedLines,
            generated.readLines ())

        def diff = UnifiedDiffUtils.generateUnifiedDiff (
            getResource (expected).text,
            generated.toString (),
            expectedLines,
            patch,
            2
        )

        diff.each {
            println it
        }
    }

    /**
     * unified diff file system <=> file system
     */
    protected static void printUnifiedDiff (Path expected, Path generated) {
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

    protected String preparePath(String path) {
        // the openapi4j parser works properly with custom protocols. To load the test files from
        // the resources we the test "resource:" protocol

        // the swagger parser works with http(s) & file protocols only.
        // If it is something different (or nothing) it tries to find the given path as-is on the
        // file system. If that fails it tries to load the path as resource. To load the test files
        // from the resources it must not have a protocol.

        testSet.parser == "OPENAPI4J" ? "resource:${path}" : path
    }

    protected InputStream getResource (String path) {
        this.class.getResourceAsStream (path)
    }

    protected static ObjectMapper createYamlParser () {
        new ObjectMapper (new YAMLFactory ())
            .configure (DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

}
