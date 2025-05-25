package generated.api;

import annotation.Mapping;
import annotation.Parameter;
import annotation.Status;
import generated.support.Generated;
import generated.validation.Values;
import jakarta.validation.constraints.NotNull;

@Generated(value = "openapi-processor-core", version = "test")
public interface EnumApi {

    @Status("204")
    @Mapping("/endpoint")
    void getEndpoint(
            @Parameter @NotNull @Values(values = {"foo", "foo-2", "foo-foo"}) String foo,
            @Parameter @NotNull @Values(values = {"bar", "bar-2", "bar-bar"}) String bar);

    @Status("204")
    @Mapping("/endpoint-dashed")
    void getEndpointDashed(@Parameter @NotNull @Values(values = {"foo", "foo-2", "foo-foo"}) String fooFoo);

}
