/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.mockk.every
import io.mockk.verify
import io.openapiprocessor.core.builder.api.`interface`
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.model.Api
import io.openapiprocessor.core.model.DataTypes
import io.openapiprocessor.core.model.datatypes.*
import io.openapiprocessor.core.tempFolder
import io.openapiprocessor.core.writer.SourceFormatter
import io.openapiprocessor.core.writer.WriterFactory
import java.io.File
import java.io.StringWriter
import java.nio.file.Path
import io.mockk.mockk as stub

class ApiWriterSpec: StringSpec({
    isolationMode = IsolationMode.InstancePerTest

    val target = tempFolder()
    val options = ApiOptions()
    val gwStub = SimpleGeneratedWriter(options)
    val wfStub = stub<WriterFactory>()
    val writer = StringWriter()
    val nf = NullFormatter()

    beforeTest {
        options.packageName = "io.openapiprocessor.test"
        options.targetDir = listOf(target.toString(), "java", "src").joinToString(File.separator)

        every { wfStub.createWriter(any(), any()) }
            .answers { writer }
    }

    "writes model enum data type sources" {
        val dts = DataTypes()

        val dtA = StringEnumDataType(DataTypeName("Foo", "Foo"), "${options.packageName}.model")
        dts.add(dtA)
        dts.addRef(dtA.getName())

        val dtB = StringEnumDataType(DataTypeName("Fooo", "FoooX"), "${options.packageName}.model")
        dts.add(dtB)
        dts.addRef(dtB.getName())

        val api = Api(dataTypes = dts)

        val enumWriter = stub<StringEnumWriter>(relaxed = true)
        ApiWriter(options, stub(relaxed = true), stub(), stub(), enumWriter, stub(), nf, wfStub)
            .write(api)

        verify(exactly = 1) { enumWriter.write(any(), dtA) }
        verify(exactly = 1) { enumWriter.write(any(), dtB) }
    }

    "re-formats enum data type source" {
        val formatter = stub<SourceFormatter>(relaxed = true)

        val dts = DataTypes()
        dts.add (StringEnumDataType(DataTypeName("Foo"), "${options.packageName}.model"))
        dts.addRef("Foo")
        val api = Api(dataTypes = dts)

        ApiWriter(options, stub(relaxed = true), stub(), stub(), stub(relaxed = true), stub(), formatter, wfStub)
            .write(api)

        verify (exactly = 2) { formatter.format(any()) }
    }

    "writes interface sources" {
        val itfs = listOf(
            `interface`("Foo", options.getSourceDir("model").toString()) {},
            `interface`("Bar", options.getSourceDir("model").toString()) {}
        )
        val api = Api(itfs)

        val itfWriter = stub<InterfaceWriter>(relaxed = true)
        ApiWriter(options, stub(relaxed = true), itfWriter, stub(), stub(), stub(), nf, wfStub)
            .write (api)

        verify(exactly = 1) { itfWriter.write(any(), itfs[0]) }
        verify(exactly = 1) { itfWriter.write(any(), itfs[1]) }
    }

    "re-formats interface source" {
        val formatter = stub<SourceFormatter>(relaxed = true)

        val itfs = listOf(
            `interface`("Foo", options.getSourceDir("model").toString()) {}
        )
        val api = Api(itfs)

        ApiWriter(options, stub(relaxed = true), stub(relaxed = true), stub(), stub(), stub(), formatter, wfStub)
            .write(api)

        verify (exactly = 2) { formatter.format(any()) }
    }

    "writes model data type sources" {
        val dts = DataTypes()

        val dtA = ObjectDataType(DataTypeName("Foo", "Foo"), "${options.packageName}.model")
        dts.add(dtA)
        dts.addRef(dtA.getName())

        val dtB = ObjectDataType(DataTypeName("Fooo", "FoooX"), "${options.packageName}.model")
        dts.add(dtB)
        dts.addRef(dtB.getName())

        val api = Api(dataTypes = dts)

        val dtWriter = stub<DataTypeWriter>(relaxed = true)
        ApiWriter(options, stub(relaxed = true), stub(), dtWriter, stub(), stub(), nf, wfStub)
            .write (api)

        verify(exactly = 1) { dtWriter.write(any(), dtA) }
        verify(exactly = 1) { dtWriter.write(any(), dtB) }
    }

    "re-formats model data type source" {
        val formatter = stub<SourceFormatter>(relaxed = true)

        val dts = DataTypes()
        dts.add (ObjectDataType(DataTypeName("Foo"), "${options.packageName}.model"))
        dts.addRef("Foo")
        val api = Api(dataTypes = dts)

        ApiWriter(options, stub(relaxed = true), stub(), stub(relaxed = true), stub(), stub(), formatter, wfStub)
            .write(api)

        verify (exactly = 2) { formatter.format(any()) }
    }

    "writes interface data type sources" {
        val dts = DataTypes()

        val dtA = InterfaceDataType(DataTypeName("Foo", "Foo"), "${options.packageName}.api")
        dts.add(dtA)
        dts.addRef(dtA.getName())

        val dtB = InterfaceDataType(DataTypeName("Fooo", "FoooX"), "${options.packageName}.api")
        dts.add(dtB)
        dts.addRef(dtB.getName())

        val api = Api(dataTypes = dts)

        val dtWriter = stub<InterfaceDataTypeWriter>(relaxed = true)
        ApiWriter(options, stub(relaxed = true), stub(), stub(), stub(), dtWriter, nf, wfStub)
            .write (api)

        verify(exactly = 1) { dtWriter.write(any(), dtA) }
        verify(exactly = 1) { dtWriter.write(any(), dtB) }
    }

    "re-formats interface data type source" {
        val formatter = stub<SourceFormatter>(relaxed = true)

        val dts = DataTypes()
        dts.add (InterfaceDataType(DataTypeName("Foo"), "${options.packageName}.api"))
        dts.addRef("Foo")
        val api = Api(dataTypes = dts)

        ApiWriter(options, stub(relaxed = true), stub(), stub(), stub(), stub(relaxed = true), formatter, wfStub)
            .write(api)

        verify (exactly = 2) { formatter.format(any()) }
    }

    "generates model for object data types only" {
        val dtWriter = io.mockk.mockk<DataTypeWriter>()

        val dt = DataTypes()
        dt.add(MappedDataType("Type", "${options.packageName}.model"))
        dt.add("simple", StringDataType())
        val api = Api(dataTypes = dt)

        // when:
        ApiWriter(options, gwStub, stub(), dtWriter, stub(), stub()).write (api)

        // then:
        verify(exactly = 0) {
            dtWriter.write (any(), any())
        }
    }

    "does not re-format sources if code formatting is disabled" {
        val formatter = stub<SourceFormatter>(relaxed = true)

        val dts = DataTypes()
        dts.add (InterfaceDataType(DataTypeName("Foo"), "${options.packageName}.api"))
        dts.addRef("Foo")
        val api = Api(dataTypes = dts)

        options.formatCode = false
        ApiWriter(options, stub(relaxed = true), stub(), stub(), stub(), stub(relaxed = true), formatter, wfStub)
            .write(api)

        verify (exactly = 0) { formatter.format(any()) }
    }
})

private fun ApiOptions.getSourceDir(pkg: String): Path {
    return Path.of(
        listOf(
            targetDir,
            packageName.replace(".", File.separator),
            pkg)
        .joinToString(File.separator))
}
