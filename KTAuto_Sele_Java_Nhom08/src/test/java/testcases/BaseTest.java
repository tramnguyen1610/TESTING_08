package testcases;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

@Listeners(TestListener.class)
public class BaseTest {

    protected WebDriver driver;

    @BeforeMethod
    public void setUp() {
        // 🎉 KHÔNG CẦN System.setProperty NỮA!
        // 🎉 KHÔNG CẦN WebDriverManager NỮA!
        driver = new ChromeDriver();  // Selenium tự lo hết
        driver.manage().window().maximize();

        TestListener.driver = driver;
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}