package generated.api;

import annotation.Mapping;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Mapping("/endpoint")
    void deleteEndpoint();

    @Mapping("/endpoint")
    void getEndpoint();

    @Mapping("/endpoint")
    void headEndpoint();

    @Mapping("/endpoint")
    void optionsEndpoint();

    @Mapping("/endpoint")
    void patchEndpoint();

    @Mapping("/endpoint")
    void postEndpoint();

    @Mapping("/endpoint")
    void putEndpoint();

    @Mapping("/endpoint")
    void traceEndpoint();

}
