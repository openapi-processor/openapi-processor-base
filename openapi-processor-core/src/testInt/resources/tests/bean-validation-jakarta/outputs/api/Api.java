package generated.api;

import annotation.Mapping;
import annotation.Parameter;
import annotation.Status;
import generated.model.Foo;
import generated.support.Generated;
import jakarta.validation.Valid;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Status("204")
    @Mapping("/foo")
    void getFoo(@Parameter @Valid Foo body);

}
