/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.test

import java.nio.file.Path
import java.nio.file.Paths

class TestFilesNative implements TestFiles {
    private File target
    private ResourceReader resource

    TestFilesNative(File target, ResourceReader resource) {
        this.target = target
        this.resource = resource
    }

    @Override
    void init(TestSet testSet) {
        // nop
    }

    @Override
    URI getApiPath(TestSet testSet) {
        return URI.create("resource:/tests/${testSet.name}/inputs/${testSet.openapi}")
    }

    @Override
    URI getTargetDir() {
        return target.toURI()
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
        return new TestItemsReader(resource).read("/tests/${testSet.name}", testSet.outputs)
    }

    @Override
    TestItems getCompileFiles(TestSet testSet) {
        def itemsReader = new TestItemsReader(resource)
        def sourcePath = "/tests/${testSet.name}"

        if (!itemsReader.exists(sourcePath, "compile.yaml")) {
            return null
        }

        return new TestItemsReader(resource).read(sourcePath, "compile.yaml")
    }

    @Override
    Path getSourcePath(TestSet testSet, String path) {
        def testFile = "/tests/${testSet.name}/${testSet.expected}/$path"
        def testFileResource = resource.getResourceUrl(testFile)
        if (!testFileResource) {
            println("ERROR: missing file '$testFile'!")
        }
        return Paths.get(testFileResource.toURI())
    }

    @Override
    Path getTargetPath(String file) {
        return Paths.get(target.toURI().resolve(file))
    }

    @Override
    void printTree() {
        // todo
    }
}
