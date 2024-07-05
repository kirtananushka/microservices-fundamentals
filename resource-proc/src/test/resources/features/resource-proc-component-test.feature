Feature: Resource Processor

  Scenario: Process a resource and save metadata
    Given a new resource with ID 1 is available
    When the resource processor processes the resource
    Then the metadata is extracted and saved correctly

  Scenario: Handle processing failure gracefully
    Given a new resource with ID 1 is available but cannot be processed
    When the resource processor attempts to process the resource
    Then an error is logged and a ResourceProcessorException is thrown
