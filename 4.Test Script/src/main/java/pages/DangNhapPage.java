package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class DangNhapPage {
    private WebDriver driver;
    private WebDriverWait wait;


    private By txtUsername = By.id("username");
    private By txtPassword = By.id("password");
    private By btnLogin = By.cssSelector(".btn-login");
    private By errorMsg = By.id("errorDiv");

    public DangNhapPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void nhapThongTinDangNhap(String user, String pass) {

        if (!driver.getCurrentUrl().contains("127.0.0.1")) {
            driver.get("http://127.0.0.1:8000/");
        }


        WebElement inputUser = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        inputUser.clear();
        inputUser.sendKeys(user);

        driver.findElement(By.id("password")).sendKeys(pass);
    }

    public void clickDangNhap() {
        driver.findElement(btnLogin).click();
    }

    public String getErrorMessage() {
        WebElement msg = wait.until(ExpectedConditions.visibilityOfElementLocated(errorMsg));
        return msg.getText();
    }
}