/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.test

import groovy.transform.CompileStatic

import javax.tools.FileObject
import javax.tools.ForwardingJavaFileManager
import javax.tools.JavaFileObject
import javax.tools.StandardJavaFileManager
import java.nio.file.Path

@CompileStatic
class MemoryFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
    MemoryFileManager(StandardJavaFileManager fileManager) {
        super(fileManager)
    }

    @Override
    JavaFileObject getJavaFileForOutput(
            Location location,
            String className,
            JavaFileObject.Kind kind,
            FileObject sibling
    ) throws IOException {
        return new MemoryFile(className, kind)
    }

    Iterable<? extends JavaFileObject> getJavaFileObjectsFromPaths(Iterable<Path> paths) {
        return fileManager.getJavaFileObjectsFromPaths(paths)
    }
}
