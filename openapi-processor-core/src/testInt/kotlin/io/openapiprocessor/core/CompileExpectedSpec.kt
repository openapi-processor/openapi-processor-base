/*
 * Copyright 2023 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.openapiprocessor.core.parser.ParserType
import io.openapiprocessor.test.*
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.net.URI
import java.nio.file.Path
import java.util.*
import javax.tools.*


class CompileExpectedSpec: StringSpec({

    class MemoryFile(name: String, kind: JavaFileObject.Kind)
        : SimpleJavaFileObject(URI("string:///${name}"), kind) {

        override fun openOutputStream(): OutputStream {
            return ByteArrayOutputStream()
        }
    }

    class MemoryFileManager(fileManager: StandardJavaFileManager)
        : ForwardingJavaFileManager<StandardJavaFileManager>(fileManager) {

        override fun getJavaFileForOutput(
            location: JavaFileManager.Location,
            className: String,
            kind: JavaFileObject.Kind,
            sibling: FileObject
        ): JavaFileObject {
            return MemoryFile(className, kind)
        }

        fun getJavaFileObjectsFromPaths(paths: Iterable<Path>): Iterable<JavaFileObject> {
            return fileManager.getJavaFileObjectsFromPaths(paths)
        }
    }


    for (testSet in sources()) {
        "compile - $testSet".config(enabled = true) {
            val itemsReader = TestItemsReader(ResourceReader(CompileExpectedSpec::class.java))

            val source = testSet.name
            val sourcePath = "/tests/$source"

            val compilePaths = mutableListOf<Path>()
            if (source != "generated") {
                compilePaths.add(Path.of("src/testInt/resources/compile/Generated.java"))
            }
            compilePaths.add(Path.of("src/testInt/resources/compile/Mapping.java"))
            compilePaths.add(Path.of("src/testInt/resources/compile/Parameter.java"))
            compilePaths.add(Path.of("src/testInt/resources/compile/Prefix.java"))
            compilePaths.add(Path.of("src/testInt/resources/compile/jakarta/Constraint.java"))
            compilePaths.add(Path.of("src/testInt/resources/compile/jakarta/ConstraintValidator.java"))
            compilePaths.add(Path.of("src/testInt/resources/compile/jakarta/ConstraintValidatorContext.java"))
            compilePaths.add(Path.of("src/testInt/resources/compile/jakarta/NotNull.java"))
            compilePaths.add(Path.of("src/testInt/resources/compile/jakarta/Payload.java"))
            compilePaths.add(Path.of("src/testInt/resources/compile/jakarta/Size.java"))
            compilePaths.add(Path.of("src/testInt/resources/compile/jakarta/DecimalMin.java"))
            compilePaths.add(Path.of("src/testInt/resources/compile/jakarta/Valid.java"))
            compilePaths.add(Path.of("src/testInt/resources/compile/javax/Valid.java"))
            compilePaths.add(Path.of("src/testInt/resources/compile/oap/FooA.java"))
            compilePaths.add(Path.of("src/testInt/resources/compile/oap/FooB.java"))
            compilePaths.add(Path.of("src/testInt/resources/compile/oap/FooC.java"))
            compilePaths.add(Path.of("src/testInt/resources/compile/oap/Wrap.java"))
            compilePaths.add(Path.of("src/testInt/resources/compile/oap/Bar1.java"))
            compilePaths.add(Path.of("src/testInt/resources/compile/oap/Bar2.java"))
            compilePaths.add(Path.of("src/testInt/resources/compile/oap/Something.java"))
            compilePaths.add(Path.of("src/testInt/resources/compile/oap/SomethingElse.java"))
            compilePaths.add(Path.of("src/testInt/resources/compile/oap/Annotation.java"))
            compilePaths.add(Path.of("src/testInt/resources/compile/oap/AnnotationA.java"))
            compilePaths.add(Path.of("src/testInt/resources/compile/oap/AnnotationB.java"))
            compilePaths.add(Path.of("src/testInt/resources/compile/oap/AnnotationC.java"))
            compilePaths.add(Path.of("src/testInt/resources/compile/oap/ParamAnnotation.java"))
            compilePaths.add(Path.of("src/testInt/resources/compile/reactor/Mono.java"))
            compilePaths.add(Path.of("src/testInt/resources/compile/reactor/Flux.java"))
            compilePaths.add(Path.of("src/testInt/resources/compile/spring/ResponseEntity.java"))

            var expected = itemsReader.read(sourcePath, "outputs.yaml").items
            expected = expected.filter { ! it.endsWith("properties") }
            val expectedFileNames = expected.map { it.replaceFirst("<model>", "model/${testSet.modelType}") }
            expectedFileNames.forEach {
                compilePaths.add(Path.of("src/testInt/resources${sourcePath}/$it"))
            }

            if (itemsReader.exists(sourcePath, "compile.yaml")) {
                val additionalFileNames = itemsReader.read(sourcePath, "compile.yaml").items
                additionalFileNames.forEach {
                    compilePaths.add(Path.of("src/testInt/resources/$it"))
                }
            }

            val diagnostics = DiagnosticCollector<JavaFileObject>()
            val compiler = ToolProvider.getSystemJavaCompiler()
            val manager = MemoryFileManager(compiler.getStandardFileManager(diagnostics, null, null))

            val options = listOf<String>()
            val compilationUnit = manager.getJavaFileObjectsFromPaths(compilePaths)
            val task = compiler.getTask(null, manager, diagnostics, options, null, compilationUnit)
            val success = task.call()
            if(!success) {
                for (diagnostic in diagnostics.diagnostics) {
                    println("CompileSpec: compile error at ${diagnostic.source.name}:${diagnostic.lineNumber}, ${diagnostic.getMessage(Locale.ENGLISH)}")
                }
            }

            success.shouldBeTrue()
        }
    }
})

private fun sources(): Collection<TestSet> {
    val compile30 = ALL_30.map {
        testSet(it.name, ParserType.INTERNAL, it.openapi, model = "default", outputs = it.outputs, expected = it.expected)
    }

    val compile31 = ALL_31.map {
        testSet(it.name, ParserType.INTERNAL, it.openapi, model = "default", outputs = it.outputs, expected = it.expected)
    }

    val compile30r = ALL_30.filter { it.modelTypes.contains(ModelTypes.RECORD) }.map {
        testSet(it.name, ParserType.INTERNAL, it.openapi, model = "record", outputs = it.outputs, expected = it.expected)
    }

    val compile31r = ALL_31.filter { it.modelTypes.contains(ModelTypes.RECORD) }.map {
        testSet(it.name, ParserType.INTERNAL, it.openapi, model = "record", outputs = it.outputs, expected = it.expected)
    }

    return compile30 + compile31 + compile30r + compile31r
}
