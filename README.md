#### Overview
The exercise is using Spring Boot for implementation. Below are the framework and tools being used in the exercise
- **Spring Batch**: batch processing of inserting the csv data
- **JPA**: Data repository
- **Mockito**: Unit test
- **Swagger**: Use OpenAPI and swagger to expose the endpoint
- **Jacoco**: Verify the code coverage
- **Lombok**: Easy the development

#### System Requirement
- Java 1.8 or above
- Maven

#### Implementation
- Add the userId to the csv file as the name cannot be unique. I am sure not whether the exercise is intentionally missed this field or not.

#### CSV Batch Processing in chunk
Use the Spring Batch to load and insert into database.
- The batch update can turn on or off with `user.batch.enabled` property.
- The insert will be skip if the `user.batch.stop-on-error` property is false.
- The items are processed in chunk. The chunk is configured with `user.batch.chunk`.

#### Endpoints - GET/POST/PUT/DELETE
- List users: http://localhost:8080/users - GET
- List users with sort: http://localhost:8080/users?sortName=[name,salary,userId]&sortDir=[ASC,DESC] - GET
- Get user: http://localhost:8080/users/{userId} - GET
- Create user: http://localhost:8080/users/ - with POST
- Update user: http://localhost:8080/users/ - with PUT
- Delete user: http://localhost:8080/users/{userId} - with DELETE
- Swagger UI: http://localhost:8080/swagger-ui.html

#### Run the program
- Command line: `mvn compile exec:java`
- IDE (IntelliJ, Eclipse): 
    1. Install the **Lombok** plugin in IDE (Eclipse, IntelliJ)
    2. Execute `mvn clean compile` to generate the source code from *swagger* file
    3. Start the MainApplication

#### Run the test
The unit tests have been added to verify the implementation for the Exercise. The code coverage is more than 90% for all classes
- Run the unit test: `mvn test`
- Verify the code coverage: `mvn test verify`
- Test report: target/site/jacoco/index.html

#### Consideration
The http://localhost:8080/users can be improved with pagination if the number of users are huge.