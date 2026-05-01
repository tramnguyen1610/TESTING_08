package testcases;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.Test;

public class DemoTest {

    @Test
    public void testMoTrinhDuyet() throws InterruptedException {
        // 1. Tự động setup ChromeDriver (nhờ thằng WebDriverManager)
        WebDriverManager.chromedriver().setup();

        // 2. Khởi tạo trình duyệt Chrome
        WebDriver driver = new ChromeDriver();

        // 3. Phóng to cửa sổ và Mở trang Google
        driver.manage().window().maximize();
        driver.get("https://www.google.com");

        // 4. Dừng 3 giây để bà kịp nhìn thấy web mở lên
        Thread.sleep(3000);

        // 5. Tắt trình duyệt
        driver.quit();
    }
}