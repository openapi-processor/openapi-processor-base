package generated.api;

import annotation.Mapping;
import annotation.Parameter;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public interface EnumApi {

    @Mapping("/endpoint")
    void getEndpoint(
            @Parameter String foo,
            @Parameter String bar);

    @Mapping("/endpoint-dashed")
    void getEndpointDashed(@Parameter String fooFoo);

}
