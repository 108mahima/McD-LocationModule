package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import utilities.CommonMethods;
import utilities.WaitUtils;

/**
 * Page Factory for /auth-with-otp, the page a guest reaches from
 * "Select Delivery Address" > "Log In / Sign Up to Continue".
 * A delivery address can only be saved after OTP login, so the tests
 * only check field validation and never request an OTP.
 */
public class LoginOtpPage {

    private final WebDriver driver;

    @FindBy(css = "input[type='tel']")
    private WebElement mobileNumberInput;

    @FindBy(xpath = "//button[normalize-space()='Verify Mobile']")
    private WebElement verifyMobileButton;

    @FindBy(xpath = "//*[contains(normalize-space(text()),'Enter your mobile number')]")
    private WebElement mobileNumberLabel;

    public LoginOtpPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public boolean isLoaded() {
        WaitUtils.waitForUrlContains(driver, "auth-with-otp");
        return WaitUtils.isVisibleWithin(driver, mobileNumberInput, 45);
    }

    public boolean isMobileLabelDisplayed() {
        return WaitUtils.isVisibleWithin(driver, mobileNumberLabel, 30);
    }

    public void enterMobileNumber(String mobile) {
        WaitUtils.waitForVisible(driver, mobileNumberInput);
        CommonMethods.enterText(mobileNumberInput, mobile);
    }

    public String getMobileNumberValue() {
        return mobileNumberInput.getAttribute("value");
    }

    public String getMobileMaxLength() {
        return mobileNumberInput.getAttribute("maxlength");
    }

    public String getMobilePlaceholder() {
        return mobileNumberInput.getAttribute("placeholder");
    }

    public boolean isVerifyMobileEnabled() {
        WaitUtils.waitForVisible(driver, verifyMobileButton);
        return verifyMobileButton.isEnabled();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
