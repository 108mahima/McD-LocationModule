package testModules.location.tests;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import base.BaseTest;
import pages.LocationPage;
import pages.LoginOtpPage;
import pages.RestaurantsNearbyPage;
import utilities.ConfigReader;
import utilities.ExcelUtils;

/**
 * Negative tests for the Location module.
 * Mobile number inputs come from Excel (Apache POI), designed with
 * Equivalence Partitioning and Boundary Value Analysis.
 *
 * Safety: these tests never click "Verify Mobile", so no OTP is ever sent.
 */
public class LocationNegativeTest extends BaseTest {

    private static final String LOGIN_URL = "https://mcdelivery.co.in/auth-with-otp";

    @DataProvider(name = "invalidMobileNumbers")
    public Object[][] invalidMobileNumbers() {
        return ExcelUtils.getSheetData(ConfigReader.getProperty("testDataFile"), "MobileValidation");
    }

    @DataProvider(name = "overLengthMobileNumbers")
    public Object[][] overLengthMobileNumbers() {
        return ExcelUtils.getSheetData(ConfigReader.getProperty("testDataFile"), "MaxLength");
    }

    @Test(priority = 10, groups = {"regression", "negative"},
            description = "TC_LOC_10: Restaurants Nearby without a shared location shows 'not served' message")
    public void verifyRestaurantsNearbyWithoutLocation() {
        RestaurantsNearbyPage nearbyPage = new LocationPage(driver).openRestaurantsNearby();

        Assert.assertTrue(nearbyPage.isLocationNotServedMessageDisplayed(),
                "'Sorry, we do not serve this location yet' should be shown when no location is shared");
        logStep("Message shown: " + nearbyPage.getLocationNotServedMessage());
        Assert.assertTrue(nearbyPage.getLocationNotServedMessage().contains("do not serve this location"));
    }

    @Test(priority = 11, groups = {"regression", "negative"}, dataProvider = "invalidMobileNumbers",
            description = "TC_LOC_11: Invalid mobile number keeps 'Verify Mobile' disabled (address login gate)")
    public void verifyInvalidMobileNumberIsRejected(String testCaseId, String description,
                                                    String mobileInput, String verifyEnabled) {
        driver.get(LOGIN_URL);
        LoginOtpPage loginPage = new LoginOtpPage(driver);
        Assert.assertTrue(loginPage.isLoaded(), "Login page should load");

        loginPage.enterMobileNumber(mobileInput);
        logStep(testCaseId + " - " + description + " - entered '" + mobileInput
                + "', field value '" + loginPage.getMobileNumberValue() + "'");

        Assert.assertEquals(loginPage.isVerifyMobileEnabled(), Boolean.parseBoolean(verifyEnabled),
                testCaseId + ": 'Verify Mobile' enabled state for input '" + mobileInput + "'");
        Assert.assertTrue(loginPage.getMobileNumberValue().length() <= 10,
                testCaseId + ": field must never hold more than 10 characters");
    }

    @Test(priority = 12, groups = {"regression", "negative"}, dataProvider = "overLengthMobileNumbers",
            description = "TC_LOC_12: Mobile number longer than 10 digits is truncated to 10 (BVA)")
    public void verifyMobileNumberMaxLength(String testCaseId, String description,
                                            String mobileInput, String expectedValue) {
        driver.get(LOGIN_URL);
        LoginOtpPage loginPage = new LoginOtpPage(driver);
        Assert.assertTrue(loginPage.isLoaded(), "Login page should load");

        Assert.assertEquals(loginPage.getMobileMaxLength(), "10", "maxlength attribute");
        Assert.assertEquals(loginPage.getMobilePlaceholder(), "10 Digit Mobile Number", "placeholder");

        loginPage.enterMobileNumber(mobileInput);
        logStep(testCaseId + " - " + description + " - field value '" + loginPage.getMobileNumberValue() + "'");

        Assert.assertEquals(loginPage.getMobileNumberValue(), expectedValue, testCaseId + ": truncated value");
    }
}
