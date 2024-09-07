package generated.api;

import annotation.Mapping;
import annotation.Prefix;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
@Prefix("/foo/bar/v1")
public interface Api {

    @Mapping("/foo")
    void getFoo();

}
