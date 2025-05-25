package generated.api;

import annotation.Mapping;
import annotation.Parameter;
import annotation.Status;
import generated.model.Bar;
import generated.model.Foo;
import generated.model.FooFoo;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public interface EnumApi {

    @Status("204")
    @Mapping("/endpoint")
    void getEndpoint(
            @Parameter Foo foo,
            @Parameter Bar bar);

    @Status("204")
    @Mapping("/endpoint-dashed")
    void getEndpointDashed(@Parameter FooFoo fooFoo);

}
