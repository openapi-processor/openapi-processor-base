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
}
