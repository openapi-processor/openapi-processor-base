package generated.api;

import annotation.Mapping;
import annotation.Parameter;
import generated.model.BarResource;
import generated.model.FooResource;
import generated.support.Generated;
import io.openapiprocessor.Bar2;
import io.openapiprocessor.ParamAnnotation;
import io.openapiprocessor.Something;
import io.openapiprocessor.SomethingElse;
import io.openapiprocessor.Wrap;
import jakarta.validation.Valid;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Generated(value = "openapi-processor-core", version = "test")
public interface FooApi {

  /**
   * foo A summary. foo A endpoint
   *
   * @param foo1 parameter foo1
   * @param foo2 parameter foo2
   * @param bar parameter bar
   * @return results, json or something
   */
  @Mapping("/fooA")
  Wrap<Collection<FooResource>> getFooAApplicationJson(
      @Parameter Collection<String> foo1,
      @Parameter List<String> foo2,
      @Parameter @ParamAnnotation @Valid BarResource bar);

  /**
   * foo A summary. foo A endpoint
   *
   * @param foo1 parameter foo1
   * @param foo2 parameter foo2
   * @param bar parameter bar
   * @return results, json or something
   */
  @Mapping("/fooA")
  Wrap<List<Something>> getFooAApplicationVndSomething(
      @Parameter Collection<String> foo1,
      @Parameter List<String> foo2,
      @Parameter @ParamAnnotation @Valid BarResource bar);

  /**
   * foo B summary. foo B endpoint
   *
   * @param foo1 parameter foo1
   * @param foo2 parameter foo2
   * @param bar parameter bar
   * @return results, json or something
   */
  @Mapping("/fooB")
  LinkedHashSet<FooResource> getFooBApplicationJson(
      @Parameter Set<String> foo1,
      @Parameter List<String> foo2,
      @Parameter @ParamAnnotation @Valid BarResource bar,
      @Parameter Bar2 bar2);

  /**
   * foo B summary. foo B endpoint
   *
   * @param foo1 parameter foo1
   * @param foo2 parameter foo2
   * @param bar parameter bar
   * @return results, json or something
   */
  @Mapping("/fooB")
  SomethingElse getFooBApplicationVndSomething(
      @Parameter Set<String> foo1,
      @Parameter List<String> foo2,
      @Parameter @ParamAnnotation @Valid BarResource bar,
      @Parameter Bar2 bar2);

}
