package com.parabank.tests;

import com.parabank.data.TestDataProvider;
import com.parabank.pages.AccountsOverviewPage;
import com.parabank.pages.ForgotLoginInfoPage;
import com.parabank.pages.LoginPage;
import com.parabank.utils.ConfigReader;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTests extends BaseTest {

    // check successful login
    @Test(priority = 1, dataProvider = "loginData", dataProviderClass = TestDataProvider.class)
    public void testSuccessfulLogin(String username, String password, String expected) {
        if (!"success".equalsIgnoreCase(expected)) return;

        String u = (username == null || username.isBlank()) ? ConfigReader.get("default.username") : username;
        String p = (password == null || password.isBlank()) ? ConfigReader.get("default.password") : password;

        LoginPage lp = new LoginPage(driver);
        AccountsOverviewPage ap = lp.login(u, p);

        Assert.assertTrue(ap.waitUntilLoaded(15), "Accounts Overview not visible after login");
        ap.logout();
    }

    // check login with blank username and password
    @Test(priority = 2, dataProvider = "loginData", dataProviderClass = TestDataProvider.class)
    public void testBlankLogin(String username, String password, String expected) {
        if (username != null && !username.isBlank()) return;
        if (password != null && !password.isBlank()) return;

        LoginPage lp = new LoginPage(driver);
        lp.login("", "");

        boolean stillOnLoginPage = lp.isStillOnLoginPage();
        String errorMsg = lp.waitForErrorMessage(5);

        Assert.assertTrue(stillOnLoginPage, "User should remain on login page for blank login.");
        Assert.assertTrue(errorMsg != null && !errorMsg.isEmpty(), "Error message should appear for blank login.");

        System.out.println("Blank Login Test Error: " + errorMsg);
    }

    // check logout works fine
    @Test(priority = 3, dataProvider = "logoutData", dataProviderClass = TestDataProvider.class)
    public void testLogout(String username, String password) {
        LoginPage lp = new LoginPage(driver);
        AccountsOverviewPage ap = lp.login(username, password);
        lp = ap.logout();

        boolean loginButtonVisible = driver.findElements(By.cssSelector("input[value='Log In']")).size() > 0;
        Assert.assertTrue(loginButtonVisible, "Login control not visible after logout");
    }

    // check forgot login info form
    @Test(priority = 4, dataProvider = "forgotLoginData", dataProviderClass = TestDataProvider.class)
    public void testForgotLoginInfo(String first, String last, String addr, String city, String state, String zip, String ssn) {
        ForgotLoginInfoPage fl = new LoginPage(driver).clickForgotLogin();
        fl.fill(first, last, addr, city, state, zip, ssn);
        fl.submit();
        String title = fl.result();
        Assert.assertTrue(title != null && (title.toLowerCase().contains("login info") || title.length() > 0),
                "No recovery result displayed");
    }
}
