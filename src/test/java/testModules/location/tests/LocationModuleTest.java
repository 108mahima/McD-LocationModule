package testModules.location.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import base.BaseTest;
import pages.LocationPage;
import pages.LoginOtpPage;

/**
 * Positive tests for the Location module.
 *
 * Requirement (Case Study, Sprint 1 scope):
 *  Story 1 - User should be able to search/mark and select a desired location
 *            for delivery; the restaurant is selected based on that location.
 *  Story 2 - The selected location should be displayed at the top of the page.
 */
public class LocationModuleTest extends BaseTest {

    private static final String BASE_URL = "https://mcdelivery.co.in";

    @Test(priority = 1, groups = {"smoke", "regression"},
            description = "TC_LOC_01: Home page title contains McDelivery")
    public void verifyHomePageTitle() {
        LocationPage locationPage = new LocationPage(driver);
        logStep("Page title: " + locationPage.getPageTitle());

        Assert.assertTrue(locationPage.getPageTitle().contains("McDelivery"),
                "Page title should contain 'McDelivery'");
    }

    @Test(priority = 2, groups = {"smoke", "regression"},
            description = "TC_LOC_02: Home page opens on the McDelivery India domain")
    public void verifyHomePageUrl() {
        LocationPage locationPage = new LocationPage(driver);
        logStep("Current URL: " + locationPage.getCurrentUrl());

        Assert.assertTrue(locationPage.getCurrentUrl().startsWith(BASE_URL),
                "URL should start with " + BASE_URL);
    }

    @Test(priority = 3, groups = {"smoke", "regression"},
            description = "TC_LOC_03: Location bar is displayed at the top of the page (Story 2)")
    public void verifyLocationBarDisplayedAtTop() {
        LocationPage locationPage = new LocationPage(driver);

        Assert.assertTrue(locationPage.isLocationBarDisplayed(), "Location bar should be displayed");
        int top = locationPage.getLocationBarTopOffset();
        logStep("Location bar top offset: " + top + "px");
        Assert.assertTrue(top < 150, "Location bar should be in the page header, but was at y=" + top);
        Assert.assertTrue(locationPage.waitForLocationBarReady(),
                "Location bar should show the store / location prompt, but shows '" + locationPage.getLocationBarText() + "'");
        logStep("Location bar text: " + locationPage.getLocationBarText());
        Assert.assertTrue(locationPage.isDeliveryTimeDisplayed(), "Delivery time ('Now') should be shown next to the location");
    }

    @Test(priority = 4, groups = {"smoke", "regression"},
            description = "TC_LOC_04: Clicking the location bar opens 'Select Delivery Address' (Story 1)")
    public void verifyLocationBarOpensSelectDeliveryAddress() {
        LocationPage locationPage = new LocationPage(driver);

        locationPage.openSelectDeliveryAddress();
        logStep("Clicked the location bar");

        Assert.assertTrue(locationPage.isSelectDeliveryAddressPanelOpen(),
                "'Select Delivery Address' panel should open");
        Assert.assertEquals(locationPage.getAddressPanelTitle(), "Select Delivery Address");
    }

    @Test(priority = 5, groups = {"regression"},
            description = "TC_LOC_05: Guest user is asked to log in before choosing a delivery address (Story 1)")
    public void verifyGuestIsAskedToLoginToSelectAddress() {
        LocationPage locationPage = new LocationPage(driver);
        locationPage.openSelectDeliveryAddress();

        Assert.assertTrue(locationPage.isLoginToContinueDisplayed(),
                "'Log In / Sign Up to Continue' should be shown to a guest user");
        Assert.assertEquals(locationPage.getLoginToContinueText(), "Log In / Sign Up to Continue");

        LoginOtpPage loginPage = locationPage.clickLoginToContinue();
        logStep("Clicked 'Log In / Sign Up to Continue'");

        Assert.assertTrue(loginPage.isLoaded(), "Mobile number login page should open");
        Assert.assertTrue(loginPage.getCurrentUrl().contains("/auth-with-otp"),
                "URL should be /auth-with-otp but was " + loginPage.getCurrentUrl());
        Assert.assertTrue(loginPage.isMobileLabelDisplayed(), "'Enter your mobile number' should be shown");
    }

    @Test(priority = 6, groups = {"regression"},
            description = "TC_LOC_06: Back arrow closes the 'Select Delivery Address' panel")
    public void verifyAddressPanelClosesWithBackArrow() {
        LocationPage locationPage = new LocationPage(driver);
        locationPage.openSelectDeliveryAddress();
        Assert.assertTrue(locationPage.isSelectDeliveryAddressPanelOpen(), "Panel should be open first");

        locationPage.closeAddressPanel();
        logStep("Clicked the back arrow");

        Assert.assertTrue(locationPage.isLocationBarDisplayed(),
                "Home page with the location bar should be visible again");
        Assert.assertTrue(locationPage.getCurrentUrl().startsWith(BASE_URL));
    }

    @Test(priority = 7, groups = {"regression"},
            description = "TC_LOC_07: Delivery mode selector offers McDelivery and Take Away")
    public void verifyDeliveryModeOptions() {
        LocationPage locationPage = new LocationPage(driver);

        Assert.assertTrue(locationPage.isDeliveryModeSelectorDisplayed(),
                "'McDelivery' mode selector should be displayed next to the location");
        locationPage.openDeliveryModeSelector();
        logStep("Opened the delivery mode selector");

        Assert.assertTrue(locationPage.isTakeAwayOptionDisplayed(), "'Take Away' option should be listed");
    }

    @Test(priority = 8, groups = {"regression"},
            description = "TC_LOC_08: Location bar is still displayed after a page refresh (Story 2)")
    public void verifyLocationBarRetainedAfterRefresh() {
        LocationPage locationPage = new LocationPage(driver);
        Assert.assertTrue(locationPage.isLocationBarDisplayed(), "Location bar should be displayed before refresh");

        locationPage.refreshPage();
        logStep("Refreshed the page");

        Assert.assertTrue(locationPage.isLocationBarDisplayed(), "Location bar should be displayed after refresh");
        Assert.assertTrue(locationPage.getLocationBarTopOffset() < 150, "Location bar should stay at the top");
    }

    @Test(priority = 9, groups = {"smoke", "regression"},
            description = "TC_LOC_09: 'Restaurants Nearby' opens the nearby restaurant locator")
    public void verifyRestaurantsNearbyNavigation() {
        LocationPage locationPage = new LocationPage(driver);

        String url = locationPage.openRestaurantsNearby().getCurrentUrl();
        logStep("Navigated to " + url);

        Assert.assertTrue(url.startsWith(BASE_URL + "/restaurants-nearby"),
                "URL should be /restaurants-nearby but was " + url);
    }
}
