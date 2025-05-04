package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;
import javax.validation.Valid;

@Generated(value = "openapi-processor-core", version = "test")
public record Foo(
    @Valid
    @JsonProperty("myProperties")
    GenericProperties myProperties
) {}
