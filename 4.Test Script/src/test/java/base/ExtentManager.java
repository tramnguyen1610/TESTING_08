package base;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentManager {
    private static ExtentReports extent;

    public static ExtentReports getExtentReports() {
        if (extent == null) {
            // Đường dẫn lưu file report HTML
            ExtentSparkReporter spark = new ExtentSparkReporter("target/ExtentReport.html");
            spark.config().setReportName("Kết quả kiểm thử Kim Liên Minimart");
            spark.config().setDocumentTitle("Automation Test Report");

            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("Người thực hiện", "Kim Liên");
            extent.setSystemInfo("Môi trường", "Local");
        }
        return extent;
    }
}