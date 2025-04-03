package generated.api;

import annotation.Mapping;
import generated.model.Foo;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Mapping("/foo")
    Foo getFooApplicationJson();

    @Mapping("/foo")
    String getFooTextPlain();

    @Mapping("/fooBarA")
    Object getFooBarAApplicationJson();

    @Mapping("/fooBarA")
    String getFooBarATextPlain();

    @Mapping("/fooBarB")
    Object getFooBarBApplicationJson();

    @Mapping("/fooBarB")
    String getFooBarBTextPlain();

}
