package io.openapiprocessor.foo;

import annotation.Mapping;
import io.openapiprocessor.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Mapping("/foo")
    Foo getFoo();

}
