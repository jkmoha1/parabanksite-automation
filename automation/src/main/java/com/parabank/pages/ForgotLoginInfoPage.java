package com.parabank.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class ForgotLoginInfoPage extends BasePage {

    @FindBy(id="firstName") 
    private WebElement firstName;

    @FindBy(id="lastName") 
    private WebElement lastName;

    @FindBy(id="address.street") 
    private WebElement address;

    @FindBy(id="address.city") 
    private WebElement city;

    @FindBy(id="address.state") 
    private WebElement state;

    @FindBy(id="address.zipCode") 
    private WebElement zip;

    @FindBy(id="ssn") 
    private WebElement ssn;

    @FindBy(css="input[value='Find My Login Info']") 
    private WebElement findBtn;

    @FindBy(css="#rightPanel .title") 
    private WebElement resultTitle;

    public ForgotLoginInfoPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    // fill all the required details in form
    public void fill(String f,String l,String a,String c,String s,String z,String ssnVal) {
        type(firstName,f);
        type(lastName,l);
        type(address,a);
        type(city,c);
        type(state,s);
        type(zip,z);
        type(ssn,ssnVal);
    }

    // click on find my login button
    public void submit() {
        click(findBtn);
    }

    // get the result text after submit
    public String result() {
        return getText(resultTitle);
    }
}
