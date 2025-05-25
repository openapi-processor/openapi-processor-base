package generated.api;

import annotation.Mapping;
import annotation.Parameter;
import annotation.Status;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public interface ExcludedApi {

    @Status("204")
    @Mapping("/endpoint-exclude/{foo}")
    void getEndpointExcludeFoo(@Parameter String foo);

}
