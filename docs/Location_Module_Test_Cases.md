# McDelivery India: Location Module Test Cases (v3, validated)

**Application:** https://mcdelivery.co.in/ · **Module:** Location · **Prepared by:** Mahima Yadav · **Validated against the live site:** 15-Oct-2026 (Edge, desktop layout 1366×900)

**Requirements (case study):**
- **REQ-LOC-01 (Location Epic, Story 1):** The user can search, mark and select a delivery location, and the restaurant is selected based on that location.
- **REQ-LOC-02 (Location Epic, Story 2):** The selected location is displayed at the top of the page.

---

## 1. Validation of the earlier test cases

### 1.1 `Sprint Case Study_MacD.csv` (Sprint 1): **not valid**

| ID | Problem found on the live site | Decision |
|---|---|---|
| TC-LOC-01 | There is no "Enter city name" field, and no "Select restaurant → Confirm" step. The location comes from a saved delivery address, which needs an OTP login, or from browser location (Restaurants Nearby). The precondition "User logged in" cannot be automated because login is OTP-based. | Replaced by TC_LOC_04, TC_LOC_05 and TC_LOC_09 |
| TC-LOC-02 | The site has no "No restaurants found" error for city "XYZ". The real negative message is *"Sorry, we do not serve this location yet"* on Restaurants Nearby. | Replaced by TC_LOC_10 |
| TC-LOC-03 | The intent (location shown at the top and kept after refresh) is valid, but the steps rely on a city that cannot be typed. | Rewritten as TC_LOC_03 and TC_LOC_08 |
| TC-LOC-04 | "Change to new city Mumbai" is not possible as a guest. Changing the address needs OTP login. | Kept as a manual-only case, TC_LOC_13 (Blocked for automation) |
| All | The status was marked **Pass** without being run against the real UI. The arrows and quotes were saved in the wrong encoding (`?`, `�`). There were no negative or boundary test cases. | Corrected below |

### 1.2 `Location_Module_Manual_Test_Cases_v2.md`: **partly valid**

| ID | Finding | Decision |
|---|---|---|
| LOC-MAN-001 | Valid flow, but the expected result ("stays on the McDelivery domain") does not check the location feature. The selector `.toolbar-v1__location-type-row` only exists in the mobile layout (<1200px). In the desktop layout the location bar is `.toolbar-v1__time-location-bar-desktop .toolbar-v1__location`. | Corrected as TC_LOC_04: "Select Delivery Address" must open |
| LOC-MAN-002 | Valid. Page title is "Order McDonald's Burger & Food Delivery Online - McDelivery India". | Kept as TC_LOC_01 |
| LOC-MAN-003 | Valid. | Kept as TC_LOC_02 |
| (missing) | Story 2 (location shown at the top), the guest login gate, and negative or boundary cases were not covered. | Added TC_LOC_03, TC_LOC_05 to TC_LOC_12 |

---

## 2. Corrected test cases

Design techniques: positive and negative testing, Equivalence Partitioning (EP) and Boundary Value Analysis (BVA) for the mobile number. P = Priority (1 = highest).

| ID | Scenario | Pre-condition | Steps | Test data | Expected result | Type | P | Req | Automated by |
|---|---|---|---|---|---|---|---|---|---|
| TC_LOC_01 | Home page title | Site reachable | 1. Open the site 2. Read the page title | URL | Title contains "McDelivery" | Positive | 1 | REQ-LOC-02 | `LocationModuleTest.verifyHomePageTitle`, `LocationJUnitTest.homePageTitleAndUrl` |
| TC_LOC_02 | Home page URL | Site reachable | 1. Open the site 2. Read the URL | URL | URL starts with `https://mcdelivery.co.in` | Positive | 1 | REQ-LOC-02 | `LocationModuleTest.verifyHomePageUrl`, `LocationJUnitTest.homePageTitleAndUrl` |
| TC_LOC_03 | Location shown at the top of the page | Home page open | 1. Wait for the header 2. Check the location bar and its position | – | Location bar is visible in the header (y < 150px) and shows the store or location prompt and the delivery time "Now" | Positive | 1 | REQ-LOC-02 | `verifyLocationBarDisplayedAtTop`, `LocationJUnitTest.locationBarDisplayedAtTop` |
| TC_LOC_04 | Location bar opens address selection | Home page open | 1. Click the location bar | – | "Select Delivery Address" panel opens | Positive | 1 | REQ-LOC-01 | `verifyLocationBarOpensSelectDeliveryAddress`, `LocationJUnitTest.locationBarOpensAddressPanel` |
| TC_LOC_05 | Guest must log in to select an address | Guest user | 1. Click the location bar 2. Click "Log In / Sign Up to Continue" | – | The CTA "Log In / Sign Up to Continue" is shown. Clicking it opens `/auth-with-otp` with "Enter your mobile number" | Positive | 2 | REQ-LOC-01 | `verifyGuestIsAskedToLoginToSelectAddress` |
| TC_LOC_06 | Close the address panel | Panel open | 1. Click the back arrow | – | Panel closes and the home page with the location bar is shown | Positive | 3 | REQ-LOC-01 | `verifyAddressPanelClosesWithBackArrow` |
| TC_LOC_07 | Delivery mode options | Home page open | 1. Click "McDelivery" next to the location | – | "Take Away" is offered as a second mode | Positive | 3 | REQ-LOC-01 | `verifyDeliveryModeOptions` |
| TC_LOC_08 | Location kept after refresh | Home page open | 1. Check the location bar 2. Refresh 3. Check again | – | Location bar is still displayed at the top | Positive | 2 | REQ-LOC-02 | `verifyLocationBarRetainedAfterRefresh` |
| TC_LOC_09 | Restaurants Nearby navigation | Home page open | 1. Click "Restaurants Nearby" | – | `/restaurants-nearby` opens | Positive | 2 | REQ-LOC-01 | `verifyRestaurantsNearbyNavigation` |
| TC_LOC_10 | Restaurants Nearby with no location shared | Browser location not shared | 1. Click "Restaurants Nearby" | No geolocation | "Sorry, we do not serve this location yet" is shown | Negative | 2 | REQ-LOC-01 | `LocationNegativeTest.verifyRestaurantsNearbyWithoutLocation` |
| TC_LOC_11 | Invalid mobile number at the address login gate (A–E) | On `/auth-with-otp` | 1. Enter the mobile number 2. Check "Verify Mobile" | Excel sheet `MobileValidation`: A `12345` (5 digits), B `987654321` (BVA 9), C `abcdefghij` (EP letters), D `@#$%^&*()!` (EP special), E blank | "Verify Mobile" stays **disabled** and the field holds at most 10 characters | Negative | 2 | REQ-LOC-01 | `verifyInvalidMobileNumberIsRejected` (POI data provider) |
| TC_LOC_12 | Mobile number longer than 10 digits (A–B) | On `/auth-with-otp` | 1. Check maxlength and placeholder 2. Type 11 or 14 digits | Excel sheet `MaxLength`: `98765432101`, `98765432101234` | maxlength = 10, placeholder "10 Digit Mobile Number", value cut to `9876543210` | Negative (BVA) | 3 | REQ-LOC-01 | `verifyMobileNumberMaxLength` (POI data provider) |
| TC_LOC_13 | Select or change a saved delivery address and see it at the top | Logged in with a real mobile number and OTP | 1. Log in 2. Click the location bar 3. Choose or add an address 4. Change to another address | Real address (e.g. Delhi, then Mumbai) | Chosen address and nearest restaurant shown at the top. Changing it updates the header | Positive | 1 | REQ-LOC-01, REQ-LOC-02 | **Manual only.** OTP cannot be automated, and automation must not send OTP SMS |

> Safety note: the automated tests never click "Verify Mobile", so no OTP is sent to any number.

---

## 3. Execution result (automation)

Final run on 15-Oct-2026 23:59 IST (Edge): **TestNG 12 of 17 passed, JUnit 5 3 of 3 passed.** TC_LOC_13 is manual only.

| Pass | Fail (cause) |
|---|---|
| TC_LOC_01, 02, 03, 05, 06, 08, 11_A, 11_C, 11_D, 11_E, 12_A, 12_B; JUnit JU_LOC_01–03 (covers TC_LOC_01–04) | TC_LOC_04 (TestNG), 07, 09, 10, 11_B: the site backend did not load the location header (DEF_LOC_01) |

Per-test results are in `Traceability_Matrix.md`. The Extent report is in `Reports/`.
