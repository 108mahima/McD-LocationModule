package pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import utilities.CommonMethods;
import utilities.WaitUtils;

/**
 * Page Factory for the Location module on the McDelivery home page:
 * the location bar at the top of the page, the "Select Delivery Address"
 * panel it opens, the delivery mode (McDelivery / Take Away) selector and
 * the "Restaurants Nearby" entry point.
 *
 * Locators were taken from the live site (desktop layout, width >= 1200px).
 */
public class LocationPage {

    private final WebDriver driver;

    // ---- Location bar at the top of the page ------------------------------

    @FindBy(className = "toolbar-v1__time-location-bar-desktop")
    private WebElement locationToolbar;

    @FindBy(css = ".toolbar-v1__time-location-bar-desktop .toolbar-v1__location")
    private WebElement locationBar;

    @FindBy(css = ".toolbar-v1__time-location-bar-desktop .toolbar-v1__time")
    private WebElement deliveryTime;

    // ---- "Select Delivery Address" panel ----------------------------------

    @FindBy(tagName = "app-select-address")
    private List<WebElement> addressPanels;

    @FindBy(xpath = "//app-select-address//div[contains(@class,'bottom-sheet__row')]")
    private WebElement addressPanelTitle;

    @FindBy(css = "app-select-address .bottom-sheet__cta-fw")
    private WebElement loginToContinueButton;

    @FindBy(css = "app-select-address img.bottom-sheet__ic-back")
    private WebElement addressPanelBackArrow;

    // ---- Delivery mode selector -------------------------------------------

    @FindBy(xpath = "//div[contains(@class,'bm-select')]//span[normalize-space()='McDelivery']")
    private WebElement deliveryModeSelector;

    @FindBy(xpath = "//*[normalize-space(text())='Take Away']")
    private List<WebElement> takeAwayOptions;

    // ---- Other entry points -----------------------------------------------

    @FindBy(xpath = "//*[normalize-space(text())='Restaurants Nearby']")
    private WebElement restaurantsNearbyLink;

    @FindBy(css = "input[placeholder='Search here']")
    private WebElement menuSearchInput;

    public LocationPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    // ---- Page information ---------------------------------------------------

    public String getPageTitle() {
        return driver.getTitle();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    // ---- Location bar -------------------------------------------------------

    /** Waits until the location bar is rendered at the top of the page. */
    public boolean isLocationBarDisplayed() {
        return WaitUtils.isVisibleWithin(driver, locationBar, 45);
    }

    /**
     * Waits for the location bar to be filled by the backend (store / location
     * prompt instead of skeleton, "Now" or "-"). The backend intermittently
     * never answers, so the page is reloaded - as a user would - up to
     * maxReloads times. Returns false if the bar never becomes ready.
     */
    public boolean waitForLocationBarReady(int maxReloads) {
        for (int attempt = 0; attempt <= maxReloads; attempt++) {
            try {
                WaitUtils.fluentWait(driver, 30).until(d -> {
                    String text = locationToolbar.getText().trim();
                    return text.length() > 5 && !text.startsWith("-");
                });
                return true;
            } catch (org.openqa.selenium.TimeoutException e) {
                if (attempt < maxReloads) {
                    System.out.println("Location bar not ready (text='" + getLocationBarText()
                            + "'), reloading page. Reload " + (attempt + 1) + " of " + maxReloads);
                    refreshPage();
                }
            }
        }
        return false;
    }

    public boolean waitForLocationBarReady() {
        return waitForLocationBarReady(2);
    }

    public String getLocationBarText() {
        try {
            return locationToolbar.getText().trim().replace('\n', ' ');
        } catch (org.openqa.selenium.WebDriverException e) {
            return "";
        }
    }

    /** Distance in pixels from the top of the viewport to the location bar. */
    public int getLocationBarTopOffset() {
        WaitUtils.waitForVisible(driver, locationToolbar);
        return locationToolbar.getRect().getY();
    }

    public boolean isDeliveryTimeDisplayed() {
        return WaitUtils.isVisibleWithin(driver, deliveryTime, 45);
    }

    /**
     * Clicks the location bar until the "Select Delivery Address" panel opens.
     * The first click can land before Angular has attached its handlers,
     * so the click is repeated (every few seconds) until the panel exists.
     */
    public void openSelectDeliveryAddress() {
        assertLocationBarReady();
        WaitUtils.until(driver, d -> {
            if (!addressPanels.isEmpty()) {
                return true;
            }
            locationBar.click();
            return WaitUtils.isVisibleWithin(d, addressPanelTitle, 5);
        });
    }

    public boolean isSelectDeliveryAddressPanelOpen() {
        return !driver.findElements(By.tagName("app-select-address")).isEmpty()
                && WaitUtils.isVisibleWithin(driver, addressPanelTitle, 10);
    }

    public String getAddressPanelTitle() {
        return CommonMethods.getText(WaitUtils.waitForVisible(driver, addressPanelTitle));
    }

    /** The guest CTA loads from the backend after a skeleton loader, so allow time. */
    public boolean isLoginToContinueDisplayed() {
        return WaitUtils.isVisibleWithin(driver, loginToContinueButton, 45);
    }

    public String getLoginToContinueText() {
        return CommonMethods.getText(WaitUtils.waitForVisible(driver, loginToContinueButton));
    }

    public LoginOtpPage clickLoginToContinue() {
        CommonMethods.click(driver, loginToContinueButton);
        return new LoginOtpPage(driver);
    }

    public void closeAddressPanel() {
        CommonMethods.click(driver, addressPanelBackArrow);
        WaitUtils.until(driver, d -> d.findElements(By.cssSelector("ion-modal.show-modal app-select-address")).isEmpty());
    }

    // ---- Delivery mode ------------------------------------------------------

    public void openDeliveryModeSelector() {
        CommonMethods.click(driver, deliveryModeSelector);
    }

    public boolean isTakeAwayOptionDisplayed() {
        return WaitUtils.until(driver, d -> takeAwayOptions.stream().anyMatch(WebElement::isDisplayed));
    }

    public boolean isDeliveryModeSelectorDisplayed() {
        return WaitUtils.isVisibleWithin(driver, deliveryModeSelector, 45);
    }

    // ---- Restaurants Nearby -------------------------------------------------

    /**
     * Clicking "Restaurants Nearby" asks the browser for the user's location and only
     * navigates once that request is answered. In an automated browser nobody answers
     * the permission prompt, so the location request is denied up front (the user
     * "does not share" their location). See TC_LOC_09 / TC_LOC_10.
     */
    public RestaurantsNearbyPage openRestaurantsNearby() {
        assertLocationBarReady();
        denyBrowserLocation();
        CommonMethods.click(driver, restaurantsNearbyLink);
        WaitUtils.waitForUrlContains(driver, "restaurants-nearby");
        return new RestaurantsNearbyPage(driver);
    }

    /** Makes the page's geolocation requests fail immediately with "permission denied". */
    private void denyBrowserLocation() {
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "var denied = {code: 1, message: 'User denied Geolocation', PERMISSION_DENIED: 1};"
                + "navigator.geolocation.getCurrentPosition = function(ok, err) { if (err) { err(denied); } };"
                + "navigator.geolocation.watchPosition = function(ok, err) { if (err) { err(denied); } return 0; };");
    }

    public boolean isMenuSearchDisplayed() {
        return WaitUtils.isVisibleWithin(driver, menuSearchInput, 45);
    }

    private void assertLocationBarReady() {
        if (!waitForLocationBarReady()) {
            throw new IllegalStateException("Location bar never loaded from the backend (shows '"
                    + getLocationBarText() + "') - see defect DEF_LOC_01");
        }
    }

    public void refreshPage() {
        driver.navigate().refresh();
        WaitUtils.waitForPageLoad(driver);
    }
}
