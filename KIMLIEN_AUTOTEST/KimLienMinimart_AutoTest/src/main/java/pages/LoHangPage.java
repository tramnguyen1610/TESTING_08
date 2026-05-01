package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class LoHangPage {
    private WebDriver driver;
    private WebDriverWait wait;

    private By menuLoHang = By.xpath("//a[contains(@href, 'lo-hang')]");
    private By sectionCanhBao = By.className("warning-section");
    private By bangLoHang = By.id("lo_table");
    private By theadColumns = By.xpath("//table[@id='lo_table']/thead/tr/th");
    private By tbodyRows = By.xpath("//table[@id='lo_table']/tbody/tr");
    private By filterStatus = By.id("lo_status");

    public LoHangPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void clickMenuLoHang() {
        wait.until(ExpectedConditions.elementToBeClickable(menuLoHang)).click();
    }

    public boolean kiemTraHienThiCanhBao() {
        try {
            WebElement canhBao = wait.until(ExpectedConditions.visibilityOfElementLocated(sectionCanhBao));
            return canhBao.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public List<String> layDanhSachTenCot() {
        List<String> tenCotList = new ArrayList<>();
        wait.until(ExpectedConditions.visibilityOfElementLocated(bangLoHang));
        List<WebElement> columns = driver.findElements(theadColumns);
        for (WebElement col : columns) {
            tenCotList.add(col.getText().trim());
        }
        return tenCotList;
    }

    public void cuonXuongBangDuLieu() {
        try {
            WebElement table = wait.until(ExpectedConditions.visibilityOfElementLocated(bangLoHang));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", table);
        } catch (Exception e) {
            System.out.println("Không thể cuộn xuống bảng: " + e.getMessage());
        }
    }

    public List<String> layDanhSachMaLoHang() {
        List<String> maLoHangList = new ArrayList<>();
        List<WebElement> rows = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(tbodyRows));
        for (WebElement row : rows) {
            WebElement cellMaLo = row.findElement(By.xpath("./td[1]"));
            maLoHangList.add(cellMaLo.getText().trim());
        }
        return maLoHangList;
    }

    public boolean kiemTraMauSacTrangThai() {
        List<WebElement> rows = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(tbodyRows));
        boolean tatCaDeuDung = true;

        for (WebElement row : rows) {
            WebElement cellTrangThai = row.findElement(By.xpath("./td[6]"));
            String textTrangThai = cellTrangThai.getText().trim();
            String className = cellTrangThai.getAttribute("class");

            if (textTrangThai.equals("Đã hết hạn") || textTrangThai.equals("Sắp hết hạn")) {
                if (className == null || !className.contains("text-red")) {
                    tatCaDeuDung = false;
                }
            } else if (textTrangThai.equals("Bình thường")) {
                if (className == null || !className.contains("text-green")) {
                    tatCaDeuDung = false;
                }
            }
        }
        return tatCaDeuDung;
    }

    public boolean kiemTraDinhDangNgay() {
        List<WebElement> rows = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(tbodyRows));
        boolean tatCaDeuDung = true;
        String datePattern = "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/\\d{4}$";

        for (WebElement row : rows) {
            String ngayNhap = row.findElement(By.xpath("./td[4]")).getText().trim();
            String hsd = row.findElement(By.xpath("./td[5]")).getText().trim();

            if (!ngayNhap.matches(datePattern) || !hsd.matches(datePattern)) {
                tatCaDeuDung = false;
            }
        }
        return tatCaDeuDung;
    }

    public boolean kiemTraSoLuongKhongAm() {
        List<WebElement> rows = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(tbodyRows));
        boolean tatCaDeuDung = true;

        for (WebElement row : rows) {
            String soLuongStr = row.findElement(By.xpath("./td[3]")).getText().trim();
            try {
                int soLuong = Integer.parseInt(soLuongStr);
                if (soLuong < 0) tatCaDeuDung = false;
            } catch (NumberFormatException e) {
                tatCaDeuDung = false;
            }
        }
        return tatCaDeuDung;
    }

    public void chonTrangThaiLoc(String status) {
        WebElement dropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(filterStatus));
        org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(dropdown);
        select.selectByVisibleText(status);
    }

    public String layThongBaoBangTrong() {
        WebElement cellThongBao = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//table[@id='lo_table']/tbody/tr/td")
        ));
        return cellThongBao.getText().trim();
    }

    public int demSoDongDuLieu() {
        List<WebElement> rows = driver.findElements(By.xpath("//table[@id='lo_table']/tbody/tr"));
        if (rows.size() == 1) {
            String textDongDau = rows.get(0).getText();
            if (textDongDau.contains("Chưa có dữ liệu") ||
                    textDongDau.contains("Không tìm thấy") ||
                    textDongDau.contains("Không có dữ liệu")) {
                return 0;
            }
        }
        return rows.size();
    }

    public boolean kiemTraThanhPhanTrangHienThi() {
        try {
            WebElement pagination = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pagination_controls")));
            List<WebElement> buttons = pagination.findElements(By.className("page-btn"));
            return buttons.size() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public void chonSoTrang(String soTrang) {
        WebElement pagination = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pagination_controls")));
        WebElement nutTrang = pagination.findElement(By.xpath(".//button[contains(@class, 'page-btn') and text()='" + soTrang + "']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", nutTrang);
        try { Thread.sleep(500); } catch (InterruptedException e) {}
        nutTrang.click();
    }

    public void clickNutTruoc() {
        WebElement pagination = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pagination_controls")));
        WebElement nutTruoc = pagination.findElement(By.xpath(".//button[contains(@class, 'page-btn') and text()='Trước']"));

        if (nutTruoc.isEnabled()) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", nutTruoc);
            try { Thread.sleep(500); } catch (InterruptedException e) {}
            nutTruoc.click();
        }
    }

    public void clickNutTiep() {
        WebElement pagination = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pagination_controls")));
        WebElement nutTiep = pagination.findElement(By.xpath(".//button[contains(@class, 'page-btn') and text()='Tiếp']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", nutTiep);
        nutTiep.click();
    }

    public boolean isNutBiVoHieuHoa(String tenNut) {
        WebElement pagination = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pagination_controls")));
        WebElement nut = pagination.findElement(By.xpath(".//button[contains(@class, 'page-btn') and text()='" + tenNut + "']"));
        return nut.getAttribute("disabled") != null;
    }

    public String laySoTrangCuoi() {
        List<WebElement> pageButtons = driver.findElements(By.cssSelector("#pagination_controls .page-btn"));
        for (int i = pageButtons.size() - 1; i >= 0; i--) {
            String text = pageButtons.get(i).getText();
            if (text.matches("\\d+")) return text;
        }
        return "1";
    }

    public void nhapTuKhoaTimKiem(String keyword) {
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("lo_search")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", searchInput);
        searchInput.clear();
        for (char ch : keyword.toCharArray()) {
            searchInput.sendKeys(String.valueOf(ch));
            try { Thread.sleep(150); } catch (InterruptedException e) {}
        }
        ((JavascriptExecutor) driver).executeScript("searchLH(1);");
    }

    public String layMaLoHangDongDauTien() {
        WebElement cellMaLH = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//table[@id='lo_table']/tbody/tr[1]/td[1]")
        ));
        return cellMaLH.getText().trim();
    }

    public String layTenSanPhamDongDauTien() {
        WebElement cellTenSP = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//table[@id='lo_table']/tbody/tr[1]/td[2]")
        ));
        return cellTenSP.getText().trim();
    }

    public void xoaTrangTimKiem() {
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("lo_search")));
        searchInput.clear();
        ((JavascriptExecutor) driver).executeScript("searchLH(1);");
    }
}