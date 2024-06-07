package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;
import io.oap.Annotation;
import io.oap.ClassAnnotation;
import io.oap.Param;
import io.oap.Bar;

@ClassAnnotation(value = Param.class, simple = 2)
@Generated(value = "openapi-processor-core", version = "test")
public class Foo {

    @Annotation(value = Bar.class)
    @JsonProperty("bar")
    private String bar;

    public String getBar() {
        return bar;
    }

    public void setBar(String bar) {
        this.bar = bar;
    }

}
