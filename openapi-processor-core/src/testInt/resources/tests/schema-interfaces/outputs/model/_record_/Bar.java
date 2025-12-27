package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;
import io.openapiprocessor.InterfaceA;
import io.openapiprocessor.InterfaceB;

@Generated(value = "openapi-processor-core", version = "test")
public record Bar(
    @JsonProperty("bar")
    String bar
) implements InterfaceA, InterfaceB {}
