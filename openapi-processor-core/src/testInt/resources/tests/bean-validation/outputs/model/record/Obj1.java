package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;
import javax.validation.Valid;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.NotNull;

@Generated(value = "openapi-processor-core", version = "test")
public record Obj1(
    @NotNull
    @JsonProperty("prop1")
    String prop1,

    @DecimalMax(value = "3")
    @JsonProperty("prop2")
    Integer prop2,

    @Valid
    @JsonProperty("prop3")
    Obj2 prop3
) {}
