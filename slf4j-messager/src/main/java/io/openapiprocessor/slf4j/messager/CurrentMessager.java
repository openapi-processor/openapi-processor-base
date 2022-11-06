/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.slf4j.messager;

import javax.annotation.processing.Messager;

public class CurrentMessager {
    static final ThreadLocal<Messager> currentMessager = new ThreadLocal<> ();

    public CurrentMessager() {
    }

    public CurrentMessager(Messager messager) {
        currentMessager.set (messager);
    }

    public Messager get() {
        return currentMessager.get ();
    }

    public void clear() {
        currentMessager.remove ();
    }
}
