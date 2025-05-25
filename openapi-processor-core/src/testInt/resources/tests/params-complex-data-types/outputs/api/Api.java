package generated.api;

import annotation.Mapping;
import annotation.Parameter;
import annotation.Status;
import generated.model.Props;
import generated.support.Generated;
import java.util.Map;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Status("204")
    @Mapping("/endpoint-object")
    void getEndpointObject(@Parameter Props props);

    @Status("204")
    @Mapping("/endpoint-map")
    void getEndpointMap(@Parameter Map<String, String> props);

}
