package pages;

import base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


public class NhapHangPage extends BasePage {
    private WebDriverWait wait;

    public NhapHangPage(WebDriver driver) {
        super(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    private By btnTaoPhieuMoi = By.xpath("//button[contains(text(), '+ Tạo phiếu nhập mới')]");

    private By txtNgayNhap = By.id("a_ngay");
    private By txtSearchNcc = By.id("search_a_ncc");

    private By txtSearchSpTen = By.id("search_sp_ten");
    private By txtSoLuong = By.id("sp_sl");
    private By txtHanSuDung = By.id("sp_hsd");
    private By txtDonGia = By.id("sp_gia");
    private By btnThemLoHang = By.xpath("//div[@id='addModal']//button[contains(text(), '+ Thêm')]");

    private By btnLuuPhieuNhap = By.xpath("//div[@id='addModal']//button[@onclick='savePhieuNhap()']");
    private By addModal = By.id("addModal");

    private By editModal = By.id("editModal");
    private By txtEditMaPhieu = By.id("e_ma");
    private By txtEditNgayNhap = By.id("e_ngay");
    private By txtEditSearchNcc = By.id("search_e_ncc");
    private By cbEditTrangThai = By.id("e_trangthai");
    private By btnCapNhatPhieuNhap = By.xpath("//div[@id='editModal']//button[contains(text(), 'Cập nhật')]");

    private By btnThemLoHangSua = By.xpath("//div[@id='editModal']//button[contains(text(), '+ Thêm lô hàng')]");
    private By popupAddBatchEdit = By.id("addBatchEditPopup");
    private By txtPeSearchTen = By.id("search_pe_ten");
    private By txtPeSoLuong = By.id("pe_sl");
    private By txtPeDonGia = By.id("pe_gia");
    private By txtPeHSD = By.id("pe_hsd");
    private By btnXacNhanThemLoSua = By.xpath("//div[@id='addBatchEditPopup']//button[contains(text(), 'Thêm vào danh sách')]");


    public void clickTaoPhieuMoi() {
        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(btnTaoPhieuMoi));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addModal")));
    }

    public String layMaPhieuTuDong() {
        WebElement txtMa = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("a_ma_phieu")));
        return txtMa.getAttribute("value");
    }

    public void nhapNgay(String yyyy_mm_dd) {
        WebElement inputNgay = wait.until(ExpectedConditions.visibilityOfElementLocated(txtNgayNhap));
        ((JavascriptExecutor) driver).executeScript("arguments[0].value='" + yyyy_mm_dd + "';", inputNgay);
        ((JavascriptExecutor) driver).executeScript("arguments[0].dispatchEvent(new Event('change'))", inputNgay);
    }

    public void chonNhaCungCap(String tenNCC) {
        try {
            WebElement inputNcc = wait.until(ExpectedConditions.visibilityOfElementLocated(txtSearchNcc));
            inputNcc.click();
            inputNcc.clear();
            inputNcc.sendKeys(tenNCC);
            Thread.sleep(800);

            By nccItem = By.xpath("//ul[@id='list_a_ncc']/li[contains(text(), '" + tenNCC + "')]");
            WebElement item = wait.until(ExpectedConditions.presenceOfElementLocated(nccItem));

            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", item);
            ((JavascriptExecutor) driver).executeScript("document.getElementById('list_a_ncc').style.display='none';");

            Thread.sleep(300);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void chonSanPham(String tenSP) {
        try {
            WebElement inputSp = wait.until(ExpectedConditions.visibilityOfElementLocated(txtSearchSpTen));
            inputSp.click();
            inputSp.clear();
            inputSp.sendKeys(tenSP);

            Thread.sleep(800);

            By spItem = By.xpath("//ul[@id='list_sp_ten']/li[contains(text(), '" + tenSP + "')]");
            WebElement item = wait.until(ExpectedConditions.presenceOfElementLocated(spItem));

            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", item);
            ((JavascriptExecutor) driver).executeScript("document.getElementById('list_sp_ten').style.display='none';");

            Thread.sleep(300);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void nhapThongTinLoHang(String sl, String hsd_yyyy_mm_dd, String donGia) {
        org.openqa.selenium.interactions.Actions actions = new org.openqa.selenium.interactions.Actions(driver);

        WebElement elSoLuong = wait.until(ExpectedConditions.presenceOfElementLocated(txtSoLuong));
        actions.click(elSoLuong)
                .keyDown(Keys.CONTROL).sendKeys("a").keyUp(Keys.CONTROL).sendKeys(Keys.BACK_SPACE)
                .sendKeys(sl)
                .perform();

        WebElement elDonGia = wait.until(ExpectedConditions.presenceOfElementLocated(txtDonGia));
        actions.click(elDonGia)
                .keyDown(Keys.CONTROL).sendKeys("a").keyUp(Keys.CONTROL).sendKeys(Keys.BACK_SPACE)
                .sendKeys(donGia)
                .perform();

        WebElement inputHSD = wait.until(ExpectedConditions.presenceOfElementLocated(txtHanSuDung));
        ((JavascriptExecutor) driver).executeScript("arguments[0].value='" + hsd_yyyy_mm_dd + "';", inputHSD);
        ((JavascriptExecutor) driver).executeScript("arguments[0].dispatchEvent(new Event('change'))", inputHSD);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void clickThemLoHang() {
        try {
            By locatorNutThem = By.xpath("//button[@onclick='addTempProduct()']");
            WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(locatorNutThem));

            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", btn);

            Thread.sleep(500);

            org.openqa.selenium.interactions.Actions actions = new org.openqa.selenium.interactions.Actions(driver);
            actions.moveToElement(btn).click().perform();

            System.out.println("-> Đã dùng chuột ảo click vào nút + Thêm thành công!");

        } catch (Exception e) {
            System.out.println("-> Lỗi khi click nút thêm: " + e.getMessage());
        }
    }

    public void clickLuuPhieuNhap() {
        driver.findElement(btnLuuPhieuNhap).click();
    }

    public String xacNhanThongBaoAlert() {
        wait.until(ExpectedConditions.alertIsPresent());
        String alertText = driver.switchTo().alert().getText();
        driver.switchTo().alert().accept();
        return alertText;
    }

    public List<String> layDuLieuPhieuTuBang(String maPhieu) {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(addModal));

        By rowLocator = By.xpath("//table[@id='pn_table']/tbody/tr[td[1][text()='" + maPhieu + "']]");
        wait.until(ExpectedConditions.visibilityOfElementLocated(rowLocator));

        List<WebElement> cells = driver.findElements(By.xpath("//table[@id='pn_table']/tbody/tr[td[1][text()='" + maPhieu + "']]/td"));
        List<String> rowData = new ArrayList<>();
        for (WebElement cell : cells) {
            rowData.add(cell.getText().trim());
        }
        return rowData;
    }

    public void xoaLoHangTam(int rowIndex) {
        By btnXoa = By.xpath("//tbody[@id='temp_sp_list']/tr[" + rowIndex + "]//button");
        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(btnXoa));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
    }

    public void clickNutSuaCuaPhieu(String maPhieu) {
        By btnSua = By.xpath("//table[@id='pn_table']/tbody/tr[td[1][text()='" + maPhieu + "']]//button[contains(text(), '✏️')]");
        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(btnSua));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", btn);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);

        wait.until(ExpectedConditions.visibilityOfElementLocated(editModal));
    }

    public String layMaPhieuDangSua() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(txtEditMaPhieu)).getAttribute("value");
    }

    public void suaNgayNhap(String yyyy_mm_dd) {
        WebElement inputNgay = wait.until(ExpectedConditions.visibilityOfElementLocated(txtEditNgayNhap));
        ((JavascriptExecutor) driver).executeScript("arguments[0].value='" + yyyy_mm_dd + "';", inputNgay);
        ((JavascriptExecutor) driver).executeScript("arguments[0].dispatchEvent(new Event('change'))", inputNgay);
    }

    public void suaNhaCungCap(String tenNCC) {
        try {
            WebElement inputNcc = wait.until(ExpectedConditions.visibilityOfElementLocated(txtEditSearchNcc));
            inputNcc.click();
            inputNcc.clear();
            inputNcc.sendKeys(tenNCC);
            Thread.sleep(800);

            By nccItem = By.xpath("//ul[@id='list_e_ncc']/li[contains(text(), '" + tenNCC + "')]");
            WebElement item = wait.until(ExpectedConditions.presenceOfElementLocated(nccItem));

            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", item);
            ((JavascriptExecutor) driver).executeScript("document.getElementById('list_e_ncc').style.display='none';");
            Thread.sleep(300);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void doiTrangThai(String trangThai) {
        WebElement selectTrangThai = wait.until(ExpectedConditions.visibilityOfElementLocated(cbEditTrangThai));
        org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(selectTrangThai);
        select.selectByVisibleText(trangThai);
    }

    public void xoaLoHangTrongFormSua(int index) {
        By btnXoa = By.xpath("//tbody[@id='edit_sp_list']/tr[" + index + "]/td[last()]");
        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(btnXoa));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
    }

    public void themLoHangMoiTrongFormSua(String tenSP, String sl, String hsd_yyyy_mm_dd, String donGia) {
        try {
            WebElement btnMoThem = wait.until(ExpectedConditions.elementToBeClickable(btnThemLoHangSua));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnMoThem);
            wait.until(ExpectedConditions.visibilityOfElementLocated(popupAddBatchEdit));

            WebElement inputSp = wait.until(ExpectedConditions.visibilityOfElementLocated(txtPeSearchTen));
            inputSp.click(); inputSp.clear(); inputSp.sendKeys(tenSP);
            Thread.sleep(800);
            By spItem = By.xpath("//ul[@id='list_pe_ten']/li[contains(text(), '" + tenSP + "')]");
            WebElement item = wait.until(ExpectedConditions.presenceOfElementLocated(spItem));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", item);
            ((JavascriptExecutor) driver).executeScript("document.getElementById('list_pe_ten').style.display='none';");

            driver.findElement(txtPeSoLuong).sendKeys(sl);
            driver.findElement(txtPeDonGia).sendKeys(donGia);

            WebElement inputHSD = driver.findElement(txtPeHSD);
            ((JavascriptExecutor) driver).executeScript("arguments[0].value='" + hsd_yyyy_mm_dd + "';", inputHSD);

            WebElement btnXacNhan = wait.until(ExpectedConditions.elementToBeClickable(btnXacNhanThemLoSua));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnXacNhan);

            wait.until(ExpectedConditions.invisibilityOfElementLocated(popupAddBatchEdit));
            Thread.sleep(500);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clickCapNhatPhieuNhap() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(btnCapNhatPhieuNhap));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
    }

    public String layMaPhieuChoKiemTraDauTien() {
        try {
            By locator = By.xpath("//table[@id='pn_table']/tbody/tr[td[contains(@class, 'st-nhap')]]/td[1]");
            WebElement maPhieuElement = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            return maPhieuElement.getText().trim();
        } catch (Exception e) {
            return null;
        }
    }

}