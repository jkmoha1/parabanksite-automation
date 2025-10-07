# ParaBank Site Automation — Detailed Project Analysis & Documentation

## 📘 Project Overview
This repository, **ParaBank Site Automation**, is a Selenium-based automated testing framework for the ParaBank web application (`https://parabank.parasoft.com`).  
It follows a **Page Object Model (POM)** design pattern integrated with **TestNG** for test execution, **Maven** for build management, and **Allure/Extent Reports** for reporting.

---

## 🧩 Project Structure

```
parabanksite-automation-main/
└── automation/
    ├── pom.xml
    ├── logs/
    │   └── execution.log
    ├── output/
    │   └── screenshots/
    │       ├── paymentHistory_FAIL_T27_20251005_145044.png
    │       ├── transferFunds_FAIL_T27_20251005_144907.png
    │       ├── transferInputValidationCases_FAIL_T27_20251005_145449.png
    │       └── transferInsufficientFunds_FAIL_T28_20251005_145832.png
    ├── src/
    │   ├── main/java/com/parabank/
    │   │   ├── automation/App.java
    │   │   ├── data/TestDataProvider.java
    │   │   ├── managers/DriverManager.java
    │   │   ├── pages/
    │   │   │   ├── BasePage.java
    │   │   │   ├── LoginPage.java
    │   │   │   ├── RegisterPage.java
    │   │   │   ├── TransferFundsPage.java
    │   │   │   ├── BillPayPage.java
    │   │   │   ├── RequestLoanPage.java
    │   │   │   └── AccountsOverviewPage.java
    │   │   └── utils/
    │   │       ├── ConfigReader.java
    │   │       ├── ExcelReader.java
    │   │       └── TestUtil.java
    │   ├── test/java/com/parabank/
    │   │   ├── automation/AppTest.java
    │   │   ├── listeners/TestListener.java
    │   │   ├── suites/A.java
    │   │   └── tests/
    │   │       ├── BaseTest.java
    │   │       ├── LoginTests.java
    │   │       ├── RegistrationTests.java
    │   │       ├── TransferTests.java
    │   │       ├── BillPayTests.java
    │   │       ├── AccountTests.java
    │   │       └── LoanTests.java
    │   └── test/resources/
    │       ├── config.properties
    │       ├── testNg.xml
    │       └── testdata.xlsx
```

---

## ⚙️ Technology Stack
| Component | Description |
|------------|-------------|
| **Language** | Java |
| **Framework** | Selenium WebDriver + TestNG |
| **Build Tool** | Maven |
| **Design Pattern** | Page Object Model (POM) |
| **Data Source** | Excel (via `ExcelReader`) |
| **Reports** | Screenshots, Allure, TestNG HTML |
| **IDE Support** | Eclipse / IntelliJ |

---

## 📂 Detailed File & Module Analysis

### 1. `pom.xml`
Defines dependencies and plugins:
- `selenium-java`
- `testng`
- `webdrivermanager`
- `allure-testng` or extent-report dependencies
- Compiler version setup (Java 8+)
- Surefire plugin for parallel test execution

### 2. `DriverManager.java`
- Handles browser initialization using WebDriverManager.
- Supports ChromeDriver, EdgeDriver, etc.
- Provides `getDriver()` and `quitDriver()` methods.
- Uses ThreadLocal for safe parallel execution.

### 3. `BasePage.java`
- Parent class for all pages.
- Encapsulates common Selenium actions (`click`, `type`, `getText`, `waitForElement`).
- Prevents code duplication across pages.

### 4. `pages/` package
Implements **Page Object Model** classes for each functional area:
- `LoginPage.java` – Handles login fields and button actions.
- `RegisterPage.java` – Handles user registration workflows.
- `TransferFundsPage.java` – Handles fund transfer UI actions.
- `BillPayPage.java` – Bill payment workflow.
- `RequestLoanPage.java` – Loan request actions.
- `AccountsOverviewPage.java` – Account balance and history verification.

Each class encapsulates web element locators (using `By`) and reusable page-level methods.

### 5. `TestDataProvider.java`
- Reads test data dynamically (from `testdata.xlsx` or other files).
- Provides `@DataProvider` for TestNG-based data-driven testing.

### 6. `ConfigReader.java`
- Reads configuration from `config.properties` (like base URL, browser type, timeout values).

### 7. `ExcelReader.java`
- Reads input test data and expected results from Excel sheets.
- Likely uses Apache POI.

### 8. `TestUtil.java`
- Helper utilities for screenshots, timestamps, waits, etc.
- Manages logging and assertion helpers.

### 9. `listeners/TestListener.java`
- Implements `ITestListener` (TestNG).
- Captures screenshots on failure and attaches them to reports.
- Logs test execution results to `execution.log`.

### 10. `BaseTest.java`
- Parent class for all TestNG test classes.
- Initializes browser, loads URL before test execution.
- Closes browser after test completion.

### 11. `tests/` package
Contains test scripts for each major feature of the ParaBank site:
- `LoginTests.java` → Valid & invalid login cases.
- `RegistrationTests.java` → Register new users.
- `TransferTests.java` → Transfer funds between accounts.
- `BillPayTests.java` → Execute bill payments.
- `AccountTests.java` → Account overview validation.
- `LoanTests.java` → Request loan and validate response.

### 12. `testNg.xml`
- Defines test suite configuration and parallel execution strategy.
- Allows selective execution of tests (e.g., Smoke, Regression).

### 13. `testdata.xlsx`
- Contains input datasets for parameterized testing.

### 14. `output/screenshots/`
- Contains screenshots for failed tests (timestamped filenames).

### 15. `logs/execution.log`
- Log file recording execution details, browser sessions, and errors.

---

## 🚀 How to Run the Tests

### **1️⃣ Using Maven**
```bash
mvn clean test
```

### **2️⃣ Generate Allure Report**
```bash
allure serve target/allure-results
```

### **3️⃣ Or Run Specific Test Suite**
```bash
mvn test -DsuiteXmlFile=src/test/resources/testNg.xml
```

---

## 🧪 Reports and Logs

### **Screenshots**
Located at:
```
output/screenshots/
```
Generated automatically on test failures.

### **Logs**
Located at:
```
logs/execution.log
```
Stores detailed execution flow.

### **Reports**
- Allure or TestNG HTML reports generated in `target/`.

---

## 🛠️ Troubleshooting

| Issue | Possible Cause | Solution |
|--------|----------------|-----------|
| WebDriver not found | Mismatch ChromeDriver version | Use WebDriverManager |
| Element not clickable | Page load delay | Use explicit waits |
| Config values not loaded | Wrong file path | Check `config.properties` path |
| Tests fail randomly | Parallel session interference | Ensure ThreadLocal driver instance |

---

## 📊 Recommendations for Improvement
1. Integrate Allure or ExtentReports for rich analytics.
2. Add CI/CD pipeline (e.g., GitHub Actions).
3. Implement retry logic for flaky tests using `IRetryAnalyzer`.
4. Use `@Factory` or `DataProvider` for scalable data-driven testing.
5. Modularize config for multiple environments (dev, QA, prod).

---

## 🧱 Future Enhancements
- Add Docker-based Selenium Grid support.
- Integrate test execution in Jenkins pipeline.
- Implement parallel cross-browser execution.

---

## 👨‍💻 Author & Maintenance
**Maintainer:** jkmoha1  
