package generated.api;

import annotation.Mapping;
import generated.model.Foo;
import generated.model.FooFalse;
import generated.model.FooTrue;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Mapping("/foo")
    Foo getFoo();

    @Mapping("/foo-true")
    FooTrue getFooTrue();

    @Mapping("/foo-false")
    FooFalse getFooFalse();

}
