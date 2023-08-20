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
 outputs/tests/my-test
 +--- outputs.yaml
 \--- outputs
      +--- api
      |    \--- EndpointApi.java
      \--- model
           +--- default
           |    \--- Foo.java          
           +--- record
                \--- Foo.java
```

the `outputs.yaml` file lists the input files in the `outputs` folder.

`model/defaults` contains the model classes as pojos and `model/record` contains them as java records.  

```yaml
items:
  - outputs/api/EndpointApi.java
  - outputs/<model>/Foo.java
```

the `<model>` is placeholder that gets replaced by `default` or `record` at test runtime. 


#### mapping

The `mapping.yaml` contains the type mapping information and is an optional file, but it is recommended to provide one. If there is no `mapping.yaml` the test runner will use a minimal version: 

```yaml
openapi-processor-spring: v4

options:
  package-name: generated
```

IMPORTANT: even if you provide an explicit `mapping.yaml` the `package-name` should be `generated`. 


## working on the code

### jdk

the minimum jdk is currently JDK 11, although most code still is JDK 8 compatible.

### ide setup

openapi-processor can be imported into IntelliJ IDEA by opening the `build.gradle` file.

### unit tests

please write unit tests.
 
### running the tests

To run the tests use `./gradlew check`. 

`check` runs the unit tests, and the integration tests.
