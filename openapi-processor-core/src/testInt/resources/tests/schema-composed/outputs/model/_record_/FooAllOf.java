package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public record FooAllOf(
    @JsonProperty("one")
    String one,

    @JsonProperty("two")
    String two,

    @JsonProperty("three")
    String three
) {}
