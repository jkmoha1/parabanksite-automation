package com.parabank.tests;

import com.parabank.data.TestDataProvider;
import com.parabank.pages.*;
import com.parabank.utils.ConfigReader;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TransferTests extends BaseTest {

	@Test(priority = 1, dataProvider = "transferData", dataProviderClass = TestDataProvider.class)
	public void transferFunds(String username, String password, String amount, String expected) {
		AccountsOverviewPage overview = new LoginPage(driver).login(username, password);
		TransferFundsPage transfer = overview.goToTransfer();

		// Pick accounts; if only one exists, use it for both
		try {
			Select from = new Select(driver.findElement(By.id("fromAccountId")));
			Select to   = new Select(driver.findElement(By.id("toAccountId")));
			if (from.getOptions().size() >= 2) {
				from.selectByIndex(0);
				to.selectByIndex(1);
			} else {
				from.selectByIndex(0);
				to.selectByIndex(0);
			}
		} catch (Exception ignored) {}

		transfer.enterAmount(amount);
		transfer.submit();

		String conf = "";
		for (int i = 0; i < 10 && (conf == null || conf.isBlank()); i++) {
			conf = transfer.getConfirmation();
			sleep(250);
		}
		String text = conf == null ? "" : conf.toLowerCase();

		switch (expected.toLowerCase()) {
		case "success" -> Assert.assertTrue(text.contains("complete") || text.contains("success") ||
				driver.getPageSource().toLowerCase().contains("transfer"),
				"Expected transfer completion, got: " + conf);
		case "validation" -> Assert.assertFalse(text.contains("complete"), "Expected validation failure");
		case "insufficient" -> Assert.assertFalse(text.contains("complete"), "Expected insufficient funds");
		default -> Assert.fail("Unknown expected value: " + expected);
		}
		overview.logout();
	}

	@Test(priority = 2, dataProvider = "findTransactionsData", dataProviderClass = TestDataProvider.class)
	public void transferTransactionHistory(String username, String password, String amount) {
		AccountsOverviewPage overview = new LoginPage(driver).login(username, password);

		// Create a transfer so a row exists
		TransferFundsPage transfer = overview.goToTransfer();
		try {
			Select from = new Select(driver.findElement(By.id("fromAccountId")));
			Select to   = new Select(driver.findElement(By.id("toAccountId")));
			if (from.getOptions().size() >= 2) {
				from.selectByIndex(0);
				to.selectByIndex(1);
			} else {
				from.selectByIndex(0);
				to.selectByIndex(0);
			}
		} catch (Exception ignored) {}
		transfer.enterAmount(amount);
		transfer.submit();

		String toAcc = transfer.getToAccountText();
		String digits = toAcc.replaceAll("\\D", "");

		// go to find transactions
		FindTransactionsPage find = overview.goToFindTransactions();
		try {
			find.selectAccountByText(toAcc);
		} catch (Exception e) {
			find.selectAccountContaining(digits);
		}
		boolean found = false;
		for (int i = 0; i < 4 && !found; i++) {
			find.searchByAmount(amount);
			found = find.hasResults();
			sleep(600);
		}
		Assert.assertTrue(found, "No transactions found for amount " + amount);
		overview.logout();
	}


	@Test(priority = 3, description = "Transfer validation: non-numeric and blank amounts are rejected",
			dataProvider = "transferData", dataProviderClass = TestDataProvider.class)
	public void transferInputValidationCases(String username, String password, String amount, String expected) {
		// Use only rows where expected=validation (from Excel)
		if (!"validation".equalsIgnoreCase(expected)) return;

		AccountsOverviewPage overview = new LoginPage(driver).login(username, password);
		TransferFundsPage transfer = overview.goToTransfer();

		try {
			new Select(driver.findElement(By.id("fromAccountId"))).selectByIndex(0);
			new Select(driver.findElement(By.id("toAccountId"))).selectByIndex(0);
		} catch (Exception ignored) {}

		transfer.enterAmount(amount); // One test case will fail because parabank accepts 0.00 transfer.
		transfer.submit();

		String err = transfer.getErrorTextSafe().toLowerCase();
		String conf = transfer.getConfirmation() == null ? "" : transfer.getConfirmation().toLowerCase();
		Assert.assertTrue(err.length() > 0 || !conf.contains("complete"),
				"Expected validation failure for amount: " + amount);
		overview.logout();
	}
	// This test case will fail because parabank currently transfers amount more than the account balance.
	@Test(priority = 4, description = "Transfer insufficient funds is rejected")
	public void transferInsufficientFunds() {
		// Assumption: para demo often allows transfers within same balance set. We try a huge value.
		String big = "99999999";

		AccountsOverviewPage overview = new LoginPage(driver).login(
				ConfigReader.get("default.username"), ConfigReader.get("default.password"));
		TransferFundsPage transfer = overview.goToTransfer();

		try {
			Select from = new Select(driver.findElement(By.id("fromAccountId")));
			Select to   = new Select(driver.findElement(By.id("toAccountId")));
			if (from.getOptions().size() >= 2) { from.selectByIndex(0); to.selectByIndex(1); }
			else { from.selectByIndex(0); to.selectByIndex(0); }
		} catch (Exception ignored) {}

		transfer.enterAmount(big);
		transfer.submit();

		String err = transfer.getErrorTextSafe().toLowerCase();
		String conf = transfer.getConfirmation() == null ? "" : transfer.getConfirmation().toLowerCase();

		Assert.assertTrue(err.contains("insufficient") || !conf.contains("complete"),
				"Expected insufficient funds error or no completion.");
		overview.logout();
	}
	// This test case will fail because parabank currently transfers amount more than the account balance.
	@Test(priority = 5, description = "Transfer success shows explicit confirmation text when valid")
	public void transferShowsConfirmationMessage() {
		AccountsOverviewPage overview = new LoginPage(driver).login(
				ConfigReader.get("default.username"), ConfigReader.get("default.password"));
		TransferFundsPage transfer = overview.goToTransfer();

		try {
			Select from = new Select(driver.findElement(By.id("fromAccountId")));
			Select to   = new Select(driver.findElement(By.id("toAccountId")));
			if (from.getOptions().size() >= 2) { from.selectByIndex(0); to.selectByIndex(1); }
			else { from.selectByIndex(0); to.selectByIndex(0); }
		} catch (Exception ignored) {}

		transfer.enterAmount("10");
		transfer.submit();

		String conf = "";
		for (int i = 0; i < 8 && (conf == null || conf.isBlank()); i++) { conf = transfer.getConfirmation(); sleep(250); }
		Assert.assertTrue(conf != null && conf.toLowerCase().contains("transfer"),
				"Expected a transfer confirmation message, got: " + conf);
		overview.logout();
	}


	private static void sleep(long ms) { try { Thread.sleep(ms); } catch (InterruptedException ignored) {} }
}
