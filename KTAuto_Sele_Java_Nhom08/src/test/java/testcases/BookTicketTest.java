package testcases;

import common.Constant;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pageobjects.BookTicketPage;
import pageobjects.HomePage;
import pageobjects.LoginPage;
import pageobjects.TimetablePage;
import java.time.Duration;

public class BookTicketTest extends BaseTest {

    private final HomePage homePage = new HomePage();
    private final LoginPage loginPage = new LoginPage();
    private final TimetablePage timetablePage = new TimetablePage();
    private final BookTicketPage bookTicketPage = new BookTicketPage();

    @BeforeMethod
    public void setUpBookTicketTest() {
        homePage.navigateToHome();
    }

    @Test
    public void TC15_UserCanOpenBookTicketFromTimetable() {
        System.out.println("TC15 - Mở trang đặt vé từ trang Lịch trình tàu");

        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));

        // Step 1: Đăng nhập
        homePage.clickLoginTab();
        loginPage.login(Constant.USERNAME, Constant.PASSWORD);

        // Step 2: Vào trang Timetable (thường click qua Book Ticket tab hoặc Timetable tab)
        // Giả sử click Book Ticket tab rồi mới vào Timetable, hoặc bạn có thể điều chỉnh xpath nếu có tab riêng
        homePage.clickBookTicketTab();

        // Step 3: Chọn lộ trình và click "book ticket"
        String departStation = "Đà Nẵng";
        String arriveStation = "Sài Gòn";

        timetablePage.clickBookTicketLink(departStation, arriveStation);

        // Step 4: Verify đã chuyển sang trang Book Ticket và giá trị được điền sẵn đúng
        wait.until(ExpectedConditions.urlContains("BookTicketPage.cshtml"));

        Assert.assertEquals(bookTicketPage.getSelectedDepartStation(), departStation,
                "LỖI: Ga đi không khớp với Timetable!");

        Assert.assertEquals(bookTicketPage.getSelectedArriveStation(), arriveStation,
                "LỖI: Ga đến không khớp với Timetable!");
    }
}
