package base;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class BaseTest {
    public WebDriver driver;
    protected static ExtentReports extent = ExtentManager.getExtentReports();
    protected ExtentTest test;

    public WebDriver getDriver() {
        return driver;
    }

    @BeforeMethod
    public void setUp(java.lang.reflect.Method method) {
        if (extent != null) {
            test = extent.createTest(method.getName());
        }

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.password_manager_leak_detection", false);

        options.setExperimentalOption("prefs", prefs);


        options.addArguments("--disable-notifications");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-save-password-bubble");


        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);


        driver = new ChromeDriver(options);


        driver.manage().deleteAllCookies();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        driver.get("http://127.0.0.1:8000/");
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        if (test != null && driver != null) {
            if (result.getStatus() == ITestResult.FAILURE) {
                String screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
                test.fail("Lỗi: " + result.getThrowable(),
                        MediaEntityBuilder.createScreenCaptureFromBase64String(screenshot).build());
            } else if (result.getStatus() == ITestResult.SUCCESS) {
                test.pass("Test case chạy thành công!");
            }
        }

        if (driver != null) {
            driver.quit();
        }
    }

    @AfterSuite
    public void finish() {
        if (extent != null) {
            extent.flush();
        }
    }
}