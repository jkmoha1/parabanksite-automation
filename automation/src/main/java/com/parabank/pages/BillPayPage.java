package com.parabank.pages;

import java.time.Duration;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.*;

public class BillPayPage extends BasePage {

    @FindBy(name = "payee.name") private WebElement payeeName;
    @FindBy(name = "payee.address.street") private WebElement payeeAddress;
    @FindBy(name = "payee.address.city") private WebElement payeeCity;
    @FindBy(name = "payee.address.state") private WebElement payeeState;
    @FindBy(name = "payee.address.zipCode") private WebElement payeeZip;
    @FindBy(name = "payee.phoneNumber") private WebElement payeePhone;
    @FindBy(name = "payee.accountNumber") private WebElement payeeAccount;
    @FindBy(name = "verifyAccount") private WebElement payeeVerify;
    @FindBy(name = "amount") private WebElement amount;
    @FindBy(css = "input[value='Send Payment']") private WebElement sendPayment;

    @FindBy(css = "#rightPanel .title") private WebElement confirmationTitle;
    @FindBy(css = "#rightPanel .error") private WebElement errorPanel;

    public BillPayPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    // fill all payee details in the form
    public void fillPayee(String n, String a, String c, String s, String z, String p, String acc, String amt) {
        type(payeeName, n);
        type(payeeAddress, a);
        type(payeeCity, c);
        type(payeeState, s);
        type(payeeZip, z);
        type(payeePhone, p);
        type(payeeAccount, acc);
        type(payeeVerify, acc);
        type(amount, amt);
    }

    // click on send payment button
    public void submit() {
        try {
            WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(10));
            w.until(ExpectedConditions.elementToBeClickable(sendPayment));
            sendPayment.click();
        } catch (Exception e) {
            submitUsingActions();
        }
    }

    // try clicking with Actions if normal click fails
    public void submitUsingActions() {
        try {
            Actions actions = new Actions(driver);
            actions.moveToElement(sendPayment).pause(Duration.ofMillis(100)).click().perform();
        } catch (Exception e) {
            submitUsingEnter();
        }
    }

    // press Enter key if still not working
    public void submitUsingEnter() {
        try { sendPayment.sendKeys(Keys.ENTER); } catch (Exception ignored) {}
    }

    // get confirmation text after payment
    public String getConfirmation() {
        return safeGetText(By.cssSelector("#rightPanel .title"));
    }

    // check if any validation error is shown
    public boolean hasValidationError() {
        try {
            java.util.List<WebElement> errs = driver.findElements(By.cssSelector("#rightPanel .error"));
            for (WebElement e : errs) {
                try {
                    if (e.isDisplayed()) return true;
                } catch (StaleElementReferenceException ignored) {}
            }
            return false;
        } catch (Exception ex) {
            return false;
        }
    }

    // get selected from account text
    public String getFromAccountTextSafe() {
        try {
            Select s = new Select(driver.findElement(By.id("fromAccountId")));
            return s.getFirstSelectedOption().getText().trim();
        } catch (Exception e1) {
            try {
                Select s = new Select(driver.findElement(By.name("fromAccountId")));
                return s.getFirstSelectedOption().getText().trim();
            } catch (Exception e2) {
                return "";
            }
        }
    }

    // clear all input fields
    public void clearAll() {
        try {
            payeeName.clear();
            payeeAddress.clear();
            payeeCity.clear();
            payeeState.clear();
            payeeZip.clear();
            payeePhone.clear();
            payeeAccount.clear();
            payeeVerify.clear();
            amount.clear();
        } catch (Exception ignored) {}
    }

    // get error message text
    public String getErrorTextSafe() {
        return safeGetText(By.cssSelector("#rightPanel .error"));
    }

    // read all text from right panel
    public String getRightPanelText() {
        try {
            WebElement rp = driver.findElement(By.cssSelector("#rightPanel"));
            return rp.getText();
        } catch (Exception e) {
            return "";
        }
    }

    // wait until confirmation panel or details appear
    public String waitForConfirmationPanel(int seconds, String expectedPayeeLowercase) {
        WebDriverWait localWait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        try {
            localWait.until(d -> {
                try {
                    for (WebElement t : d.findElements(By.cssSelector("#rightPanel .title"))) {
                        try {
                            if (t.isDisplayed() && t.getText().toLowerCase().contains("complete")) {
                                return true;
                            }
                        } catch (StaleElementReferenceException ignored) {}
                    }
                    for (WebElement det : d.findElements(By.xpath("//*[@id='rightPanel']//*[contains(normalize-space(),'Bill Payment to')]"))) {
                        try {
                            if (det.isDisplayed()) {
                                String txt = det.getText().toLowerCase();
                                if (txt.contains("bill payment to") && (expectedPayeeLowercase == null || expectedPayeeLowercase.isBlank() || txt.contains(expectedPayeeLowercase))) {
                                    return true;
                                }
                            }
                        } catch (StaleElementReferenceException ignored) {}
                    }
                    return false;
                } catch (StaleElementReferenceException se) {
                    return false;
                }
            });
            return getRightPanelText();
        } catch (TimeoutException te) {
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    // select from account using text or index
    public void selectFromAccountByVisibleTextOrIndex(String visibleText, Integer index) {
        try {
            WebElement selectEl = driver.findElement(By.id("fromAccountId"));
            Select sel = new Select(selectEl);
            if (visibleText != null && !visibleText.isBlank()) {
                sel.selectByVisibleText(visibleText);
            } else if (index != null) {
                sel.selectByIndex(index);
            }
        } catch (NoSuchElementException e1) {
            try {
                WebElement selectEl = driver.findElement(By.name("fromAccountId"));
                Select sel = new Select(selectEl);
                if (visibleText != null && !visibleText.isBlank()) {
                    sel.selectByVisibleText(visibleText);
                } else if (index != null) {
                    sel.selectByIndex(index);
                }
            } catch (Exception ignored) {}
        } catch (Exception ignored) {}
    }

    // wait for confirmation or error message
    public String waitForConfirmationOrError(int seconds) {
        WebDriverWait localWait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        try {
            localWait.until(d -> {
                try {
                    boolean conf = d.findElements(By.cssSelector("#rightPanel .title")).stream()
                            .anyMatch(el -> {
                                try { return el.isDisplayed(); } catch (StaleElementReferenceException se) { return false; }
                            });
                    boolean err = d.findElements(By.cssSelector("#rightPanel .error")).stream()
                            .anyMatch(el -> {
                                try { return el.isDisplayed(); } catch (StaleElementReferenceException se) { return false; }
                            });
                    return conf || err;
                } catch (StaleElementReferenceException se) {
                    return false;
                }
            });

            if (!driver.findElements(By.cssSelector("#rightPanel .title")).isEmpty()) {
                try {
                    WebElement t = driver.findElement(By.cssSelector("#rightPanel .title"));
                    if (t.isDisplayed()) return "confirmation";
                } catch (StaleElementReferenceException ignored) {}
            }
            if (!driver.findElements(By.cssSelector("#rightPanel .error")).isEmpty()) {
                try {
                    WebElement e = driver.findElement(By.cssSelector("#rightPanel .error"));
                    if (e.isDisplayed()) return "error";
                } catch (StaleElementReferenceException ignored) {}
            }
            return "timeout";
        } catch (TimeoutException te) {
            return "timeout";
        } catch (Exception e) {
            return "timeout";
        }
    }

    // get inner html of right panel
    public String dumpRightPanelHtmlSafe() {
        try {
            WebElement rp = driver.findElement(By.cssSelector("#rightPanel"));
            String html = rp.getAttribute("innerHTML");
            return html == null ? "" : html;
        } catch (Exception e) {
            return "(failed to read #rightPanel html: " + e.getClass().getSimpleName() + ")";
        }
    }

    // check if confirmation contains payee name
    public boolean confirmationContainsPayee(String payee) {
        try {
            String payeeLower = payee == null ? "" : payee.toLowerCase();
            String panel = waitForConfirmationPanel(10, payeeLower);
            return panel.toLowerCase().contains(payeeLower);
        } catch (Exception e) {
            return false;
        }
    }

    // return confirmation text
    public String dumpConfirmationText() {
        try { return getRightPanelText(); } catch (Exception e) { return "(failed to read #rightPanel)"; }
    }

    public void closePopups() {
        super.closePopupsIfAny();
    }

    private String safeGetText(By locator) {
        return safeGetText(locator, 5, 150);
    }

    private String safeGetText(By locator, int attempts, long waitMs) {
        for (int i = 0; i < attempts; i++) {
            try {
                java.util.List<WebElement> els = driver.findElements(locator);
                if (els.isEmpty()) return "";
                for (WebElement el : els) {
                    try {
                        if (el.isDisplayed()) {
                            String t = el.getText();
                            return t == null ? "" : t.trim();
                        }
                    } catch (StaleElementReferenceException ignored) {}
                }
                return "";
            } catch (StaleElementReferenceException se) {
            } catch (Exception e) {
                return "";
            }
            try { Thread.sleep(waitMs); } catch (InterruptedException ignored) {}
        }
        return "";
    }
}
