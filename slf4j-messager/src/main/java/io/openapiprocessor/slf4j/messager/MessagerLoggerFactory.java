/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.slf4j.messager;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public class MessagerLoggerFactory implements ILoggerFactory {

    public MessagerLoggerFactory() {
        MessagerLogger.init ();
    }

    @Override
    public Logger getLogger (String name) {
        return new MessagerLogger (name, new CurrentMessager ());
    }
}
