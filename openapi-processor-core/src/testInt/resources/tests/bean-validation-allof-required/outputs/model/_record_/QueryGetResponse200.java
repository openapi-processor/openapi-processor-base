package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;
import javax.validation.constraints.NotNull;

@Generated(value = "openapi-processor-core", version = "test")
public record QueryGetResponse200(
    @NotNull
    @JsonProperty("prop1")
    String prop1,

    @JsonProperty("prop2")
    String prop2
) {}
