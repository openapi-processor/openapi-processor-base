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
        def reader = new TestItemsReader(resource)
        def testSetPath = "/tests/${testSet.name}"

        // copy input files to file system
        def inputs = reader.read(testSetPath, testSet.inputs)
        copy(testSetPath, inputs.items, source)

        // copy expected files to file system
        def outputs = reader.read(testSetPath, testSet.outputs)
        outputs = outputs.resolvePlaceholder(getModelTypePath(testSet))
        copy(testSetPath, outputs.items, source)
    }

    @Override
    URI getApiPath(TestSet testSet) {
        return source.resolve ("inputs/${testSet.openapi}").toUri()
    }

    @Override
    URI getTargetDir() {
        return target.toUri()
    }

    private static String getModelTypePath(TestSet testSet) {
        if (testSet.modelType == 'default' || testSet.modelType == 'model') {
            return 'model/default'
        }
        else if (testSet.modelType == 'record') {
            return 'model/record'
        }
        else {
            // error
            return "unset"
        }
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
