package com.parabank.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

/**
 * Page Object for the "Transfer Funds" screen in ParaBank.
 * Keeps it simple: type amount, choose accounts, click transfer, read confirmation.
 */
public class TransferFundsPage extends BasePage {

	@FindBy(id = "amount")private WebElement amount;

	@FindBy(id = "fromAccountId")
	private WebElement fromAccount;

	@FindBy(id = "toAccountId")
	private WebElement toAccount;

	@FindBy(css = "input[value='Transfer']")
	private WebElement transferBtn;

	@FindBy(css = "#rightPanel .title")
	private WebElement confirmationTitle;
	
	@FindBy(css = "#rightPanel .error, .error") 
	private WebElement errorPanel;

	public TransferFundsPage(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);
	}

	public boolean onPage() {
		return !driver.findElements(By.id("amount")).isEmpty();
	}

	public void enterAmount(String amt) {
		type(amount, amt);
	}


	public void chooseAccounts(String fromVisibleText, String toVisibleText) {
		Select fromSel = new Select(fromAccount);
		Select toSel = new Select(toAccount);

		if (fromVisibleText == null || fromVisibleText.isBlank()) {
			fromSel.selectByIndex(0);
		} else {
			try {
				fromSel.selectByVisibleText(fromVisibleText);
			} catch (Exception e) {
				fromSel.selectByIndex(0);
			}
		}

		if (toVisibleText == null || toVisibleText.isBlank()) {
			toSel.selectByIndex(0);
		} else {
			try {
				toSel.selectByVisibleText(toVisibleText);
			} catch (Exception e) {
				toSel.selectByIndex(0);
			}
		}
	}
	public void setFromAccountIndex(int idx) { try { new Select(fromAccount).selectByIndex(idx);} catch(Exception ignored){} }
	public void setToAccountIndex(int idx) { try { new Select(toAccount).selectByIndex(idx);} catch(Exception ignored){} }

	public void submit() {
		click(transferBtn);
	}

	/** Convenience: do the whole flow in one call. */
	public void transfer(String amt, String fromVisibleText, String toVisibleText) {
		enterAmount(amt);
		chooseAccounts(fromVisibleText, toVisibleText);
		submit();
	}

	/** Returns the success title text after a transfer (e.g., "Transfer Complete!"). */
	public String getConfirmation() {
		return getText(confirmationTitle);
	}
	public String getFromAccountText() {
		return new Select(fromAccount).getFirstSelectedOption().getText().trim();
	}

	public String getToAccountText() {
		return new Select(toAccount).getFirstSelectedOption().getText().trim();
	}
	public String getErrorTextSafe() { try { return getText(errorPanel);} catch(Exception e){ return ""; } }
	
}

