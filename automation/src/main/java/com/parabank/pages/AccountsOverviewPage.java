package com.parabank.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class AccountsOverviewPage extends BasePage {

    @FindBy(xpath = "//a[normalize-space()='Accounts Overview']")
    private WebElement header;

    @FindBy(linkText = "Log Out")
    private WebElement logoutLink;

    @FindBy(linkText = "Transfer Funds")
    private WebElement transferLink;

    @FindBy(linkText = "Bill Pay")
    private WebElement billPayLink;

    @FindBy(linkText = "Open New Account")
    private WebElement openNewAccountLink;

    @FindBy(linkText = "Find Transactions")
    private WebElement findTransLink;

    @FindBy(css = "#accountTable") 
    private WebElement accountsTable;

    public AccountsOverviewPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    // wait until overview page is loaded
    public boolean waitUntilLoaded(int seconds) {
        try {
            WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(seconds));
            w.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//a[normalize-space()='Accounts Overview']")));
            return true;
        } catch (TimeoutException te) {
            return false;
        }
    }

    // check if we are on overview page
    public boolean isOnOverviewNow() {
        return !driver.findElements(By.xpath("//h1[text()='Accounts Overview']")).isEmpty();
    }

    // verify accounts overview header is visible
    public boolean isAccountsOverviewDisplayed() {
        try {
            waitForVisible(header);
            return header.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // logout and go back to login page
    public LoginPage logout() {
        click(logoutLink);
        return new LoginPage(driver);
    }

    // go to transfer funds page
    public TransferFundsPage goToTransfer() {
        click(transferLink);
        return new TransferFundsPage(driver);
    }

    // go to bill pay page
    public BillPayPage goToBillPay() {
        click(billPayLink);
        return new BillPayPage(driver);
    }

    // go to open new account page
    public OpenNewAccountPage goToOpenNewAccount() {
        click(openNewAccountLink);
        return new OpenNewAccountPage(driver);
    }

    // go to find transactions page
    public FindTransactionsPage goToFindTransactions() {
        click(findTransLink);
        return new FindTransactionsPage(driver);
    }

    // open first account from list
    public AccountDetailsPage openFirstAccount() {
        waitForVisible(header);
        List<WebElement> links = getAccountLinks();
        if (links.isEmpty()) {
            throw new IllegalStateException("No account links found on Accounts Overview page.");
        }
        links.get(0).click();
        return new AccountDetailsPage(driver);
    }

    // get list of account ids visible
    public List<String> getVisibleAccountIds() {
        List<String> ids = new ArrayList<>();
        for (WebElement l : getAccountLinks()) {
            ids.add(l.getText().trim());
        }
        return ids;
    }

    // return total accounts count
    public int getAccountsCount() {
        List<WebElement> links = getAccountLinks();
        return links == null ? 0 : links.size();
    }

    // open account by index
    public AccountDetailsPage openAccountByIndex(int index) {
        waitForVisible(header);
        List<WebElement> links = getAccountLinks();
        if (links.isEmpty() || index < 0 || index >= links.size()) {
            throw new IllegalArgumentException("Invalid account index: " + index);
        }
        links.get(index).click();
        return new AccountDetailsPage(driver);
    }

    // get total balance shown in table
    public double getTotalBalanceDisplayed() {
        By totalLocator = By.xpath("//tr[td[normalize-space()='Total']]/td[2]");
        WebElement totalCell = new WebDriverWait(driver, java.time.Duration.ofSeconds(6))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated(totalLocator));
        return parseMoney(totalCell.getText());
    }

    // get balances of all visible accounts
    public List<Double> getVisibleBalances() {
        List<WebElement> balanceCells = driver.findElements(
            By.xpath("//table//tbody/tr[not(td[normalize-space()='Total'])]/td[2]")
        );

        List<Double> balances = new ArrayList<>();
        for (WebElement cell : balanceCells) {
            balances.add(parseMoney(cell.getText()));
        }
        return balances;
    }

    private double parseMoney(String s) {
        if (s == null) return 0.0;
        String clean = s.replaceAll("[^0-9\\-\\.]", "");
        try { return Double.parseDouble(clean); } catch (Exception e) { return 0.0; }
    }

    private List<WebElement> getAccountLinks() {
        return driver.findElements(By.xpath("//a[contains(@href,'activity.htm?id=')]"));
    }
}
