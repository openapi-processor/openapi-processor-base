package generated.api;

import annotation.Mapping;
import annotation.Status;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Mapping("/foo")
    void getFoo();

    @Mapping("/bar")
    String getBar();

    @Status("202")
    @Mapping("/bar-multi")
    String getBarMultiTextPlain();

    @Status("201")
    @Mapping("/bar-multi")
    String getBarMultiApplicationJson();

}
