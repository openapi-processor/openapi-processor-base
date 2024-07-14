package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;
import io.openapiprocessor.AnnotationA;
import io.openapiprocessor.AnnotationC;
import jakarta.validation.constraints.DecimalMin;
import java.util.UUID;

@AnnotationC(UUID.class)
@Generated(value = "openapi-processor-core", version = "test")
public class FooResource {

  @DecimalMin(value = "0")
  @AnnotationA
  @JsonProperty("foo1")
  private Integer foo1;

  @DecimalMin(value = "-10")
  @AnnotationA
  @JsonProperty("foo2")
  private Integer foo2;

  public Integer getFoo1() {
    return foo1;
  }

  public void setFoo1(Integer foo1) {
    this.foo1 = foo1;
  }

  public Integer getFoo2() {
    return foo2;
  }

  public void setFoo2(Integer foo2) {
    this.foo2 = foo2;
  }

}
