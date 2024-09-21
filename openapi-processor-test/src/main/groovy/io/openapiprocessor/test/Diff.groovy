/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.test

import com.github.difflib.DiffUtils
import com.github.difflib.UnifiedDiffUtils

import java.nio.file.Path

class Diff {

    /**
     * unified diff file path <=> file path
     *
     * @param expected file path
     * @param generated file path
     *
     * @return true if there is a difference
     */
    static boolean printUnifiedDiff (Path expected, Path generated) {
        def patch = DiffUtils.diff (
            expected.readLines (),
            generated.readLines ())

        def diff = UnifiedDiffUtils.generateUnifiedDiff (
            expected.toString (),
            generated.toString (),
            expected.readLines (),
            patch,
            4
        )

        if (!patch.deltas.isEmpty()) {
            println "$expected"
        }
        diff.each {
            println it
        }

        return !patch.deltas.isEmpty ()
    }
}
