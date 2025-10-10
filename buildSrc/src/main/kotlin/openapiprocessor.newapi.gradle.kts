import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

tasks.register("newTestApi") {
    group = "utility"
    description = "setup new openapiXX.yaml test files."

    doLast {
        val yamlFactory = YAMLFactory.builder()
            .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
            .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
            .enable(YAMLGenerator.Feature.INDENT_ARRAYS_WITH_INDICATOR)
            .build()

        val mapper = ObjectMapper(yamlFactory).registerKotlinModule()

        val srcVersion = "31"
        val dstVersion = "32"
        val apiVersion = "3.2.0"

        val tests = File(rootDir, "openapi-processor-core/src/testInt/resources/tests")
        val testDirs = tests.listFiles()
            ?.sorted()
            ?.toList()
            ?.filter { it.isDirectory }
            ?: emptyList()

        for (testDir in testDirs) {
            println("checking 'tests/${testDir.name}' ...")
            val srcFile = File(testDir, "inputs/openapi$srcVersion.yaml")
            val dstFile = File(testDir, "inputs/openapi$dstVersion.yaml")

            if(!srcFile.exists()) {
                println("no '${srcFile.name}' ...")
                continue
            }
            
            if (!dstFile.exists()) {
                println("creating '${dstFile.name}' ...")
                srcFile.copyTo(dstFile)
            }

            val apiYaml = mapper.readValue(dstFile, object : TypeReference<MutableMap<String, Any?>>() {})
            if (apiYaml["openapi"] != apiVersion) {
                println("updating '${dstFile.name}' ...")
                apiYaml["openapi"] = apiVersion
                mapper.writeValue(dstFile, apiYaml)
            }

            val inputsFile = File(testDir, "inputs.yaml")
            val inputsYaml = mapper.readValue(inputsFile, object : TypeReference<MutableMap<String, Any?>>() {})
            @Suppress("UNCHECKED_CAST") val items = inputsYaml["items"] as MutableList<String>
            if (!items.contains("inputs/openapi$dstVersion.yaml")) {
                println("updating inputs.yaml ...")
                items.indexOf("inputs/openapi$srcVersion.yaml").let { index ->
                    items.add(index + 1, "inputs/openapi$dstVersion.yaml")
                }
                mapper.writeValue(inputsFile, inputsYaml)
            }
        }
    }
}
