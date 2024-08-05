package com.tananushka.resource.svc.e2e;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

// Run containers before running this test
@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = "com.tananushka.resource.svc.e2e",
        plugin = {"pretty", "html:target/cucumber-reports"}
)
public class CucumberE2ETestRunner {
}
