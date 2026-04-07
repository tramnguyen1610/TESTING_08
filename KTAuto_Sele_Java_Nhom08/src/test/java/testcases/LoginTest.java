package testcases;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import common.Constant;

public class LoginTest extends BaseTest {

    @BeforeMethod
    public void beforeMethod() {
        Constant.WEBDRIVER = new ChromeDriver();
        Constant.WEBDRIVER.manage().window().maximize();
        Constant.WEBDRIVER.get(Constant.BASE_URL);
    }

    @AfterMethod
    public void afterMethod() {
        if (Constant.WEBDRIVER != null) {
            Constant.WEBDRIVER.quit();
        }
    }

    @Test
    public void TC01_UserCanLoginWithValidAccount() {
        WebDriver driver = Constant.WEBDRIVER;
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        // Click tab Login
        WebElement loginTab = wait.until(
            ExpectedConditions.elementToBeClickable(By.xpath("//a[@href='/Account/Login.cshtml']"))
        );
        loginTab.click();
        
        // Input username
        driver.findElement(By.id("username")).sendKeys(Constant.USERNAME);
        
        // Input password
        driver.findElement(By.id("password")).sendKeys(Constant.PASSWORD);
        
        // Click Login button (value="login" - chữ thường)
        WebElement loginBtn = wait.until(
            ExpectedConditions.elementToBeClickable(By.xpath("//input[@type='submit' and @value='login']"))
        );
        loginBtn.click();
        
        // Wait for account name to appear (tên tài khoản được hiển thị ở header)
        WebElement accountName = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='account']/strong"))
        );
        assert accountName.isDisplayed();
    }

    @Test
    public void TC04_LoginPageDisplaysWhenClickBookTicketWithoutLogin() {
        WebDriver driver = Constant.WEBDRIVER;
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        // Click on "Book ticket" tab
        WebElement bookTicketTab = wait.until(
            ExpectedConditions.elementToBeClickable(By.xpath("//a[@href='/Page/BookTicketPage.cshtml']"))
        );
        bookTicketTab.click();
        
        // Verify that login page is displayed (not book ticket page)
        // Check by URL or form elements
        wait.until(ExpectedConditions.urlContains("Login.cshtml"));
        
        // Also verify form login exists
        WebElement loginForm = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.xpath("//form[@class='LoginForm']"))
        );
        assert loginForm.isDisplayed();
    }

    @Test
    public void TC14_UserCanBook1TicketAtATime() {
        WebDriver driver = Constant.WEBDRIVER;
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // --- BƯỚC 1 & 2: LOGIN (Pre-condition) ---
        // Click tab Login (Dựa trên ảnh 2: thẻ <a> chứa text 'Login' hoặc href)
        WebElement loginTab = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href,'Login.cshtml')]"))
        );
        loginTab.click();

        driver.findElement(By.id("username")).sendKeys(Constant.USERNAME);
        driver.findElement(By.id("password")).sendKeys(Constant.PASSWORD);

        // Click Login button (Dựa trên thực tế web thường dùng type='submit')
        driver.findElement(By.xpath("//input[@type='submit' and @value='login']")).click();

        // Chờ login thành công (Check sự xuất hiện của logout hoặc tên account)
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(@href,'Logout')]")));

        // --- BƯỚC 3: Click "Book ticket" tab ---
        // Dựa trên Ảnh 2: Thẻ span nằm trong thẻ a
        WebElement bookTicketTab = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href,'BookTicketPage.cshtml')]"))
        );
        bookTicketTab.click();

        // --- BƯỚC 4: Select "Depart date" ---
        // Dựa trên Ảnh 3: name='Date'
        WebElement departDateDropdown = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.name("Date"))
        );
        org.openqa.selenium.support.ui.Select selectDate = new org.openqa.selenium.support.ui.Select(departDateDropdown);
        // Chọn option thứ 2 (bỏ qua cái đầu tiên thường là placeholder hoặc chọn ngày xa nhất)
        selectDate.selectByIndex(1);

        // --- BƯỚC 5: Select "Sài Gòn" và "Nha Trang" ---
        // Chọn Depart from: Sài Gòn
        WebElement departFrom = driver.findElement(By.name("DepartStation"));
        org.openqa.selenium.support.ui.Select selectDepart = new org.openqa.selenium.support.ui.Select(departFrom);
        selectDepart.selectByVisibleText("Sài Gòn");

        // LƯU Ý: ArriveStation thường load động sau khi chọn DepartStation, nên đợi một chút
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//select[@name='ArriveStation']/option[text()='Nha Trang']")));

        WebElement arriveAt = driver.findElement(By.name("ArriveStation"));
        org.openqa.selenium.support.ui.Select selectArrive = new org.openqa.selenium.support.ui.Select(arriveAt);
        selectArrive.selectByVisibleText("Nha Trang");

        // --- BƯỚC 6: Select "Seat type" ---
        // Dựa trên Ảnh 3 & 4: value='6' là Soft bed with air conditioner
        WebElement seatType = driver.findElement(By.name("SeatType"));
        org.openqa.selenium.support.ui.Select selectSeat = new org.openqa.selenium.support.ui.Select(seatType);
        selectSeat.selectByValue("6");

        // --- BƯỚC 7: Select "Ticket amount" ---
        // Dựa trên Ảnh 4: name='TicketAmount'
        WebElement ticketAmount = driver.findElement(By.name("TicketAmount"));
        org.openqa.selenium.support.ui.Select selectAmount = new org.openqa.selenium.support.ui.Select(ticketAmount);
        selectAmount.selectByValue("1");

        // --- BƯỚC 8: Click "Book ticket" button ---
        // Dựa trên Ảnh 4: input type='submit' value='Book ticket'
        WebElement bookTicketBtn = driver.findElement(By.xpath("//input[@type='submit' and @value='Book ticket']"));
        // Scroll xuống nếu nút bị che bởi footer quảng cáo trong ảnh
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", bookTicketBtn);
        bookTicketBtn.click();

        // --- VERIFY ---
        // Kiểm tra thông báo thành công
        WebElement successMsg = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[contains(text(),'Ticket Booked Successfully')] | //div[@id='content']//li[contains(text(),'successfully')]"))
        );

        String msgText = successMsg.getText();
        assert msgText.toLowerCase().contains("successfully");
    }}