package testModules.location.junit;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.WebDriver;

import pages.LocationPage;
import utilities.ConfigReader;
import utilities.DriverFactory;

/**
 * JUnit 5 smoke tests for the Location module, re-using the same
 * Page Factory classes as the TestNG suite.
 */
@DisplayName("McDelivery Location module - JUnit 5 smoke")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LocationJUnitTest {

    private WebDriver driver;
    private LocationPage locationPage;

    @BeforeEach
    void setUp() {
        driver = DriverFactory.initDriver();
        driver.get(ConfigReader.getProperty("url"));
        locationPage = new LocationPage(driver);
    }

    @AfterEach
    void tearDown() {
        DriverFactory.quitDriver();
    }

    @Test
    @Order(1)
    @DisplayName("JU_LOC_01: Home page title and URL are correct")
    void homePageTitleAndUrl() {
        assertAll(
                () -> assertTrue(locationPage.getPageTitle().contains("McDelivery"), "title"),
                () -> assertTrue(locationPage.getCurrentUrl().startsWith("https://mcdelivery.co.in"), "url"));
    }

    @Test
    @Order(2)
    @DisplayName("JU_LOC_02: Location bar is displayed at the top of the page")
    void locationBarDisplayedAtTop() {
        assertTrue(locationPage.isLocationBarDisplayed(), "location bar should be displayed");
        assertTrue(locationPage.getLocationBarTopOffset() < 150, "location bar should be in the header");
    }

    @Test
    @Order(3)
    @DisplayName("JU_LOC_03: Location bar opens 'Select Delivery Address'")
    void locationBarOpensAddressPanel() {
        locationPage.openSelectDeliveryAddress();
        assertEquals("Select Delivery Address", locationPage.getAddressPanelTitle());
    }
}
