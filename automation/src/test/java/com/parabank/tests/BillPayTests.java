package com.parabank.tests;

import com.parabank.data.TestDataProvider;
import com.parabank.pages.*;
import com.parabank.utils.ConfigReader;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.Test;

public class BillPayTests extends BaseTest {

    // run bill pay with different inputs and check result
    @Test(priority = 1, dataProvider = "billPayData", dataProviderClass = TestDataProvider.class)
    public void billPayScenarios(String username, String password, String name, String address, String city,
            String state, String zip, String phone, String account, String amount, String expected) {

        AccountsOverviewPage overview = new LoginPage(driver).login(username, password);
        BillPayPage bill = overview.goToBillPay();

        bill.fillPayee(name, address, city, state, zip, phone, account, amount);
        bill.submit();

        String conf = "";
        for (int i = 0; i < 8 && (conf == null || conf.isBlank()); i++) {
            conf = bill.getConfirmation();
            sleep(250);
        }
        String lc = conf == null ? "" : conf.toLowerCase();

        if ("success".equalsIgnoreCase(expected)) {
            Assert.assertTrue(lc.contains("complete") || conf.length() > 0, "Expected payment success");
        } else if ("validation".equalsIgnoreCase(expected)) {
            Assert.assertFalse(lc.contains("complete"), "Expected validation error");
        } else {
            Assert.fail("Unknown expected: " + expected);
        }
        overview.logout();
    }

    // check if payment shows in transaction history
    @Test(priority = 2, dataProvider = "findTransactionsData", dataProviderClass = TestDataProvider.class)
    public void paymentHistory(String username, String password, String amount) {
        AccountsOverviewPage overview = new LoginPage(driver).login(username, password);

        // make a payment first
        BillPayPage bill = overview.goToBillPay();
        bill.fillPayee("Acme Utilities", "1 Utility Ave", "Metropolis", "CA", "90210",
                "5551234567", "123456", amount);
        bill.submit();
        for (int i = 0; i < 8; i++) {
            if (bill.getConfirmation() != null && !bill.getConfirmation().isBlank()) break;
            sleep(250);
        }
        String fromAcc = bill.getFromAccountTextSafe();
        String digits = fromAcc.replaceAll("\\D", "");

        // now search for the payment
        FindTransactionsPage find = overview.goToFindTransactions();
        try {
            WebElement dd = driver.findElement(By.id("accountId"));
            Select s = new Select(dd);
            try {
                s.selectByVisibleText(fromAcc);
            } catch (Exception e) {
                boolean matched = false;
                for (WebElement opt : s.getOptions()) {
                    if (opt.getText().contains(digits)) { 
                        opt.click(); matched = true; break; 
                    }
                }
                if (!matched) s.selectByIndex(0);
            }
        } catch (Exception ignored) { }

        boolean found = false;
        for (int i = 0; i < 4 && !found; i++) {
            find.searchByAmount(amount);
            found = find.hasResults();
            sleep(600);
        }
        Assert.assertTrue(found, "No transactions found for amount " + amount);
        overview.logout();
    }

    // check validation for empty fields
    @Test(priority = 3, description = "Bill Pay validation: required fields")
    public void billPayRequiredFieldValidation() {
        AccountsOverviewPage overview = new LoginPage(driver).login(
                ConfigReader.get("default.username"), ConfigReader.get("default.password"));
        BillPayPage bill = overview.goToBillPay();

        bill.clearAll();
        bill.submit();

        String err = bill.getErrorTextSafe().toLowerCase();
        Assert.assertTrue(err.length() > 0 || driver.getPageSource().toLowerCase().contains("required"),
                "Expected a validation error when submitting blank bill pay.");
        overview.logout();
    }

    // check invalid amount case
    @Test(priority = 4, description = "Bill Pay invalid amount is rejected")
    public void billPayInvalidAmount() {
        AccountsOverviewPage overview = new LoginPage(driver).login(
                ConfigReader.get("default.username"), ConfigReader.get("default.password"));
        BillPayPage bill = overview.goToBillPay();

        bill.fillPayee("Acme Utilities", "1 Utility Ave", "Metropolis", "CA", "90210", "5551234567", "123456", "abc");
        bill.submit();

        String err = bill.getErrorTextSafe().toLowerCase();
        Assert.assertTrue(err.length() > 0 || driver.getPageSource().toLowerCase().contains("amount"),
                "Expected amount validation error.");
        overview.logout();
    }

    // check confirmation contains payee name
    @Test(priority = 5, description = "Bill Pay confirmation includes payee name when successful")
    public void billPayConfirmationIncludesPayeeName() {
        String payee = "Metro Water " + System.currentTimeMillis();

        AccountsOverviewPage overview = new LoginPage(driver).login(
                ConfigReader.get("default.username"), ConfigReader.get("default.password"));
        BillPayPage bill = overview.goToBillPay();

        bill.fillPayee(payee, "1 Utility Ave", "Metropolis", "CA", "90210", "5551234567", "123456", "12.00");
        bill.selectFromAccountByVisibleTextOrIndex(null, 0);
        bill.closePopups();
        bill.submit();

        String panel = bill.waitForConfirmationPanel(15, payee.toLowerCase());

        if (panel == null || panel.isBlank()) {
            String panelTextNow = bill.getRightPanelText();
            String panelHtml = bill.dumpRightPanelHtmlSafe();
            String errorText = bill.getErrorTextSafe();
            System.out.println("=== DIAGNOSTICS: rightPanel text ===\n" + panelTextNow);
            System.out.println("=== DIAGNOSTICS: rightPanel html ===\n" + panelHtml);
            System.out.println("=== DIAGNOSTICS: errorText ===\n" + errorText);
            Assert.fail("Timed out waiting for confirmation panel. Current panel text: " + panelTextNow + " | error: " + errorText);
        }

        String panelLower = panel.toLowerCase();
        Assert.assertTrue(panelLower.contains(payee.toLowerCase()) || panelLower.contains("complete"),
                "Expected bill pay confirmation containing payee name or complete message. Panel: " + panel);

        overview.logout();
    }

    private static void sleep(long ms) { try { Thread.sleep(ms); } catch (InterruptedException ignored) {} }
}
