package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;
import java.util.Collection;
import javax.validation.Valid;

@Generated(value = "openapi-processor-core", version = "test")
public record FooL(
    @JsonProperty("bars")
    Collection<@Valid Bar> bars
) {}
