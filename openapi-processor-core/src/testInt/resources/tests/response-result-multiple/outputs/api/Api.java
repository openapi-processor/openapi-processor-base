package generated.api;

import annotation.Mapping;
import generated.model.Foo;
import generated.model.GetFooBarAApplicationJsonResponse;
import generated.model.GetFooBarBApplicationJsonResponse;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Mapping("/foo")
    Foo getFooApplicationJson();

    @Mapping("/foo")
    String getFooTextPlain();

    @Mapping("/fooBarA")
    GetFooBarAApplicationJsonResponse getFooBarAApplicationJson();

    @Mapping("/fooBarA")
    String getFooBarATextPlain();

    @Mapping("/fooBarB")
    GetFooBarBApplicationJsonResponse getFooBarBApplicationJson();

    @Mapping("/fooBarB")
    String getFooBarBTextPlain();

}
