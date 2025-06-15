package io.openapiprocessor.foo;

import annotation.Mapping;
import io.openapiprocessor.openapi.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public interface FooApi {

    @Mapping("/foo")
    Foo getFoo();

}
