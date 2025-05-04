/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.test

/**
 * input.yaml / output.yaml items.
 */
class TestItems {
    private String prefix
    private List<String> items

    TestItems() {
    }

    private TestItems(List<String> items) {
        this.items = items
    }

    String getPrefix() {
        return prefix
    }

    List<String> getItems() {
        return items
    }

    void setItems(List<String> items) {
        this.items = items
    }

    /** convert to absolute paths */
    List<String> addPrefix(String path) {
        items.collect {
            "${path}/${it}".toString ()
        }
    }

    TestItems resolvePlaceholder(String replacement) {
        def results = new TreeSet<String> ()

        items.each {
            results.add(resolveModel(it, replacement))
        }

        return new TestItems(results as List<String>)
    }

    private static String resolveModel(String path, String replacement) {
        return path.replaceFirst("<model>", replacement)
    }
}
