package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

public class HomePage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Locators
    private By txtGreeting = By.className("greeting-text");
    private By valTongNCC = By.id("val_tong_ncc");
    private By valDonNhap = By.id("val_don_nhap");
    private By warningItems = By.className("warning-item");

    // Menu Sidebar
    private By menuSanPham = By.xpath("//a[contains(@href, '/san-pham/')]");
    private By menuNhaCungCap = By.xpath("//a[contains(@href, '/nha-cung-cap/')]");

    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public String getGreetingText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(txtGreeting)).getText();
    }

    public String getTongNCC() {

        wait.until(driver -> !driver.findElement(valTongNCC).getText().equals("..."));
        return driver.findElement(valTongNCC).getText();
    }

    public int getSoLuongCanhBao() {
        List<WebElement> items = driver.findElements(warningItems);
        return items.size();
    }

    public void clickMenuSanPham() {
        driver.findElement(menuSanPham).click();
    }
}