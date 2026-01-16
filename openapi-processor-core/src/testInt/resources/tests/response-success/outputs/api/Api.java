package generated.api;

import annotation.Mapping;
import annotation.Status;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Status("102")
    @Mapping("/informational-is-success")
    void getInformationalIsSuccess();

    @Status("302")
    @Mapping("/redirection-is-success")
    void getRedirectionIsSuccess();

}
