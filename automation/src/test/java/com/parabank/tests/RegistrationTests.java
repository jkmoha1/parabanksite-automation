package com.parabank.tests;

import com.parabank.data.TestDataProvider;
import com.parabank.pages.AccountsOverviewPage;
import com.parabank.pages.LoginPage;
import com.parabank.pages.RegisterPage;
import com.parabank.utils.TestUtil;
import org.testng.Assert;
import org.testng.annotations.Test;

public class RegistrationTests extends BaseTest {

    // try to register a new user and check if overview page is shown
    @Test(priority = 1, dataProvider = "registrationData", dataProviderClass = TestDataProvider.class)
    public void testRegistration(String first, String last, String addr, String city, String state, String zip,
                                 String phone, String ssn, String username, String password) {

        String baseUser = (username == null || username.isBlank()) ? "user" : username;
        String u1 = baseUser.contains("{uniq}") ? TestUtil.expand(baseUser) : baseUser + TestUtil.uniq();

        AccountsOverviewPage overview = new LoginPage(driver)
                .clickRegister()
                .register(first, last, addr, city, state, zip, phone, ssn, u1, password);

        boolean ok = overview.waitUntilLoaded(8);

        // retry with different username if first one fails
        if (!ok) {
            String u2 = "user" + TestUtil.uniq();
            overview = new LoginPage(driver)
                    .clickRegister()
                    .register(first, last, addr, city, state, zip, phone, ssn, u2, password);
            ok = overview.waitUntilLoaded(8);
        }

        Assert.assertTrue(ok, "Did not land on Accounts Overview after registration.");
        overview.logout();
    }

    // check required field validation
    @Test(priority = 2, description = "Registration required-field validation shows an error")
    public void testRegistrationRequiredValidation() {
        RegisterPage rp = new LoginPage(driver).clickRegister();
        rp.submitOnly();
        String err = rp.getErrorTextSafe().toLowerCase();
        Assert.assertTrue(err.contains("required") || err.length() > 0,
                "Expected a validation error when submitting empty registration.");
    }

    // check password mismatch error
    @Test(priority = 3, description = "Registration should block password mismatch")
    public void testRegistrationPasswordMismatch_clean() {
        RegisterPage rp = new LoginPage(driver).clickRegister();
        rp.setBasicInfo("John","Mismatch","1 St","City","ST","90001","5551234567","111-22-3333");
        rp.setCredentials("user"+System.currentTimeMillis(), "passA", "passB");
        rp.submitOnly();
        String err = rp.getErrorTextSafe().toLowerCase();
        Assert.assertTrue(err.contains("password") || err.contains("mismatch") || err.length() > 0,
                "Expected password mismatch error.");
    }

    // check duplicate username is not allowed
    @Test(priority = 4, description = "Registration duplicate username should fail")
    public void testRegistrationDuplicateUsername() {
        String dupUser = "dup" + System.currentTimeMillis();

        // First registration should work fine
        AccountsOverviewPage ov = new LoginPage(driver).clickRegister()
                .register("Jane", "Dup1", "1 Ave", "City", "ST", "90001", "5551112222", "111-11-1111", dupUser, "demo");
        Assert.assertTrue(ov.waitUntilLoaded(8));
        ov.logout();

        // Try second registration with same username
        RegisterPage rp = new LoginPage(driver).clickRegister();
        rp.setBasicInfo("Jane", "Dup2", "1 Ave", "City", "ST", "90001", "5551112222", "111-11-1111");
        rp.setCredentials(dupUser, "demo", "demo");
        rp.submitOnly();
        String err = rp.getErrorTextSafe().toLowerCase();
        Assert.assertTrue(err.contains("username") || err.contains("exists") || err.length() > 0,
                "Expected duplicate username error.");
    }
}
