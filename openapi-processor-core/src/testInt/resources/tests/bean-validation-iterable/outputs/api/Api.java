package generated.api;

import annotation.Mapping;
import annotation.Parameter;
import annotation.Status;
import generated.model.Foo;
import generated.model.FooL;
import generated.support.Generated;
import javax.validation.Valid;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Status("204")
    @Mapping("/foo")
    void postFoo(@Parameter @Valid Foo body);

    @Status("204")
    @Mapping("/fooL")
    void postFooL(@Parameter @Valid FooL body);

}
