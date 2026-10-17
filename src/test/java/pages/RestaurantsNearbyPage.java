package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import utilities.WaitUtils;

/**
 * Page Factory for /restaurants-nearby. Without a shared location the
 * site shows "Sorry, we do not serve this location yet".
 */
public class RestaurantsNearbyPage {

    private final WebDriver driver;

    @FindBy(xpath = "//*[contains(normalize-space(text()),'do not serve this location')]")
    private WebElement locationNotServedMessage;

    public RestaurantsNearbyPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public boolean isLocationNotServedMessageDisplayed() {
        return WaitUtils.isVisibleWithin(driver, locationNotServedMessage, 60);
    }

    public String getLocationNotServedMessage() {
        return locationNotServedMessage.getText().trim();
    }
}
