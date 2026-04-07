package testcases;

import common.Constant;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pageobjects.HomePage;
import pageobjects.LoginPage;
import java.time.Duration;

public class LoginTest extends BaseTest {

    private HomePage homePage = new HomePage();
    private LoginPage loginPage = new LoginPage();

    @BeforeMethod
    public void setUpLoginTest() {
        Constant.WEBDRIVER = this.driver;
        homePage.navigateToHome();   // dùng method từ HomePage
    }

    @Test
    public void TC01_UserCanLoginWithValidAccount() {
        System.out.println("TC01 - User can login with valid account");

        homePage.clickLoginTab();
        loginPage.login(Constant.USERNAME, Constant.PASSWORD);

        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));
        boolean isAccountDisplayed = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='account']/strong"))
        ).isDisplayed();

        Assert.assertTrue(isAccountDisplayed, "LỖI: Tên tài khoản không hiển thị sau khi đăng nhập!");
    }

    @Test
    public void TC02_VerifyLoginWithBlankPassword() {
        System.out.println("TC02 - Login with blank password");

        homePage.clickLoginTab();
        loginPage.login(Constant.USERNAME, "");

        String actualMsg = loginPage.getLoginErrorMessage();
        String expectedMsg = "There was a problem with your login and/or errors exist in your form. ";

        Assert.assertEquals(actualMsg, expectedMsg, "LỖI: Thông báo lỗi không khớp!");
    }

    @Test
    public void TC04_LoginPageDisplaysWhenClickBookTicketWithoutLogin() {
        System.out.println("TC04 - Click Book Ticket without login redirects to Login page");

        homePage.clickBookTicketTab();   // dùng Page Object

        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("Login.cshtml"));

        boolean isLoginFormDisplayed = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//form[@class='LoginForm']"))
        ).isDisplayed();

        Assert.assertTrue(isLoginFormDisplayed, "LỖI: Không chuyển hướng về trang Login!");
    }

    @Test
    public void TC05_VerifyLoginWithWrongPasswordSeveralTimes() {
        System.out.println("TC05 - Login with wrong password several times");

        homePage.clickLoginTab();
        String wrongPassword = "WrongPassword123";

        for (int i = 1; i <= 4; i++) {
            loginPage.login(Constant.USERNAME, wrongPassword);
            // Có thể cần refresh hoặc navigate lại nếu form bị clear
        }

        String actualMsg = loginPage.getLoginErrorMessage();
        Assert.assertTrue(actualMsg.contains("4 out of 5"), "LỖI: Thông báo giới hạn đăng nhập không đúng!");
    }
}
