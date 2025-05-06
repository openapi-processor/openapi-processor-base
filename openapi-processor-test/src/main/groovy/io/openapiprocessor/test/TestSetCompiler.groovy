/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.test

import javax.tools.*
import java.nio.file.Path

/**
 * used to compile test sets.
 */
class TestSetCompiler {
    TestSet testSet
    TestFiles testFiles

    TestSetCompiler(TestSet testSet, TestFiles testFiles) {
        this.testSet = testSet
        this.testFiles = testFiles
    }

    /**
     * compiles a test-set
     *
     * @return true on success, false on failure
     */
    boolean run() {
        def sourcePath = "/tests/${testSet.name}"

        def compilePaths = []

        // stuff used by all tests
        compilePaths.add(Path.of("src/testInt/resources/compile/Generated.java"))
        compilePaths.add(Path.of("src/testInt/resources/compile/Mapping.java"))
        compilePaths.add(Path.of("src/testInt/resources/compile/Parameter.java"))

        def expectedFiles = testFiles.getOutputFiles(testSet).items
        expectedFiles = expectedFiles.findAll { item -> !item.endsWith(".properties") }

        def expectedFileNames = expectedFiles
                .collect { it.replaceFirst("<model>", "_${testSet.modelType}_") }

        expectedFileNames.forEach {
            compilePaths.add(Path.of("src/testInt/resources${sourcePath}/$it"))
        }

        def compileFiles = testFiles.getCompileFiles(testSet)
        if(compileFiles) {
            compileFiles.items.forEach {
                if (it.startsWith("not ")) {
                    compilePaths.remove(Path.of("src/testInt/resources/${it.substring(4)}"))
                } else {
                    compilePaths.add(Path.of("src/testInt/resources/$it"))
                }
            }
        }

        def diagnostics = new DiagnosticCollector<JavaFileObject>()
        def compiler = ToolProvider.getSystemJavaCompiler()
        def manager = new MemoryFileManager(compiler.getStandardFileManager(diagnostics, null, null))

        def options = []
        def compilationUnit = manager.getJavaFileObjectsFromPaths(compilePaths)
        def task = compiler.getTask(null, manager, diagnostics, options, null, compilationUnit)
        def success = task.call()
        if (!success) {
            for (diagnostic in diagnostics.diagnostics) {
                println("CompileSpec: compile error at ${diagnostic.source.name}:${diagnostic.lineNumber}, ${diagnostic.getMessage(Locale.ENGLISH)}")
            }
        }

        return success
    }
}
