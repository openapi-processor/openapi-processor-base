/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-test
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.test.api;

/**
 * (integration) test support.
 * provides the destination folders in the targetDir depending on the targetDir layout. This
 * information is available only <b>after</b> running the processor.
 */
public interface OpenApiProcessorTest {
    /**
     * provides the target source root folder in targetDir.
     *
     * @return the source root below the targetDir or null if the targetDir is the source root.
     */
    String getSourceRoot();

    /**
     * provides the target resource root folder in targetDir.
     *
     * @return the resource root below the targetDir or null.
     */
    String getResourceRoot();
}
