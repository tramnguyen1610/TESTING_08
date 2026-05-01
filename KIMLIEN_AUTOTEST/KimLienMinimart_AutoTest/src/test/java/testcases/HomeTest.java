package testcases;

import base.BaseTest;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.DangNhapPage;
import pages.HomePage;
import java.time.Duration;
import base.TestListener;// Import đúng class Listener đã tạo
import org.testng.annotations.Listeners; // Dòng này cực kỳ quan trọng
@Listeners(TestListener.class)

public class HomeTest extends BaseTest {

    @Test
    public void testThongTinDashboard() {
        DangNhapPage dangNhapPage = new DangNhapPage(driver);
        HomePage homePage = new HomePage(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // BƯỚC 1: Đăng nhập để vào Dashboard
        dangNhapPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        dangNhapPage.clickDangNhap();

        // Xử lý Alert thành công
        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();

        // BƯỚC 2: Kiểm tra lời chào
        String greeting = homePage.getGreetingText();
        Assert.assertEquals(greeting, "Xin chào, Kimlien!");

        // BƯỚC 3: Kiểm tra dữ liệu thống kê (Đảm bảo API đã trả về số liệu)
        String nccCount = homePage.getTongNCC();
        System.out.println("Số lượng NCC hoạt động: " + nccCount);
        Assert.assertNotEquals(nccCount, "...", "Dữ liệu NCC chưa được tải.");

        // BƯỚC 4: Kiểm tra điều hướng Sidebar
        homePage.clickMenuSanPham();
        Assert.assertTrue(driver.getCurrentUrl().contains("/san-pham/"), "Không thể chuyển hướng sang trang Sản phẩm");
    }
}