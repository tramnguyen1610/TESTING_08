package testcases;

import common.Constant;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pageobjects.ForgotPasswordPage;
import pageobjects.HomePage;
import java.time.Duration;

public class ForgotPasswordTest extends BaseTest {

    private final HomePage homePage = new HomePage();
    private final ForgotPasswordPage forgotPasswordPage = new ForgotPasswordPage();

    @BeforeMethod
    public void setUpForgotPasswordTest() {
        homePage.navigateToHome();
    }

    @Test
    public void TC12_ForgotPassword() {
        System.out.println("TC12 - Forgot Password");

        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));

        // Bước 1 & 2: Click Login tab → Click Forgot Password link
        homePage.clickLoginTab();
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[@href='/Account/ForgotPassword.cshtml']"))).click();

        // Bước 3 & 4: Gửi email khôi phục
        forgotPasswordPage.submitEmailForReset(Constant.USERNAME);

        // Verify thông báo gửi email thành công
        String actualMessage = forgotPasswordPage.getSuccessMessage();
        String expectedMessage = "Instructions to reset your password have been sent to the specified email address.";

        Assert.assertEquals(actualMessage, expectedMessage,
                "LỖI: Thông báo gửi email khôi phục không đúng!");

        // Bước 5: Mở link reset (giả lập token)
        String resetToken = "TOKEN_GIA_LAP";
        String resetLink = Constant.BASE_URL + "/Account/PasswordReset.cshtml?resetToken=" + resetToken;
        Constant.WEBDRIVER.get(resetLink);

        // Bước 6 & 7: Nhập mật khẩu mới và Reset (vẫn raw vì chưa có Page Object cho reset page)
        String newPassword = "NewPassword123!@#";

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("newPassword"))).sendKeys(newPassword);
        Constant.WEBDRIVER.findElement(By.id("confirmPassword")).sendKeys(newPassword);

        Constant.WEBDRIVER.findElement(By.id("resetToken")).clear();
        Constant.WEBDRIVER.findElement(By.id("resetToken")).sendKeys(resetToken);

        Constant.WEBDRIVER.findElement(By.xpath("//input[@type='submit' and @value='Reset Password']")).click();

        // Verify kết quả reset thành công
        String successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[@class='message success']"))).getText().trim();

        Assert.assertEquals(successMsg, "Your password has been reset.",
                "LỖI: Thông báo reset mật khẩu không đúng!");

        // Lưu ý: Test này sẽ fail khi chạy thực tế vì token là giả lập
    }
}
