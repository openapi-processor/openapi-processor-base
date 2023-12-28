package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;
import io.oap.FooA;
import io.oap.FooB;
import io.oap.FooC;

@Generated(value = "openapi-processor-core", version = "test")
public record Foo(
    @FooA(value = "any")
    @FooB
    @FooC
    @JsonProperty("bar")
    String bar
) {}
