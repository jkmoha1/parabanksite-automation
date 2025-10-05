package com.parabank.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

public class AccountDetailsPage extends BasePage {

    @FindBy(xpath = "//h1[contains(text(),'Account Details')]") 
    private WebElement header;

    @FindBy(id="month") 
    private WebElement periodSelect;

    @FindBy(id="transactionType") 
    private WebElement typeSelect; // sometimes not present

    @FindBy(css="input[value='Go']") 
    private WebElement goBtn;

    public AccountDetailsPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    // check if account details page is opened
    public boolean isOpen() {
        try {
            waitForVisible(header);
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    // filter transactions by period
    public void filterByPeriod(String period) {
        new Select(periodSelect).selectByVisibleText(period);
        click(goBtn);
    }

    // filter transactions by type
    public void filterByType(String type) {
        new Select(typeSelect).selectByVisibleText(type);
        click(goBtn);
    }

    // check if any transactions are listed
    public boolean hasTransactions() {
        return driver.findElements(By.cssSelector("#transactionTable tbody tr")).size() > 0;
    }
}
