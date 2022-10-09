# contributing to openapi-processor

## feature & bug reports

In case some feature is missing, or the generated code is not 100% what you would expect please create an issue with test case. Such a test case includes the source files (i.e. OpenAPI & a `mapping.yaml`) and the expected output files (i.e. the generated interfaces and classes).

Providing a test case will help significantly :-) Such a test case is used to run an end-to-end test of the openapi-processor. 

A template for such a test case can be found in `openapi-processor-core/resources/tests/template`.

### integration test case layout

#### inputs

the input files:

```
resources/tests/my-test
+--- inputs.yaml
\--- inputs
  +--- mapping.yaml
  +--- openapi30.yaml
  \--- openapi31.yaml
```

the `inputs.yaml` file lists the input files in the `inputs` folder.

```yaml
items:
  - inputs/mapping.yaml
  - inputs/openapi30.yaml
  - inputs/openapi31.yaml
```

#### outputs

the expected files:

```
 resources/tests/my-test
 +--- generated.yaml
 \--- generated
      +--- api
      |    \--- EndpointApi.java
      \--- model
           \--- Foo.java
```

the `generated.yaml` file lists the input files in the `generated` folder.

```yaml
items:
  - generated/api/EndpointApi.java
  - generated/model/Foo.java
```

#### mapping

The `mapping.yaml` contains the type mapping information and is an optional file, but it is recommended to provide one. If there is no `mapping.yaml` the test runner will use a minimal version: 

```yaml
openapi-processor-spring: v2

options:
  package-name: generated
```

IMPORTANT: even if you provide an explicit `mapping.yaml` the `package-name` must be `generated`. 


## working on the code

### jdk

the minimum jdk is currently JDK 8

### ide setup

openapi-processor can be imported into IntelliJ IDEA by opening the `build.gradle` file.

### unit tests

please write unit tests.
 
### running the tests

To run the tests use `./gradlew check`. 

`check` runs the unit tests, and the integration tests.
