package generated.api;

import annotation.Mapping;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Mapping("/fooBar")
    Object getFooBarpplicationJson();

    @Mapping("/fooBar")
    Object getFooBarTextPlain();

}
