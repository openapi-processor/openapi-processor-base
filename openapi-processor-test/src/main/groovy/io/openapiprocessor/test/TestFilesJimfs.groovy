/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.test

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class TestFilesJimfs implements TestFiles {
    private static final Logger log = LoggerFactory.getLogger (TestFilesJimfs)

    private FileSystem fs
    private ResourceReader resource

    private Path source // test set files source (without "/inputs/${testSet.name}" prefix
    private Path target

    TestFilesJimfs(FileSystem fs, ResourceReader resource) {
        this.fs = fs
        this.resource = resource
        this.source = Files.createDirectory (fs.getPath ("source"))
        this.target = fs.getPath ('target')
    }

    @Override
    void init(TestSet testSet) {
        copyTestFiles("/tests/${testSet.name}", source)
    }

    @Override
    URI getApiPath(TestSet testSet) {
        return source.resolve ("inputs/${testSet.openapi}").toUri()
    }

    @Override
    URI getTargetDir() {
        return target.toUri()
    }

    @Override
    Mapping getMapping(TestSet testSet) {
        def api = URI.create("/tests/${testSet.name}/inputs/${testSet.openapi}")
        def mapping = api.resolve("mapping.yaml").toString()
        def url = resource.getResourceUrl(mapping)
        if (url == null) {
            println("ERROR: missing mapping file '$mapping'!")
        }

        return Mapping.createMapping(Paths.get(url.toURI()), testSet.defaultOptions)
    }

    @Override
    TestItems getOutputFiles(TestSet testSet) {
        return new TestItemsReader(resource).read( "/tests/${testSet.name}", testSet.outputs)
    }

    @Override
    Path getSourcePath(TestSet testSet, String file) {
        return Paths.get(source.resolve("${testSet.expected}/${file}").toUri())
    }

    @Override
    Path getTargetPath(String file) {
        return Paths.get(target.resolve(file).toUri())
    }

    @Override
    void printTree() {
        Files.walk(fs.getPath("/"))
            .forEach {
                println "${it.toAbsolutePath()}"
            }
    }

    /**
     * copy all files from resource folder to the target path
     * @param parent the root resource folder
     * @param target the target path
     */
    private copyTestFiles(String parent, Path target) {
        def sourcePath = Paths.get(resource.getResourceUrl(parent).toURI())
        def sourceFiles =  getSourceFiles(sourcePath)
        copy(parent, sourceFiles, target)
    }

    private static List<String> getSourceFiles (Path path) {
        def result = new ArrayList<String> ()
        if (Files.exists(path)) {
            result.addAll (Collector.collectPaths (path))
        }
        result
    }

    /**
     * copy paths: resources <=> file system
     *
     * @param parent source parent
     * @param sources source files
     * @param target target folder
     */
    private void copy (String parent, List<String> sources, Path target) {
        for (String p : sources) {
            def pWithParent = "${parent}/${p}"

            try {
                Path targetPath = target.resolve (p)
                Files.createDirectories (targetPath.getParent ())

                InputStream src = resource.getResource(pWithParent)
                OutputStream dst = Files.newOutputStream (targetPath)
                src.transferTo (dst)
            } catch (Exception ex) {
                log.error ("failed to copy {}", pWithParent, ex)
            }
        }
    }
}
