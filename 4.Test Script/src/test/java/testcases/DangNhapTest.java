package testcases;

import base.BaseTest;
import org.openqa.selenium.Alert;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.DangNhapPage;
import java.time.Duration;
import base.TestListener;
import org.testng.annotations.Listeners;
@Listeners(TestListener.class)
public class DangNhapTest extends BaseTest {

    @Test
    public void testDangNhapThanhCong() {
        DangNhapPage dangNhapPage = new DangNhapPage(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));


        dangNhapPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        dangNhapPage.clickDangNhap();


        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();
        String alertText = alert.getText();
        Assert.assertEquals(alertText, "Đăng nhập thành công.");
        alert.accept();


        wait.until(ExpectedConditions.urlContains("/dashboard/"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/dashboard/"));
    }

    @Test
    public void testDangNhapSaiMatKhau() {
        DangNhapPage dangNhapPage = new DangNhapPage(driver);


        dangNhapPage.nhapThongTinDangNhap("kimlien", "sai_pass");
        dangNhapPage.clickDangNhap();


        String errorText = dangNhapPage.getErrorMessage();
        Assert.assertTrue(errorText.contains("⚠️"));
    }
}