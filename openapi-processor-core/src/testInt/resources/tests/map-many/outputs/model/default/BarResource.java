package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;
import io.openapiprocessor.AnnotationA;
import io.openapiprocessor.AnnotationB;

@Generated(value = "openapi-processor-core", version = "test")
public class BarResource {

  @AnnotationA
  @JsonProperty("bar1")
  private Integer bar1;

  @AnnotationB
  @JsonProperty("bar2")
  private Integer bar2;

  public Integer getBar1() {
    return bar1;
  }

  public void setBar1(Integer bar1) {
    this.bar1 = bar1;
  }

  public Integer getBar2() {
    return bar2;
  }

  public void setBar2(Integer bar2) {
    this.bar2 = bar2;
  }

}
