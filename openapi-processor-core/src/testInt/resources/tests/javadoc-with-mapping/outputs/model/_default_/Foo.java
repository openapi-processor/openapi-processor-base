package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;

/** schema level description. */
@Generated(value = "openapi-processor-core", version = "test")
public class Foo {

  /** property level description. */
  @JsonProperty("bar")
  private String bar;

  public String getBar() {
    return bar;
  }

  public void setBar(String bar) {
    this.bar = bar;
  }

}
