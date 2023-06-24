package generated.api;

import annotation.Mapping;
import generated.model.Value;
import generated.support.Generated;
import java.util.List;
import java.util.Map;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

  /**
   * query object dictionary
   *
   * @return dictionary response
   */
  @Mapping("/values")
  Map<String, Value> getValues();

  /**
   * query object dictionary
   *
   * @return dictionary response
   */
  @Mapping("/multi-values")
  Map<String, List<Value>> getMultiValues();

}
