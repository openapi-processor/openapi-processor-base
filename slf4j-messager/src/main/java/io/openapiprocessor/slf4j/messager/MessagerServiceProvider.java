/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.slf4j.messager;

import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.helpers.NOPMDCAdapter;
import org.slf4j.spi.MDCAdapter;
import org.slf4j.spi.SLF4JServiceProvider;

public class MessagerServiceProvider implements SLF4JServiceProvider {
    public static String REQUESTED_API_VERSION = "2.0.99"; // !final

    private ILoggerFactory loggerFactory;
    private IMarkerFactory markerFactory;
    private MDCAdapter mdcAdapter;

    @Override
    public ILoggerFactory getLoggerFactory () {
        return loggerFactory;
    }

    @Override
    public IMarkerFactory getMarkerFactory () {
        return markerFactory;
    }

    @Override
    public MDCAdapter getMDCAdapter () {
        return mdcAdapter;
    }

    @Override
    public String getRequestedApiVersion () {
        return REQUESTED_API_VERSION;
    }

    @Override
    public void initialize () {
        loggerFactory = new MessagerLoggerFactory ();
        markerFactory = new BasicMarkerFactory ();
        mdcAdapter = new NOPMDCAdapter ();
    }
}
