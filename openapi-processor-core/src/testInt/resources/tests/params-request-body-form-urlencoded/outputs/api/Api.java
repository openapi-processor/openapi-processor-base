package generated.api;

import annotation.Mapping;
import annotation.Parameter;
import annotation.Status;
import generated.model.Foo;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Status("204")
    @Mapping("/foo/params")
    void postFooParams(
            @Parameter String foo,
            @Parameter String bar);

    @Status("204")
    @Mapping("/foo/object")
    void postFooObject(@Parameter Foo body);

}
