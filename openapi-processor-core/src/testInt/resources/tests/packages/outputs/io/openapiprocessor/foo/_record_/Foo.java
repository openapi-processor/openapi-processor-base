package io.openapiprocessor.foo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.openapiprocessor.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public record Foo(
    @JsonProperty("bar")
    String bar
) {}
