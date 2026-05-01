package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class SanPhamPage {
    private WebDriver driver;
    private WebDriverWait wait;

    private By menuSanPham = By.xpath("//a[@href='/san-pham/']");
    private By btnThemMoi = By.xpath("//button[contains(text(), '+ Thêm mới sản phẩm')]");
    private By txtTen = By.id("a_ten");
    private By selDVT = By.id("a_dvt");
    private By btnXacNhan = By.xpath("//button[contains(text(),'Xác nhận')]");
    private By txtSearch = By.id("sp_search");
    private By selFilterStatus = By.id("sp_status");
    private By btnSuaDauTien = By.xpath("//table[@id='sp_table']/tbody/tr[1]//button[contains(text(),'✏️')]");
    private By txtTenEdit = By.id("e_ten");
    private By selDVTEdit = By.id("e_dvt");
    private By selTrangThaiEdit = By.id("e_trangthai");
    private By btnCapNhatEdit = By.xpath("//button[contains(text(),'Cập nhật')]");
    private By btnXemDauTien = By.xpath("//table[@id='sp_table']/tbody/tr[1]//button[contains(text(),'👁️')]");
    private By modalXemChiTiet = By.id("viewModal");
    private By txtXemMa = By.id("v_ma");
    private By txtXemTen = By.id("v_ten");
    private By btnDongView = By.xpath("//div[@id='viewModal']//button[contains(text(),'Đóng')]");
    private By btnXView = By.xpath("//div[@id='viewModal']//span[contains(text(),'×')]");
    private By txtStatDangKD = By.id("s_dangkd");
    private By txtStatNgungKD = By.id("s_ngungkd");

    public SanPhamPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void vaoTrangSanPham() {
        wait.until(ExpectedConditions.elementToBeClickable(menuSanPham)).click();
    }

    public void clickThemMoisp() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(btnThemMoi));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
    }

    public void nhapTenSanPham(String ten) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(txtTen));
        input.clear();
        input.sendKeys(ten);
    }

    public void chonDonViTinh(String dvt) {
        WebElement dropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(selDVT));
        Select select = new Select(dropdown);
        select.selectByVisibleText(dvt);
    }

    public void clickXacNhan() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(btnXacNhan));
        btn.click();
    }

    public void clickXemSanPhamDauTien() {
        wait.until(ExpectedConditions.elementToBeClickable(btnXemDauTien)).click();
    }

    public boolean isModalXemHienThi() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(modalXemChiTiet)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getTenSanPhamXem() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(txtXemTen)).getAttribute("value");
    }

    public String getMaSanPhamXem() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(txtXemMa)).getAttribute("value");
    }

    public void clickDongModalXem() {
        wait.until(ExpectedConditions.elementToBeClickable(btnDongView)).click();
    }

    public void clickXModalXem() {
        wait.until(ExpectedConditions.elementToBeClickable(btnXView)).click();
    }

    public void timKiemSanPham(String tuKhoa) {
        WebElement search = wait.until(ExpectedConditions.visibilityOfElementLocated(txtSearch));
        search.clear();
        search.sendKeys(tuKhoa);
    }

    public void locTheoTrangThai(String trangThai) {
        WebElement selectElement = wait.until(ExpectedConditions.visibilityOfElementLocated(selFilterStatus));
        new Select(selectElement).selectByVisibleText(trangThai);
    }

    public void clickSuaSanPhamDauTien() {
        wait.until(ExpectedConditions.elementToBeClickable(btnSuaDauTien)).click();
    }

    public void nhapTenSua(String tenMoi) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(txtTenEdit));
        input.clear();
        input.sendKeys(tenMoi);
    }

    public void chonTrangThaiSua(String trangThai) {
        Select select = new Select(driver.findElement(selTrangThaiEdit));
        select.selectByVisibleText(trangThai);
    }

    public void clickCapNhat() {
        wait.until(ExpectedConditions.elementToBeClickable(btnCapNhatEdit)).click();
    }

    public String getSoLuongDangKD() {
        return driver.findElement(txtStatDangKD).getText();
    }

    public void chonDVTSua(String dvt) {
        Select select = new Select(driver.findElement(selDVTEdit));
        select.selectByVisibleText(dvt);
    }

    public void chonTrangThaiLoc(String trangThai) {
        Select select = new Select(driver.findElement(selFilterStatus));
        select.selectByVisibleText(trangThai);
    }
}