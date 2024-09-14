/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.options

import io.openapiprocessor.core.converter.options.TargetDirLayout.Companion.isStandard

class TargetDirOptions {
    /**
     * enable/disable clearing of targetDir (optional).
     */
    var clear = true

    /**
     * the layout of the target dir
     *
     * classic: targetDir/packages...
     * standard: targetDir/java, targetDir/resources
     */
    var layout = TargetDirLayout.CLASSIC

    val standardLayout: Boolean get() = isStandard(layout)
}
