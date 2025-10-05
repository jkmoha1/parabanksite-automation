package com.parabank.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

public class OpenNewAccountPage extends BasePage {

	@FindBy(id="type") 
	private WebElement typeSelect;

	@FindBy(id="fromAccountId") 
	private WebElement fromAccountSelect;

	@FindBy(css="input[value='Open New Account']") 
	private WebElement openBtn;

	@FindBy(id="newAccountId") 
	private WebElement newAccountId;

	public OpenNewAccountPage(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);
	}

	// select account type and click on open button
	public void open(String type) {
		new Select(typeSelect).selectByVisibleText(type);
		click(openBtn);
	}

	// get id of the newly created account
	public String getNewAccountId() {
		return getText(newAccountId);
	}
}
