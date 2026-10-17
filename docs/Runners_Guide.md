# Runners Guide: All the Ways to Run the Tests

This project can be run in **6 ways**. They all execute the same tests, but they differ in **which tests run**, whether you get the **Extent report**, and whether **retry** is active.

> **Most important rule:** the Extent report, the failure screenshots in the report, and the automatic retry come from `listeners.TestListener`. That listener is registered **in `testng.xml`**. Any run that goes **through `testng.xml`** gets them. A run that starts a single class or method directly does not.

---

## Quick comparison

| # | How | What runs | Extent report + retry | Best for |
|---|---|---|---|---|
| 1 | Eclipse: right-click `testng.xml` → **Run As → TestNG Suite** | Whole TestNG suite (positive + negative), in parallel | ✅ Yes | Normal full run inside Eclipse |
| 2 | Eclipse: `runners/TestRunner.java` → **Run As → Java Application** | Same as 1 | ✅ Yes | Running the suite without the TestNG plugin, choosing the browser via arguments |
| 3 | Eclipse: right-click a test class or method → **Run As → TestNG Test** | Just that class or method | ❌ No (TestNG HTML only) | Debugging one test |
| 4 | Eclipse: right-click `LocationJUnitTest` → **Run As → JUnit Test** | 3 JUnit 5 tests | ❌ No (JUnit view only) | JUnit part of the assignment |
| 5 | Terminal: `mvn clean test` | TestNG suite **and** JUnit 5 tests | ✅ Yes (TestNG part) | Final run, matches what Jenkins does |
| 6 | Jenkins: job **McD-LocationModule → Build with Parameters** | Same as 5, headless | ✅ Yes, archived in Jenkins | CI / nightly run / demo |

---

## Runner 1: `testng.xml` (TestNG suite)

### Steps
1. In Package Explorer, right-click **`testng.xml`** (project root).
2. **Run As → TestNG Suite**.
3. Two Edge windows open at the same time. This is parallel execution, one per test class.
4. Watch the results in the **Results of running suite** tab.
5. Afterwards, right-click the project → **Refresh (F5)**. Then open `Reports/LocationModuleReport_<date_time>.html` (right-click → Open With → Web Browser).

### What is inside `testng.xml`
```xml
<suite name="McD Location Module Suite" parallel="classes" thread-count="2">
    <listeners>
        <listener class-name="listeners.TestListener"/>   <!-- Extent report + screenshots + retry -->
    </listeners>
    <test name="Location Module Tests">
        <groups><run>
            <include name="smoke"/> <include name="regression"/> <include name="negative"/>
        </run></groups>
        <classes>
            <class name="testModules.location.tests.LocationModuleTest"/>
            <class name="testModules.location.tests.LocationNegativeTest"/>
        </classes>
    </test>
</suite>
```

| Setting | Meaning | How to change it |
|---|---|---|
| `parallel="classes"` | Each class runs in its own thread and browser | `parallel="none"` runs one at a time. Slower, but kinder to the slow site. |
| `thread-count="2"` | Maximum 2 browsers at once | Raise it only if you add more classes |
| `<listeners>` | Hooks `TestListener` into every test | Don't remove it, or you lose the report and retry |
| `<groups>` | Only tests in these groups run | Keep only `<include name="smoke"/>` for a quick 5-test smoke run |
| `<classes>` | Which test classes are in the suite | Add a line here when you create a new test class |

### Groups and priorities used in the tests

| Group | Tests |
|---|---|
| `smoke` | TC_LOC_01, 02, 03, 04, 09 (quick health check) |
| `regression` | All positive and negative tests |
| `negative` | TC_LOC_10, 11, 12 |

`priority = 1 … 12` on each `@Test` sets the order **inside a class**. TC_LOC_01 runs first, then 02, and so on.

---

## Runner 2: `runners.TestRunner` (Java main class)

`TestRunner` is a plain Java class with a `main` method. It loads `testng.xml` and runs it through the TestNG API. It produces the same result as Runner 1 and doesn't need the Eclipse TestNG plugin.

```java
TestNG testng = new TestNG();
testng.setTestSuites(List.of("testng.xml"));
testng.setOutputDirectory("test-output");
testng.run();
System.exit(testng.hasFailure() ? 1 : 0);   // exit code 1 when any test fails
```

### Steps
1. Open `src/test/java/runners/TestRunner.java`.
2. Right-click → **Run As → Java Application**. This runs on Edge with a visible window.

### Choosing the browser or headless mode
1. **Run → Run Configurations… → Java Application → TestRunner → Arguments** tab.
2. In **Program arguments**, type, for example:
   - `chrome`: run on Chrome
   - `chrome true`: Chrome, headless (no window)
   - `edge false`: Edge with a window (the default)
3. Click **Run**.

Output: the Extent report goes to `Reports/`, and the TestNG HTML report to `test-output/index.html` (refresh the project to see it).

---

## Runner 3: single TestNG class or method

### Steps
- **Whole class:** right-click `LocationModuleTest.java` → **Run As → TestNG Test**.
- **One method:** in the editor, double-click a method name (e.g. `verifyHomePageTitle`) to select it → right-click → **Run As → TestNG Test**.
- **Debugging:** click in the left margin next to a line to set a breakpoint → right-click → **Debug As → TestNG Test**. Execution pauses there, and you can inspect variables and step through with `F6`.

### Note
Because this run does **not** go through `testng.xml`:
- ❌ no Extent report, no screenshot in a report, no retry
- ✅ TestNG's own report is still in `test-output/`
- ✅ the screenshot is not taken either. Use the debugger to look at the browser instead.

---

## Runner 4: JUnit 5 tests

`LocationJUnitTest` uses **JUnit 5** (`org.junit.jupiter`), not TestNG. It reuses the same `LocationPage` Page Factory and `DriverFactory`.

| JUnit 5 annotation | TestNG equivalent | Used for |
|---|---|---|
| `@Test` | `@Test` | a test method |
| `@BeforeEach` / `@AfterEach` | `@BeforeMethod` / `@AfterMethod` | open / close the browser |
| `@Order(n)` + `@TestMethodOrder` | `priority = n` | run order |
| `@DisplayName("…")` | `description = "…"` | readable test name |
| `assertAll(...)` | soft assertions | check several things and report all failures |

### Steps
1. Open `src/test/java/testModules/location/junit/LocationJUnitTest.java`.
2. Right-click → **Run As → JUnit Test**. Eclipse has JUnit built in, so no plugin is needed.
3. Results appear in the green/red **JUnit** tab.

---

## Runner 5: Maven from the terminal

Open PowerShell or Command Prompt in the project folder:
```powershell
cd C:\Users\Mahim\Downloads\McD-LocationModule
```

| Command | What it does |
|---|---|
| `mvn clean test` | Deletes old build output, compiles, runs **testng.xml + JUnit 5 tests**. This is the full official run. |
| `mvn clean test -Dbrowser=chrome` | Same, on Chrome |
| `mvn clean test -Dheadless=true` | Same, with no browser window (like Jenkins) |
| `mvn test -Dtest=LocationJUnitTest` | Only the JUnit 5 class |
| `mvn test -Dtest=LocationModuleTest` | Only the positive TestNG class (no Extent report, see note) |
| `mvn test -Dtest="LocationNegativeTest#verifyMobileNumberMaxLength"` | Only one method |
| `mvn clean test-compile` | Only compile, no tests (quick check for code errors) |

> `-Dtest=...` replaces `testng.xml`, so those runs have **no Extent report and no retry**, the same as Runner 3.

> Inside Eclipse you can do the same: right-click project → **Run As → Maven test**, or **Run As → Maven build…** with Goals = `clean test -Dheadless=true`.

### How Maven knows what to run (`pom.xml` → surefire plugin)
```xml
<suiteXmlFiles><suiteXmlFile>testng.xml</suiteXmlFile></suiteXmlFiles>   <!-- TestNG part -->
<includes><include>**/junit/**/*Test.java</include></includes>          <!-- JUnit 5 part -->
<!-- two providers: surefire-testng and surefire-junit-platform -->
```
At the end, Maven prints two "Tests run:" lines: one for TestNG (`TestSuite`) and one for JUnit 5 (`McDelivery Location module - JUnit 5 smoke`).

`BUILD FAILURE` at the end only means **at least one test failed**. Scroll up to the `[ERROR]` lines to see which one.

---

## Runner 6: Jenkins

1. Start Jenkins if it is not running. In PowerShell, from the project folder:
   ```powershell
   .\jenkins\start-jenkins.ps1
   ```
   Keep that window open.
2. Open **http://localhost:8090**. User `admin`, password in `C:\Users\Mahim\Jenkins\home\secrets\admin-password.txt`.
3. Click job **McD-LocationModule**.
   - **First time only:** click **Build Now**. This first build loads the parameters from the `Jenkinsfile`.
4. Click **Build with Parameters** → choose **BROWSER** (edge/chrome/firefox) and **HEADLESS** → **Build**.
5. Click the build number (#2, #3…) → **Console Output** to watch it live.
6. After it finishes:
   - **Test Result**: pass/fail per test (from the JUnit XML files)
   - **Build Artifacts**: the Extent report (`Reports/*.html`) and screenshots

| Build colour | Meaning |
|---|---|
| 🔵/🟢 Blue/Green (SUCCESS) | All tests passed |
| 🟡 Yellow (UNSTABLE) | The build worked but some tests failed. Look at Test Result. |
| 🔴 Red (FAILURE) | Something broke before the tests (checkout or compile) |

Pipeline stages (from `Jenkinsfile`): **Checkout** (pull from GitHub) → **Build** (`mvn clean test-compile`) → **Test** (`mvn test -Dbrowser=… -Dheadless=…`) → publish results and archive reports. It also runs automatically every night (`cron('H 2 * * *')`).

> Jenkins builds the code **on GitHub**, not your local folder. After you change code, commit and push before building in Jenkins.

---

## Settings that apply to every runner: `config.properties`

File: `src/test/resources/config.properties`

| Key | Default | Meaning |
|---|---|---|
| `browser` | `edge` | `edge`, `chrome` or `firefox` |
| `headless` | `false` | `true` runs with no visible window |
| `url` | `https://mcdelivery.co.in/` | Site under test |
| `windowWidth` / `windowHeight` | `1366` / `900` | Must stay ≥ 1200 wide, or the site switches to the mobile layout and the locators won't match |
| `explicitWait` | `45` | Seconds to wait for an element (the site is slow) |
| `pageLoadTimeout` | `90` | Seconds to wait for a page load |
| `retryCount` | `1` | Re-runs of a failed TestNG test (suite runs only) |
| `testDataFile` | `src/test/resources/testdata/LocationTestData.xlsx` | Excel file for the data-driven tests |

**Priority:** a `-D` value always wins over the file. For example, `mvn test -Dbrowser=chrome` uses Chrome even though the file says `edge`. In Eclipse, put `-Dbrowser=chrome` under Run Configuration → Arguments → **VM arguments**.

---

## Test data (Apache POI)

`LocationTestData.xlsx` has two sheets. Each **row = one test run**, which is why `verifyInvalidMobileNumberIsRejected` runs 5 times.

| Sheet | Columns | Used by |
|---|---|---|
| `MobileValidation` | TestCaseID, Description, MobileInput, VerifyMobileEnabled | `LocationNegativeTest.verifyInvalidMobileNumberIsRejected` |
| `MaxLength` | TestCaseID, Description, MobileInput, ExpectedValue | `LocationNegativeTest.verifyMobileNumberMaxLength` |

To add a case, open the file in Excel, add a row (keep the header), and save. The next run picks it up automatically through `@DataProvider` → `ExcelUtils.getSheetData()`.

> ⚠️ Never add a **real 10-digit mobile number** with "VerifyMobileEnabled = true" and code that clicks *Verify Mobile*. That would send a real OTP SMS. The current tests never click that button.

---

## Where results go

| Output | Location | Created by |
|---|---|---|
| Extent HTML report | `Reports/LocationModuleReport_<date_time>.html` | Runners 1, 2, 5, 6 |
| Failure screenshots | `Screenshots/<testName>.png` | Runners 1, 2, 5, 6 |
| TestNG HTML report | `test-output/index.html` (Eclipse/TestRunner) or `target/surefire-reports/index.html` (Maven) | All TestNG runs |
| TestNG XML | `target/surefire-reports/testng-results.xml` | Maven / Jenkins |
| JUnit XML (TestNG and JUnit 5) | `target/surefire-reports/TEST-*.xml` | Maven / Jenkins |
| Saved copy of the final run | `Reports/surefire/` | Copied manually for submission |

In Eclipse, press **F5 (Refresh)** on the project after a run, or new reports won't show in Package Explorer.

---

## Things to know about this site and these tests

1. **The site is slow and sometimes broken.** When the McDelivery backend doesn't answer, the location bar shows "-" and the menu shows skeleton boxes. Tests then fail with *"Location bar never loaded from the backend … DEF_LOC_01"*. This is a site defect. Re-run later.
2. **Built-in protection:** the page reloads up to 2 times when the location bar is stuck, and a failed test is retried once (suite runs only). In reports, a retried test appears as **Skipped** first, then Pass or Fail.
3. **Real address selection is not automated** (TC_LOC_13). It needs an OTP sent to a real phone. Test it manually.
4. **Parallel runs open 2 browsers.** If your PC or network is slow, set `parallel="none"` in `testng.xml`.
5. **Don't change the window size** while tests run. The locators are for the desktop layout.
6. A full `mvn clean test` takes about **3 to 15 minutes**. It's shortest when the site is healthy, and longer when it is retrying.
