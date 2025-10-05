package com.parabank.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

public class FindTransactionsPage extends BasePage {

    @FindBy(xpath="//input[@id='amount']") 
    private WebElement amount;

    @FindBy(xpath= "//button[@id='findByAmount']") 
    private WebElement findBtn;

    public FindTransactionsPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver,this);
    }    

    // search transaction by amount
    public void searchByAmount(String amt) {
        amount.clear();
        type(this.amount, amt);
        click(findBtn);
    }

    // check if any results are shown
    public boolean hasResults() {
        return driver.findElements(By.cssSelector("#transactionTable tbody tr")).size() > 0;
    }

    // select account from dropdown by visible text
    public void selectAccountByText(String text) {
        WebElement dd = driver.findElement(By.id("accountId")); // dropdown element
        new Select(dd).selectByVisibleText(text);
    }

    // select account that contains given text
    public void selectAccountContaining(String part) {
        WebElement dd = driver.findElement(By.id("accountId"));
        Select s = new Select(dd);
        for (WebElement opt : s.getOptions()) {
            if (opt.getText().contains(part)) {
                opt.click();
                return;
            }
        }
        s.selectByIndex(0); // if nothing matches, pick first
    }
}
