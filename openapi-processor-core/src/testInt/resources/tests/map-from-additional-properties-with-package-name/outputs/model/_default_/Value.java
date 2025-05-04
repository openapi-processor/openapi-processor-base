package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;
import javax.validation.Valid;

@Generated(value = "openapi-processor-core", version = "test")
public class Value {

  @JsonProperty("text")
  private String text;

  @Valid
  @JsonProperty("nested")
  private NestedValue nested;

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public NestedValue getNested() {
    return nested;
  }

  public void setNested(NestedValue nested) {
    this.nested = nested;
  }

}
