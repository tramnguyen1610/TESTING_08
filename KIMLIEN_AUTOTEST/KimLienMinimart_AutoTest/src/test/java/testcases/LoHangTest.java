package testcases;

import base.BaseTest;
import base.TestListener;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pages.DangNhapPage;
import pages.LoHangPage;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import org.openqa.selenium.By;
import java.util.ArrayList;
import org.openqa.selenium.WebElement;

@Listeners(TestListener.class)
public class LoHangTest extends BaseTest {

    @Test
    public void FUN_LH_SC01_01() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));


        driver.get("http://127.0.0.1:8000/");
        test.info("Đăng nhập bằng tài khoản kimlien");

        DangNhapPage dangNhapPage = new DangNhapPage(driver);
        dangNhapPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        dangNhapPage.clickDangNhap();


        try {

            Thread.sleep(500);

            WebDriverWait alertWait = new WebDriverWait(driver, Duration.ofSeconds(10));
            alertWait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
            test.info("Đã đóng popup đăng nhập thành công");
        } catch (Exception e) {
            System.out.println("Không thấy popup đăng nhập");
        }


        wait.until(ExpectedConditions.urlContains("dashboard"));


        LoHangPage loHangPage = new LoHangPage(driver);

        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        loHangPage.clickMenuLoHang();
        test.info("Đã click vào menu Lô hàng");

        wait.until(ExpectedConditions.urlContains("lo-hang"));
        Assert.assertTrue(driver.getCurrentUrl().contains("lo-hang"),
                "Fail: Không chuyển hướng đến màn hình Lô hàng được!");

        try { Thread.sleep(2000); } catch (InterruptedException e) {}
        loHangPage.cuonXuongBangDuLieu();
        try { Thread.sleep(1000); } catch (InterruptedException e) {}


        boolean isCanhBaoHienThi = loHangPage.kiemTraHienThiCanhBao();
        try {
            Assert.assertTrue(isCanhBaoHienThi, "Fail: Phần cảnh báo không hiển thị!");
            test.pass("Phần cảnh báo hiển thị thành công ở phía trên.");
        } catch (AssertionError e) {
            test.fail("Lỗi: Không tìm thấy phần cảnh báo hết hạn.");
            throw e;
        }


        List<String> actualColumns = loHangPage.layDanhSachTenCot();
        List<String> expectedColumns = Arrays.asList(
                "Mã lô hàng", "Tên sản phẩm", "Số lượng nhập", "Ngày nhập", "HSD", "Trạng thái"
        );

        try {
            Assert.assertEquals(actualColumns, expectedColumns,
                    "Fail: Bảng danh sách không hiển thị đúng 6 cột như mong đợi!");
            test.pass("TC1 Pass: Màn hình tải thành công. Phần cảnh báo hiển thị đầy đủ ở trên cùng. Hiển thị đầy đủ 6 cột.");
        } catch (AssertionError e) {
            test.fail("Lỗi: Cấu trúc cột của bảng không khớp với kịch bản.");
            throw e;
        }
    }

    @Test
    public void FUN_LH_SC01_02() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));


        driver.get("http://127.0.0.1:8000/");
        test.info("Đăng nhập và truy cập trang Lô hàng");

        DangNhapPage dangNhapPage = new DangNhapPage(driver);
        dangNhapPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        dangNhapPage.clickDangNhap();


        try {
            Thread.sleep(500);
            WebDriverWait waitPopup = new WebDriverWait(driver, Duration.ofSeconds(10));
            waitPopup.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
            test.info("Đã đóng popup đăng nhập");
        } catch (Exception e) {
            System.out.println("Bỏ qua lỗi popup: " + e.getMessage());
        }


        wait.until(ExpectedConditions.urlContains("dashboard"));


        LoHangPage loHangPage = new LoHangPage(driver);

        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        loHangPage.clickMenuLoHang();
        wait.until(ExpectedConditions.urlContains("lo-hang"));

        try { Thread.sleep(2000); } catch (InterruptedException e) {}
        loHangPage.cuonXuongBangDuLieu();
        test.info("Đã tải xong bảng danh sách lô hàng");


        List<String> danhSachMaLo = loHangPage.layDanhSachMaLoHang();
        Assert.assertFalse(danhSachMaLo.isEmpty(), "Fail: Bảng trống không có dữ liệu!");

        boolean isSortedDescending = true;
        for (int i = 0; i < danhSachMaLo.size() - 1; i++) {
            if (danhSachMaLo.get(i).compareTo(danhSachMaLo.get(i+1)) < 0) {
                isSortedDescending = false;
                break;
            }
        }

        try {
            Assert.assertTrue(isSortedDescending, "Fail: Dữ liệu KHÔNG được sắp xếp giảm dần theo mã!");
            test.pass("Dữ liệu được sắp xếp theo mã lô hàng giảm dần (Mã lớn nhất/mới nhất ở trên cùng).");
        } catch (AssertionError e) {
            test.fail("Lỗi: Sai thứ tự sắp xếp mã lô hàng. Danh sách hiện tại: " + danhSachMaLo);
            throw e;
        }


        boolean mauSacHopLy = loHangPage.kiemTraMauSacTrangThai();

        try {
            Assert.assertTrue(mauSacHopLy, "Fail: Màu sắc trạng thái KHÔNG đúng theo kịch bản!");
            test.pass("Các lô có HSD cận date (Sắp hết hạn) hiển thị chữ màu đỏ.");
            test.pass("Các lô có HSD an toàn (Bình thường) hiển thị chữ màu xanh lá cây.");
            test.pass("TC2 PASS: Danh sách sắp xếp đúng và màu sắc trạng thái hợp lý.");
        } catch (AssertionError e) {
            test.fail("Lỗi: Màu sắc trạng thái hiển thị sai.");
            throw e;
        }
    }
    @Test
    public void FUN_LH_SC01_03() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));


        driver.get("http://127.0.0.1:8000/");
        test.info("Đăng nhập và truy cập trang Lô hàng");

        DangNhapPage dangNhapPage = new DangNhapPage(driver);
        dangNhapPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        dangNhapPage.clickDangNhap();

        try {
            Thread.sleep(500);
            WebDriverWait waitPopup = new WebDriverWait(driver, Duration.ofSeconds(10));
            waitPopup.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}

        wait.until(ExpectedConditions.urlContains("dashboard"));


        LoHangPage loHangPage = new LoHangPage(driver);

        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        loHangPage.clickMenuLoHang();
        wait.until(ExpectedConditions.urlContains("lo-hang"));

        try { Thread.sleep(2000); } catch (InterruptedException e) {}
        loHangPage.cuonXuongBangDuLieu();
        test.info("Đã tải xong bảng danh sách lô hàng");


        boolean isDateValid = loHangPage.kiemTraDinhDangNgay();
        try {
            Assert.assertTrue(isDateValid, "Fail: Có lỗi định dạng Ngày nhập hoặc HSD (không chuẩn dd/mm/yyyy)!");
            test.pass("Kiểm tra 1: Cột Ngày nhập và HSD hiển thị chuẩn định dạng dd/mm/yyyy.");
        } catch (AssertionError e) {
            test.fail("Lỗi: Định dạng ngày không chính xác.");
            throw e;
        }


        boolean isQuantityValid = loHangPage.kiemTraSoLuongKhongAm();
        try {
            Assert.assertTrue(isQuantityValid, "Fail: Tồn tại số lượng nhập là số âm hoặc không phải số nguyên!");
            test.pass("Kiểm tra 2: Cột Số lượng nhập hiển thị số nguyên không âm hợp lệ.");
            test.pass("TC3 PASS: Định dạng dữ liệu Ngày và Số hoàn toàn chính xác.");
        } catch (AssertionError e) {
            test.fail("Lỗi: Cột số lượng hiển thị không hợp lệ.");
            throw e;
        }
    }
    @Test
    public void FUN_LH_SC02_01() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));


        driver.get("http://127.0.0.1:8000/");
        test.info("Đăng nhập và truy cập trang Lô hàng");

        DangNhapPage dangNhapPage = new DangNhapPage(driver);
        dangNhapPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        dangNhapPage.clickDangNhap();

        try {
            Thread.sleep(500);
            WebDriverWait waitPopup = new WebDriverWait(driver, Duration.ofSeconds(10));
            waitPopup.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}

        wait.until(ExpectedConditions.urlContains("dashboard"));


        LoHangPage loHangPage = new LoHangPage(driver);
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        loHangPage.clickMenuLoHang();
        wait.until(ExpectedConditions.urlContains("lo-hang"));

        try { Thread.sleep(2000); } catch (InterruptedException e) {}
        loHangPage.cuonXuongBangDuLieu();


        test.info("Chọn bộ lọc trạng thái: Đã hết hạn");
        loHangPage.chonTrangThaiLoc("Đã hết hạn");


        try { Thread.sleep(2000); } catch (InterruptedException e) {}
        test.info("Đã lọc xong, bắt đầu kiểm tra dữ liệu bảng");


        List<String> danhSachTrangThai = new ArrayList<>();

        List<org.openqa.selenium.WebElement> rows = driver.findElements(By.xpath("//table[@id='lo_table']/tbody/tr"));

        boolean tatCaLaHetHan = true;
        for (org.openqa.selenium.WebElement row : rows) {
            String text = row.findElement(By.xpath("./td[6]")).getText().trim();
            if (!text.equals("Đã hết hạn")) {
                tatCaLaHetHan = false;
                System.out.println("Lỗi: Tìm thấy dòng có trạng thái khác: " + text);
                break;
            }
        }

        try {
            Assert.assertTrue(tatCaLaHetHan, "Fail: Danh sách vẫn hiển thị các lô hàng chưa hết hạn!");
            test.pass("Kết quả trả về ở bảng: Tất cả trạng thái đều là 'Đã hết hạn'.");
        } catch (AssertionError e) {
            test.fail("Lỗi: Bộ lọc hoạt động không chính xác.");
            throw e;
        }


        boolean isRed = loHangPage.kiemTraMauSacTrangThai();
        try {
            Assert.assertTrue(isRed, "Fail: Các dòng 'Đã hết hạn' không được in chữ màu đỏ!");
            test.pass("Các dòng dữ liệu đã được highlight màu đỏ chuẩn xác.");
            test.pass("TC 02_01 PASS: Bộ lọc trạng thái hoạt động tốt.");
        } catch (AssertionError e) {
            test.fail("Lỗi: Màu sắc hiển thị sau khi lọc bị sai.");
            throw e;
        }
    }
    @Test
    public void FUN_LH_SC03_01() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));


        driver.get("http://127.0.0.1:8000/");
        test.info("Đăng nhập và truy cập trang Lô hàng");

        DangNhapPage dangNhapPage = new DangNhapPage(driver);
        dangNhapPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        dangNhapPage.clickDangNhap();

        try {
            Thread.sleep(500);
            WebDriverWait waitPopup = new WebDriverWait(driver, Duration.ofSeconds(10));
            waitPopup.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}

        wait.until(ExpectedConditions.urlContains("dashboard"));


        LoHangPage loHangPage = new LoHangPage(driver);
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        loHangPage.clickMenuLoHang();
        wait.until(ExpectedConditions.urlContains("lo-hang"));

        try { Thread.sleep(2000); } catch (InterruptedException e) {}
        loHangPage.cuonXuongBangDuLieu();


        test.info("Chọn bộ lọc trạng thái: Sắp hết hạn");
        loHangPage.chonTrangThaiLoc("Sắp hết hạn");


        try { Thread.sleep(2000); } catch (InterruptedException e) {}
        test.info("Đã lọc xong, bắt đầu kiểm tra dữ liệu bảng");



        List<org.openqa.selenium.WebElement> rows = driver.findElements(By.xpath("//table[@id='lo_table']/tbody/tr"));

        boolean tatCaLaSapHetHan = true;
        for (org.openqa.selenium.WebElement row : rows) {

            String text = row.findElement(By.xpath("./td[6]")).getText().trim();
            if (!text.equals("Sắp hết hạn")) {
                tatCaLaSapHetHan = false;
                System.out.println("Lỗi: Tìm thấy dòng có trạng thái khác: " + text);
                break;
            }
        }

        try {
            Assert.assertTrue(tatCaLaSapHetHan, "Fail: Danh sách vẫn hiển thị các lô hàng có trạng thái khác 'Sắp hết hạn'!");
            test.pass("Kết quả trả về ở bảng: Tất cả trạng thái đều là 'Sắp hết hạn'.");
        } catch (AssertionError e) {
            test.fail("Lỗi: Bộ lọc 'Sắp hết hạn' hoạt động không chính xác.");
            throw e;
        }


        boolean isRed = loHangPage.kiemTraMauSacTrangThai();
        try {
            Assert.assertTrue(isRed, "Fail: Các dòng 'Sắp hết hạn' không được in chữ màu đỏ!");
            test.pass("Các dòng 'Sắp hết hạn' hiển thị chữ màu đỏ chuẩn xác.");
            test.pass("TC 03_01 PASS: Bộ lọc trạng thái 'Sắp hết hạn' hoạt động tốt.");
        } catch (AssertionError e) {
            test.fail("Lỗi: Màu sắc hiển thị cho trạng thái 'Sắp hết hạn' bị sai.");
            throw e;
        }
    }
    @Test
    public void FUN_LH_SC04_01() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));


        driver.get("http://127.0.0.1:8000/");
        test.info("Đăng nhập và truy cập trang Lô hàng");

        DangNhapPage dangNhapPage = new DangNhapPage(driver);
        dangNhapPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        dangNhapPage.clickDangNhap();


        try {
            Thread.sleep(500);
            WebDriverWait waitPopup = new WebDriverWait(driver, Duration.ofSeconds(10));
            waitPopup.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}

        wait.until(ExpectedConditions.urlContains("dashboard"));


        LoHangPage loHangPage = new LoHangPage(driver);
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        loHangPage.clickMenuLoHang();
        wait.until(ExpectedConditions.urlContains("lo-hang"));

        try { Thread.sleep(2000); } catch (InterruptedException e) {}
        loHangPage.cuonXuongBangDuLieu();


        test.info("Chọn bộ lọc trạng thái: Bình thường");
        loHangPage.chonTrangThaiLoc("Bình thường");


        try { Thread.sleep(2000); } catch (InterruptedException e) {}
        test.info("Đã lọc xong, bắt đầu kiểm tra dữ liệu");


        List<org.openqa.selenium.WebElement> rows = driver.findElements(By.xpath("//table[@id='lo_table']/tbody/tr"));

        boolean tatCaLaBinhThuong = true;
        for (org.openqa.selenium.WebElement row : rows) {
            String text = row.findElement(By.xpath("./td[6]")).getText().trim();
            if (!text.equals("Bình thường")) {
                tatCaLaBinhThuong = false;
                System.out.println("Lỗi: Tìm thấy dòng có trạng thái sai: " + text);
                break;
            }
        }

        try {
            Assert.assertTrue(tatCaLaBinhThuong, "Fail: Danh sách vẫn hiển thị các lô hàng không phải 'Bình thường'!");
            test.pass("Kết quả trả về ở bảng: Tất cả trạng thái đều là 'Bình thường'.");
        } catch (AssertionError e) {
            test.fail("Lỗi: Bộ lọc 'Bình thường' hoạt động không chính xác.");
            throw e;
        }


        boolean isGreen = loHangPage.kiemTraMauSacTrangThai();
        try {
            Assert.assertTrue(isGreen, "Fail: Các dòng 'Bình thường' không được in chữ màu xanh lá cây!");
            test.pass("Các dòng 'Bình thường' hiển thị màu xanh lá cây (#4CAF50) chuẩn xác.");
            test.pass("TC 04_01 PASS: Bộ lọc trạng thái 'Bình thường' hoạt động hoàn hảo.");
        } catch (AssertionError e) {
            test.fail("Lỗi: Màu sắc hiển thị cho trạng thái 'Bình thường' bị sai.");
            throw e;
        }
    }
    @Test
    public void FUN_LH_SC05_01() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        driver.get("http://127.0.0.1:8000/");
        test.info("Đăng nhập và truy cập trang Lô hàng");

        DangNhapPage dangNhapPage = new DangNhapPage(driver);
        dangNhapPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        dangNhapPage.clickDangNhap();

        try {
            Thread.sleep(500);
            WebDriverWait waitPopup = new WebDriverWait(driver, Duration.ofSeconds(10));
            waitPopup.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}

        wait.until(ExpectedConditions.urlContains("dashboard"));

        LoHangPage loHangPage = new LoHangPage(driver);
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        loHangPage.clickMenuLoHang();
        wait.until(ExpectedConditions.urlContains("lo-hang"));

        try { Thread.sleep(2000); } catch (InterruptedException e) {}

        loHangPage.cuonXuongBangDuLieu();
        test.info("Đã cuộn xuống cuối bảng");

        int soDong = loHangPage.demSoDongDuLieu();
        try {
            Assert.assertEquals(soDong, 5, "Fail: Số lượng dòng trên trang 1 không đúng là 5 dòng!");
            test.pass("Kiểm tra 1: Bảng hiển thị chính xác 5 dòng dữ liệu trên trang 1.");
        } catch (AssertionError e) {
            test.fail("Lỗi: Số lượng dòng thực tế hiển thị là: " + soDong);
            throw e;
        }

        boolean isPaginationVisible = loHangPage.kiemTraThanhPhanTrangHienThi();
        try {
            Assert.assertTrue(isPaginationVisible, "Fail: Thanh phân trang hoặc các nút [Trước], 1, 2... không hiển thị!");
            test.pass("Kiểm tra 2: Thanh phân trang hiển thị rõ ràng với đầy đủ các nút điều hướng.");
            test.pass("TC 05_01 PASS: Giao diện phân trang và giới hạn dòng hoạt động đúng thiết kế.");
        } catch (AssertionError e) {
            test.fail("Lỗi: Không tìm thấy thanh phân trang.");
            throw e;
        }
    }
    @Test
    public void FUN_LH_SC05_02() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        driver.get("http://127.0.0.1:8000/");
        test.info("Đăng nhập và truy cập trang Lô hàng");

        DangNhapPage dangNhapPage = new DangNhapPage(driver);
        dangNhapPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        dangNhapPage.clickDangNhap();

        try {
            Thread.sleep(500);
            WebDriverWait waitPopup = new WebDriverWait(driver, Duration.ofSeconds(10));
            waitPopup.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}

        wait.until(ExpectedConditions.urlContains("dashboard"));

        LoHangPage loHangPage = new LoHangPage(driver);
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        loHangPage.clickMenuLoHang();
        wait.until(ExpectedConditions.urlContains("lo-hang"));

        try { Thread.sleep(2000); } catch (InterruptedException e) {}
        loHangPage.cuonXuongBangDuLieu();

        String maLoTrang1 = driver.findElement(By.xpath("//table[@id='lo_table']/tbody/tr[1]/td[1]")).getText();
        test.info("Mã lô hàng đầu tiên trang 1: " + maLoTrang1);

        test.info("Nhấn chọn chuyển sang trang 2");
        try {
            loHangPage.chonSoTrang("2");
            try { Thread.sleep(2000); } catch (InterruptedException e) {}
        } catch (Exception e) {
            test.fail("Lỗi: Không tìm thấy nút trang 2. Hãy đảm bảo bạn có > 5 lô hàng trong kho!");
            throw e;
        }

        int soDongTrang2 = loHangPage.demSoDongDuLieu();
        String maLoTrang2 = driver.findElement(By.xpath("//table[@id='lo_table']/tbody/tr[1]/td[1]")).getText();

        test.info("Mã lô hàng đầu tiên trang 2: " + maLoTrang2);

        try {
            Assert.assertTrue(soDongTrang2 > 0, "Fail: Trang 2 không hiển thị dòng dữ liệu nào!");

            Assert.assertNotEquals(maLoTrang1, maLoTrang2, "Fail: Dữ liệu trang 2 bị trùng lặp hoàn toàn với trang 1!");

            test.pass("Kiểm tra: Bảng làm mới và hiển thị dữ liệu trang 2 thành công, không trùng lặp.");
            test.pass("TC 05_02 PASS: Chuyển trang bất kỳ hoạt động chính xác.");
        } catch (AssertionError e) {
            test.fail("Lỗi: Dữ liệu phân trang hiển thị không đúng.");
            throw e;
        }
    }
    @Test
    public void FUN_LH_SC05_03() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        driver.get("http://127.0.0.1:8000/");
        test.info("Đăng nhập và truy cập trang Lô hàng");

        DangNhapPage dangNhapPage = new DangNhapPage(driver);
        dangNhapPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        dangNhapPage.clickDangNhap();

        try {
            Thread.sleep(500);
            WebDriverWait waitPopup = new WebDriverWait(driver, Duration.ofSeconds(10));
            waitPopup.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}

        wait.until(ExpectedConditions.urlContains("dashboard"));

        LoHangPage loHangPage = new LoHangPage(driver);
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        loHangPage.clickMenuLoHang();
        wait.until(ExpectedConditions.urlContains("lo-hang"));

        try { Thread.sleep(2000); } catch (InterruptedException e) {}
        String maLoTrang1_BanDau = driver.findElement(By.xpath("//table[@id='lo_table']/tbody/tr[1]/td[1]")).getText();

        test.info("Đang ở trang 1, chuyển sang trang 2 để chuẩn bị test nút [Trước]");
        try {
            loHangPage.chonSoTrang("2");
            try { Thread.sleep(2000); } catch (InterruptedException e) {}
        } catch (Exception e) {
            test.fail("Không thể thực hiện test vì không có đủ dữ liệu để hiện trang 2!");
            throw e;
        }

        test.info("Nhấn nút [Trước] để quay lại trang 1");
        loHangPage.clickNutTruoc();

        try { Thread.sleep(2000); } catch (InterruptedException e) {}

        String maLoHienTai = driver.findElement(By.xpath("//table[@id='lo_table']/tbody/tr[1]/td[1]")).getText();
        int soDong = loHangPage.demSoDongDuLieu();

        try {
            Assert.assertEquals(maLoHienTai, maLoTrang1_BanDau, "Fail: Nút [Trước] không đưa người dùng về đúng trang 1!");

            Assert.assertEquals(soDong, 5, "Fail: Số lượng dòng sau khi quay lại không đúng 5 dòng!");

            test.pass("Kết quả: Bảng tải lại và hiển thị chính xác dữ liệu của trang liền trước đó (Trang 1).");
            test.pass("TC 05_03 PASS: Nút [Trước] hoạt động hoàn toàn chính xác.");
        } catch (AssertionError e) {
            test.fail("Lỗi: Nút [Trước] hoạt động sai logic phân trang.");
            throw e;
        }
    }
    @Test
    public void FUN_LH_SC05_04() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        driver.get("http://127.0.0.1:8000/");
        DangNhapPage dangNhapPage = new DangNhapPage(driver);
        dangNhapPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        dangNhapPage.clickDangNhap();

        try {
            Thread.sleep(500);
            WebDriverWait waitPopup = new WebDriverWait(driver, Duration.ofSeconds(10));
            waitPopup.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}
        wait.until(ExpectedConditions.urlContains("dashboard"));

        LoHangPage loHangPage = new LoHangPage(driver);
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        loHangPage.clickMenuLoHang();
        wait.until(ExpectedConditions.urlContains("lo-hang"));
        try { Thread.sleep(2000); } catch (InterruptedException e) {}
        loHangPage.cuonXuongBangDuLieu();

        test.info("Đang ở trang 1, kiểm tra nút [Trước]");
        boolean isPrevDisabled = loHangPage.isNutBiVoHieuHoa("Trước");

        try {
            Assert.assertTrue(isPrevDisabled, "Fail: Nút [Trước] không bị vô hiệu hóa khi đang ở trang 1!");
            test.pass("Hệ thống xử lý an toàn: Nút [Trước] đã bị vô hiệu hóa ở trang đầu tiên.");
        } catch (AssertionError e) {
            test.fail("Lỗi: Nút [Trước] vẫn cho phép nhấn ở trang 1.");
            throw e;
        }

        String trangCuoi = loHangPage.laySoTrangCuoi();
        test.info("Di chuyển tới trang cuối cùng: Trang " + trangCuoi);

        if (!trangCuoi.equals("1")) {
            loHangPage.chonSoTrang(trangCuoi);
            try { Thread.sleep(2000); } catch (InterruptedException e) {}

            boolean isNextDisabled = loHangPage.isNutBiVoHieuHoa("Tiếp");
            try {
                Assert.assertTrue(isNextDisabled, "Fail: Nút [Tiếp] không bị vô hiệu hóa khi đang ở trang cuối!");
                test.pass("Hệ thống xử lý an toàn: Nút [Tiếp] đã bị vô hiệu hóa ở trang cuối cùng.");
            } catch (AssertionError e) {
                test.fail("Lỗi: Nút [Tiếp] vẫn cho phép nhấn ở trang cuối.");
                throw e;
            }
        } else {
            test.info("Dữ liệu chỉ có 1 trang, bỏ qua kiểm tra nút [Tiếp] trang cuối.");
        }

        test.pass("TC 05_04 PASS: Hệ thống xử lý giới hạn phân trang an toàn.");
    }
    @Test
    public void FUN_TKLH_SC01_01() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        driver.get("http://127.0.0.1:8000/");
        DangNhapPage dangNhapPage = new DangNhapPage(driver);
        dangNhapPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        dangNhapPage.clickDangNhap();

        try {
            Thread.sleep(500);
            WebDriverWait waitPopup = new WebDriverWait(driver, Duration.ofSeconds(10));
            waitPopup.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}
        wait.until(ExpectedConditions.urlContains("dashboard"));

        LoHangPage loHangPage = new LoHangPage(driver);
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        loHangPage.clickMenuLoHang();
        wait.until(ExpectedConditions.urlContains("lo-hang"));
        try { Thread.sleep(2000); } catch (InterruptedException e) {}

        test.info("Nhập mã lô hàng đầy đủ: 'LH014' vào thanh tìm kiếm");
        loHangPage.nhapTuKhoaTimKiem("LH014");

        try {
            wait.until(d -> loHangPage.demSoDongDuLieu() == 1);
            test.info("Bảng đã lọc xong, hiện tại còn 1 dòng.");
        } catch (Exception e) {
            System.out.println("Lỗi chờ lọc bảng: " + e.getMessage());
        }

        loHangPage.cuonXuongBangDuLieu();

        int soDong = loHangPage.demSoDongDuLieu();
        String maLoThucTe = loHangPage.layMaLoHangDongDauTien();

        try {
            Assert.assertEquals(soDong, 1, "Fail: Bảng không hiển thị duy nhất 1 dòng!");
            Assert.assertEquals(maLoThucTe, "LH014", "Fail: Mã hiển thị không khớp!");
            test.pass("Tìm kiếm thành công mã LH014, hệ thống hiển thị chính xác 1 dòng.");
        } catch (AssertionError e) {
            test.fail("Lỗi tìm kiếm: Số dòng: " + soDong + ", Mã thực tế: " + maLoThucTe);
            throw e;
        }
    }
    @Test
    public void FUN_TKLH_SC01_02() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        driver.get("http://127.0.0.1:8000/");
        DangNhapPage dangNhapPage = new DangNhapPage(driver);
        dangNhapPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        dangNhapPage.clickDangNhap();

        try {
            WebDriverWait waitAlert = new WebDriverWait(driver, Duration.ofSeconds(10));
            waitAlert.until(ExpectedConditions.alertIsPresent());

            String alertText = driver.switchTo().alert().getText();
            test.info("Đã bắt được popup: " + alertText);

            driver.switchTo().alert().accept();
            test.pass("Đã tắt popup thành công.");
        } catch (Exception e) {
            test.info("Không thấy popup xuất hiện sau 10s, có thể hệ thống vào thẳng Dashboard.");
        }

        wait.until(ExpectedConditions.urlContains("dashboard"));

        LoHangPage loHangPage = new LoHangPage(driver);

        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        loHangPage.clickMenuLoHang();
        wait.until(ExpectedConditions.urlContains("lo-hang"));

        test.info("Nhập từ khóa tìm kiếm: 'LH0'");
        loHangPage.nhapTuKhoaTimKiem("LH0");

        try { Thread.sleep(2500); } catch (InterruptedException e) {}

        List<WebElement> rows = driver.findElements(By.xpath("//table[@id='lo_table']/tbody/tr"));

        test.info("Hệ thống tìm thấy " + rows.size() + " lô hàng khớp với 'LH0'");

        boolean tatCaBatDauBangLH0 = true;
        for (WebElement row : rows) {
            String maLo = row.findElement(By.xpath("./td[1]")).getText().trim();
            if (!maLo.startsWith("LH0")) {
                tatCaBatDauBangLH0 = false;
                test.fail("Lỗi: Tìm thấy mã lô không bắt đầu bằng 'LH0': " + maLo);
                break;
            }
        }

        try {
            Assert.assertTrue(rows.size() > 0, "Fail: Bảng trống, không tìm thấy mã nào chứa 'LH0'!");

            Assert.assertTrue(tatCaBatDauBangLH0, "Fail: Có kết quả tìm kiếm không chứa 'LH0'!");

            test.pass("Kết quả: Bảng hiển thị danh sách các mã lô LH01-LH09 chính xác.");
            test.pass("TC TKLH_SC01_02 PASS: Tìm kiếm theo mã không đầy đủ hoạt động tốt.");
        } catch (AssertionError e) {
            test.fail("Lỗi logic tìm kiếm: Bảng hiển thị sai dữ liệu.");
            throw e;
        }
    }
    @Test
    public void FUN_TKLH_SC02_01() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        driver.get("http://127.0.0.1:8000/");
        DangNhapPage dangNhapPage = new DangNhapPage(driver);
        dangNhapPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        dangNhapPage.clickDangNhap();

        try {
            WebDriverWait waitAlert = new WebDriverWait(driver, Duration.ofSeconds(10));
            waitAlert.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
            test.info("Đã vượt qua bước popup đăng nhập.");
        } catch (Exception e) {
            test.info("Không thấy popup, tiếp tục.");
        }

        wait.until(ExpectedConditions.urlContains("dashboard"));

        LoHangPage loHangPage = new LoHangPage(driver);
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        loHangPage.clickMenuLoHang();
        wait.until(ExpectedConditions.urlContains("lo-hang"));

        String tenTimKiem = "Nước giải khát Coca-Cola 330ml";
        test.info("Nhập tên sản phẩm đầy đủ: '" + tenTimKiem + "'");

        loHangPage.nhapTuKhoaTimKiem(tenTimKiem);

        try {
            wait.until(ExpectedConditions.textToBePresentInElementLocated(
                    By.xpath("//table[@id='lo_table']/tbody/tr[1]/td[2]"), tenTimKiem));
            test.info("Bảng đã lọc xong theo tên sản phẩm.");
        } catch (Exception e) {
            test.fail("Tìm kiếm quá lâu hoặc không ra kết quả khớp với: " + tenTimKiem);
        }

        loHangPage.cuonXuongBangDuLieu();

        int soDong = loHangPage.demSoDongDuLieu();
        String tenThucTe = loHangPage.layTenSanPhamDongDauTien();

        try {
            Assert.assertEquals(tenThucTe, tenTimKiem, "Fail: Tên sản phẩm hiển thị không đúng!");

            test.pass("Hệ thống lọc chính xác, hiển thị đúng dòng sản phẩm: " + tenThucTe);
            test.pass("TC TKLH_SC02_01 PASS: Tìm kiếm theo tên đầy đủ hoạt động hoàn hảo.");
        } catch (AssertionError e) {
            test.fail("Lỗi logic tìm kiếm: Số dòng: " + soDong + ", Tên thực tế: " + tenThucTe);
            throw e;
        }
    }
    @Test
    public void FUN_TKLH_SC02_02() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        driver.get("http://127.0.0.1:8000/");
        DangNhapPage dangNhapPage = new DangNhapPage(driver);
        dangNhapPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        dangNhapPage.clickDangNhap();

        try {
            WebDriverWait waitAlert = new WebDriverWait(driver, Duration.ofSeconds(10));
            waitAlert.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {
            test.info("Không thấy popup, tiếp tục.");
        }

        wait.until(ExpectedConditions.urlContains("dashboard"));

        LoHangPage loHangPage = new LoHangPage(driver);
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        loHangPage.clickMenuLoHang();
        wait.until(ExpectedConditions.urlContains("lo-hang"));

        String tuKhoa = "Nước";
        test.info("Nhập từ khóa tìm kiếm một phần: '" + tuKhoa + "'");

        loHangPage.nhapTuKhoaTimKiem(tuKhoa);

        try { Thread.sleep(2500); } catch (InterruptedException e) {}

        List<WebElement> rows = driver.findElements(By.xpath("//table[@id='lo_table']/tbody/tr"));
        test.info("Hệ thống tìm thấy " + rows.size() + " lô hàng có chứa từ '" + tuKhoa + "'");

        boolean tatCaDeuChuaTuKhoa = true;
        for (WebElement row : rows) {
            String tenSP = row.findElement(By.xpath("./td[2]")).getText();

            if (!tenSP.toLowerCase().contains(tuKhoa.toLowerCase())) {
                tatCaDeuChuaTuKhoa = false;
                test.fail("Lỗi: Tìm thấy sản phẩm không liên quan: " + tenSP);
                break;
            }
        }

        try {
            Assert.assertTrue(rows.size() > 0, "Fail: Không tìm thấy lô hàng nào chứa từ '" + tuKhoa + "'!");

            Assert.assertTrue(tatCaDeuChuaTuKhoa, "Fail: Có kết quả tìm kiếm không chứa từ khóa '" + tuKhoa + "'!");

            test.pass("Kết quả: Bảng hiển thị đúng các lô hàng có chữ '" + tuKhoa + "' (Ví dụ: Nước giải khát, Nước tương...)");
            test.pass("TC TKLH_SC02_02 PASS: Tìm kiếm theo tên không đầy đủ hoạt động ổn định.");
        } catch (AssertionError e) {
            test.fail("Lỗi logic tìm kiếm một phần.");
            throw e;
        }
    }
    @Test
    public void FUN_TKLH_SC03_01() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        driver.get("http://127.0.0.1:8000/");
        DangNhapPage dangNhapPage = new DangNhapPage(driver);
        dangNhapPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        dangNhapPage.clickDangNhap();

        try {
            WebDriverWait waitAlert = new WebDriverWait(driver, Duration.ofSeconds(10));
            waitAlert.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
            test.info("Đã tắt popup để vào Dashboard.");
        } catch (Exception e) {
            test.info("Không thấy popup, tiếp tục.");
        }

        wait.until(ExpectedConditions.urlContains("dashboard"));

        LoHangPage loHangPage = new LoHangPage(driver);
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        loHangPage.clickMenuLoHang();
        wait.until(ExpectedConditions.urlContains("lo-hang"));

        String tuKhoaTimKiem = "hảo hảo";
        test.info("Nhập từ khóa bằng chữ thường: '" + tuKhoaTimKiem + "'");

        loHangPage.nhapTuKhoaTimKiem(tuKhoaTimKiem);

        try {
            wait.until(ExpectedConditions.textToBePresentInElementLocated(
                    By.xpath("//table[@id='lo_table']/tbody/tr[1]/td[2]"), "Hảo Hảo"));
            test.info("Bảng đã cập nhật kết quả tìm kiếm cho '" + tuKhoaTimKiem + "'");
        } catch (Exception e) {
            test.fail("Không tìm thấy sản phẩm nào khớp với '" + tuKhoaTimKiem + "' (Có thể kho đang hết Hảo Hảo)");
        }

        loHangPage.cuonXuongBangDuLieu();

        String tenThucTe = loHangPage.layTenSanPhamDongDauTien();
        try {
            Assert.assertTrue(tenThucTe.equalsIgnoreCase("Mì Hảo Hảo") || tenThucTe.toLowerCase().contains(tuKhoaTimKiem),
                    "Fail: Tên sản phẩm '" + tenThucTe + "' không chứa từ khóa '" + tuKhoaTimKiem + "'");

            test.pass("Hệ thống bỏ qua phân biệt hoa/thường. Nhập '" + tuKhoaTimKiem + "' vẫn ra kết quả: " + tenThucTe);
            test.pass("TC TKLH_SC03_01 PASS: Tìm kiếm không phân biệt chữ hoa/thường hoạt động tốt.");
        } catch (AssertionError e) {
            test.fail("Lỗi: Hệ thống có phân biệt hoa thường hoặc không tìm thấy dữ liệu.");
            throw e;
        }
    }
    @Test
    public void FUN_TKLH_SC04_01() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        driver.get("http://127.0.0.1:8000/");
        DangNhapPage dangNhapPage = new DangNhapPage(driver);
        dangNhapPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        dangNhapPage.clickDangNhap();

        try {
            WebDriverWait waitAlert = new WebDriverWait(driver, Duration.ofSeconds(10));
            waitAlert.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}

        wait.until(ExpectedConditions.urlContains("dashboard"));

        LoHangPage loHangPage = new LoHangPage(driver);
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        loHangPage.clickMenuLoHang();
        wait.until(ExpectedConditions.urlContains("lo-hang"));

        String tuKhoaAo = "XYZ123";
        test.info("Nhập chuỗi ký tự ngẫu nhiên không có trong kho: '" + tuKhoaAo + "'");

        loHangPage.nhapTuKhoaTimKiem(tuKhoaAo);

        try { Thread.sleep(2000); } catch (InterruptedException e) {}

        int soDong = loHangPage.demSoDongDuLieu();
        String thongBaoThucTe = loHangPage.layThongBaoBangTrong();

        String thongBaoMongDoi = "Không tìm thấy lô hàng phù hợp";

        try {
            Assert.assertEquals(soDong, 0, "Fail: Bảng vẫn hiển thị dòng dữ liệu dù tìm từ khóa không tồn tại!");

            Assert.assertEquals(thongBaoThucTe, thongBaoMongDoi, "Fail: Nội dung thông báo trống không đúng yêu cầu!");

            test.pass("Kết quả: Bảng trống và hiển thị đúng thông báo: '" + thongBaoThucTe + "'");
            test.pass("TC TKLH_SC04_01 PASS: Tìm kiếm từ khóa không tồn tại hoạt động đúng.");
        } catch (AssertionError e) {
            test.fail("Lỗi: Thông báo thực tế là '" + thongBaoThucTe + "' thay vì '" + thongBaoMongDoi + "'");
            throw e;
        }
    }
    @Test
    public void FUN_TKLH_SC05_01() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // --- BƯỚC 0: ĐĂNG NHẬP & VÀO TRANG ---
        driver.get("http://127.0.0.1:8000/");
        DangNhapPage dangNhapPage = new DangNhapPage(driver);
        dangNhapPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        dangNhapPage.clickDangNhap();

        try {
            WebDriverWait waitAlert = new WebDriverWait(driver, Duration.ofSeconds(10));
            waitAlert.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}

        wait.until(ExpectedConditions.urlContains("dashboard"));

        LoHangPage loHangPage = new LoHangPage(driver);
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        loHangPage.clickMenuLoHang();
        wait.until(ExpectedConditions.urlContains("lo-hang"));

        // Đợi bảng tải xong dữ liệu mặc định ban đầu
        try { Thread.sleep(2000); } catch (InterruptedException e) {}

        // BƯỚC MỚI: LƯU LẠI SỐ DÒNG MẶC ĐỊNH BAN ĐẦU ĐỂ LÀM MỐC SO SÁNH
        int soDongBanDau = loHangPage.demSoDongDuLieu();
        test.info("Số dòng mặc định ban đầu trước khi tìm kiếm: " + soDongBanDau);

        // --- BƯỚC 1: THỰC HIỆN TÌM KIẾM TRƯỚC (Để bảng thu hẹp lại) ---
        test.info("Tìm kiếm mã 'LH014' để bảng thu hẹp kết quả.");
        loHangPage.nhapTuKhoaTimKiem("LH014");

        // Đợi 2 giây để bảng lọc xong
        try { Thread.sleep(2000); } catch (InterruptedException e) {}

        int soDongKhiLoc = loHangPage.demSoDongDuLieu();
        test.info("Số dòng hiện tại sau khi lọc: " + soDongKhiLoc);

        // --- BƯỚC 2: XÓA TRẮNG THANH TÌM KIẾM ---
        test.info("Thực hiện xóa trắng thanh tìm kiếm.");
        loHangPage.xoaTrangTimKiem();

        // Đợi 2.5 giây để hệ thống tải lại toàn bộ danh sách
        try { Thread.sleep(2500); } catch (InterruptedException e) {}

        // --- BƯỚC 3: KIỂM TRA BẢNG CÓ HIỂN THỊ LẠI MẶC ĐỊNH KHÔNG ---
        int soDongSauKhiXoa = loHangPage.demSoDongDuLieu();
        test.info("Số dòng hiển thị sau khi xóa trắng: " + soDongSauKhiXoa);

        try {
            // Đảm bảo bảng đã "nở" ra lại nếu ban đầu có nhiều hơn 1 dòng
            if (soDongBanDau > soDongKhiLoc) {
                Assert.assertTrue(soDongSauKhiXoa > soDongKhiLoc, "Fail: Bảng không hiển thị lại thêm dữ liệu sau khi xóa trắng!");
            }

            // SÓ SÁNH THÔNG MINH: Số dòng hiện tại phải bằng đúng số dòng ban đầu
            Assert.assertEquals(soDongSauKhiXoa, soDongBanDau, "Fail: Bảng không quay về hiển thị đúng số dòng mặc định ban đầu!");

            test.pass("Hệ thống nhận diện ô rỗng và hiển thị lại toàn bộ danh sách mặc định.");
            test.pass("TC TKLH_SC05_01 PASS: Xóa trắng thanh tìm kiếm hoạt động chính xác.");
        } catch (AssertionError e) {
            test.fail("Lỗi: Bảng không tự động làm mới khi xóa từ khóa.");
            throw e;
        }
    }
    @Test
    public void FUN_TKLH_SC06_01() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // --- BƯỚC 0: ĐĂNG NHẬP & VÀO TRANG ---
        driver.get("http://127.0.0.1:8000/");
        DangNhapPage dangNhapPage = new DangNhapPage(driver);
        dangNhapPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        dangNhapPage.clickDangNhap();

        // Tắt popup để tránh bị chặn menu
        try {
            WebDriverWait waitAlert = new WebDriverWait(driver, Duration.ofSeconds(10));
            waitAlert.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}

        wait.until(ExpectedConditions.urlContains("dashboard"));

        LoHangPage loHangPage = new LoHangPage(driver);
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        loHangPage.clickMenuLoHang();
        wait.until(ExpectedConditions.urlContains("lo-hang"));

        // --- BƯỚC 1: NHẬP KÝ TỰ ĐẶC BIỆT "`@$1-1" ---
        String kyTuDacBiet = "`@$1-1";
        test.info("Nhập chuỗi ký tự đặc biệt ngẫu nhiên: '" + kyTuDacBiet + "'");

        // Sử dụng hàm gõ chậm để trigger onkeyup an toàn
        loHangPage.nhapTuKhoaTimKiem(kyTuDacBiet);

        // Đợi 2 giây để hệ thống thực hiện tìm kiếm và render kết quả trống
        try { Thread.sleep(2000); } catch (InterruptedException e) {}

        // --- BƯỚC 2: KIỂM TRA TÍNH AN TOÀN VÀ THÔNG BÁO ---
        int soDong = loHangPage.demSoDongDuLieu();
        String thongBaoThucTe = loHangPage.layThongBaoBangTrong();

        // Theo kịch bản mong đợi: "Không tìm thấy lô hàng phù hợp"
        String thongBaoMongDoi = "Không tìm thấy lô hàng phù hợp";

        try {
            // 1. Kiểm tra bảng phải trống hoàn toàn (không vỡ giao diện)
            Assert.assertEquals(soDong, 0, "Fail: Bảng vẫn hiện dữ liệu khi nhập ký tự đặc biệt vô nghĩa!");

            // 2. Kiểm tra thông báo (Bắt lỗi Cosmetic từng bị Fail trước đó)
            Assert.assertEquals(thongBaoThucTe, thongBaoMongDoi,
                    "Fail: Thông báo hiển thị sai! Thực tế là: '" + thongBaoThucTe + "'");

            test.pass("Hệ thống hoạt động an toàn, không vỡ giao diện.");
            test.pass("Kết quả: Bảng hiển thị đúng thông báo: '" + thongBaoThucTe + "'");
            test.pass("TC TKLH_SC06_01 PASS.");
        } catch (AssertionError e) {
            test.fail("Lỗi: Hệ thống xử lý ký tự đặc biệt chưa đúng hoặc sai câu thông báo.");
            throw e;
        }
    }




}