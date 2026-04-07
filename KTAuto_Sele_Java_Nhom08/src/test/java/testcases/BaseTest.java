package testcases;

import common.Constant;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

@Listeners(TestListener.class)
public class BaseTest {

    protected WebDriver driver;

    @BeforeMethod
    public void setUp() {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");   // Thay vì maximize sau khi tạo driver

        driver = new ChromeDriver(options);

        // Đồng bộ driver vào Constant để Page Object sử dụng
        Constant.WEBDRIVER = driver;

        TestListener.driver = driver;   // Để listener chụp screenshot khi fail
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}
