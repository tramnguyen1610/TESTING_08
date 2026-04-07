package testcases;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TestListener extends TestListenerAdapter {

    public static WebDriver driver;   // static để các class khác truy cập

    @Override
    public void onTestFailure(ITestResult result) {
        if (driver == null) {
            return;
        }

        String testCaseName = result.getName();
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(Calendar.getInstance().getTime());

        File screenshotDir = new File("evidence/screenshots/");
        if (!screenshotDir.exists()) {
            screenshotDir.mkdirs();
        }

        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        String filePath = "evidence/screenshots/" + testCaseName + "_" + timestamp + ".png";

        try {
            FileUtils.copyFile(screenshot, new File(filePath));
            System.out.println("SCREENSHOT SAVED: " + filePath);
        } catch (IOException e) {
            System.err.println("Failed to save screenshot: " + e.getMessage());
        }
    }
}
