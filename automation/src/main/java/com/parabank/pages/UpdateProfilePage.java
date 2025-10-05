package com.parabank.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class UpdateProfilePage extends BasePage {

    @FindBy(linkText="Update Contact Info") 
    private WebElement updateContactLink;

    @FindBy(id="customer.address.street") 
    private WebElement address;

    @FindBy(id="customer.address.city") 
    private WebElement city;

    @FindBy(id="customer.address.state") 
    private WebElement state;

    @FindBy(id="customer.address.zipCode") 
    private WebElement zip;

    @FindBy(id="customer.phoneNumber") 
    private WebElement phone;

    @FindBy(css="input[value='Update Profile']") 
    private WebElement updateBtn;

    @FindBy(css="#rightPanel .title") 
    private WebElement confirmationTitle;

    public UpdateProfilePage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    // click on update contact info link
    public void open() {
        updateContactLink.click();
    }

    // update address and phone then submit
    public void updateAddress(String a, String c, String s, String z, String p) {
        type(address,a);
        type(city,c);
        type(state,s);
        type(zip,z);
        type(phone,p);
        click(updateBtn);
    }

    // get confirmation message after update
    public String getConfirmation() {
        return getText(confirmationTitle);
    }
}
