# McDelivery India — Location Module Manual Test Cases

**Application:** https://mcdelivery.co.in/  
**Module:** Location Module / Home Page  
**Test type:** Manual testing  
**Related automation class:** `LocationModuleTest.java`  
**Related page class:** `LocationPage.java`

## Preconditions

- The tester has an active internet connection.
- The McDelivery India website is accessible in a supported browser.
- The website loads without a blocking browser or network error.

## Manual Test Cases

| Test Case ID | Test Case | Steps | Expected Result | Actual Result | Status |
|---|---|---|---|---|---|
| LOC-MAN-001 | Verify location module test flow | 1. Open `https://mcdelivery.co.in/`.<br>2. Set the browser window to a smaller size if required (approximately 652 × 672).<br>3. Locate and click the location control. | The location control can be found and clicked. The website remains on the McDelivery domain. | To be filled during execution. | Not Run |
| LOC-MAN-002 | Verify home page title | 1. Open `https://mcdelivery.co.in/`.<br>2. Check the browser tab title or page title. | The page title contains the text `McDelivery`. | To be filled during execution. | Not Run |
| LOC-MAN-003 | Verify home page URL | 1. Open `https://mcdelivery.co.in/`.<br>2. Check the browser address bar. | The URL starts with `https://mcdelivery.co.in`. | To be filled during execution. | Not Run |

## Execution Summary

- **Total test cases:** 3
- **Passed:** To be filled
- **Failed:** To be filled
- **Blocked:** To be filled
- **Not Run:** 3

## Execution Notes

- Record the actual result and update each status to `Pass`, `Fail`, or `Blocked` after manual execution.
- `LOC-MAN-001` reflects the current automated flow, which uses the CSS selector `.toolbar-v1__location-type-row`. Previous Selenium output reported zero matching elements for this selector. Confirm the actual location control on the live website before marking this case as passed.
- The title and URL checks validate the page title and domain only; they do not confirm successful city, postal code, or delivery-address selection.
