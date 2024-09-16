/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.openapiprocessor.core.builder.api.`interface`
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.converter.options.TargetDirLayout
import io.openapiprocessor.core.model.Api
import io.openapiprocessor.core.model.DataTypes
import io.openapiprocessor.core.model.Resource
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
    val writer = StringWriter()
    val factoryStub = stub<WriterFactory>()

    fun createApiWriter(
        generatedWriter: GeneratedWriter = stub(relaxed = true),
        validationWriter: ValidationWriter = stub(relaxed = true),
        interfaceWriter: InterfaceWriter = stub(),
        dataTypeWriter: DataTypeWriter = stub(),
        enumWriter: StringEnumWriter = stub(),
        interfaceDataTypeWriter: InterfaceDataTypeWriter = stub(),
        additionalWriters: List<AdditionalWriter> = emptyList(),
        formatter: SourceFormatter = NullFormatter()
    ): ApiWriter {
        return ApiWriter(
            options,
            generatedWriter,
            validationWriter,
            interfaceWriter,
            dataTypeWriter,
            enumWriter,
            interfaceDataTypeWriter,
            additionalWriters,
            formatter,
            factoryStub
        )
    }

    beforeTest {
        options.packageName = "io.openapiprocessor.test"
        options.targetDir = listOf(target.toString(), "java", "src").joinToString(File.separator)
        options.formatCode = true

        every { factoryStub.createWriter(any(), any()) }.answers { writer }
        every { factoryStub.createResourceWriter(any()) }.answers { writer }
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
        createApiWriter(enumWriter = enumWriter).write(api)

        verify(exactly = 1) { enumWriter.write(any(), dtA) }
        verify(exactly = 1) { enumWriter.write(any(), dtB) }
    }

    "re-formats enum data type source" {
        val dts = DataTypes()
        dts.add (StringEnumDataType(DataTypeName("Foo"), "${options.packageName}.model"))
        dts.addRef("Foo")
        val api = Api(dataTypes = dts)

        val enumWriter = stub<StringEnumWriter>(relaxed = true)
        val formatter = stub<SourceFormatter>(relaxed = true)
        createApiWriter(enumWriter = enumWriter, formatter = formatter).write(api)

        verify (exactly = 2) { formatter.format(any()) }
    }

    "writes interface sources" {
        val itfs = listOf(
            `interface`("Foo", options.getSourceDir("model").toString()) {},
            `interface`("Bar", options.getSourceDir("model").toString()) {}
        )
        val api = Api(itfs)

        val interfaceWriter = stub<InterfaceWriter>(relaxed = true)
        createApiWriter(interfaceWriter = interfaceWriter).write(api)

        verify(exactly = 1) { interfaceWriter.write(any(), itfs[0]) }
        verify(exactly = 1) { interfaceWriter.write(any(), itfs[1]) }
    }

    "re-formats interface source" {
        val itfs = listOf(
            `interface`("Foo", options.getSourceDir("model").toString()) {}
        )
        val api = Api(itfs)

        val interfaceWriter = stub<InterfaceWriter>(relaxed = true)
        val formatter = stub<SourceFormatter>(relaxed = true)
        createApiWriter(interfaceWriter = interfaceWriter, formatter = formatter).write(api)

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

        val dataTypeWriter = stub<DataTypeWriter>(relaxed = true)
        createApiWriter(dataTypeWriter = dataTypeWriter).write(api)

        verify(exactly = 1) { dataTypeWriter.write(any(), dtA) }
        verify(exactly = 1) { dataTypeWriter.write(any(), dtB) }
    }

    "re-formats model data type source" {
        val dts = DataTypes()
        dts.add (ObjectDataType(DataTypeName("Foo"), "${options.packageName}.model"))
        dts.addRef("Foo")
        val api = Api(dataTypes = dts)

        val dataTypeWriter = stub<DataTypeWriter>(relaxed = true)
        val formatter = stub<SourceFormatter>(relaxed = true)
        createApiWriter(dataTypeWriter = dataTypeWriter, formatter = formatter).write(api)

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

        val interfaceDataTypeWriter = stub<InterfaceDataTypeWriter>(relaxed = true)
        createApiWriter(interfaceDataTypeWriter = interfaceDataTypeWriter).write(api)

        verify(exactly = 1) { interfaceDataTypeWriter.write(any(), dtA) }
        verify(exactly = 1) { interfaceDataTypeWriter.write(any(), dtB) }
    }

    "re-formats interface data type source" {
        val dts = DataTypes()
        dts.add (InterfaceDataType(DataTypeName("Foo"), "${options.packageName}.api"))
        dts.addRef("Foo")
        val api = Api(dataTypes = dts)

        val interfaceDataTypeWriter = stub<InterfaceDataTypeWriter>(relaxed = true)
        val formatter = stub<SourceFormatter>(relaxed = true)
        createApiWriter(interfaceDataTypeWriter = interfaceDataTypeWriter, formatter = formatter).write(api)

        verify (exactly = 2) { formatter.format(any()) }
    }

    "generates model for object data types only" {
        val dt = DataTypes()
        dt.add(MappedDataType("Type", "${options.packageName}.model"))
        dt.add("simple", StringDataType())
        val api = Api(dataTypes = dt)

        val dataTypeWriter = io.mockk.mockk<DataTypeWriter>()
        createApiWriter(dataTypeWriter = dataTypeWriter).write(api)

        verify(exactly = 0) {
            dataTypeWriter.write (any(), any())
        }
    }

    "does not re-format sources if code formatting is disabled" {
        val dts = DataTypes()
        dts.add (InterfaceDataType(DataTypeName("Foo"), "${options.packageName}.api"))
        dts.addRef("Foo")
        val api = Api(dataTypes = dts)

        options.formatCode = false
        val interfaceDataTypeWriter = stub<InterfaceDataTypeWriter>(relaxed = true)
        val formatter = stub<SourceFormatter>(relaxed = true)
        createApiWriter(interfaceDataTypeWriter = interfaceDataTypeWriter, formatter = formatter).write(api)

        verify (exactly = 0) { formatter.format(any()) }
    }

    "writes custom validation sources" {
        val validation = stub<ValidationWriter>(relaxed = true)

        val dts = DataTypes()
        options.beanValidation = true
        val api = Api(dataTypes = dts)

        createApiWriter(validationWriter = validation).write(api)

        verify(exactly = 1) { validation.write(any(), any()) }
    }

    "writes additional sources" {
        val dts = DataTypes()
        val api = Api(dataTypes = dts)

        val additionalWriterA: AdditionalWriter = mockk(relaxed = true)
        val additionalWriterB: AdditionalWriter = mockk(relaxed = true)
        createApiWriter(additionalWriters = listOf(additionalWriterA, additionalWriterB)).write(api)

        verify(exactly = 1) { additionalWriterA.invoke(any(), any(), any()) }
        verify(exactly = 1) { additionalWriterB.invoke(any(), any(), any()) }
    }

    "writes resources" {
        options.targetDirOptions.layout = TargetDirLayout.STANDARD

        val resource = Resource("api.properties", "anything")
        val api = Api(resources = listOf(resource))

        createApiWriter().write(api)

        verify (exactly = 1) { factoryStub.createResourceWriter(resource.name) }
        writer.toString() shouldBeEqual resource.content
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
