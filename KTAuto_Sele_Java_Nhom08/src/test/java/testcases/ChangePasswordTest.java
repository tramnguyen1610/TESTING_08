package testcases;

import common.Constant;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pageobjects.ChangePasswordPage;
import pageobjects.HomePage;
import pageobjects.LoginPage;
import java.time.Duration;

public class ChangePasswordTest extends BaseTest {

    private final HomePage homePage = new HomePage();
    private final LoginPage loginPage = new LoginPage();
    private final ChangePasswordPage changePasswordPage = new ChangePasswordPage();

    @BeforeMethod
    public void setUpChangePasswordTest() {
        homePage.navigateToHome();
    }

    @Test
    public void TC09_UserCanChangePassword() {
        System.out.println("TC09 - User can change password");

        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));

        // Step 1: Đăng nhập
        homePage.clickLoginTab();
        loginPage.login(Constant.USERNAME, Constant.PASSWORD);

        // Step 2: Vào trang Change Password
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[@href='/Account/ChangePassword.cshtml']"))).click();

        // Step 3: Thực hiện đổi mật khẩu
        String newPassword = "NewPassword123!@#";
        changePasswordPage.changePassword(Constant.PASSWORD, newPassword, newPassword);

        // Step 4: Verify kết quả
        String actualMsg = changePasswordPage.getSuccessMessage();
        String expectedMsg = "Your password has been updated!";

        Assert.assertEquals(actualMsg, expectedMsg,
                "LỖI: Thông báo đổi mật khẩu không đúng!");
    }
}
