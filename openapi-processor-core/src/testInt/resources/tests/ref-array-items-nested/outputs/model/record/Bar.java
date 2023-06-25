package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public record Bar(
    @JsonProperty("bar")
    String bar,

    @JsonProperty("foos")
    Foo[] foos
) {}
