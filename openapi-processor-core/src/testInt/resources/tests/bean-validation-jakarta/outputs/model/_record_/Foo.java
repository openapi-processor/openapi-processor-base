package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;
import jakarta.validation.constraints.Size;

@Generated(value = "openapi-processor-core", version = "test")
public record Foo(
    @Size(max = 10)
    @JsonProperty("bar")
    String bar
) {}
