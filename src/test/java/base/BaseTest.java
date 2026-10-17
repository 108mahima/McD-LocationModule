package base;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import listeners.TestListener;
import utilities.ConfigReader;
import utilities.DriverFactory;

/**
 * TestNG base class: opens a fresh browser on the McDelivery home page
 * before every test and closes it afterwards.
 * Reporting and screenshots are handled by listeners.TestListener.
 */
public class BaseTest {

    protected WebDriver driver;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        driver = DriverFactory.initDriver();
        driver.get(ConfigReader.getProperty("url"));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quitDriver();
    }

    /** Adds a step line to the Extent report for the running test. */
    protected void logStep(String message) {
        if (TestListener.getTest() != null) {
            TestListener.getTest().info(message);
        }
        System.out.println("[" + Thread.currentThread().getName() + "] " + message);
    }
}
