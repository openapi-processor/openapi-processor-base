package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public record FooResource(
    @JsonProperty("prop")
    String prop,

    @JsonProperty("nested")
    BarResource nested,

    @JsonProperty("inline")
    FooInlineResource inline
) {}
