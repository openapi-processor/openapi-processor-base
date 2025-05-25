package generated.api;

import annotation.Mapping;
import annotation.Parameter;
import annotation.Status;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public interface EndpointApi {

    @Status("204")
    @Mapping("/endpoint")
    void getEndpoint(@Parameter String foo);

    @Status("204")
    @Mapping("/endpoint-optional")
    void getEndpointOptional(@Parameter String foo);

}
