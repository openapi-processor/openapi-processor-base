/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.test

import io.openapiprocessor.test.api.OpenApiProcessorTest

import java.nio.file.Files
import java.nio.file.Path

class Test {
    enum ResolveType {
        PATH_IN_TARGET, // resolve to package name in targetDir, i.e. to "model" sub package
        PATH_IN_SOURCE // resolve to path in test outputs, i.e. "model/default"/"model/record"
    }

    private TestSet testSet
    private TestFiles testFiles
    private OpenApiProcessorTest testProcessor

    private Mapping mapping
    private String packageName

    Test(TestSet testSet, TestFiles testFiles) {
        this.testSet = testSet
        this.testFiles = testFiles
        this.testProcessor = testSet.processor as OpenApiProcessorTest
    }

    void init() {
        testFiles.init(testSet)

        mapping = testFiles.getMapping(testSet)
        mapping.setModelType(testSet.modelType)
        packageName = mapping.packageName
    }

    String getParser() {
        return testSet.parser
    }

    URI getApiPath() {
        return testFiles.getApiPath(testSet)
    }

    String getTargetDir() {
        return testFiles.getTargetDir()
    }

    Mapping getMapping() {
        return mapping
    }

    /**
     * get the expected files (from outputs.yaml). Returns a map of file to location. The location may be null
     * (with classic layout) or "src" or "resources" (with standard layout).
     *
     * @return the expected files map
     */
    Map<String, String> getExpectedFiles() {
        def sourceRoot = testProcessor.sourceRoot
        def resourceRoot = testProcessor.resourceRoot

        def result = new TreeMap<String, String>()

        def testItems = testFiles.getOutputFiles(testSet)
        def files = testItems.items.collect {
            it.substring (testSet.expected.size () + 1)
        }

        files.each {
            if (sourceRoot != null && it.startsWith(sourceRoot)) {
                result[it.substring(sourceRoot.length() + 1)] = sourceRoot

            } else if (resourceRoot != null && it.startsWith(resourceRoot)) {
                result[it.substring(resourceRoot.length() + 1)] = resourceRoot

            } else {
                result[it] = null
            }
        }

        return result
    }

    Set<String> getGeneratedSourceFiles() {
        def sourceRoot = testProcessor.sourceRoot
        def targetPath = Path.of(testFiles.targetDir)
        def sourcePath = getGeneratedSourcePath(targetPath, sourceRoot, packageName)
        return getGeneratedFiles(sourcePath)
    }

    Set<String> getGeneratedResourceFiles() {
        def resourceRoot = testProcessor.resourceRoot
        if (resourceRoot == null) {
            return Set.of()
        }

        def targetPath = Path.of(testFiles.targetDir)
        def sourcePath = getGeneratedResourcePath(targetPath, resourceRoot)
        return getGeneratedFiles(sourcePath)
    }

    Path getExpectedFilePath(String file, String sourcePrefix) {
        def expectedFilePath = file
        if (sourcePrefix != null) {
            expectedFilePath = "${sourcePrefix}/${file}"
        }

        return resolveModelTypeInSource(expectedFilePath)
    }

    Path getGeneratedFilePath(String file, String sourcePrefix) {
        def sourceRoot = testProcessor.sourceRoot
        def generatedFilePath = file

        if (sourcePrefix == sourceRoot) {
            generatedFilePath = "${packageName}/${generatedFilePath}"
        }

        if (sourcePrefix != null) {
            generatedFilePath = "${sourcePrefix}/${generatedFilePath}"
        }

        return resolveModelTypeInTarget(generatedFilePath)
    }

    Set<String> resolveModelTypeInTarget(Collection<String> paths) {
        return resolveModelType(paths, ResolveType.PATH_IN_TARGET)
    }

    Path resolveModelTypeInTarget(String path) {
        return testFiles.getTargetPath(resolveModelTypeName(path, ResolveType.PATH_IN_TARGET))
    }

    Path resolveModelTypeInSource(String path) {
        return testFiles.getSourcePath(testSet, resolveModelTypeName(path, ResolveType.PATH_IN_SOURCE))
    }

    static boolean printUnifiedDiff (Path expected, Path generated) {
        return Diff.printUnifiedDiff(expected, generated)
    }

    void print() {
        testFiles.printTree()
    }

    private Set<String> resolveModelType(Collection<String> paths, ResolveType type) {
        def result = new TreeSet<String> ()

        paths.each {
            result.add(resolveModelTypeName(it, type))
        }

        result
    }

    private String resolveModelTypeName(String path, ResolveType type) {
        def model = "unset"

        if (type == ResolveType.PATH_IN_TARGET) {
            model = 'model'

        } else if (type == ResolveType.PATH_IN_SOURCE) {
            model = 'model/default'

            if (testSet.modelType == 'record') {
                model = 'model/record'
            }
        }

        def result = path.replaceFirst("<model>", model)
        return result
    }

    private static Path getGeneratedSourcePath(Path target, String source, String packageName) {
        def path = target
        if (source != null) {
            path = path.resolve(source)
        }
        return path.resolve (packageName)
    }

    private static Path getGeneratedResourcePath(Path target, String resource) {
        def path = target
        if (resource != null) {
            path = path.resolve(resource)
        }
        return path
    }

    /**
     * get the generated files.
     *
     * @param path path of the generated files
     * @return the generated files
     */
    private static Set<String> getGeneratedFiles (Path path) {
        def result = new TreeSet<String> ()
        if (Files.exists(path)) {
            result.addAll (Collector.collectPaths (path))
        }
        result
    }
}
