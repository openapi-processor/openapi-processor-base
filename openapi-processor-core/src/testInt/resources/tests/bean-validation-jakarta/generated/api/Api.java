package generated.api;

import annotation.Mapping;
import annotation.Parameter;
import generated.model.Foo;
import generated.support.Generated;
import jarkata.validation.Valid;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Mapping("/foo")
    void getFoo(@Parameter @Valid Foo body);

}
