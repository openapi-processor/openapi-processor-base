/*
 * This class is auto generated by https://github.com/hauner/openapi-processor-core.
 * TEST ONLY.
 */

package generated.api;

import annotation.Mapping;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface Api {

    @Mapping("/array-global")
    Collection<String> getArrayGlobal();

    @Mapping("/array-global-response")
    List<String> getArrayGlobalResponse();

    @Mapping("/array-endpoint-response")
    Set<String> getArrayEndpointResponse();

}