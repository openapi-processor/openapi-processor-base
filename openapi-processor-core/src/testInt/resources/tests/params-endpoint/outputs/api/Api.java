package generated.api;

import annotation.Mapping;
import annotation.Parameter;
import annotation.Status;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Status("204")
    @Mapping("/foo")
    void getFoo(@Parameter String bar);

}
