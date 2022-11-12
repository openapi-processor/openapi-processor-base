/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.annotations;

import java.lang.annotation.*;

/**
 * enable & configure openapi-processor-spring.
 *
 * <p>
 * recommended way is to create a configuration class with the annotation:
 * <pre>
 * &#064;SpringApi
 * class OpenApiConfiguration {}
 * </pre>
 *
 * The parameters {@code apiPath} & {@code mapping} are <b>relative</b> to the <b>project root</b>.
 * Unfortunately an annotation processor does not know where the <b>project root</b> is. Therefore,
 * it is necessary to explicitly pass the <b>project root</b> path as option to the java compiler:
 * <p>
 * {@code -Aio.openapiprocessor.project.root=${projectRoot}}
 * <p>
 * For example using gradle this can look like this:
 * <pre>
 * // build.gradle
 * compileJava {
 *   options.compilerArgs += [
 *     "-Aio.openapiprocessor.project.root=${projectDir}",
 *   ]
 * }
 * </pre>
 */
@Target (ElementType.TYPE)
@Retention (RetentionPolicy.SOURCE)
@Repeatable (SpringApis.class)
public @interface SpringApi {
    /**
     * the path of the root {@code openapi.yaml} file.
     *
     * @return the root {@code openapi.yaml}
     */
    String apiPath() default "src/api/openapi.yaml";

    /**
     * the file name of the {@code mapping.yaml} configuration file. The yaml file name must end
     * with {@code .yaml} or {@code .yml}.
     *
     * @return the processor configuration {@code mapping.yaml}
     */
    String mapping() default "src/api/mapping.yaml";

    /**
     * the selected OpenAPI parser. Default (recommended) is the internal OpenAPI 3.0/3.1 parser.
     * <p>
     * Other allowed values are {@code SWAGGER} (3.0 only) or {@code OPENAPI4J} (3.0 only, not
     * maintained anymore).
     *
     * @return the selected OpenAPI parser.
     */
    String parser() default "INTERNAL";
}
