package base;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public class BaseTest {
    protected AppiumDriver driver;
    protected BedrockRuntimeClient bedrockClient;
    private ProfileCredentialsProvider credentialsProvider;

    static {
        // Set log levels programmatically
        Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.INFO);

        Logger asyncLogger = (Logger) LoggerFactory.getLogger("org.asynchttpclient");
        asyncLogger.setLevel(Level.ERROR);

        Logger nettyLogger = (Logger) LoggerFactory.getLogger("io.netty");
        nettyLogger.setLevel(Level.ERROR);
    }

    @BeforeClass
    public void setup() throws MalformedURLException {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("platformName", "Android");
        caps.setCapability("appium:deviceName", "emulator-5554");
        caps.setCapability("appium:automationName", "UiAutomator2");
        caps.setCapability("appium:noReset", false);
        caps.setCapability("appium:fullReset", false);
        caps.setCapability("appium:appPackage", "com.amazon.kindle");
        caps.setCapability("appium:appActivity", "com.amazon.kindle.UpgradePage");

        driver = new AndroidDriver(
                new URL("http://127.0.0.1:4723"), caps);

        ProfileCredentialsProvider.create();
        bedrockClient = BedrockRuntimeClient.builder()
                .region(Region.US_EAST_1)  // Make sure this matches your model's region
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    @AfterClass
    public void tearDown() {
        try {
            if (driver != null) {
                driver.quit();
            }
            if (credentialsProvider != null) {
                credentialsProvider.close();
            }
            if (bedrockClient != null) {
                bedrockClient.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Refresh AWS credentials by creating a new credentials provider and client
     */
    protected void refreshCredentials() {
        try {
            // Close existing resources
            if (credentialsProvider != null) {
                credentialsProvider.close();
            }
            if (bedrockClient != null) {
                bedrockClient.close();
            }

            // Create new credentials provider and client
            credentialsProvider = ProfileCredentialsProvider.create();
            bedrockClient = BedrockRuntimeClient.builder()
                    .region(Region.US_EAST_1)
                    .credentialsProvider(credentialsProvider)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Failed to refresh AWS credentials", e);
        }
    }
}
