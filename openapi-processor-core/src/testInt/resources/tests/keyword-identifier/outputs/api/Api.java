package generated.api;

import annotation.Mapping;
import annotation.Parameter;
import generated.model.Class;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Mapping("/class")
    Class getAClass();

    @Mapping("/class")
    Class postClass(@Parameter Class aClass);

}
