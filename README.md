# McD-LocationModule

Automation testing of the **Location module** of [McDelivery India](https://mcdelivery.co.in/), done as Sprint 2 of the McDelivery case study.

**Stack:** Java 21 · Maven · Selenium WebDriver 4 (Page Factory) · TestNG 7 · JUnit 5 · Apache POI · Extent Reports · Jenkins

**Tester:** Mahima Yadav

## Scope (from the case study)

| Requirement | Description |
|---|---|
| Location Epic, Story 1 | The user can search, mark and select a delivery location, and the restaurant is chosen based on that location. |
| Location Epic, Story 2 | The selected location is displayed at the top of the page. |

Only the Location module is automated. Search, Play Store, Customisation and Cart are out of scope for this repository.

## How the live site behaves (checked on 15-Oct-2026)

- The location bar sits at the top of the home page. It shows the store or location prompt and the delivery time ("Now").
- Clicking the location bar opens **Select Delivery Address**. A guest user only sees **Log In / Sign Up to Continue**, which leads to `/auth-with-otp` (10-digit mobile number + OTP).
- Saving or selecting a delivery address needs a real OTP login, so it **cannot be automated**. The tests check everything up to the login gate and never request an OTP.
- **Restaurants Nearby** (`/restaurants-nearby`) uses the browser location. If no location is shared, it shows *"Sorry, we do not serve this location yet"*.
- The site backend is slow and sometimes never loads. The location bar then stays on "-" or skeleton loaders (see `docs/Defect_Report.md`). The framework waits up to 45s, reloads the page up to 2 times, and retries a failed test once.

## Project structure

```
McD-LocationModule
├── pom.xml                       Maven dependencies (Selenium, TestNG, JUnit 5, POI, Extent)
├── testng.xml                    TestNG suite: Location module, parallel by class
├── Jenkinsfile                   CI pipeline (Checkout → Build → Test → publish reports)
├── jenkins/                      Local Jenkins install script, plugins, auto-created job
├── docs/                         Test cases, traceability matrix, defect report, Sprint 2 summary
├── Reports/                      Extent HTML reports (one per run)
├── Screenshots/                  Screenshots of failed tests
└── src/test
    ├── java
    │   ├── base/BaseTest.java                    Browser set-up / tear-down (TestNG)
    │   ├── listeners/TestListener.java           Extent report, screenshots, retry
    │   ├── listeners/RetryAnalyzer.java
    │   ├── pages/LocationPage.java               Page Factory: location bar, address panel, mode
    │   ├── pages/LoginOtpPage.java               Page Factory: /auth-with-otp
    │   ├── pages/RestaurantsNearbyPage.java      Page Factory: /restaurants-nearby
    │   ├── runners/TestRunner.java               Runs testng.xml from Java (no Maven)
    │   ├── testModules/location/tests/LocationModuleTest.java     TestNG positive tests
    │   ├── testModules/location/tests/LocationNegativeTest.java   TestNG negative + POI data-driven
    │   ├── testModules/location/junit/LocationJUnitTest.java      JUnit 5 smoke tests
    │   └── utilities/                    Config, DriverFactory (ThreadLocal), waits, Excel, Extent
    └── resources
        ├── config.properties             browser, headless, URL, timeouts
        └── testdata/LocationTestData.xlsx  Mobile number data (EP / BVA)
```

## Run the tests

Prerequisites: JDK 21+, Maven 3.9+, and Microsoft Edge (or Chrome or Firefox). Selenium Manager downloads the browser driver automatically.

```bash
mvn clean test                                  # TestNG suite + JUnit 5 tests, Edge
mvn clean test -Dbrowser=chrome -Dheadless=true # any browser, without a window
mvn clean test -Dtest=LocationJUnitTest         # JUnit 5 tests only
```

You can also run the TestNG suite from the IDE with `runners.TestRunner` (Run As → Java Application) or by right-clicking `testng.xml`.

### Reports

| Report | Location |
|---|---|
| Extent HTML report | `Reports/LocationModuleReport_<timestamp>.html` |
| TestNG HTML / XML | `target/surefire-reports/index.html`, `emailable-report.html`, `testng-results.xml` |
| JUnit XML (TestNG and JUnit 5) | `target/surefire-reports/TEST-*.xml` |
| Failure screenshots | `Screenshots/<testName>.png` |

## Jenkins

`Jenkinsfile` defines a parameterised pipeline (BROWSER, HEADLESS) with a nightly trigger. It publishes JUnit results and archives the Extent report and screenshots. A failed test marks the build **UNSTABLE**, not FAILED, so the reports are still published.

To install Jenkins locally on Windows, with plugins, an admin user and the pipeline job created automatically:

```powershell
.\jenkins\start-jenkins.ps1
# open http://localhost:8090  -> user "admin", password in %USERPROFILE%\Jenkins\home\secrets\admin-password.txt
# job "McD-LocationModule" -> Build with Parameters
```

## Documents

- [docs/Project_Explained.md](docs/Project_Explained.md): the whole project explained from scratch (testing basics, every tool, problems and solutions, viva questions)
- [docs/Getting_Started_Eclipse.md](docs/Getting_Started_Eclipse.md): open the project in Eclipse, set it up, project map
- [docs/Runners_Guide.md](docs/Runners_Guide.md): every way to run the tests, step by step, and where reports go
- [docs/Location_Module_Test_Cases.md](docs/Location_Module_Test_Cases.md): validation of the earlier manual test cases, and the corrected test cases
- [docs/Traceability_Matrix.md](docs/Traceability_Matrix.md): requirement → test case → script → result → defect
- [docs/Defect_Report.md](docs/Defect_Report.md)
- [docs/Sprint2_Summary.md](docs/Sprint2_Summary.md): Sprint 2 day-by-day deliverables and test execution summary
