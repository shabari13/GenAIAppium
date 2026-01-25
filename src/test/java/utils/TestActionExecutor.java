package utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.PerformsTouchActions;
import io.appium.java_client.TouchAction;
import io.appium.java_client.remote.SupportsContextSwitching;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import io.appium.java_client.touch.WaitOptions;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.PointerInput.Kind;
import org.openqa.selenium.Point;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Set;


public class TestActionExecutor {
    private final AppiumDriver driver;
    private final ObjectMapper objectMapper;

    public TestActionExecutor(AppiumDriver driver) {
        this.driver = driver;
        this.objectMapper = new ObjectMapper();
    }

 /*   public void executeAction(TestAction action) {
        int maxRetries = 1;
        long waitBetweenRetries = 1000;
        Exception lastException = null;

        System.out.println("Executing action: " + action);

        for (int i = 0; i < maxRetries; i++) {
            try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

                if (action.getLocatorStrategy() == LocatorStrategy.VISUAL) {
                    handleVisualAction(action);
                    return;
                } else {
                    // Handle DOM-based strategies
                    By locator = getBy(action.getLocatorStrategy(), action.getLocatorValue());
                    WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));

                    // Ensure element is visible and clickable
                    wait.until(ExpectedConditions.elementToBeClickable(element));

                    // Perform the action
                    switch (action.getActionType()) {
                        case CLICK:
                            element.click();
                            break;
                        case DOUBLE_CLICK:
                            new Actions(driver)
                                    .doubleClick(element)
                                    .perform();
                            break;
                        case SET_SLIDER:
                            // Handle slider movement
                            System.out.println("DEBUG: Found slider element: " + element.isDisplayed());

                            moveSlider(element, Double.parseDouble(action.getInputValue()));

                            break;
                        case SENDKEYS:
                            element.sendKeys(action.getInputValue());
                            break;
                        case VERIFY:
                            Assert.assertTrue(element.isDisplayed());
                            break;
                        case SWIPE:
                            performSwipe(element, action);
                            break;
                    }
                    return;
                }
            } catch (Exception e) {
                lastException = e;
                System.err.println("Attempt " + (i + 1) + " failed: " + e.getMessage());
                try {
                    Thread.sleep(waitBetweenRetries);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        throw new RuntimeException("Failed to execute action after " + maxRetries + " attempts", lastException);
    }*/

    public void executeAction(TestAction action) {
        int maxRetries = 3;
        long waitBetweenRetries = 1000;
        Exception lastException = null;

        System.out.println("Executing action: " + action);

        for (int i = 0; i < maxRetries; i++) {
            try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

                if (action.getLocatorStrategy() == LocatorStrategy.VISUAL) {
                    handleVisualAction(action);
                    return;
                } else {
                    // Switch to WebView context if needed
                    try {
                        if (driver instanceof SupportsContextSwitching) {
                            SupportsContextSwitching contextAwareDriver = (SupportsContextSwitching) driver;
                            Set<String> contextHandles = contextAwareDriver.getContextHandles();
                            System.out.println("Available contexts: " + contextHandles);

                            for (String context : contextHandles) {
                                if (context.contains("WEBVIEW")) {
                                    contextAwareDriver.context(context);
                                    System.out.println("Switched to context: " + context);
                                    break;
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Error switching context: " + e.getMessage());
                        e.printStackTrace();
                    }

                    // Rest of your existing code...
                    By locator = getBy(action.getLocatorStrategy(), action.getLocatorValue());
                    WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));

                    // Ensure element is visible and clickable
                    wait.until(ExpectedConditions.elementToBeClickable(element));

                    // Perform the action
                    switch (action.getActionType()) {
                        case CLICK:
                            element.click();
                            break;
                        case DOUBLE_CLICK:
                            new Actions(driver)
                                    .doubleClick(element)
                                    .perform();
                            break;
                        case SET_SLIDER:
                            // Handle slider movement
                            System.out.println("DEBUG: Found slider element: " + element.isDisplayed());

                            moveSlider(element, Double.parseDouble(action.getInputValue()));

                            break;
                        case SENDKEYS:
                            element.sendKeys(action.getInputValue());
                            break;
                        case TYPE:
                            try {
                                // First attempt - standard sendKeys
                                element.clear();
                                element.sendKeys(action.getInputValue());
                            } catch (Exception e1) {
                                try {
                                    // Second attempt - using Actions class
                                    new Actions(driver)
                                            .moveToElement(element)
                                            .click()
                                            .sendKeys(action.getInputValue())
                                            .perform();
                                } catch (Exception e2) {
                                    try {
                                        // Third attempt - using JavaScript
                                        ((JavascriptExecutor) driver).executeScript(
                                                "arguments[0].value=arguments[1]",
                                                element,
                                                action.getInputValue()
                                        );
                                    } catch (Exception e3) {
                                        // Fourth attempt - using different locator strategies
                                        try {
                                            // Try by accessibility ID
                                            WebElement elementByAccess = driver.findElement(
                                                    By.xpath("//XCUIElementTypeTextField[@label='Enter mobile number or email']")
                                            );
                                            elementByAccess.sendKeys(action.getInputValue());
                                        } catch (Exception e4) {
                                            // Try by class name and index
                                            List<WebElement> textFields = driver.findElements(
                                                    By.className("XCUIElementTypeTextField")
                                            );
                                            if (!textFields.isEmpty()) {
                                                textFields.get(0).sendKeys(action.getInputValue());
                                            } else {
                                                throw new RuntimeException("Could not find text field using any method");
                                            }
                                        }
                                    }
                                }
                            }
                            break;
                        case VERIFY:
                            Assert.assertTrue(element.isDisplayed());
                            break;
                        case SWIPE:
                            performSwipe(element, action);
                            break;
                    }
                    return;
                }
            } catch (Exception e) {
                lastException = e;
                System.err.println("Attempt " + (i + 1) + " failed: " + e.getMessage());
                try {
                    Thread.sleep(waitBetweenRetries);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        throw new RuntimeException("Failed to execute action after " + maxRetries + " attempts", lastException);
    }
    private void handleVisualAction(TestAction action) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode coordinates = objectMapper.readTree(action.getLocatorValue());

        // Calculate center point of the element
        int centerX = coordinates.get("x").asInt() + (coordinates.get("width").asInt() / 2);
        int centerY = coordinates.get("y").asInt() + (coordinates.get("height").asInt() / 2);

        switch (action.getActionType()) {
            case CLICK:
                // Using W3C Actions instead of TouchAction
                new Actions(driver)
                        .moveToLocation(centerX, centerY)
                        .click()
                        .perform();
                break;
            case DOUBLE_CLICK:
                try {
                    // First ensure focus by tapping once
                    new Actions(driver)
                            .moveToLocation(centerX, centerY)
                            .click()
                            .perform();

                    // Short wait to ensure focus is gained
                    Thread.sleep(500);

                   /* // Now perform the double click
                    new Actions(driver)
                            .moveToLocation(centerX, centerY)
                            .click()
                            .pause(Duration.ofMillis(200))
                            .click()
                            .perform();*/

                /*Alternative approach using direct W3C actions if above doesn't work*/
                Point targetPoint = new Point(centerX, centerY);
                Sequence sequence = new Sequence(new PointerInput(Kind.TOUCH, "finger"), 0)
                    .addAction(new PointerInput(Kind.TOUCH, "finger").createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), targetPoint.x, targetPoint.y))
                    .addAction(new PointerInput(Kind.TOUCH, "finger").createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                    .addAction(new PointerInput(Kind.TOUCH, "finger").createPointerUp(PointerInput.MouseButton.LEFT.asArg()))
                    .addAction(new PointerInput(Kind.TOUCH, "finger").createPointerMove(Duration.ofMillis(100), PointerInput.Origin.viewport(), targetPoint.x, targetPoint.y))
                    .addAction(new PointerInput(Kind.TOUCH, "finger").createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                    .addAction(new PointerInput(Kind.TOUCH, "finger").createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

                driver.perform(Arrays.asList(sequence));


                } catch (Exception e) {
                    // Try alternative approach with separate action chains
                    try {
                        // First tap to ensure focus
                        new Actions(driver)
                                .moveToLocation(centerX, centerY)
                                .click()
                                .perform();

                        Thread.sleep(500);  // Wait for focus

                        // Then perform two separate clicks
                        new Actions(driver)
                                .moveToLocation(centerX, centerY)
                                .click()
                                .perform();

                        Thread.sleep(200);  // Wait between clicks

                        new Actions(driver)
                                .moveToLocation(centerX, centerY)
                                .click()
                                .perform();
                    } catch (Exception ex) {
                        throw new RuntimeException("Failed to perform double click", ex);
                    }
                }
                break;


            // Alternative approach using JavascriptExecutor if above doesn't work
            /*String script = String.format(
                "mobile: clickGesture",
                "{ x: %d, y: %d }",
                centerX,
                centerY
            );
            ((JavascriptExecutor) driver).executeScript(script);*/

            case VERIFY:
                double confidence = action.getConfidence();
                Assert.assertTrue(confidence >= 0.8, "Visual match confidence too low: " + confidence);
                break;

            default:
                throw new UnsupportedOperationException(
                        "Action type " + action.getActionType() + " not supported for visual strategy"
                );
        }
    }

    private void moveSlider(WebElement slider, double percentage) {
        try {
            // Get the exact coordinates of the SeekBar
            WebElement seekBar = slider ;//driver.findElement(By.xpath("//android.widget.SeekBar[@content-desc='Reading Progress Bar']"));
            Rectangle bounds = seekBar.getRect();

            // Calculate positions
            int startX = bounds.x + 50; // Add offset to ensure we're on the slider
            int endX = bounds.x + bounds.width - 50; // Subtract offset from end
            int centerY = bounds.y + (bounds.height / 2);

            // Calculate target position
            int targetX = startX + (int)((endX - startX) * percentage);

            System.out.println("Attempting to slide from X: " + startX + " to X: " + targetX + " at Y: " + centerY);

            // Try W3C Actions (more modern approach)
            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
            Sequence sequence = new Sequence(finger, 0);

            // Press and hold
            sequence.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, centerY));
            sequence.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));

            // Small pause
            sequence.addAction(finger.createPointerMove(Duration.ofMillis(200), PointerInput.Origin.viewport(), startX, centerY));

            // Slow, deliberate move
            for (int i = 0; i <= 10; i++) {
                int intermediateX = startX + (int)((targetX - startX) * (i/10.0));
                sequence.addAction(finger.createPointerMove(Duration.ofMillis(50),
                        PointerInput.Origin.viewport(), intermediateX, centerY));
            }

            // Hold briefly at target
            sequence.addAction(finger.createPointerMove(Duration.ofMillis(200), PointerInput.Origin.viewport(), targetX, centerY));

            // Release
            sequence.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

            driver.perform(Arrays.asList(sequence));

            // Add debug information
            Thread.sleep(1000);
            System.out.println("Slider action completed. New position: " + seekBar.getAttribute("text"));

        } catch (Exception e) {
            System.out.println("Error during slider movement: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private boolean verifySliderPosition(WebElement slider, double expectedPercentage) {
        try {
            // Get the current value from the slider
            String currentValue = slider.getAttribute("text");
            double maxValue = Double.parseDouble(currentValue);
            // Add your verification logic here
            return true; // Replace with actual verification
        } catch (Exception e) {
            return false;
        }
    }



    private By getBy(LocatorStrategy strategy, String value) {
        switch (strategy) {
            case ID:
                return By.id(value);
            case XPATH:
                return By.xpath(value);
            case ACCESSIBILITY_ID:
                return MobileBy.AccessibilityId(value);
            case CLASS_NAME:
                return By.className(value);
            case UISELECTOR:
                return MobileBy.AndroidUIAutomator(value);
            default:
                throw new IllegalArgumentException("Unsupported locator strategy: " + strategy);
        }
    }


    private void performSwipe(WebElement element, TestAction action) {
        try {
            // Get element bounds
            Rectangle bounds = element.getRect();

            // Calculate swipe coordinates
            int startX = bounds.x + (bounds.width / 2);
            int endX = startX; // Keep X constant for vertical swipe
            int startY = bounds.y + (bounds.height * 3/4); // Start from 75% down
            int endY = bounds.y + (bounds.height / 4);     // End at 25% up

            // Create W3C Actions sequence
            new Actions(driver)
                    .moveToElement(element)
                    .clickAndHold()
                    .moveByOffset(0, -bounds.height/2) // Move up by half the height
                    .release()
                    .perform();

            // Alternative approach using Sequence
            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
            Sequence swipe = new Sequence(finger, 1);

            swipe.addAction(finger.createPointerMove(Duration.ofMillis(0),
                    PointerInput.Origin.viewport(), startX, startY));
            swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            swipe.addAction(finger.createPointerMove(Duration.ofMillis(600),
                    PointerInput.Origin.viewport(), endX, endY));
            swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

            ((AppiumDriver) driver).perform(Arrays.asList(swipe));

            // Wait for animation
            Thread.sleep(1000);

        } catch (Exception e) {
            throw new RuntimeException("Failed to perform swipe action", e);
        }
    }

    private WebElement findElement(TestAction action) {
        switch (action.getLocatorStrategy()) {
            case ID:
                return driver.findElement(By.id(action.getLocatorValue()));
            case XPATH:
                return driver.findElement(By.xpath(action.getLocatorValue()));
            case ACCESSIBILITY_ID:
                return driver.findElement(AppiumBy.accessibilityId(action.getLocatorValue()));
            case ANDROID_UIAUTOMATOR:
                return driver.findElement(AppiumBy.androidUIAutomator(
                        "new UiSelector().text(\"" + action.getLocatorValue() + "\")"));
            case CLASS_NAME:
                return driver.findElement(By.className(action.getLocatorValue()));
            case VISUAL:
                // Implement visual location strategy using Appium's image recognition
                return driver.findElement(AppiumBy.image(action.getLocatorValue()));
            default:
                throw new IllegalArgumentException("Unsupported locator strategy");
        }
    }
}
