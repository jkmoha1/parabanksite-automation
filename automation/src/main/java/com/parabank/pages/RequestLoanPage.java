package com.parabank.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

public class RequestLoanPage extends BasePage {

    @FindBy(id="amount") 
    private WebElement amount;

    @FindBy(id="downPayment") 
    private WebElement downPayment;

    @FindBy(id="fromAccountId") 
    private WebElement fromAccount;

    @FindBy(css="input[value='Apply Now']") 
    private WebElement applyBtn;

    @FindBy(id="loanStatus") 
    private WebElement loanStatus;

    public RequestLoanPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver,this);
    }    

    // enter amount, down payment and submit loan form
    public void requestLoan(String amt, String down) {
        type(amount, amt); // type loan amount
        type(downPayment, down); // type down payment
        try {
            new Select(fromAccount).selectByIndex(0); // select first account
        } catch (Exception ignored) {}
        click(applyBtn); // click on apply button
    }    

    // get the loan status text
    public String getStatus() {
        try {
            return getText(loanStatus); // return loan status
        } catch(Exception e) {
            return "";
        } 
    }    
}
