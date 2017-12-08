# json2java

This project converts a JSON document to its implied Java source code
* Use with Gradle
* add to your repositories
    ```
    repositories {
        maven { url "https://jitpack.io" }
    }
    ```
* In your app build.gradle, add:  ```compile "com.github.inder123:json2java:1.0.4"```


Look at the Json2JavaTest for examples on how to generate Java classes from a JSON document, and how define custom mappings of names.

Example public projects using Json2Java:

 * <a href="https://github.com/inder123/gracenote-java-api">gracenote-java-api</a>
 * <a href="https://github.com/inder123/geocoding">Geocoding</a>

Look at the generator projects in the repositories above to see how to customized output Java code.

TODO:
 * limit line width to 100 characters
 * Use JsonPath expressions for custom mappings
