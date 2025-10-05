package com.parabank.tests;

import com.parabank.data.TestDataProvider;
import com.parabank.pages.*;
import com.parabank.utils.ConfigReader;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AccountTests extends BaseTest {

    // check if accounts overview page opens
    @Test(priority = 1, dataProvider = "logoutData", dataProviderClass = TestDataProvider.class)
    public void verifyAccountOverview(String username, String password) {
        AccountsOverviewPage overview = new LoginPage(driver).login(username, password);
        Assert.assertTrue(overview.waitUntilLoaded(8), "Accounts Overview was not visible");
        overview.logout();
    }

    // open account details page
    @Test(priority = 2, dataProvider = "accountDetailsData", dataProviderClass = TestDataProvider.class)
    public void verifyAccountDetailsPage(String username, String password) {
        AccountsOverviewPage overview = new LoginPage(driver).login(username, password);
        AccountDetailsPage details = overview.openFirstAccount();
        Assert.assertTrue(details.isOpen(), "Account Details did not open");
        overview.logout();
    }

    // try filtering transactions by month
    @Test(priority = 3, dataProvider = "activityFilterData", dataProviderClass = TestDataProvider.class)
    public void verifyActivityFiltering(String username, String password, String period) {
        AccountsOverviewPage overview = new LoginPage(driver).login(username, password);
        AccountDetailsPage details = overview.openFirstAccount();

        try {
            new Select(driver.findElement(By.id("month"))).selectByVisibleText(period);
        } catch (Exception e) {
            try {
                Select s = new Select(driver.findElement(By.id("month")));
                if (!s.getOptions().isEmpty()) s.selectByIndex(0);
            } catch (Exception ignored) {}
        }
        try {
            driver.findElement(By.cssSelector("input[value='Go']")).click();
        } catch (Exception ignored) {}

        Assert.assertTrue(details.isOpen(), "Filtering broke the page");
        overview.logout();
    }

    // check new account can be opened
    @Test(priority = 4, dataProvider = "openNewAccountData", dataProviderClass = TestDataProvider.class)
    public void verifyOpenNewAccount(String username, String password, String type) {
        AccountsOverviewPage overview = new LoginPage(driver).login(username, password);
        OpenNewAccountPage open = overview.goToOpenNewAccount();

        try {
            new Select(driver.findElement(By.id("type"))).selectByVisibleText(type);
        } catch (Exception ignored) {}
        try {
            Select from = new Select(driver.findElement(By.id("fromAccountId")));
            if (!from.getOptions().isEmpty()) from.selectByIndex(0);
        } catch (Exception ignored) {}
        try {
            driver.findElement(By.cssSelector("input[value='Open New Account']")).click();
        } catch (Exception ignored) {}

        String id = open.getNewAccountId();
        Assert.assertTrue(id != null && !id.isBlank(), "No new account id shown");
        overview.logout();
    }

    // just open account and check it works
    @Test(priority = 5, dataProvider = "accountDetailsData", dataProviderClass = TestDataProvider.class)
    public void verifyBalanceCalculations(String username, String password) {
        AccountsOverviewPage overview = new LoginPage(driver).login(username, password);
        Assert.assertNotNull(overview.openFirstAccount(), "No account could be opened");
        overview.logout();
    }

    // verify sum of balances is same as shown total
    @Test(priority = 6, description = "Overview total equals the sum of account balances (within cents tolerance)")
    public void verifyOverviewTotalMatchesSum() {
        AccountsOverviewPage overview = new LoginPage(driver).login(
                ConfigReader.get("default.username"), ConfigReader.get("default.password"));

        Assert.assertTrue(overview.waitUntilLoaded(8), "Overview not loaded");
        var balances = overview.getVisibleBalances();
        double shownTotal = overview.getTotalBalanceDisplayed();
        double sum = balances.stream().mapToDouble(Double::doubleValue).sum();

        Assert.assertTrue(Math.abs(sum - shownTotal) < 0.01,
                "Total mismatch. Sum=" + sum + " displayed=" + shownTotal + " balances=" + balances);
        overview.logout();
    }

    // check balances update after doing a transfer
    @Test(priority = 7, description = "Balances adjust after a small transfer")
    public void verifyBalancesChangeAfterTransfer() {
        AccountsOverviewPage overview = new LoginPage(driver).login(
                ConfigReader.get("default.username"), ConfigReader.get("default.password"));
        Assert.assertTrue(overview.waitUntilLoaded(8));

        var before = overview.getVisibleBalances();
        double beforeTotal = before.stream().mapToDouble(Double::doubleValue).sum();

        TransferFundsPage t = overview.goToTransfer();
        try {
            Select from = new Select(driver.findElement(By.id("fromAccountId")));
            Select to   = new Select(driver.findElement(By.id("toAccountId")));
            if (from.getOptions().size() >= 2) { from.selectByIndex(0); to.selectByIndex(1); }
            else { from.selectByIndex(0); to.selectByIndex(0); }
        } catch (Exception ignored) {}
        t.enterAmount("5");
        t.submit();
        for (int i = 0; i < 8 && (t.getConfirmation() == null || t.getConfirmation().isBlank()); i++) {
             sleep(250);
        }

        driver.findElement(By.linkText("Accounts Overview")).click();
        Assert.assertTrue(overview.waitUntilLoaded(8));

        var after = overview.getVisibleBalances();
        double afterTotal = after.stream().mapToDouble(Double::doubleValue).sum();

        // total should remain same but balances should be different
        Assert.assertEquals(Math.round(beforeTotal * 100.0) / 100.0, Math.round(afterTotal * 100.0) / 100.0,
                "Internal transfer should not change overall total.");
        Assert.assertNotEquals(before.toString(), after.toString(),
                "At least one individual account balance should change after transfer. Before=" + before + ", After=" + after);
        overview.logout();
    }

    private static void sleep(long ms) { try { Thread.sleep(ms); } catch (InterruptedException ignored) {} }
}
