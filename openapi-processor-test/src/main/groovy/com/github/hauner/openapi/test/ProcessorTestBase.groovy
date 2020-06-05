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
        def expectedPath = "/tests/${source}/${packageName}"
        def generatedPath = Path.of (folder.root.toString()).resolve (packageName)

        def expectedFiles = collectExpectedPaths (expectedPath)
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
//        copy ("/tests/${source}/input", root)
        // expected results
        copy ("/tests/${source}", [
            "mapping.yaml", "openapi.yaml"
        ], root)
        copy ("/tests/${source}/${testSet.packageName}", root)


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
    protected void copy (String source, Path target) {
        copy (source, collectResourcePaths (source), target)
    }

    /**
     * copy paths resources <=> file system
     */
    protected void copy (String source, List<String> sources, Path target) {
        for (String p : sources) {
            String relativePath = p.substring (source.size ())

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
    private static List<String> collectPaths(Path source) {
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
     * collect expected paths, strips path prefix
     */
    private List<String> collectExpectedPaths (String path) {
        collectResourcePaths (path).collect {
            it.substring (path.size () + 1)
        }
    }

    /**
     * collect paths from resources
     */
    private List<String> collectResourcePaths (String path) {
        def files = []
        def folders = []

        this.class.getResourceAsStream (path).eachLine {
            String item = "${path}/$it"

            if (item.endsWith (".java") || item.endsWith (".yaml")) {
                files << item
            } else {
                folders << item
            }
        }

        for (String f: folders) {
            files.addAll (collectResourcePaths (f))
        }

        return files
    }

    /**
     * unified diff resources <=> file system
     */
    void printUnifiedDiff (String expected, Path generated) {
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

    private String preparePath(String path) {
        // the openapi4j parser works properly with custom protocols. To load the test files from
        // the resources we the test "resource:" protocol

        // the swagger parser works with http(s) & file protocols only.
        // If it is something different (or nothing) it tries to find the given path as-is on the
        // file system. If that fails it tries to load the path as resource. To load the test files
        // from the resources it must not have a protocol.

        testSet.parser == "OPENAPI4J" ? "resource:${path}" : path
    }

    private InputStream getResource (String path) {
        this.class.getResourceAsStream (path)
    }

}


/*

package com.github.hauner.openapi.core.processor

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.github.hauner.openapi.core.processor.mapping.Mapping
import com.github.hauner.openapi.core.processor.mapping.VersionedMapping
import com.github.hauner.openapi.core.processor.mapping.Parameter
import com.github.hauner.openapi.core.processor.mapping.ParameterDeserializer
import com.github.hauner.openapi.core.processor.mapping.version.Mapping as VersionMapping
import com.github.hauner.openapi.core.processor.mapping.v2.Mapping as MappingV2
import com.github.hauner.openapi.core.processor.mapping.v2.Parameter as ParameterV2
import com.github.hauner.openapi.core.processor.mapping.v2.ParameterDeserializer as ParameterDeserializerV2


class MappingReader {

    VersionedMapping read (String typeMappings) {
        if (typeMappings == null || typeMappings.empty) {
            return null
        }

        def mapping
        if (isUrl (typeMappings)) {
            mapping = new URL (typeMappings).text
        } else if (isFileName (typeMappings)) {
            mapping = new File (typeMappings).text
        } else {
            mapping = typeMappings
        }

        def versionMapper = createVersionParser ()
        VersionMapping version = versionMapper.readValue (mapping, VersionMapping)
        if (version.v2) {
            def mapper = createV2Parser ()
            mapper.readValue (mapping, MappingV2)
        } else {
            // assume v1
            def mapper = createYamlParser ()
            mapper.readValue (mapping, Mapping)
        }
    }

    private ObjectMapper createV2Parser () {
        SimpleModule module = new SimpleModule ()
        module.addDeserializer (ParameterV2, new ParameterDeserializerV2 ())

        new ObjectMapper (new YAMLFactory ())
            .configure (DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setPropertyNamingStrategy (PropertyNamingStrategy.KEBAB_CASE)
            .registerModules (new KotlinModule (), module)
    }

    private ObjectMapper createYamlParser () {
        SimpleModule module = new SimpleModule ()
        module.addDeserializer (Parameter, new ParameterDeserializer ())

        new ObjectMapper (new YAMLFactory ())
            .configure (DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setPropertyNamingStrategy (PropertyNamingStrategy.KEBAB_CASE)
            .registerModule (module)
    }

    private ObjectMapper createVersionParser () {
        new ObjectMapper (new YAMLFactory ())
            .configure (DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setPropertyNamingStrategy (PropertyNamingStrategy.KEBAB_CASE)
            .registerModule (new KotlinModule ())
    }

    private boolean isFileName (String name) {
        name.endsWith ('.yaml') || name.endsWith ('.yml')
    }

    private boolean isUrl (String source) {
        try {
            new URL (source)
        } catch (MalformedURLException ignore) {
            false
        }
    }

}

 */
