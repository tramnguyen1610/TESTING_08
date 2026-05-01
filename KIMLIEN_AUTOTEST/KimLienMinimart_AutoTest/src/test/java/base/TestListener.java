package base;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.io.FileHandler;
import org.testng.ITestListener;
import org.testng.ITestResult;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        // Ép kiểu để lấy driver từ BaseTest
        Object currentClass = result.getInstance();
        var driver = ((BaseTest) currentClass).getDriver();

        if (driver != null) {
            // 1. Tạo tên file theo thời gian thực
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = result.getName() + "_" + timestamp + ".png";

            // 2. Kiểm tra và tạo folder screenshots nếu chưa có
            File directory = new File("screenshots");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 3. Chụp và lưu file
            File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            try {
                FileHandler.copy(source, new File("screenshots/" + fileName));
                System.out.println(">>> Đã lưu ảnh minh chứng lỗi tại: screenshots/" + fileName);
            } catch (IOException e) {
                System.out.println("Lỗi khi lưu ảnh: " + e.getMessage());
            }
        }
    }
}