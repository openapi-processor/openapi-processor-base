package generated.api;

import annotation.Mapping;
import annotation.Parameter;
import annotation.Status;
import generated.model.Foo;
import generated.model.Foo2;
import generated.support.Generated;
import javax.validation.Valid;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Status("204")
    @Mapping("/foo")
    void patchFoo(@Parameter @Valid Foo body);

    @Status("204")
    @Mapping("/foo2")
    void patchFoo2(@Parameter @Valid Foo2 body);

}
