### Testing Strategy for `resource-proc`, `resource-svc`, and `song-svc` Projects

#### Objective

The primary goal of this testing strategy is to ensure the stability and reliability of
the `resource-proc`, `resource-svc`, and `song-svc` applications. This will be achieved through a comprehensive testing
approach that includes various levels of testing: Unit tests, Integration tests, Component tests, Contract tests, and
End-to-end tests.

#### Testing Approach

1. **Unit Tests**
    - **Description**: Unit tests will focus on testing individual methods and classes in isolation.
    - **Tools**: JUnit
    - **Scope**:
        - Validate business logic in service classes.
        - Ensure utility methods and helper functions work as expected.
        - Test exception handling in methods.
    - **Coverage**: Aim for 70-80% code coverage with unit tests.
    - **Module**: `resource-proc`, specifically testing the `Mp3MetadataService` methods for extracting and formatting
      metadata.

2. **Integration Tests**
    - **Description**: Integration tests will verify the interaction between multiple components, such as services,
      repositories, and external dependencies.
    - **Tools**: JUnit, Testcontainers (for databases, ActiveMQ, and LocalStack)
    - **Scope**:
        - Test interactions with the database.
        - Validate communication with external services like `song-svc` and `resource-svc`.
    - **Coverage**: Ensure critical integration points are covered.
    - **Module**: `resource-svc`, specifically testing the interaction between `ResourceService` and the database, as
      well as the interaction with AWS S3 via `S3Service`.

3. **Component Tests**
    - **Description**: Component tests focus on testing the `resource-proc` microservice in isolation with its
      dependencies mocked.
    - **Tools**: Cucumber, Mockito
    - **Scope**:
        - Validate the behavior of the `resource-proc` microservice by isolating it from other microservices.
        - Mock interactions with `SongSvcClient` and `ResourceSvcClient` to test the microservice in isolation.
        - Test the interaction of the `resource-proc` microservice with its internal components
          like `Mp3MetadataService`.
    - **Coverage**: Ensure all major components and their interactions within the `resource-proc` microservice are
      covered.
    - **Scenarios**:
        - Scenario: Process a resource and save metadata.
            - Expected Outcome: Audio data is fetched, metadata is extracted, and saved correctly.
        - Scenario: Handle processing failure gracefully.
            - Expected Outcome: An error is logged, and a `ResourceProcessorException` is thrown.
    - **Module**: `resource-proc`

4. **Contract Tests**
    - **Description**: Contract tests will ensure that the interactions between services adhere to predefined contracts.
    - **Tools**: Spring Cloud Contract, Pact
    - **Scope**:
        - Verify that service interactions adhere to the expected API contracts.
        - Cover both synchronous HTTP and messaging communication styles.
    - **Coverage**: Focus on critical service interactions.
    - **Scenarios**:
        - Test the contract between `resource-proc` and `song-svc` for the `/songs` endpoint.
   - **Module**: `resource-proc`, `song-svc`

5. **End-to-End Tests**
    - **Description**: End-to-end (E2E) tests will validate the complete workflow of the applications from start to
      finish.
    - **Tools**: Cucumber
    - **Scope**:
        - Ensure that the entire application flow works as expected in a production-like environment.
        - Validate user scenarios and business use cases.
    - **Coverage**: Cover all major user flows and critical paths.
    - **Scenarios**:
        - Scenario: Upload an audio file, process it, and store metadata in the `song-svc`.
            - Expected Outcome: Audio file is uploaded, processed, and metadata is stored correctly.
        - Scenario: Fetch a Resource by ID.
            - Expected Outcome: The correct resource data is returned.
        - Scenario: Delete a Resource by ID.
            - Expected Outcome: Then the resource is successfully deleted.
    - **Module**: `resource-proc`, `resource-svc`, and `song-svc`

#### Combined Strategy

The combined strategy of using unit tests, integration tests, component tests, contract tests, and end-to-end tests
ensures comprehensive coverage and stability of the application. Here’s how each type of test contributes to the overall
strategy:

- **Unit Tests**: Provide fast feedback on the correctness of individual methods and classes, ensuring that the basic
  building blocks of the application are reliable.
- **Integration Tests**: Validate the interaction between components and external dependencies, ensuring that the
  integration points work as expected.
- **Component Tests**: Test the behavior of individual microservices in isolation, ensuring that each microservice works
  correctly within its context without relying on other microservices.
- **Contract Tests**: Ensure that the interactions between services adhere to predefined contracts, reducing the risk of
  integration issues.
- **End-to-End Tests**: Validate the complete workflows and user scenarios, ensuring that the application works as
  expected in a production-like environment.

By combining these testing strategies, we can achieve a balanced approach that provides confidence in the stability and
reliability of the applications while maintaining a manageable test suite.

#### Conclusion

This comprehensive testing strategy ensures that the `resource-proc`, `resource-svc`, and `song-svc` applications are
thoroughly tested at all levels, from individual units to complete end-to-end workflows. This approach helps to identify
issues early, validate interactions between components and services, and ensure that the applications meet the expected
quality standards.