package generated.api;

import annotation.Mapping;
import annotation.Parameter;
import generated.model.FooPostRequestBody;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Mapping("/foo")
    String postFoo(@Parameter FooPostRequestBody body);

}
