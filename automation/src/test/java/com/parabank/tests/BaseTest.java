package com.parabank.tests;

import com.parabank.managers.DriverManager;
import com.parabank.utils.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

public abstract class BaseTest {

    protected WebDriver driver;

    @Parameters("browser")
    @BeforeMethod(alwaysRun = true)
    public void setUp(@Optional("") String browser) {
        // decide which browser to use
        String sysPropBrowser = System.getProperty("browser");
        String resolvedBrowser = (sysPropBrowser != null && !sysPropBrowser.isBlank())
                ? sysPropBrowser
                : (browser == null || browser.isBlank() ? null : browser);

        System.out.println("[BaseTest] setUp() starting. -Dbrowser=" + sysPropBrowser +
                ", TestNG param=" + browser +
                ", final resolved=" + (resolvedBrowser == null ? "config.properties default" : resolvedBrowser));

        // start driver and open app url
        DriverManager.initDriver(resolvedBrowser);
        driver = DriverManager.getDriver();

        String url = ConfigReader.get("url");
        System.out.println("[BaseTest] Navigating to: " + url);
        driver.get(url);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        // quit driver after each test
        System.out.println("[BaseTest] tearDown() quitting driver");
        DriverManager.quitDriver();
    }
}
