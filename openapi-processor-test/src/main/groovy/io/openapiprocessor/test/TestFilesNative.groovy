/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.test

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
        return Mapping.createMapping(
                resource.getResource("/tests/${testSet.name}/inputs/mapping.yaml"),
                testSet.defaultOptions)
    }

    @Override
    TestItems getOutputFiles(TestSet testSet) {
        return new TestItemsReader(resource).read( "/tests/${testSet.name}", testSet.outputs)
    }
}
