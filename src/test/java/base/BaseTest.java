package base;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
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

    @Parameters({"platform"})
    @BeforeClass
    public void setup(@Optional("android") String platform) throws MalformedURLException {
        platform = platform.toLowerCase();

        DesiredCapabilities caps = new DesiredCapabilities();

        if (platform.equals("android")) {
            setupAndroidDriver(caps);
        } else if (platform.equals("ios")) {
            setupIOSDriver(caps);
        } else {
            throw new IllegalArgumentException("Invalid platform. Use 'android' or 'ios'");
        }

        initializeBedrockClient();
    }

    private void setupAndroidDriver(DesiredCapabilities caps) throws MalformedURLException {
        caps.setCapability("platformName", "Android");
        caps.setCapability("appium:deviceName", "emulator-5554");
        caps.setCapability("appium:automationName", "UiAutomator2");
        caps.setCapability("appium:noReset", true);
        caps.setCapability("appium:fullReset", false);
        caps.setCapability("appium:appPackage", "com.amazon.kindle");
        caps.setCapability("appium:appActivity", "com.amazon.kindle.UpgradePage");

        driver = new AndroidDriver(new URL("http://127.0.0.1:4723"), caps);
    }

    private void setupIOSDriver(DesiredCapabilities caps) throws MalformedURLException {
        caps.setCapability("platformName", "iOS");
        caps.setCapability("appium:deviceName", "iPhone Simulator");
        caps.setCapability("appium:automationName", "XCUITest");
        caps.setCapability("appium:platformVersion", "18.5"); // Update with your iOS version
        caps.setCapability("appium:noReset", false);
        caps.setCapability("appium:fullReset", false);
        caps.setCapability("appium:bundleId", "com.amazon.LassenDev");
        caps.setCapability("appium:udid", "CAA5A29A-2CA8-41C2-BD98-0A23853A4D6A");

        // Update with your app's bundle ID
        // If using a .app file locally
        // caps.setCapability("appium:app", "/path/to/your/app.app");

        driver = new IOSDriver(new URL("http://127.0.0.1:4723"), caps);
    }

    private void initializeBedrockClient() {
        ProfileCredentialsProvider.create();
        bedrockClient = BedrockRuntimeClient.builder()
                .region(Region.US_EAST_1)
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

    protected void refreshCredentials() {
        try {
            if (credentialsProvider != null) {
                credentialsProvider.close();
            }
            if (bedrockClient != null) {
                bedrockClient.close();
            }

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
