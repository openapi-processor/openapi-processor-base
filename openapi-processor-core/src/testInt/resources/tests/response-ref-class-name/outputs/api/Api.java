package generated.api;

import annotation.Mapping;
import generated.model.Foo;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Mapping("/foo")
    Foo getFoo();

}
