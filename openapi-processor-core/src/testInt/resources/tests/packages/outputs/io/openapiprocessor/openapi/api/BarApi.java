package io.openapiprocessor.openapi.api;

import annotation.Mapping;
import io.openapiprocessor.openapi.model.Bar;
import io.openapiprocessor.openapi.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public interface BarApi {

    @Mapping("/bar")
    Bar getBar();

}
