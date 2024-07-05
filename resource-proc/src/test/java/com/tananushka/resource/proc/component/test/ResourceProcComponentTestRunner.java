package com.tananushka.resource.proc.component.test;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = "com.tananushka.resource.proc",
        plugin = {"pretty", "html:target/cucumber-reports"}
)
public class ResourceProcComponentTestRunner {
}