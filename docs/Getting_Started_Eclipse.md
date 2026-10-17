# Getting Started: Open, Set Up and Navigate the Project (Eclipse)

This guide covers how to open **McD-LocationModule** in Eclipse (or Spring Tool Suite, which is Eclipse), get it compiling, and find your way around the code.

---

## 1. What you need installed

| Tool | Version | How to check | Notes |
|---|---|---|---|
| JDK | 21 or newer | `java -version` | You have JDK 21 and 22 in `C:\Program Files\Java`. Either works for the project. |
| Maven | 3.9+ | `mvn -v` | Eclipse has its own Maven built in. A command-line Maven is only needed for `mvn` commands. |
| Eclipse IDE for Java Developers, or STS 4 | 2023+ | – | Your `sts-4.23.1.RELEASE` in Downloads works. |
| TestNG plugin for Eclipse | latest | Help → Eclipse Marketplace → search **TestNG** | Needed for the right-click **Run As → TestNG Test / TestNG Suite** menu. |
| Microsoft Edge | any recent | – | Default browser. Chrome or Firefox also work. |
| Git | any | `git --version` | Only needed to pull or push to GitHub. |

> You do **not** download any browser driver (edgedriver.exe etc.). Selenium Manager downloads the right driver automatically on the first run, so you need internet.

---

## 2. Get the project

If you already have the folder `C:\Users\Mahim\Downloads\McD-LocationModule`, use that.

Otherwise, clone it from GitHub:
```powershell
cd C:\Users\Mahim\Downloads
git clone https://github.com/108mahima/McD-LocationModule.git
```

> Do not use the old folder `AutomationTesting-SwagLabs-main`. It is the outdated copy.

---

## 3. Import into Eclipse (step by step)

1. Open Eclipse and pick any workspace folder. It should **not** be the project folder itself.
2. **File → Import… → Maven → Existing Maven Projects → Next**.
3. **Root Directory → Browse…** → select `C:\Users\Mahim\Downloads\McD-LocationModule`.
4. `/pom.xml` appears ticked. Click **Finish**.
5. Wait for the bottom-right progress bar ("Building workspace", "Downloading…"). The first time, Maven downloads Selenium, TestNG, JUnit, POI and Extent. This can take a few minutes.
6. If the project shows a red **X**: right-click the project → **Maven → Update Project… → tick "Force Update of Snapshots/Releases" → OK**.

> Why "Existing Maven Projects" and not "Existing Projects into Workspace"? The Eclipse files (`.project`, `.classpath`) are not stored in Git. The Maven import creates them from `pom.xml`.

### Set the Java version (only if you see "compiler compliance" errors)

1. **Window → Preferences → Java → Installed JREs → Add… → Standard VM** → select `C:\Program Files\Java\jdk-21.0.12.1` → Finish → tick it.
2. Right-click the project → **Properties → Java Build Path → Libraries** → check that the JRE System Library is **JavaSE-21**. If it isn't, select it → **Edit** → choose your JDK.
3. **Maven → Update Project** again.

### Install the TestNG plugin (once)

**Help → Eclipse Marketplace…** → search `TestNG` → install **TestNG for Eclipse** → restart Eclipse.
After this, right-clicking a test class or `testng.xml` shows **Run As → TestNG Test / TestNG Suite**.

---

## 4. Project map: what is where

In Eclipse, open **Package Explorer** (Window → Show View → Package Explorer). Everything lives under `src/test/java` and `src/test/resources`, because this is a test-only project.

```
McD-LocationModule
│
├── src/test/java
│   ├── base
│   │   └── BaseTest.java              ← Opens the browser before each TestNG test and closes it after
│   │
│   ├── pages                          ← PAGE FACTORY (all locators live here, nowhere else)
│   │   ├── LocationPage.java          ← Location bar, "Select Delivery Address" panel,
│   │   │                                McDelivery/Take Away selector, Restaurants Nearby link
│   │   ├── LoginOtpPage.java          ← /auth-with-otp mobile number page (login gate)
│   │   └── RestaurantsNearbyPage.java ← /restaurants-nearby page
│   │
│   ├── testModules/location
│   │   ├── tests
│   │   │   ├── LocationModuleTest.java    ← TestNG POSITIVE tests (TC_LOC_01 – TC_LOC_09)
│   │   │   └── LocationNegativeTest.java  ← TestNG NEGATIVE tests (TC_LOC_10 – TC_LOC_12) + Excel data
│   │   └── junit
│   │       └── LocationJUnitTest.java     ← JUnit 5 smoke tests (JU_LOC_01 – JU_LOC_03)
│   │
│   ├── listeners
│   │   ├── TestListener.java          ← Writes the Extent report, takes screenshots, adds retry
│   │   └── RetryAnalyzer.java         ← Re-runs a failed test once
│   │
│   ├── runners
│   │   └── TestRunner.java            ← Java "main" class that runs testng.xml
│   │
│   └── utilities
│       ├── ConfigReader.java          ← Reads config.properties (+ -D overrides)
│       ├── DriverFactory.java         ← Creates Edge/Chrome/Firefox, headless, window size
│       ├── WaitUtils.java             ← Explicit/fluent waits
│       ├── CommonMethods.java         ← click / type / getText helpers
│       ├── ExcelUtils.java            ← Apache POI: reads LocationTestData.xlsx
│       ├── ExtentManager.java         ← Creates the Extent HTML report file
│       └── ScreenshotUtils.java       ← Saves PNGs into Screenshots/
│
├── src/test/resources
│   ├── config.properties              ← browser, headless, URL, timeouts, retry count
│   └── testdata/LocationTestData.xlsx ← Mobile number test data (2 sheets)
│
├── testng.xml                         ← TestNG suite: which classes, groups, parallel
├── pom.xml                            ← Maven dependencies and how `mvn test` runs
├── Jenkinsfile                        ← Jenkins pipeline
├── jenkins/                           ← Script to install/start Jenkins locally
├── Reports/                           ← Extent reports (+ Reports/surefire = TestNG/JUnit XML)
├── Screenshots/                       ← Screenshot of each failed test
└── docs/                              ← Test cases, traceability, defects, these guides
```

### How the pieces connect (one test, end to end)

Take `LocationModuleTest.verifyLocationBarOpensSelectDeliveryAddress` as an example:

1. **TestNG** starts the test. `BaseTest.setUp()` runs first. It calls `DriverFactory.initDriver()` (opens Edge at 1366×900) and opens the URL from `config.properties`.
2. The test creates `new LocationPage(driver)`. `PageFactory.initElements` connects the `@FindBy` fields to the page.
3. The test calls `locationPage.openSelectDeliveryAddress()`. The page class waits (using `WaitUtils`) for the location bar to load, reloading if the site is stuck, then clicks it until the panel opens.
4. The test checks the result with `Assert.assertEquals(...)`.
5. **TestListener** marks the test pass or fail in the Extent report and takes a screenshot if it failed.
6. `BaseTest.tearDown()` closes the browser.

So: **tests say *what* to check, pages say *how* to find and use elements, utilities do the shared work.**

---

## 5. Useful Eclipse shortcuts for navigating

| Shortcut | What it does |
|---|---|
| `Ctrl + Shift + R` | Open any file by name (e.g. type `testng`, `config`) |
| `Ctrl + Shift + T` | Open any Java class by name (e.g. `LocationPage`) |
| `Ctrl + Click` on a method or class | Jump to its definition |
| `Ctrl + Alt + H` | Show everywhere a method is called (e.g. who uses `openSelectDeliveryAddress`) |
| `Ctrl + O` | List all methods in the current file |
| `Alt + ←` / `Alt + →` | Go back or forward to where you were |
| `Ctrl + Shift + F` | Format the code (indentation) |
| `Ctrl + Shift + O` | Fix imports |

---

## 6. First run: check that everything works

1. In Package Explorer, open `src/test/java → testModules.location.tests → LocationModuleTest.java`.
2. Right-click inside the editor → **Run As → TestNG Test**.
3. An Edge window opens and closes for each test. Results appear in the **Results of running class LocationModuleTest** tab.
4. If `verifyHomePageTitle` and `verifyHomePageUrl` pass, your setup is correct.

> Some location tests can fail when the McDelivery site itself doesn't load the location bar (it shows "-"). That is the site defect **DEF_LOC_01**, not your setup. See `docs/Defect_Report.md`.

For **all the ways to run tests** (suite, single test, Maven, TestRunner, Jenkins), see **[Runners_Guide.md](Runners_Guide.md)**.

---

## 7. Common problems and fixes

| Problem | Fix |
|---|---|
| Red X on project, "Missing artifact …" | Right-click project → Maven → Update Project → tick **Force Update** |
| `Unable to load config.properties` / `Could not initialize class utilities.ConfigReader` | Fixed in the latest code: config and Excel data now also load from the classpath. Pull the latest code, then Project → Clean. If it still happens, the project was not imported as a Maven project (section 3), so `src/test/resources` is not on the classpath. |
| Reports or screenshots are not in the project folder | Eclipse is running with a different working directory. Run → Run Configurations → your config → **Arguments** tab → Working directory: **Default (${workspace_loc:McD-LocationModule})** |
| No **Run As → TestNG** option | Install the TestNG plugin (section 3) |
| `SessionNotCreatedException` / driver error | Update Edge, check your internet connection (Selenium Manager downloads the driver), then try again |
| Browser opens but the page looks like a phone layout | Don't resize or maximise manually. The desktop layout needs a width of at least 1200px (`windowWidth` in `config.properties`) |
| Tests fail with "Location bar never loaded from the backend" | Site outage (DEF_LOC_01). Wait a while and re-run |
| Console shows `WARNING: Unable to find CDP implementation matching 153` or `SLF4J(W): No SLF4J providers` | Harmless. These are Selenium DevTools and logging warnings, and the tests don't use DevTools. Ignore them. |
| "Restaurants Nearby" test hangs on the home page | Fixed: the test now denies the browser's "know your location" prompt automatically. Pull the latest code. |
| Eclipse shows errors but `mvn test` works | Project → Clean…, then Maven → Update Project |
