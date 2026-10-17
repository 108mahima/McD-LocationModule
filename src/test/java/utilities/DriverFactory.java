package utilities;

import java.time.Duration;
import java.util.Map;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

/**
 * Creates one WebDriver per thread so TestNG can run classes in parallel.
 * Browser drivers are resolved automatically by Selenium Manager.
 */
public class DriverFactory {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    /**
     * Block the browser's location permission (2 = block). Otherwise "Restaurants Nearby"
     * waits forever on the "Know your location" prompt that nobody answers in a test.
     */
    private static final Map<String, Object> CHROMIUM_PREFS =
            Map.of("profile.default_content_setting_values.geolocation", 2);

    private DriverFactory() {
    }

    public static WebDriver initDriver() {
        String browser = ConfigReader.getProperty("browser");
        boolean headless = ConfigReader.getBoolean("headless");
        int width = ConfigReader.getInt("windowWidth");
        int height = ConfigReader.getInt("windowHeight");
        String sizeArg = "--window-size=" + width + "," + height;

        WebDriver driver;
        switch (browser.toLowerCase()) {
            case "chrome": {
                ChromeOptions options = new ChromeOptions();
                if (headless) {
                    options.addArguments("--headless=new");
                }
                options.addArguments(sizeArg, "--disable-notifications");
                options.setExperimentalOption("prefs", CHROMIUM_PREFS);
                driver = new ChromeDriver(options);
                break;
            }
            case "firefox": {
                FirefoxOptions options = new FirefoxOptions();
                if (headless) {
                    options.addArguments("-headless");
                }
                options.addPreference("geo.enabled", false);
                driver = new FirefoxDriver(options);
                break;
            }
            default: {
                EdgeOptions options = new EdgeOptions();
                if (headless) {
                    options.addArguments("--headless=new");
                }
                options.addArguments(sizeArg, "--disable-notifications");
                options.setExperimentalOption("prefs", CHROMIUM_PREFS);
                driver = new EdgeDriver(options);
            }
        }

        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(ConfigReader.getInt("pageLoadTimeout")));
        driver.manage().window().setSize(new Dimension(width, height));
        DRIVER.set(driver);
        return driver;
    }

    public static WebDriver getDriver() {
        return DRIVER.get();
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            driver.quit();
            DRIVER.remove();
        }
    }
}
