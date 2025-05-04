/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-test
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.test

import java.nio.file.Files
import java.nio.file.Path

class Collector {
    /**
     * collect paths in source path relative to source. It will convert all paths to use "/" as path separator.
     *
     * @param source source path
     * @return list of relative path names
     */
    static List<String> collectPaths(Path source) {
        List<String> files = []

        def found = Files.walk (source)
            .filter ({ f ->
                !Files.isDirectory (f)
            })

        found.forEach ({f ->
                files << source.relativize (f).toString ()
            })
        found.close ()

        files.collect {
            it.replace ('\\', '/')
        }
    }

    /**
     * collect map from path (relative to source) to its absolute path. It will convert all paths keys to use "/" as
     * path separator.
     *
     * @param source source path
     * @return map from relative path name to absolute path
     */
    static Map<String, Path> collectPathDictionary(Path source) {
        def files = new TreeMap<String, Path>()

        def found = Files.walk (source)
            .filter ({ f ->
                !Files.isDirectory (f)
            })

        found.forEach ({f ->
                def relativeKey = source.relativize (f).toString ().replace ('\\', '/')
                files.put(relativeKey, f)
            })
        found.close ()

        return files
    }
}
