package tests;

import base.BaseTest;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import utils.AICommandInterpreter;
import utils.TestAction;
import utils.TestActionExecutor;

public class AIBasedTests extends BaseTest {
    private AICommandInterpreter interpreter;
    private TestActionExecutor executor;

    @BeforeMethod
    public void setupTest() {
        refreshCredentials();
        interpreter = new AICommandInterpreter(bedrockClient);
        executor = new TestActionExecutor(driver);
    }

    @Test(description = "Test login flow")
    public void testLoginFlow() {
        try {
/*            executeStep("If there is a popup, click on Allow button.");
            Thread.sleep(1000);
            executeStep("Click on Library tab.");
            Thread.sleep(2000);
            executeStep("Click on SIGN IN button.");
            Thread.sleep(2000);
            executeStep("Tap on text field with hint \"Enter mobile number or email\"");
            Thread.sleep(1000);
            //executeStep("Wait for \"Enter mobile number or email\" text is present.");
            //executeStep("Click on the text field.");
            executeStep("Type \"shabars+201@amazon.com\" in the text field which has a hint \"Enter mobile number or email\".");
            executeStep("Tap on the \"Continue\" button.");
            //executeStep("Wait for \"Amazon password\" text is present.");
            Thread.sleep(2000);
            executeStep("Tap on \"Amazon password\" text field.");
            Thread.sleep(2000);
            executeStep("Type \"labone2six\" in the password text field.");
            Thread.sleep(2000);
            executeStep("Click \"Sign in\" button.");
            Thread.sleep(5000);
            executeStep("Wait until the filter icon is displayed");*/
            executeStep("Click on Library tab.");
            Thread.sleep(2000);
            executeStep("Double click on \"Automating and Testing a REST API\" book.");
            Thread.sleep(2000);
            executeStep("Click on top center of the page.");
            Thread.sleep(2000);
            executeStep("Click reading progress at middle");
            Thread.sleep(2000);


        } catch (Exception e) {
            throw new RuntimeException("Test failed: " + e.getMessage(), e);
        }
    }

    private void executeStep(String naturalLanguageStep) throws Exception {
        System.out.println("**************** Executing step: " + naturalLanguageStep);
        try {
            String pageSource = driver.getPageSource();
            System.out.println("Page source: " + pageSource);
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);

            //TestAction action = interpreter.interpretCommand(naturalLanguageStep, pageSource);
            TestAction action = interpreter.interpretCommand(naturalLanguageStep, pageSource, screenshot);
            System.out.println("Generated TestAction: " + action.toString()); // Add this line
            int maxRetries = 1;
            Exception lastException = null;

            for (int i = 0; i < maxRetries; i++) {
                try {
                    executor.executeAction(action);
                    return; // Success
                } catch (Exception e) {
                    lastException = e;
                    Thread.sleep(1000); // Wait before retry
                }
            }
            throw lastException;
        } catch (Exception e) {
            // If we get a credentials error, try refreshing and retry once
            if (isCredentialError(e)) {
                refreshCredentials();
                String pageSource = driver.getPageSource();
                byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                TestAction action = interpreter.interpretCommand(naturalLanguageStep, pageSource, screenshot);
                // TestAction action = interpreter.interpretCommand(naturalLanguageStep, pageSource);
                executor.executeAction(action);
            } else {
                throw e;
            }
        }
    }

    private boolean isCredentialError(Exception e) {
        // Add logic to detect credential-related errors
        return e.getMessage() != null &&
                (e.getMessage().contains("credentials") ||
                        e.getMessage().contains("authentication"));
    }
}
