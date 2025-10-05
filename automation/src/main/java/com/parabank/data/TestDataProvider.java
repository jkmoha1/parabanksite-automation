package com.parabank.data;

import com.parabank.utils.ExcelReader;
import org.testng.annotations.DataProvider;

public class TestDataProvider {
    private static final String PATH = "testdata.xlsx";

    @DataProvider(name = "loginData")
    public static Object[][] loginData(){ return new ExcelReader(PATH).getTestData("login"); }

    @DataProvider(name = "registrationData")
    public static Object[][] registrationData(){ return new ExcelReader(PATH).getTestData("registration"); }

    @DataProvider(name = "logoutData")
    public static Object[][] logoutData(){ return new ExcelReader(PATH).getTestData("logout"); }

    @DataProvider(name = "forgotLoginData")
    public static Object[][] forgotLoginData(){ return new ExcelReader(PATH).getTestData("forgotLogin"); }

    @DataProvider(name = "accountDetailsData")
    public static Object[][] accountDetailsData(){ return new ExcelReader(PATH).getTestData("accountDetails"); }

    @DataProvider(name = "activityFilterData")
    public static Object[][] activityFilterData(){ return new ExcelReader(PATH).getTestData("activityFilter"); }

    @DataProvider(name = "openNewAccountData")
    public static Object[][] openNewAccountData(){ return new ExcelReader(PATH).getTestData("openNewAccount"); }

    @DataProvider(name = "transferData")
    public static Object[][] transferData(){ return new ExcelReader(PATH).getTestData("transfer"); }

    @DataProvider(name = "billPayData")
    public static Object[][] billPayData(){ return new ExcelReader(PATH).getTestData("billpay"); }

    @DataProvider(name = "findTransactionsData")
    public static Object[][] findTransactionsData(){ return new ExcelReader(PATH).getTestData("findTransactions"); }

    @DataProvider(name = "contactData")
    public static Object[][] contactData(){ return new ExcelReader(PATH).getTestData("contact"); }

    @DataProvider(name = "loanData")
    public static Object[][] loanData(){ return new ExcelReader(PATH).getTestData("loan"); }

    @DataProvider(name = "updateProfileData")
    public static Object[][] updateProfileData(){ return new ExcelReader(PATH).getTestData("updateProfile"); }

    @DataProvider(name = "responsivenessData")
    public static Object[][] responsivenessData(){ return new ExcelReader(PATH).getTestData("responsiveness"); }
}
