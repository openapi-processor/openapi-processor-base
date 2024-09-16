/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.test

class Expected {
    TestSet testSet
    String packageName
    TestItemsReader reader

    String path
    Set<String> items

    Expected(TestSet testSet, String packageName, TestItemsReader reader) {
        this.testSet = testSet
        this.packageName = packageName
        this.reader = reader

        def sourcePath = "/tests/${testSet.name}"
        path = "${sourcePath}/${testSet.expected}"
        items = files (sourcePath, testSet.expected)
    }

    /**
     * get the expected files (from outputs.yaml) and strips the prefix.
     *
     * @param path the resource path of the test, i.e. /tests/{test-name}
     * @param stripPrefix prefix to strip, i.e. inputs/outputs folder
     * @return the expected files
     */
    private Set<String> files(String path, String stripPrefix) {
        def items = reader.read (path, testSet.outputs)

        def wanted = items.items.collect {
            it.substring (stripPrefix.size () + 1)
        }

        def result = new TreeSet<String> ()
        result.addAll (wanted)
        result
    }
}
