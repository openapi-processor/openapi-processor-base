package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public record Foo(
    @JsonProperty("bar")
    Bar bar,

    @JsonProperty(value = "barRO", access = JsonProperty.Access.READ_ONLY)
    Bar barRo,

    @JsonProperty(value = "barWO", access = JsonProperty.Access.WRITE_ONLY)
    Bar barWo,

    @JsonProperty(value = "barNameRO", access = JsonProperty.Access.READ_ONLY)
    String barNameRo,

    @JsonProperty(value = "barNameWO", access = JsonProperty.Access.WRITE_ONLY)
    String barNameWo
) {}
