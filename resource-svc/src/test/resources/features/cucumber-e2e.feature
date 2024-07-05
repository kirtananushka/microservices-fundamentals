Feature: Resource Service

  Scenario: Upload an Audio File and Get Resource Details
    Given a user has an audio file
    When the user uploads the audio file to the resource-svc
    Then the resource is successfully uploaded
    And the user receives the resource details

  Scenario: Fetch a Resource by ID
    Given a resource exists with a specific ID
    When the user requests the resource by ID from the resource-svc
    Then the correct resource data is returned

  Scenario: Delete a Resource by ID
    Given a resource exists with a specific ID
    When the user deletes the resource by ID from the resource-svc
    Then the resource is successfully deleted

