# The McD Location Module Project, Explained from Scratch

This guide explains everything done in this project in simple language, from "what is testing?" up to Jenkins. It is written so you can learn it and then explain it to your teacher. Read it top to bottom once; after that, use the headings to revise.

---

## Part 0: The whole project in 6 sentences

1. We were given a real website, **McDelivery India** (https://mcdelivery.co.in/), and asked to test its **Location module**: the part where you choose where your food should be delivered.
2. First we **checked the old test cases** against the real website and found that many of them were wrong, so we **rewrote them correctly**.
3. Then we **automated** those test cases. We wrote Java programs that open a browser, click through the website and check that it behaves correctly, with no human needed.
4. The tools for this were **Selenium WebDriver** (controls the browser), **TestNG** and **JUnit** (organise and run the tests), **Apache POI** (reads test data from Excel) and **Maven** (manages the project).
5. We **ran** the tests, **reported the bugs (defects)** we found, and created a **traceability matrix** and **reports**.
6. Finally we put the code on **GitHub** and set up **Jenkins**, a tool that runs the tests automatically.

---

## Part 1: Testing basics (the theory your teacher will expect)

### 1.1 What is software testing?
Software testing means **checking whether software works the way it is supposed to**, and finding problems (bugs) before real users do.

Example: the requirement says *"the selected location should be shown at the top of the page."* Testing means opening the website and checking whether the location really is shown at the top.

### 1.2 Manual testing vs automation testing

| | Manual testing | Automation testing |
|---|---|---|
| Who does it | A person clicks through the website | A program (script) clicks through the website |
| Speed | Slow | Fast, and can run at night with nobody watching |
| Repeating the same test 100 times | Boring, and mistakes happen | Easy and always the same |
| Good for | New features, look and feel, one-off checks | Repeated checks (regression), many data values |
| In this project | Sprint 1 (test case design), plus TC_LOC_13 | Sprint 2 (this project) |

**Important point:** automation does not replace manual testing. First you design good test cases (manual thinking), then you automate the ones that are repeated often.

### 1.3 Key testing terms

| Term | Meaning | Example from our project |
|---|---|---|
| **Requirement** | What the software must do | "Selected location should be displayed on top of the page" |
| **Test scenario** | *What* to test, in one line | "Verify the location bar opens address selection" |
| **Test case** | *How* to test it, step by step, with an expected result | TC_LOC_04: click the location bar → "Select Delivery Address" panel opens |
| **Precondition** | What must be true before you start | "Home page is open" |
| **Test data** | Input values used in the test | Mobile number `12345` |
| **Expected result** | What *should* happen | "Verify Mobile button stays disabled" |
| **Actual result** | What *really* happened | "Button was disabled" → Pass |
| **Pass / Fail** | Actual = Expected → Pass, otherwise Fail | |
| **Defect / Bug** | Actual ≠ Expected because the software is wrong | DEF_LOC_01: location bar sometimes never loads |
| **Severity** | How *badly* the bug hurts the user | High: user cannot see the location at all |
| **Priority** | How *soon* it must be fixed | P1: fix first |

### 1.4 Types of testing used

| Type | Meaning | In our project |
|---|---|---|
| **Functional testing** | Does the feature do what the requirement says? | All our tests |
| **Positive testing** | Use valid input or normal actions; it should work | TC_LOC_01–09 |
| **Negative testing** | Use invalid input or unusual actions; the system should refuse it properly | TC_LOC_10–12: wrong mobile numbers, no location shared |
| **Smoke testing** | A quick check that the most important things work at all | The `smoke` group: title, URL, location bar, address panel, Restaurants Nearby |
| **Regression testing** | Re-running old tests after changes to make sure nothing broke | The `regression` group, run nightly by Jenkins |
| **UI / navigation testing** | Is the element there, does clicking go to the right page? | Location bar at the top, Restaurants Nearby navigation |

### 1.5 Test design techniques (how we chose the test data)

**Equivalence Partitioning (EP).** Divide all possible inputs into groups that behave the same, then test one value from each group.
For the mobile number field:
- Group 1, valid: exactly 10 digits
- Group 2, invalid: letters (`abcdefghij`)
- Group 3, invalid: special characters (`@#$%^&*()!`)
- Group 4, invalid: empty

**Boundary Value Analysis (BVA).** Bugs often hide at the *edges* of a limit, so test just below, at, and just above the limit.
The limit is 10 digits, so we tested **9 digits** (`987654321`, just below), **11 digits** (`98765432101`, just above) and 14 digits. The site cuts anything longer down to 10.

### 1.6 Agile and Scrum words from the case study

| Term | Meaning |
|---|---|
| **Agile** | Building software in small pieces, with frequent feedback, instead of one big plan |
| **Scrum** | A popular Agile method that works in fixed time boxes called sprints |
| **Sprint** | A short fixed period of work (here: Sprint 1 = manual test design, Sprint 2 = automation, Day 1 to Day 6) |
| **Epic** | A big feature, e.g. "Location" |
| **User story** | One small requirement from the user's view, e.g. "As a user I want my location shown at the top" |
| **Product backlog** | The list of all stories still to do |

### 1.7 Traceability Matrix (RTM)
A table that links **Requirement → Test cases → Automated script → Result → Defect**.
It proves that **every requirement is tested** and shows the result for each one. Ours is in `docs/Traceability_Matrix.md`.

---

## Part 2: The application and what we had to test

**Application:** McDelivery India, the website for ordering McDonald's food for delivery.

**Our module, Location.** The case study gave two user stories:
- **Story 1 (REQ-LOC-01):** The user can search, mark and select a delivery location, and the restaurant is chosen based on that location.
- **Story 2 (REQ-LOC-02):** The selected location is shown at the top of the page.

### What we found by exploring the real website
Before writing tests, we opened the site and studied how the location feature *actually* works:

1. At the top of the home page there is a **location bar**. It shows the store name ("McDonald's India, Tumkur"), a message "Please enter your location…", and the delivery time "Now".
2. Clicking it opens a panel called **"Select Delivery Address"**.
3. A user who is not logged in (a "guest") only sees a button **"Log In / Sign Up to Continue"**. It goes to a page asking for a **10-digit mobile number**, and then an **OTP** (one-time password sent by SMS).
4. **"Restaurants Nearby"** in the menu asks the browser for your location. If you don't share it, it says *"Sorry, we do not serve this location yet"*.
5. Next to the location there is a **McDelivery / Take Away** switch (delivery or pickup).

**Important limitation:** choosing a real delivery address needs an OTP sent to a real phone. A test program cannot receive an SMS, and we must never send OTPs to random numbers. So that part (TC_LOC_13) stays **manual**, and the automation tests everything **up to** the login step.

---

## Part 3: What we did, step by step

### Step 1: Validated the old test cases
There were two older sets of test cases:
- **Sprint Case Study CSV:** mostly **wrong** for the real site. It talked about typing a city name ("Delhi", "XYZ") and an error "No restaurants found", but the website has no city box and no such message. Its results were marked "Pass" without being run. It also had broken characters from saving in the wrong encoding.
- **v2 manual test cases:** **partly right**. The title and URL checks were fine. The location check was too weak: it only checked "we are still on the McDelivery site", and its locator only worked in the small mobile layout.

### Step 2: Wrote corrected test cases
We wrote **13 test cases** (TC_LOC_01 to TC_LOC_13): positive and negative, using EP and BVA, each linked to a requirement. See `docs/Location_Module_Test_Cases.md`.

### Step 3: Built the automation framework (Sprint 2, Day 1–3)
We created a **Maven project** with Selenium, TestNG, JUnit and POI, and organised it using the **Page Object Model** (explained in Part 4).

### Step 4: Ran the tests and analysed failures (Day 4)
We ran everything several times, looked at every failure, and separated **real website bugs** from **mistakes in our own tests**.

### Step 5: Reported defects (Day 4)
We logged **4 defects**, e.g. DEF_LOC_01: "Location bar sometimes never loads; header stays '-'". See `docs/Defect_Report.md`.

### Step 6: Traceability matrix, reports, Git (Day 4)
We created the RTM, generated HTML and XML reports, and pushed all the code to GitHub.

### Step 7: Jenkins (Day 5)
We installed Jenkins and created a pipeline that downloads the code from GitHub and runs all tests automatically, including every night.

### Step 8: Made it work in Eclipse, and fixed two problems found there
Running from Eclipse revealed two issues in our code, and we fixed both (see Part 7).

---

## Part 4: Every tool, explained

### 4.1 Java
**What:** the programming language all our tests are written in.
**Why Java:** Selenium, TestNG, JUnit and POI are all Java libraries, and Java is the most common language for Selenium in companies.
**Concepts you will see in our code:**
- **Class**: a blueprint, e.g. `LocationPage`
- **Object**: a real instance of a class, e.g. `new LocationPage(driver)`
- **Method**: an action, e.g. `openSelectDeliveryAddress()`
- **Inheritance**: `LocationModuleTest extends BaseTest`, so every test class automatically gets the browser setup from `BaseTest`
- **Package**: a folder that groups classes, e.g. `pages`, `utilities`

### 4.2 Maven
**What:** a **build and dependency management tool** for Java projects.

**Problem it solves:** our project needs dozens of library files (Selenium, TestNG, POI…). Downloading them by hand and keeping versions correct is painful. Maven does it automatically.

**`pom.xml`** (Project Object Model) is Maven's main file. In it we list our **dependencies**:
```xml
<dependency>
    <groupId>org.seleniumhq.selenium</groupId>
    <artifactId>selenium-java</artifactId>
    <version>4.38.0</version>
</dependency>
```
Maven reads this and downloads Selenium 4.38.0 (and everything Selenium itself needs) into `C:\Users\Mahim\.m2\repository`.

**Standard folder structure:** test code in `src/test/java`, test files (config, Excel) in `src/test/resources`.

**Common commands:**
- `mvn clean`: delete old build output (`target/` folder)
- `mvn test-compile`: compile only
- `mvn test`: compile and run all tests

The **Surefire plugin** (configured in `pom.xml`) is the part of Maven that runs the tests. We configured it to run **both** `testng.xml` and the JUnit 5 tests.

### 4.3 Selenium WebDriver (the heart of automation)
**What:** a library that lets a program **control a real web browser**: open pages, click, type, read text, take screenshots.

**How it works:**
```
Our Java test  →  Selenium WebDriver  →  Browser driver (msedgedriver.exe)  →  Edge browser
```
The browser driver is a small program made by the browser company that receives commands. **Selenium Manager** (built into Selenium 4) downloads the correct driver automatically, so we never download it by hand.

**Basic commands used:**
```java
WebDriver driver = new EdgeDriver();           // open Edge
driver.get("https://mcdelivery.co.in/");       // open a page
driver.getTitle();                             // read the page title
driver.getCurrentUrl();                        // read the address bar
element.click();                               // click
element.sendKeys("12345");                     // type
element.getText();                             // read text
driver.navigate().refresh();                   // refresh
driver.quit();                                 // close the browser
```

#### Locators: how Selenium finds an element on the page
A web page is built from **HTML elements** (`<div>`, `<span>`, `<input>`, `<button>`). To click something, Selenium must first *find* it. A **locator** is the rule used to find it. Selenium has 8 locator types:

| Locator | Finds by | Used in our project? |
|---|---|---|
| `id` | `id="..."` attribute | No, this site's elements have no ids |
| `name` | `name="..."` attribute | No, none available |
| `className` | CSS class | ✅ `toolbar-v1__time-location-bar-desktop` |
| `tagName` | Element type | ✅ `app-select-address` |
| `linkText` / `partialLinkText` | Text of an `<a>` link | No, the menu items are `<span>`, not links |
| `cssSelector` | CSS pattern | ✅ `input[type='tel']`, `app-select-address .bottom-sheet__cta-fw` |
| `xpath` | Path or text expression | ✅ `//button[normalize-space()='Verify Mobile']` |

**How did we find these?** By inspecting the real website's HTML (in a browser, right-click → Inspect). We wrote a small temporary "probe" program that printed the page structure.

**Example of a real problem:** the old locator `.toolbar-v1__location-type-row` only exists when the window is narrow (mobile layout). On a normal-size window it isn't there. That's why we fixed the window size to 1366×900 and used the desktop locator.

#### Waits: why tests need patience
Modern websites load content **after** the page appears. If Selenium clicks before the button exists, the test fails. So we **wait**:

| Wait type | Meaning | Used? |
|---|---|---|
| `Thread.sleep(5000)` | Always wait 5 seconds | Avoided. It wastes time and is still unreliable |
| **Implicit wait** | Every `findElement` waits up to N seconds | Not used |
| **Explicit wait** (`WebDriverWait`) | Wait **until a specific condition** is true, e.g. "until URL contains restaurants-nearby" | ✅ |
| **Fluent wait** | Explicit wait plus a custom polling interval plus exceptions to ignore while waiting | ✅ in `WaitUtils` (polls every 0.5s, up to 45s) |

**Why 45 seconds?** The McDelivery site is very slow. Its content often shows grey "skeleton" boxes for a long time.

**Stale element:** the site is built with **Angular**, which sometimes *re-draws* parts of the page. An element found a moment ago becomes "stale" (outdated). Our fluent wait **ignores** that error and simply tries again.

### 4.4 Page Object Model (POM) and Page Factory
**Problem:** if every test contains its own locators and clicks, then when the website changes one button you have to fix 20 tests.

**Page Object Model** is a design pattern. For **each page** of the website, create **one Java class** that holds that page's locators and actions. Tests then only call readable methods.

| Page class | Represents |
|---|---|
| `LocationPage` | Home page header: location bar, address panel, delivery mode, Restaurants Nearby |
| `LoginOtpPage` | The mobile number login page `/auth-with-otp` |
| `RestaurantsNearbyPage` | The `/restaurants-nearby` page |

A test then reads like plain English:
```java
LocationPage locationPage = new LocationPage(driver);
locationPage.openSelectDeliveryAddress();
Assert.assertEquals(locationPage.getAddressPanelTitle(), "Select Delivery Address");
```

**Page Factory** is Selenium's built-in helper for POM. You declare elements with the **`@FindBy`** annotation, and `PageFactory.initElements(driver, this)` connects them:
```java
@FindBy(css = "app-select-address .bottom-sheet__cta-fw")
private WebElement loginToContinueButton;
```

**Benefits:** locators live in **one place**, the code is reusable, the tests are readable, and maintenance is easier.

### 4.5 TestNG
**What:** a **testing framework**. It decides which methods are tests, in what order they run, what happens before and after each one, and whether each passed or failed. It also writes reports. (The name means "Test Next Generation".)

**Annotations used:**

| Annotation | Meaning | In our project |
|---|---|---|
| `@Test` | This method is a test | Every test method |
| `@BeforeMethod` | Run before **each** test | `BaseTest.setUp()` opens the browser and the site |
| `@AfterMethod` | Run after **each** test | `BaseTest.tearDown()` closes the browser |
| `@DataProvider` | Supplies several sets of data, so the test runs once per set | Mobile numbers from Excel |

**`@Test` options used:**
```java
@Test(priority = 4, groups = {"smoke", "regression"},
      description = "TC_LOC_04: Clicking the location bar opens 'Select Delivery Address'")
```
- `priority`: run order (1 first)
- `groups`: labels, so you can run only "smoke" or only "negative" tests
- `description`: readable name shown in reports

**Assertions:** the actual check. If it fails, the test fails.
```java
Assert.assertTrue(condition, "message if it fails");
Assert.assertEquals(actual, expected);
```

**`testng.xml`** is the **suite file**. It says which classes to run, which groups, whether to run in **parallel**, and which **listeners** to attach:
```xml
<suite name="McD Location Module Suite" parallel="classes" thread-count="2">
```
`parallel="classes"` with `thread-count="2"` means the positive and negative classes run **at the same time in two browsers**, which is faster.

**Listener:** a class that "listens" to test events (start, pass, fail) and reacts. Our `TestListener`:
- writes each result into the Extent HTML report
- takes a **screenshot when a test fails**
- attaches the **RetryAnalyzer**, which re-runs a failed test once, because the site is flaky

**Data-driven testing:** `verifyInvalidMobileNumberIsRejected` is written **once** but runs **5 times**, once per Excel row. This avoids writing 5 copies of the same test.

### 4.6 JUnit 5
**What:** another testing framework, older and very popular, and the standard in many Java projects. Version 5 is also called "Jupiter".

**Why both TestNG and JUnit?** The case study asks for "TestNG / JUnit". We used **TestNG for the main suite** (it has better grouping, parallel runs, data providers and XML suites) and **JUnit 5 for 3 smoke tests** (`LocationJUnitTest`). Both reuse the **same Page classes**, which shows the framework design is reusable.

| JUnit 5 | TestNG equivalent |
|---|---|
| `@Test` | `@Test` |
| `@BeforeEach` / `@AfterEach` | `@BeforeMethod` / `@AfterMethod` |
| `@Order(1)` | `priority = 1` |
| `@DisplayName("…")` | `description = "…"` |
| `assertEquals`, `assertTrue`, `assertAll` | `Assert.assertEquals`, `Assert.assertTrue` |

**TestNG vs JUnit, in one line:** JUnit is simpler and the industry standard for unit tests; TestNG adds features useful for large UI test suites (groups, parallel runs, XML suites, built-in data providers, dependencies).

### 4.7 Apache POI
**What:** a Java library to **read and write Microsoft Office files**, including Excel (`.xlsx`).
**Why:** test data (the mobile numbers) is kept in **Excel**, outside the code. A tester can add a new case by adding an Excel row, without touching Java.
**Our file:** `src/test/resources/testdata/LocationTestData.xlsx`, with two sheets:
- `MobileValidation`: 5 invalid numbers → "Verify Mobile" must stay disabled
- `MaxLength`: 11 and 14 digits → the field must cut them to 10

**How it works:** `ExcelUtils.getSheetData()` opens the file with POI, reads every row, and returns it to TestNG's `@DataProvider`. TestNG then runs the test once per row.

### 4.8 Extent Reports
**What:** a library that creates a **good-looking HTML report** with pass/fail colours, charts, timings, steps and **screenshots of failures**.
**Where:** `Reports/LocationModuleReport_<date_time>.html`, which opens in any browser.
TestNG and Maven also create their own reports (`target/surefire-reports/`), including **XML** files that Jenkins reads.

### 4.9 Git and GitHub
**Git:** a **version control** tool. It records every change to the code as a **commit** (a saved snapshot with a message), so you can see history and undo mistakes.
**GitHub:** a website that stores Git projects online, so you can share and back them up. Ours is **https://github.com/108mahima/McD-LocationModule**.

**Commands used:**
```bash
git init                 # start tracking this folder
git add .                # choose files for the next snapshot
git commit -m "message"  # save the snapshot
git branch -M main       # name the main branch "main"
git remote add origin <url>   # link to the GitHub repo
git push -u origin main  # upload to GitHub
```
`.gitignore` lists files **not** to upload (build output `target/`, Eclipse settings).

### 4.10 Jenkins (CI/CD)
**CI (Continuous Integration):** every time code changes, or on a schedule, a server automatically downloads the code, builds it and runs the tests, so problems are found early.

**Jenkins:** the most popular free CI tool. We installed it locally at **http://localhost:8090**.

**Pipeline:** the steps Jenkins follows, written in a file called **`Jenkinsfile`** inside our project ("pipeline as code"):

```
Checkout  →  Build  →  Test  →  Publish results
(get code    (mvn       (mvn test,  (JUnit results +
 from GitHub) compile)   headless)   Extent report archived)
```

- **Parameters:** choose the browser (edge/chrome/firefox) and headless mode before each run
- **Trigger:** runs automatically every night (`cron('H 2 * * *')`)
- **Headless:** the browser runs without a visible window, which is normal on servers
- **Build colours:** blue/green = all passed, yellow (UNSTABLE) = some tests failed, red = the build itself broke

### 4.11 Eclipse IDE
**What:** the program you write and run Java code in. We import the project as a **Maven project** and install the **TestNG plugin** so you can right-click → *Run As → TestNG Suite*. Details are in `docs/Getting_Started_Eclipse.md`.

---

## Part 5: How the project is organised (framework architecture)

```
          testng.xml / JUnit / Maven / Jenkins   ← RUNNERS (start the tests)
                          │
                          ▼
      Test classes  (LocationModuleTest, LocationNegativeTest, LocationJUnitTest)
                          │  what to check (assertions)
                          ▼
      Page classes  (LocationPage, LoginOtpPage, RestaurantsNearbyPage)
                          │  where elements are + how to use them
                          ▼
      Utilities  (DriverFactory, WaitUtils, ConfigReader, ExcelUtils, ExtentManager, ScreenshotUtils)
                          │  shared helpers
                          ▼
      Config & data  (config.properties, LocationTestData.xlsx)
```

| File | Job, in one line |
|---|---|
| `config.properties` | Settings: browser, URL, wait times, window size, retry count |
| `ConfigReader` | Reads those settings (a `-Dbrowser=chrome` on the command line overrides the file) |
| `DriverFactory` | Opens the right browser with the right options. One browser per thread, for parallel runs |
| `BaseTest` | Opens the browser before each test and closes it after |
| `WaitUtils` | All the smart waits |
| `CommonMethods` | Reusable click, type and read-text helpers |
| `ExcelUtils` | Reads Excel with Apache POI |
| `TestListener` / `RetryAnalyzer` | Report, screenshot, retry |
| `TestRunner` | A Java `main` that runs `testng.xml` without Maven |

**Why this structure?** It separates **what** we test (test classes) from **how** we talk to the page (page classes) and from **shared tools** (utilities). Changing one part rarely breaks the others. This is called a **hybrid framework**: Page Object Model, plus data-driven (Excel), plus TestNG.

---

## Part 6: One test, from start to finish

Test **TC_LOC_04**, "Clicking the location bar opens Select Delivery Address":

1. You run `testng.xml`. TestNG finds `LocationModuleTest.verifyLocationBarOpensSelectDeliveryAddress`.
2. `@BeforeMethod setUp()` runs. `DriverFactory` opens Edge at 1366×900 and goes to `https://mcdelivery.co.in/`.
3. `TestListener` creates a new entry in the Extent report.
4. The test creates `new LocationPage(driver)`, and Page Factory links all the `@FindBy` elements.
5. `openSelectDeliveryAddress()` runs. It **waits** until the location bar shows real text (not "-"), **reloads the page** up to 2 times if it is stuck, then **clicks** the bar until the panel appears.
6. **Assertion:** the panel title must equal `"Select Delivery Address"`. If it does, the test **passes**.
7. If it fails, the listener takes a **screenshot**, marks it failed in the report, and the RetryAnalyzer **runs it once more**.
8. `@AfterMethod tearDown()` closes the browser.
9. At the end, the Extent report is written to `Reports/`.

---

## Part 7: Problems we faced and how we solved them

Teachers usually like this part most. It shows real understanding.

| # | Problem | Cause | Solution |
|---|---|---|---|
| 1 | Old test cases didn't match the website | They were written without checking the real site | Explored the live site first, then rewrote the test cases |
| 2 | Old locator found 0 elements | It only exists in the mobile layout (window < 1200px) | Fixed the window size at 1366×900 and used desktop locators |
| 3 | Selecting a real address is impossible to automate | It needs an OTP sent by SMS to a real phone | Automated everything up to the login step; kept TC_LOC_13 manual; tests **never** click "Verify Mobile" |
| 4 | Location bar sometimes stuck on "-" for minutes | **Website bug** (backend doesn't respond) | Logged as defect DEF_LOC_01; the test waits, reloads up to 2 times, and retries once |
| 5 | "Stale element" errors | Angular re-draws the page after loading | Fluent wait that ignores stale errors and retries |
| 6 | First click on the location bar sometimes did nothing | The click happened before Angular attached its handlers | Click repeatedly until the panel appears |
| 7 | JUnit tests didn't run at all | Version mismatch between JUnit libraries | Aligned the JUnit version (5.12.1) with what Maven Surefire expects |
| 8 | In Eclipse: "Unable to load config.properties" | Eclipse ran from a different working folder | Load config and Excel from the **classpath** as a fallback |
| 9 | "Restaurants Nearby" test always timed out | Clicking it asks the browser for **location permission**, and in a test nobody answers the pop-up | The test now automatically answers "deny" (the "no location shared" case), and the browser blocks location |

**Lesson worth saying out loud:** not every failed test is a website bug. Problems 2, 5, 6, 8 and 9 were in **our** tests; problem 4 was a real **defect** in the website. Part of a tester's job is telling these apart.

---

## Part 8: Results

- **13 test cases** designed: 12 automated, 1 manual (OTP login).
- **20 automated executions**: 17 TestNG (some test cases run several times with Excel data) and 3 JUnit.
- **Final full run (15-Oct):** TestNG 12/17 passed, JUnit 3/3 passed. All failures were caused by the website outage (DEF_LOC_01) or the location-prompt issue that we fixed later.
- **Later Eclipse run (16-Oct):** 8 of 9 positive tests passed. The one failure (Restaurants Nearby) was then fixed, and both Restaurants Nearby tests passed when re-run.
- **4 defects** reported.
- Code on **GitHub**, pipeline in **Jenkins**.

---

## Part 9: Questions your teacher may ask, with answers

**Q: Why automate at all?**
A: To run the same checks quickly and reliably again and again (regression), with many data values, and even at night via Jenkins. That saves time and catches problems early.

**Q: What is the Page Object Model and why use it?**
A: Each web page gets its own class holding its locators and actions. Tests call those methods. If the website changes, we fix one page class instead of many tests. The code is cleaner and reusable.

**Q: What is the difference between Page Object Model and Page Factory?**
A: POM is the design idea (one class per page). Page Factory is Selenium's tool for implementing it, using `@FindBy` annotations and `PageFactory.initElements()`.

**Q: Which locators did you use and why not id?**
A: className, cssSelector, xpath and tagName. The site's elements have no id or name attributes, and the menu items are `<span>`s, not links, so linkText doesn't apply.

**Q: Implicit vs explicit wait?**
A: An implicit wait applies to every element search. An explicit wait waits for one specific condition. We used explicit and fluent waits because they are precise and the site loads unpredictably.

**Q: Why TestNG and not only JUnit?**
A: TestNG gives groups, priorities, data providers, parallel execution and XML suites out of the box, which is ideal for UI test suites. We also wrote JUnit 5 tests to show both.

**Q: What is a DataProvider?**
A: A TestNG method that returns several sets of data. The test runs once for each set. Ours reads the sets from Excel with Apache POI.

**Q: What is `testng.xml`?**
A: The suite configuration file. It lists the classes to run, groups, parallel settings and listeners.

**Q: What is a listener?**
A: A class that reacts to test events. Ours writes the HTML report, takes screenshots on failure and adds retry.

**Q: How do you run tests in parallel?**
A: `parallel="classes" thread-count="2"` in `testng.xml`. Each thread gets its own browser through a `ThreadLocal` in `DriverFactory`, so they don't interfere.

**Q: What is Maven's role?**
A: It downloads the libraries listed in `pom.xml`, compiles the code and runs the tests with `mvn test`. It also lets Jenkins build the project with one command.

**Q: What is Jenkins / CI?**
A: Continuous Integration: a server automatically fetches the code from GitHub, builds it and runs the tests, on demand or on a schedule, and publishes the results.

**Q: What is headless mode?**
A: Running the browser without opening a visible window. It is faster, and needed on servers with no screen.

**Q: What is EP and BVA? Give your example.**
A: EP: divide inputs into groups and test one from each (letters, special characters, empty, valid digits). BVA: test at the edges of limits (9 and 11 digits around the 10-digit limit).

**Q: What is a traceability matrix?**
A: A table mapping each requirement to its test cases, scripts, results and defects, to prove full coverage.

**Q: Severity vs priority?**
A: Severity is how badly a bug affects the user. Priority is how soon it should be fixed.

**Q: Why couldn't you automate selecting an address?**
A: It requires logging in with an OTP sent by SMS to a real phone. Automation cannot receive it, and sending OTPs to random numbers is unsafe. So it is a manual test case.

**Q: One test failed. How do you know if it's a bug in the website or in your test?**
A: Look at the error message and the screenshot, and reproduce it by hand. If a human sees the same problem, it's a website defect (like DEF_LOC_01). If the website works by hand but the test fails, the test is wrong (like the location-permission prompt).

---

## Part 10: Glossary (quick revision)

| Word | Meaning |
|---|---|
| Automation framework | The organised structure of code, tools and rules used to write and run automated tests |
| Assertion | A check that makes a test pass or fail |
| Browser driver | Small program (e.g. msedgedriver) that lets Selenium control the browser |
| Build | Compiling and packaging code (Maven) |
| CI | Continuous Integration: automatic build and test on every change or schedule (Jenkins) |
| Commit / Push | Save a snapshot in Git / upload it to GitHub |
| Data-driven testing | Same test, many data rows (Excel + DataProvider) |
| Defect | A difference between expected and actual behaviour caused by the software |
| Dependency | An external library the project needs (listed in `pom.xml`) |
| Flaky test | A test that sometimes passes and sometimes fails without code changes, often because of a slow site |
| Headless | Browser running without a visible window |
| Locator | The rule for finding an element on a page (id, css, xpath…) |
| OTP | One-Time Password sent by SMS |
| Page Factory | Selenium's `@FindBy` support for the Page Object Model |
| Parallel execution | Running several tests at the same time |
| Pipeline | The ordered steps Jenkins runs (Checkout → Build → Test → Publish) |
| Regression | Re-testing old features to make sure new changes didn't break them |
| Skeleton loader | Grey placeholder boxes a site shows while data is loading |
| Smoke test | Quick test of the most important functions |
| Stale element | An element reference that is outdated because the page re-drew itself |
| Suite | A collection of tests run together (`testng.xml`) |
| Test runner | The thing that starts the tests (testng.xml, TestRunner class, Maven, Jenkins) |
