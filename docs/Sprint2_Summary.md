# Sprint 2: Automation Testing Summary (Location Module)

**Tester:** Mahima Yadav · **Application:** https://mcdelivery.co.in/ · **Tools:** Selenium WebDriver 4.38 (Page Factory), TestNG 7.11, JUnit 5.12, Apache POI 5.5, Extent Reports 5, Maven, Jenkins 2.580.1 LTS

Following the brief, Cucumber/BDD is **not** used. Only Selenium WebDriver, TestNG and JUnit are used, and only the Location module is covered.

## Day-wise deliverables

| Sprint day | Task (from case study) | Done in this repository |
|---|---|---|
| Day 1 | Identify manual test cases to automate (positive and negative); plan the application flow; create the Maven project; configure dependencies | Earlier test cases validated against the live site and corrected (`Location_Module_Test_Cases.md`). Flow: Home → location bar → Select Delivery Address → login gate; Restaurants Nearby; delivery mode. `pom.xml` with Selenium, TestNG, JUnit 5, POI and Extent. |
| Day 2 | Page Factory / object repository with all locator types; parameterisation with Apache POI; TestNG reports and XML for parallel execution; test runner class | `pages/` use `@FindBy` with className, css, xpath and tagName (the site has no usable id, name or link-text locators). `LocationTestData.xlsx` with `@DataProvider`. `testng.xml` runs in parallel by class with thread-count 2, groups smoke/regression/negative, and priorities. `runners/TestRunner.java`. |
| Day 3 | Test scripts for all functional requirements; TestNG assertions; waits; new windows, pop-ups and navigation; good practices | 9 positive and 3 negative TestNG tests (17 executions with data rows) plus 3 JUnit 5 tests. Fluent and explicit waits that tolerate Angular re-renders. The "Select Delivery Address" pop-up, the mode drop-down and page navigation are handled. Comments, naming conventions and indentation follow standard Java style. |
| Day 4 | Traceability matrix; TestNG/XML report with pass/fail; code in Git | `Traceability_Matrix.md`; Extent report in `Reports/`; TestNG and JUnit XML in `target/surefire-reports`; pushed to GitHub `108mahima/McD-LocationModule`. |
| Day 5 | Jenkins implementation and build | `Jenkinsfile` (parameterised, nightly, JUnit publishing, report archiving). Jenkins 2.580.1 LTS installed locally (`jenkins/start-jenkins.ps1`) with the `McD-LocationModule` pipeline job. |
| Day 6 | Sprint evaluation | This document. |

## Test execution summary (final run, 15-Oct-2026 23:59 IST, Edge)

| Suite | Executed | Passed | Failed | Not automated |
|---|---|---|---|---|
| TestNG: `LocationModuleTest` (positive) | 9 | 6 | 3 | – |
| TestNG: `LocationNegativeTest` (negative, POI data) | 8 | 6 | 2 | – |
| JUnit 5: `LocationJUnitTest` (smoke) | 3 | 3 | 0 | – |
| Manual only: TC_LOC_13 (OTP login) | – | – | – | 1 |
| **Total** | **20** | **15** | **5** | **1** |

- Failed: TC_LOC_04 (TestNG), TC_LOC_07, TC_LOC_09, TC_LOC_10, TC_LOC_11_B. In each case the site backend did not load the store/location header ("-") or redirected `/auth-with-otp` to home (DEF_LOC_01). TC_LOC_04 passed in JUnit in the same run.
- Across the three full runs on 15-Oct, every automated test case except TC_LOC_09 passed at least once.
- **Update 16-Oct:** TC_LOC_09 and TC_LOC_10 had a test-side cause. Clicking "Restaurants Nearby" asks the browser for location and only navigates once that request is answered, and in an automated browser nobody answers the permission prompt. `LocationPage.openRestaurantsNearby()` now denies the location request up front (the "no location shared" case), and the browser is set to block location. After the fix, both tests passed when re-run (9s each). A later Eclipse run of `LocationModuleTest` (before this fix) passed 8 of 9; the one failure was TC_LOC_09.
- 4 defects were logged (`Defect_Report.md`). The most important is DEF_LOC_01: the location bar intermittently never loads.

## Limitations

- Choosing or changing a saved delivery address requires OTP login to a real mobile number. It is kept as a manual test case (TC_LOC_13), and the automation never requests an OTP.
- Results depend on the live production site and its backend availability. A failed test is retried once, and the page is reloaded up to 2 times when the location bar stays blank.
