package com.parabank.pages;

import com.parabank.utils.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

@SuppressWarnings("unused")
public abstract class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage(WebDriver driver){
        this.driver = driver;
        long explicit = ConfigReader.getInt("explicit.wait", 20);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(explicit));
    }

    // click on element
    protected void click(WebElement e){
        wait.until(ExpectedConditions.elementToBeClickable(e)).click();
    }

    // type text inside input field
    protected void type(WebElement e, String text){
        wait.until(ExpectedConditions.visibilityOf(e)).clear();
        e.sendKeys(text);
    }

    // wait until element is visible
    protected void waitForVisible(WebElement e){
        wait.until(ExpectedConditions.visibilityOf(e));
    }

    // get text from element
    protected String getText(WebElement e){
        return wait.until(ExpectedConditions.visibilityOf(e)).getText();
    }

    // check if element appears within given seconds
    protected boolean appearsInTime(org.openqa.selenium.By locator, int seconds) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(seconds))
                    .until(ExpectedConditions.presenceOfElementLocated(locator));
            return true;
        } catch (org.openqa.selenium.TimeoutException te) {
            return false;
        }
    }

    // check if element becomes visible in given time
    protected boolean visibleInTime(org.openqa.selenium.By locator, int seconds) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(seconds))
                    .until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (org.openqa.selenium.TimeoutException te) {
            return false;
        }
    }

    // close alert or popup if any
    protected void closePopupsIfAny() {
        try {
            driver.switchTo().alert().accept();
            System.out.println("Closed an alert popup");
        } catch (Exception ignored) {
        }

        try {
            var buttons = driver.findElements(
                    org.openqa.selenium.By.cssSelector("button[aria-label='Close'], .close, [data-dismiss='modal']"));
            if (!buttons.isEmpty()) {
                buttons.get(0).click();
                System.out.println("Closed a popup/modal");
            }
        } catch (Exception ignored) {
        }
    }
}
