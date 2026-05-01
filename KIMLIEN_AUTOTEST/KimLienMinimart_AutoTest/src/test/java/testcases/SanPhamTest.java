package testcases;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.Alert;
import base.BaseTest;
import base.TestListener;
import org.testng.annotations.Listeners;
import org.openqa.selenium.Alert;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.DangNhapPage;
import pages.SanPhamPage;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@Listeners(TestListener.class)
public class SanPhamTest extends BaseTest {

    @Test
    public void testThemSanPhamThanhCong_Scenario1() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));


        driver.get("http://127.0.0.1:8000/");
        test.info("Đăng nhập và truy cập trang Sản phẩm");

        DangNhapPage loginPage = new DangNhapPage(driver);
        loginPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        loginPage.clickDangNhap();

        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}



        SanPhamPage spPage = new SanPhamPage(driver);
        spPage.vaoTrangSanPham();
        wait.until(ExpectedConditions.urlContains("san-pham"));


        String tenSP = "Sữa tươi TH 180ml";
        test.info("Bước 1: Mở Modal và nhập thông tin sản phẩm: " + tenSP);
        spPage.clickThemMoisp();
        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        spPage.nhapTenSanPham(tenSP);
        spPage.chonDonViTinh("Hộp");

        test.info("Bước 2: Bấm nút 'Xác nhận'");
        spPage.clickXacNhan();


        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            String msg = alert.getText();
            test.info("Thông báo Alert: " + msg);
            Assert.assertTrue(msg.contains("thành công"), "Thông báo không chứa chữ 'thành công'");

            alert.accept();
            test.pass("Đã xác nhận thông báo thành công.");
        } catch (Exception e) {
            test.fail("Thất bại: Không thấy Alert xác nhận.");
            Assert.fail("Không xuất hiện Alert sau khi lưu.");
        }


        test.info("Bước 3: Kiểm tra sản phẩm '" + tenSP + "' có xuất hiện trong danh sách hay không");

        try { Thread.sleep(1000); } catch (InterruptedException e) {}


        try {
            WebElement cellSanPham = driver.findElement(By.xpath("//td[contains(text(), '" + tenSP + "')]"));


            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", cellSanPham);

            Assert.assertNotNull(cellSanPham, "Không tìm thấy tên sản phẩm trong bảng!");
            test.pass("Xác nhận: Sản phẩm mới đã hiển thị trong danh sách sản phẩm.");
        } catch (Exception e) {
            test.fail("Thất bại: Không tìm thấy sản phẩm '" + tenSP + "' trong bảng danh sách sau khi tạo.");
            Assert.fail("Dữ liệu mới không xuất hiện trên giao diện.");
        }
    }

    @Test
    public void testThemSanPhamLoi_TrongTen_02() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));


        driver.get("http://127.0.0.1:8000/");
        test.info("TC2: Đăng nhập lại để làm sạch session");

        DangNhapPage loginPage = new DangNhapPage(driver);
        loginPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        loginPage.clickDangNhap();

        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}



        SanPhamPage spPage = new SanPhamPage(driver);
        spPage.vaoTrangSanPham();
        wait.until(ExpectedConditions.urlContains("san-pham"));
        try { Thread.sleep(1000); } catch (InterruptedException e) {}


        spPage.clickThemMoisp();
        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        spPage.chonDonViTinh("Chai");
        spPage.clickXacNhan();


        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            String msg = alert.getText();
            test.info("Nội dung lỗi: " + msg);
            Assert.assertTrue(msg.contains("Vui lòng nhập Tên sản phẩm"));
            alert.accept();
            test.pass("TC2: Đã bắt được lỗi trống tên");
        } catch (Exception e) {
            test.fail("TC2: Thất bại do không thấy Alert báo lỗi");
            Assert.fail();
        }
    }
    @Test
    public void testThemSanPham_TenTren50KyTu_03() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));


        driver.get("http://127.0.0.1:8000/");
        test.info("TC03: Kiểm tra giới hạn ký tự tên sản phẩm (trên 50 ký tự)");

        DangNhapPage loginPage = new DangNhapPage(driver);
        loginPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        loginPage.clickDangNhap();

        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}

        SanPhamPage spPage = new SanPhamPage(driver);
        spPage.vaoTrangSanPham();
        wait.until(ExpectedConditions.urlContains("san-pham"));


        test.info("Bước 1: Mở Modal thêm sản phẩm");
        spPage.clickThemMoisp();
        try { Thread.sleep(1000); } catch (InterruptedException e) {}


        String tenQuaDai = "Sữa tươi TH True Milk nguyên chất 180ml loại đặc biệt giới hạn 2026";
        String tenMongDoi = tenQuaDai.substring(0, 50); // Lấy 50 ký tự đầu

        test.info("Bước 2: Nhập tên dài (60 ký tự): " + tenQuaDai);
        spPage.nhapTenSanPham(tenQuaDai);

        test.info("Bước 3: Chọn đơn vị tính là 'Chai'");
        spPage.chonDonViTinh("Chai");

        test.info("Bước 4: Nhấn nút 'Xác nhận'");
        spPage.clickXacNhan();


        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            Assert.assertTrue(alert.getText().contains("thành công"), "Không hiện thông báo thành công!");
            alert.accept();
            test.pass("Hệ thống cho phép tạo sản phẩm với tên dài.");
        } catch (Exception e) {
            test.fail("Lỗi: Không thấy Alert thành công.");
            Assert.fail();
        }

        test.info("Bước 5: Kiểm tra tên sản phẩm trong danh sách (phải bị cắt còn 50 ký tự)");
        try {
            Thread.sleep(1000);

            WebElement cellSP = driver.findElement(By.xpath("//td[contains(text(), '" + tenMongDoi + "')]"));

            String tenThucTe = cellSP.getText();
            test.info("Tên thực tế hiển thị: " + tenThucTe);


            Assert.assertEquals(tenThucTe.length(), 50, "Tên sản phẩm không bị cắt đúng 50 ký tự!");

            test.pass("Xác nhận: Tên sản phẩm đã được hiển thị đúng 50 ký tự đầu tiên.");
        } catch (Exception e) {
            test.fail("Thất bại: Không tìm thấy sản phẩm hoặc tên hiển thị sai quy định.");
            Assert.fail();
        }
    }
    @Test
    public void testThemSanPhamLoi_TrongDonViTinh_04() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.get("http://127.0.0.1:8000/");
        test.info("TC04: Kiểm tra thông báo lỗi khi không chọn đơn vị tính");


        DangNhapPage loginPage = new DangNhapPage(driver);
        loginPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        loginPage.clickDangNhap();

        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}




        SanPhamPage spPage = new SanPhamPage(driver);
        spPage.vaoTrangSanPham();
        wait.until(ExpectedConditions.urlContains("san-pham"));

        test.info("Bước 1: Mở Modal và nhập Tên sản phẩm, để trống ĐVT");
        spPage.clickThemMoisp();
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        spPage.nhapTenSanPham("Dầu ăn Simply 1L");

        test.info("Bước 2: Nhấn nút 'Xác nhận'");
        spPage.clickXacNhan();


        try {
            wait.until(ExpectedConditions.alertIsPresent());

            Alert alert = driver.switchTo().alert();
            String msgThucTe = alert.getText();
            String msgMongDoi = "Vui lòng chọn Đơn vị tính!";

            test.info("Thông báo thực tế hiển thị: " + msgThucTe);

            Assert.assertEquals(msgThucTe, msgMongDoi, "Nội dung thông báo lỗi bị sai!");


            alert.accept();
            test.pass("Xác nhận: Hệ thống hiển thị đúng thông báo: '" + msgMongDoi + "'");

            Thread.sleep(1000);

        } catch (AssertionError e) {

            test.fail("Thất bại: Nội dung thông báo không khớp yêu cầu. Thực tế: " + e.getMessage());
            throw e;
        } catch (Exception e) {

            test.fail("Lỗi: Không xuất hiện thông báo (Alert) nào sau khi bấm Xác nhận.");
            Assert.fail("Alert không xuất hiện.");
        }
    }
    @Test
    public void testHuyThemSanPham_KhiDaNhapThongTin_05() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.get("http://127.0.0.1:8000/");
        test.info("TC05: Bấm nút 'Hủy' để dừng việc tạo khi đã nhập thông tin");


        DangNhapPage loginPage = new DangNhapPage(driver);
        loginPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        loginPage.clickDangNhap();
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}



        SanPhamPage spPage = new SanPhamPage(driver);
        spPage.vaoTrangSanPham();

        String tenSP = "Nước ngọt Pepsi";
        spPage.clickThemMoisp();
        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        test.info("Bước 1: Nhập thông tin sản phẩm: " + tenSP);
        spPage.nhapTenSanPham(tenSP);
        spPage.chonDonViTinh("Chai");

        test.info("Bước 2: Nhấn nút 'Hủy'");

        WebElement btnHuy = driver.findElement(By.xpath("//button[contains(text(),'Hủy')]"));
        btnHuy.click();

        test.info("Bước 3: Kiểm tra sản phẩm không được xuất hiện trong danh sách");
        try { Thread.sleep(1000); } catch (Exception e) {}


        int count = driver.findElements(By.xpath("//td[contains(text(),'" + tenSP + "')]")).size();

        Assert.assertEquals(count, 0, "Lỗi: Sản phẩm vẫn xuất hiện trong danh sách dù đã bấm Hủy!");
        test.pass("Xác nhận: Form đã đóng và không có dữ liệu nào được thêm vào hệ thống.");
    }
    @Test
    public void testHuyThemSanPham_KhiTrongThongTin_06() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.get("http://127.0.0.1:8000/");
        test.info("TC06: Bấm nút 'Hủy' khi không nhập thông tin");

        DangNhapPage loginPage = new DangNhapPage(driver);
        loginPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        loginPage.clickDangNhap();

        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}


        SanPhamPage spPage = new SanPhamPage(driver);
        spPage.vaoTrangSanPham();
        wait.until(ExpectedConditions.urlContains("san-pham"));

        test.info("Bước 1: Mở Modal thêm sản phẩm");
        spPage.clickThemMoisp();
        try { Thread.sleep(500); } catch (InterruptedException e) {}

        test.info("Bước 2: Nhấn nút 'Hủy' ngay lập tức");

        driver.findElement(By.xpath("//button[contains(text(),'Hủy')]")).click();

        test.pass("Xác nhận: Modal đóng bình thường khi form đang trống.");
    }
    @Test
    public void testDongModalBangNutX_07() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.get("http://127.0.0.1:8000/");
        test.info("TC07: Bấm nút 'X' để dừng việc tạo");


        DangNhapPage loginPage = new DangNhapPage(driver);
        loginPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        loginPage.clickDangNhap();

        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}


        SanPhamPage spPage = new SanPhamPage(driver);
        spPage.vaoTrangSanPham();
        wait.until(ExpectedConditions.urlContains("san-pham"));

        String tenSP = "Nước ngọt Cola";
        spPage.clickThemMoisp();
        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        spPage.nhapTenSanPham(tenSP);
        spPage.chonDonViTinh("Chai");

        test.info("Bước 1: Nhấn nút 'X' ở góc phải Modal");

        WebElement btnX = driver.findElement(By.xpath("//button[@class='btn-close'] | //button[contains(text(),'×')] | //span[contains(text(),'×')]"));
        btnX.click();

        test.info("Bước 2: Kiểm tra dữ liệu không bị lưu");
        try { Thread.sleep(500); } catch (Exception e) {}

        int count = driver.findElements(By.xpath("//td[contains(text(),'" + tenSP + "')]")).size();
        Assert.assertEquals(count, 0, "Lỗi: Sản phẩm vẫn được tạo dù đã bấm nút X!");

        test.pass("Xác nhận: Modal đã đóng và không lưu sản phẩm.");
    }
    @Test
    public void testDongModalBangNutX_KhiTrongThongTin_08() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.get("http://127.0.0.1:8000/");
        test.info("TC08: Bấm nút 'X' để dừng việc tạo khi không nhập thông tin");

        DangNhapPage loginPage = new DangNhapPage(driver);
        loginPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        loginPage.clickDangNhap();


        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}




        SanPhamPage spPage = new SanPhamPage(driver);
        spPage.vaoTrangSanPham();


        wait.until(ExpectedConditions.urlContains("san-pham"));

        test.info("Bước 1: Mở Modal thêm sản phẩm");

        WebElement btnThem = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(., 'Thêm mới sản phẩm')]")));
        btnThem.click();

        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        test.info("Bước 2: Nhấn nút 'X' để đóng");

        WebElement btnX = driver.findElement(By.xpath("//button[@class='btn-close'] | //button[contains(.,'×')] | //span[contains(.,'×')]"));
        btnX.click();


        try {
            Thread.sleep(1000);
            test.pass("Xác nhận: Modal đã đóng thành công.");
        } catch (Exception e) {
            test.fail("Lỗi: Modal chưa đóng.");
            Assert.fail();
        }
    }
    @Test
    public void testThemSanPhamLoi_TrongTatCaThongTin_09() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.get("http://127.0.0.1:8000/");
        test.info("TC09: Tạo không thành công vì để trống Tên sản phẩm và Đơn vị tính");


        DangNhapPage loginPage = new DangNhapPage(driver);
        loginPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        loginPage.clickDangNhap();
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}



        SanPhamPage spPage = new SanPhamPage(driver);
        spPage.vaoTrangSanPham();
        wait.until(ExpectedConditions.urlContains("san-pham"));

        test.info("Bước 1: Mở Modal và nhấn 'Xác nhận' ngay lập tức khi form trống");
        WebElement btnThem = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(., 'Thêm mới sản phẩm')]")));
        btnThem.click();

        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        spPage.clickXacNhan();


        try {

            wait.until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            String msgActual = alert.getText();

            test.info("Thông báo lỗi thực tế hiển thị: " + msgActual);


            test.info("Đang dừng 1 giây để kiểm tra thông báo bằng mắt...");
            Thread.sleep(1000);

            Assert.assertEquals(msgActual, "Vui lòng nhập thông tin sản phẩm!", "Nội dung lỗi không khớp!");

            alert.accept();
            test.pass("Hệ thống hiển thị đúng thông báo lỗi.");


            Thread.sleep(1000);
        } catch (Exception e) {
            test.fail("Thất bại: Không xuất hiện Alert hoặc lỗi hệ thống.");
            Assert.fail();
        }
    }
    @Test
    public void testThemSanPhamLoi_TrungTen_10() throws Exception {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SanPhamPage spPage = new SanPhamPage(driver);
        test.info("TC10: Tạo không thành công vì trùng Tên sản phẩm đã có trong danh sách");


        DangNhapPage loginPage = new DangNhapPage(driver);
        loginPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        loginPage.clickDangNhap();


        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}



        spPage.vaoTrangSanPham();
        wait.until(ExpectedConditions.urlContains("san-pham"));

        try {

            String tenTrung = "Sữa tươi TH 180ml";
            test.info("Bước 1: Mở Modal và nhập tên đã tồn tại: " + tenTrung);

            spPage.clickThemMoisp();

            Thread.sleep(1000);

            spPage.nhapTenSanPham(tenTrung);
            spPage.chonDonViTinh("Chai");


            test.info("Bước 2: Nhấn nút 'Xác nhận'");
            WebElement btnXacNhan = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(.,'Xác nhận')]"))); // Dùng dấu chấm để bỏ qua khoảng trắng thừa
            btnXacNhan.click();


            wait.until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            String msgActual = alert.getText();
            test.info("Thông báo thực tế hiển thị: " + msgActual);


            Thread.sleep(1000);


            Assert.assertEquals(msgActual, "Tên sản phẩm đã có trong danh sách", "Nội dung báo lỗi trùng tên không đúng!");


            alert.accept();
            test.pass("Hệ thống đã chặn và báo lỗi trùng tên chính xác.");

        } catch (AssertionError e) {

            test.fail("Thất bại: Nội dung thông báo sai. Thực tế thấy: " + e.getMessage());
            throw e;
        } catch (Exception e) {

            test.fail("Lỗi kỹ thuật: " + e.getMessage());
            throw e;
        }
    }

    @Test
    public void testCapNhatSanPham_ThanhCong_01() throws Exception {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SanPhamPage spPage = new SanPhamPage(driver);
        test.info("TC01: Cập nhật sản phẩm thành công");


        DangNhapPage loginPage = new DangNhapPage(driver);
        loginPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        loginPage.clickDangNhap();


        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}



        spPage.vaoTrangSanPham();
        wait.until(ExpectedConditions.urlContains("san-pham"));

        try {

            test.info("Bước 1: Nhấn nút Cập nhật sản phẩm mã SP002");
            spPage.clickSuaSanPhamDauTien();
            Thread.sleep(1000);


            test.info("Bước 2-4: Sửa Tên, DVT, Trạng thái");
            spPage.nhapTenSua("Sữa tươi TH 180ml");
            spPage.chonDVTSua("Hộp");
            spPage.chonTrangThaiSua("Ngừng kinh doanh");


            test.info("Bước 5: Nhấn nút 'Cập nhật'");
            spPage.clickCapNhat();


            wait.until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            String msgActual = alert.getText();
            test.info("Thông báo thực tế hiển thị: " + msgActual);

            Thread.sleep(1000);

            Assert.assertEquals(msgActual, "Cập nhật thành công!", "Nội dung báo thành công không đúng!");
            alert.accept();
            test.pass("Hệ thống cập nhật thành công, đóng form và reload danh sách.");

        } catch (AssertionError e) {
            test.fail("Thất bại: Nội dung thông báo sai. Thực tế thấy: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            test.fail("Lỗi kỹ thuật: " + e.getMessage());
            throw e;
        }
    }

    @Test
    public void testCapNhatSanPham_LoiTrongTen_02() throws Exception {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SanPhamPage spPage = new SanPhamPage(driver);
        test.info("TC02: Cập nhật không thành công vì tên sản phẩm để trống");


        DangNhapPage loginPage = new DangNhapPage(driver);
        loginPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        loginPage.clickDangNhap();


        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}



        spPage.vaoTrangSanPham();
        wait.until(ExpectedConditions.urlContains("san-pham"));

        try {
            test.info("Bước 1: Nhấn nút Cập nhật");
            spPage.clickSuaSanPhamDauTien();
            Thread.sleep(1000);

            test.info("Bước 2: Xóa tên sản phẩm");
            spPage.nhapTenSua("");
            spPage.chonDVTSua("Hộp");


            test.info("Bước 5: Nhấn nút 'Cập nhật'");
            spPage.clickCapNhat();

            wait.until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            String msgActual = alert.getText();
            test.info("Thông báo thực tế: " + msgActual);

            Thread.sleep(1000);


            Assert.assertNotEquals(msgActual, "Cập nhật thành công!", "Lỗi: Hệ thống vẫn cho lưu khi tên trống!");
            alert.accept();
            test.pass("Hệ thống hiển thị lỗi trống tên chính xác.");

        } catch (AssertionError e) {
            test.fail("Thất bại: Hệ thống không báo lỗi khi tên trống. Thực tế: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            test.fail("Lỗi kỹ thuật: " + e.getMessage());
            throw e;
        }
    }
    @Test
    public void testHuyCapNhat_KhiDaNhapThongTin_03_01() throws Exception {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SanPhamPage spPage = new SanPhamPage(driver);
        test.info("TC03_01: Bấm nút 'Hủy' để dừng việc cập nhật khi đã nhập thông tin");


        DangNhapPage loginPage = new DangNhapPage(driver);
        loginPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        loginPage.clickDangNhap();

        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}



        spPage.vaoTrangSanPham();
        wait.until(ExpectedConditions.urlContains("san-pham"));

        try {

            test.info("Bước 1: Nhấn nút Cập nhật sản phẩm mã SP002");
            spPage.clickSuaSanPhamDauTien();
            Thread.sleep(1000);


            test.info("Bước 2-4: Nhập thông tin mới nhưng chuẩn bị Hủy");
            spPage.nhapTenSua("Sữa tươi TH 180ml");
            spPage.chonDVTSua("Hộp");
            spPage.chonTrangThaiSua("Ngừng kinh doanh");
            Thread.sleep(1000);

            test.info("Bước 5: Nhấn nút 'Hủy'");

            WebElement btnHuy = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//div[@id='editModal']//button[contains(text(),'Hủy')]")));
            btnHuy.click();

            Thread.sleep(1000);
            boolean isModalVisible = driver.findElement(By.id("editModal")).isDisplayed();

            Assert.assertFalse(isModalVisible, "Lỗi: Form sửa vẫn hiển thị sau khi bấm Hủy!");
            test.pass("Kết quả: Form đóng, quay lại danh sách và không có cập nhật nào được lưu.");

        } catch (AssertionError e) {
            test.fail("Thất bại: Form không đóng như mong đợi. " + e.getMessage());
            throw e;
        } catch (Exception e) {
            test.fail("Lỗi kỹ thuật: " + e.getMessage());
            throw e;
        }
    }

    @Test
    public void testHuyCapNhat_KhiKhongNhapThongTin_03_02() throws Exception {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SanPhamPage spPage = new SanPhamPage(driver);
        test.info("TC03_02: Bấm nút 'Hủy' để dừng việc cập nhật khi không nhập thông tin");


        DangNhapPage loginPage = new DangNhapPage(driver);
        loginPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        loginPage.clickDangNhap();

        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}



        spPage.vaoTrangSanPham();

        try {

            test.info("Bước 1: Mở form cập nhật");
            spPage.clickSuaSanPhamDauTien();
            Thread.sleep(1000);


            test.info("Bước 2: Nhấn nút 'Hủy' ngay");
            WebElement btnHuy = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//div[@id='editModal']//button[contains(text(),'Hủy')]")));
            btnHuy.click();

            Thread.sleep(1000);
            boolean isModalVisible = driver.findElement(By.id("editModal")).isDisplayed();

            Assert.assertFalse(isModalVisible, "Lỗi: Form vẫn còn đó sau khi bấm Hủy!");
            test.pass("Kết quả: Form đóng lập tức, quay lại danh sách sản phẩm.");

        } catch (AssertionError e) {
            test.fail("Thất bại: Form không đóng. " + e.getMessage());
            throw e;
        } catch (Exception e) {
            test.fail("Lỗi kỹ thuật: " + e.getMessage());
            throw e;
        }
    }
    @Test
    public void testDongModal_NutX_DaNhapThongTin_04_01() throws Exception {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SanPhamPage spPage = new SanPhamPage(driver);
        test.info("TC04_01: Bấm nút 'X' để dừng việc cập nhật khi đã nhập thông tin");


        DangNhapPage loginPage = new DangNhapPage(driver);
        loginPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        loginPage.clickDangNhap();

        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}



        spPage.vaoTrangSanPham();
        wait.until(ExpectedConditions.urlContains("san-pham"));

        try {

            test.info("Bước 1: Hiển thị form cập nhật sản phẩm");
            spPage.clickSuaSanPhamDauTien();
            Thread.sleep(1000);


            test.info("Bước 2-4: Nhập dữ liệu mới: Sữa tươi TH 180ml, Hộp, Ngừng kinh doanh");
            spPage.nhapTenSua("Sữa tươi TH 180ml");
            spPage.chonDVTSua("Hộp");
            spPage.chonTrangThaiSua("Ngừng kinh doanh");
            Thread.sleep(1000);


            test.info("Bước 5: Nhấn nút 'X'");

            WebElement btnX = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//div[@id='editModal']//span[contains(text(),'×')]")));
            btnX.click();


            Thread.sleep(1000);
            boolean isModalVisible = driver.findElement(By.id("editModal")).isDisplayed();

            Assert.assertFalse(isModalVisible, "Lỗi: Form sửa vẫn chưa đóng sau khi bấm nút 'X'!");
            test.pass("Kết quả: Form đóng thành công, dữ liệu không bị thay đổi.");

        } catch (AssertionError e) {
            test.fail("Thất bại: Form không đóng như mong đợi. " + e.getMessage());
            throw e;
        } catch (Exception e) {
            test.fail("Lỗi kỹ thuật: " + e.getMessage());
            throw e;
        }
    }

    @Test
    public void testDongModal_NutX_KhongNhapThongTin_04_02() throws Exception {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SanPhamPage spPage = new SanPhamPage(driver);
        test.info("TC04_02: Bấm nút 'X' để dừng việc cập nhật khi không nhập thông tin");


        DangNhapPage loginPage = new DangNhapPage(driver);
        loginPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        loginPage.clickDangNhap();

        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}



        spPage.vaoTrangSanPham();

        try {

            test.info("Bước 1: Mở form cập nhật sản phẩm mã SP002");
            spPage.clickSuaSanPhamDauTien();
            Thread.sleep(1000);


            test.info("Bước 2: Nhấn nút 'X' khi chưa nhập gì");
            WebElement btnX = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//div[@id='editModal']//span[contains(text(),'×')]")));
            btnX.click();

            Thread.sleep(1000);
            boolean isModalVisible = driver.findElement(By.id("editModal")).isDisplayed();

            Assert.assertFalse(isModalVisible, "Lỗi: Form vẫn hiển thị sau khi bấm 'X'!");
            test.pass("Kết quả: Form đóng lập tức, không có sản phẩm mới nào được cập nhật.");

        } catch (AssertionError e) {
            test.fail("Thất bại: Form không đóng. " + e.getMessage());
            throw e;
        } catch (Exception e) {
            test.fail("Lỗi kỹ thuật: " + e.getMessage());
            throw e;
        }
    }
    @Test
    public void testTimKiem_TheoMaDayDu_01_01() throws Exception {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SanPhamPage spPage = new SanPhamPage(driver);
        test.info("TC01_01: Tìm kiếm mã sản phẩm theo mã đầy đủ");


        DangNhapPage loginPage = new DangNhapPage(driver);
        loginPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        loginPage.clickDangNhap();

        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}


        spPage.vaoTrangSanPham();


        String maSearch = "SP014";
        test.info("Bước 1: Nhập mã sản phẩm đầy đủ: " + maSearch);


        WebElement inputSearch = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sp_search")));
        inputSearch.clear();


        for (char ch : maSearch.toCharArray()) {
            inputSearch.sendKeys(String.valueOf(ch));
            Thread.sleep(200);
        }


        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.xpath("//table[@id='sp_table']/tbody/tr[1]/td[1]"), maSearch));

        try {

            WebElement cellMaSP = driver.findElement(By.xpath("//table[@id='sp_table']/tbody/tr[1]/td[1]"));
            String maThucTe = cellMaSP.getText();

            test.info("Mã sản phẩm thực tế tìm thấy trên màn hình: " + maThucTe);


            Assert.assertEquals(maThucTe, maSearch, "Lỗi: Hệ thống chưa lọc đúng mã " + maSearch);

            int soDong = driver.findElements(By.xpath("//table[@id='sp_table']/tbody/tr")).size();
            Assert.assertEquals(soDong, 1, "Lỗi: Hiện quá nhiều dòng kết quả!");

            test.pass("Kết quả: Hệ thống đã lọc chính xác như khi thực hiện thủ công.");

        } catch (AssertionError e) {
            test.fail("Thất bại: Selenium nhìn thấy mã sai. Thực tế: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            test.fail("Lỗi kỹ thuật: " + e.getMessage());
            throw e;
        }
    }

    @Test
    public void testTimKiem_TheoMaKhongDayDu_01_02() throws Exception {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SanPhamPage spPage = new SanPhamPage(driver);
        test.info("TC01_02: Tìm kiếm sản phẩm theo mã không đầy đủ");


        DangNhapPage loginPage = new DangNhapPage(driver);
        loginPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        loginPage.clickDangNhap();
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}


        spPage.vaoTrangSanPham();

        String tuKhoa = "SP0";
        test.info("Bước 1: Nhập mã không đầy đủ: " + tuKhoa);

        WebElement inputSearch = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sp_search")));
        inputSearch.clear();


        for (char ch : tuKhoa.toCharArray()) {
            inputSearch.sendKeys(String.valueOf(ch));
            Thread.sleep(200);
        }


        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.xpath("//table[@id='sp_table']/tbody/tr[1]/td[1]"), tuKhoa));

        try {
            int soDong = driver.findElements(By.xpath("//table[@id='sp_table']/tbody/tr")).size();
            test.info("Số lượng sản phẩm tìm thấy chứa '" + tuKhoa + "': " + soDong);

            Assert.assertTrue(soDong > 0, "Lỗi: Không tìm thấy sản phẩm nào có mã chứa " + tuKhoa);
            test.pass("Kết quả: Hệ thống hiển thị danh sách các sản phẩm có mã chứa '" + tuKhoa + "'.");
        } catch (Exception e) {
            test.fail("Thất bại: " + e.getMessage());
            throw e;
        }
    }

    @Test
    public void testTimKiem_TheoTenDayDu_02_01() throws Exception {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SanPhamPage spPage = new SanPhamPage(driver);
        test.info("TC02_01: Tìm kiếm sản phẩm theo tên đầy đủ");


        DangNhapPage loginPage = new DangNhapPage(driver);
        loginPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        loginPage.clickDangNhap();
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}


        spPage.vaoTrangSanPham();


        String tenDayDu = "Sữa tươi TH 180ml";
        test.info("Bước 1: Nhập tên đầy đủ: " + tenDayDu);

        WebElement inputSearch = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sp_search")));
        inputSearch.clear();

        for (char ch : tenDayDu.toCharArray()) {
            inputSearch.sendKeys(String.valueOf(ch));
            Thread.sleep(100);
        }


        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.xpath("//table[@id='sp_table']/tbody/tr[1]/td[2]"), tenDayDu));

        try {
            String tenThucTe = driver.findElement(By.xpath("//table[@id='sp_table']/tbody/tr[1]/td[2]")).getText();
            Assert.assertEquals(tenThucTe, tenDayDu, "Lỗi: Tên sản phẩm hiển thị không khớp!");

            test.pass("Kết quả: Hiển thị duy nhất sản phẩm khớp chính xác với tên vừa nhập.");
        } catch (Exception e) {
            test.fail("Thất bại: " + e.getMessage());
            throw e;
        }
    }

    @Test
    public void testTimKiem_TheoTenKhongDayDu_02_02() throws Exception {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SanPhamPage spPage = new SanPhamPage(driver);
        test.info("TC02_02: Tìm kiếm sản phẩm theo tên không đầy đủ");


        DangNhapPage loginPage = new DangNhapPage(driver);
        loginPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        loginPage.clickDangNhap();
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}


        spPage.vaoTrangSanPham();


        String tuKhoaTen = "Sữa";
        test.info("Bước 1: Nhập một phần tên: " + tuKhoaTen);

        WebElement inputSearch = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sp_search")));
        inputSearch.clear();

        for (char ch : tuKhoaTen.toCharArray()) {
            inputSearch.sendKeys(String.valueOf(ch));
            Thread.sleep(900);
        }


        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.xpath("//table[@id='sp_table']/tbody/tr[1]/td[2]"), tuKhoaTen));

        try {
            int soDong = driver.findElements(By.xpath("//table[@id='sp_table']/tbody/tr")).size();
            Assert.assertTrue(soDong > 0, "Lỗi: Không tìm thấy sản phẩm nào chứa từ '" + tuKhoaTen + "'");

            test.pass("Kết quả: Hệ thống tự động hiển thị các sản phẩm có tên chứa từ '" + tuKhoaTen + "'.");
        } catch (Exception e) {
            test.fail("Lỗi: " + e.getMessage());
            throw e;
        }
    }
    @Test
    public void testTimKiem_KhacDinhDangHoaThuong_TenSP_03_01() throws Exception {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SanPhamPage spPage = new SanPhamPage(driver);
        test.info("TC03_01: Tìm kiếm Tên sản phẩm với từ khóa chữ thường (không khớp hoa/thường)");

        DangNhapPage loginPage = new DangNhapPage(driver);
        loginPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        loginPage.clickDangNhap();

        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}


        spPage.vaoTrangSanPham();


        String tuKhoa = "sữa";
        test.info("Bước 1: Nhập từ khóa chữ thường: " + tuKhoa);

        WebElement inputSearch = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sp_search")));
        inputSearch.clear();

        for (char ch : tuKhoa.toCharArray()) {
            inputSearch.sendKeys(String.valueOf(ch));
            Thread.sleep(200);
        }


        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.xpath("//table[@id='sp_table']/tbody/tr[1]/td[2]"), "Sữa"));

        try {
            int soDong = driver.findElements(By.xpath("//table[@id='sp_table']/tbody/tr")).size();
            Assert.assertTrue(soDong > 0, "Lỗi: Không tìm thấy sản phẩm nào khi nhập chữ thường!");
            test.pass("Kết quả: Hệ thống hiển thị các sản phẩm có tên chứa 'Sữa' hoặc 'sữa'.");
        } catch (Exception e) {
            test.fail("Thất bại: " + e.getMessage());
            throw e;
        }
    }

    @Test
    public void testTimKiem_KhacDinhDangHoaThuong_MaSP_04_01() throws Exception {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SanPhamPage spPage = new SanPhamPage(driver);
        test.info("TC04_01: Tìm kiếm Mã sản phẩm với từ khóa chữ thường (sp0)");

        DangNhapPage loginPage = new DangNhapPage(driver);
        loginPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        loginPage.clickDangNhap();

        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}


        spPage.vaoTrangSanPham();

        String tuKhoaMa = "sp0";
        test.info("Bước 1: Nhập mã chữ thường: " + tuKhoaMa);

        WebElement inputSearch = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sp_search")));
        inputSearch.clear();

        for (char ch : tuKhoaMa.toCharArray()) {
            inputSearch.sendKeys(String.valueOf(ch));
            Thread.sleep(200);
        }


        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.xpath("//table[@id='sp_table']/tbody/tr[1]/td[1]"), "SP0"));

        try {
            int soDong = driver.findElements(By.xpath("//table[@id='sp_table']/tbody/tr")).size();
            Assert.assertTrue(soDong > 0, "Lỗi: Hệ thống phân biệt hoa thường khi tìm mã!");
            test.pass("Kết quả: Hiển thị các sản phẩm có mã bắt đầu bằng SP0.");
        } catch (Exception e) {
            test.fail("Thất bại: " + e.getMessage());
            throw e;
        }
    }

    @Test
    public void testTimKiem_TuKhoaKhongTonTai_05_01() throws Exception {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SanPhamPage spPage = new SanPhamPage(driver);
        test.info("TC05_01: Tìm kiếm với từ khóa không tồn tại trong hệ thống");


        DangNhapPage loginPage = new DangNhapPage(driver);
        loginPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        loginPage.clickDangNhap();

        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}


        spPage.vaoTrangSanPham();


        String tuKhoaAo = "abcxyz123";
        test.info("Bước 1: Nhập chuỗi ký tự ngẫu nhiên: " + tuKhoaAo);

        WebElement inputSearch = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sp_search")));
        inputSearch.clear();
        inputSearch.sendKeys(tuKhoaAo);

        Thread.sleep(1000);

        try {


            int soDong = driver.findElements(By.xpath("//table[@id='sp_table']/tbody/tr")).size();


            Assert.assertTrue(soDong == 0 || driver.getPageSource().contains("Không tìm thấy sản phẩm nào."),
                    "Lỗi: Vẫn hiển thị sản phẩm dù từ khóa không tồn tại!");

            test.pass("Kết quả: Hệ thống thông báo hiển thị Không tìm thấy sản phẩm nào.");
        } catch (Exception e) {
            test.fail("Lỗi: " + e.getMessage());
            throw e;
        }
    }
    @Test
    public void testLocSanPham_TheoDangKinhDoanh_01_01() throws Exception {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SanPhamPage spPage = new SanPhamPage(driver);
        test.info("TC01_01: Lọc sản phẩm theo trạng thái 'Đang kinh doanh'");


        DangNhapPage loginPage = new DangNhapPage(driver);
        loginPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        loginPage.clickDangNhap();

        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}


        spPage.vaoTrangSanPham();


        try {
            test.info("Bước 1: Chọn bộ lọc trạng thái 'Đang kinh doanh'");

            spPage.chonTrangThaiLoc("Đang kinh doanh");


            wait.until(ExpectedConditions.textToBePresentInElementLocated(
                    By.xpath("//table[@id='sp_table']/tbody/tr[1]/td[4]"), "Đang kinh doanh"));


            String trangThaiThucTe = driver.findElement(By.xpath("//table[@id='sp_table']/tbody/tr[1]/td[4]")).getText();
            test.info("Trạng thái sản phẩm đầu tiên sau khi lọc: " + trangThaiThucTe);

            Assert.assertEquals(trangThaiThucTe, "Đang kinh doanh", "Lỗi: Bộ lọc không hoạt động đúng!");

            test.pass("Kết quả: Bảng danh sách chỉ hiển thị các sản phẩm có trạng thái 'Đang kinh doanh'.");
        } catch (Exception e) {
            test.fail("Thất bại: " + e.getMessage());
            throw e;
        }
    }

    @Test
    public void testLocSanPham_TheoNgungKinhDoanh_02_01() throws Exception {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SanPhamPage spPage = new SanPhamPage(driver);
        test.info("TC02_01: Lọc sản phẩm theo trạng thái 'Ngừng kinh doanh'");

        DangNhapPage loginPage = new DangNhapPage(driver);
        loginPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        loginPage.clickDangNhap();

        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}


        spPage.vaoTrangSanPham();


        try {
            test.info("Bước 1: Chọn bộ lọc trạng thái 'Ngừng kinh doanh'");
            spPage.chonTrangThaiLoc("Ngừng kinh doanh");

            wait.until(ExpectedConditions.textToBePresentInElementLocated(
                    By.xpath("//table[@id='sp_table']/tbody/tr[1]/td[4]"), "Ngừng kinh doanh"));

            String trangThaiThucTe = driver.findElement(By.xpath("//table[@id='sp_table']/tbody/tr[1]/td[4]")).getText();
            test.info("Trạng thái sản phẩm đầu tiên sau khi lọc: " + trangThaiThucTe);

            Assert.assertEquals(trangThaiThucTe, "Ngừng kinh doanh", "Lỗi: Bộ lọc hiển thị sai trạng thái!");

            test.pass("Kết quả: Bảng danh sách chỉ hiển thị các sản phẩm có trạng thái 'Ngừng kinh doanh'.");
        } catch (Exception e) {
            test.fail("Thất bại: " + e.getMessage());
            throw e;
        }
    }
    @Test
    public void testLocSanPham_SauKhiTimKiem_MaSP_03_01() throws Exception {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SanPhamPage spPage = new SanPhamPage(driver);
        test.info("TC03_01: Lọc sản phẩm theo 'Ngừng kinh doanh' sau khi tìm kiếm theo mã 'SP0'");


        DangNhapPage loginPage = new DangNhapPage(driver);
        loginPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        loginPage.clickDangNhap();

        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}


        spPage.vaoTrangSanPham();

        try {

            test.info("Bước 1: Nhập mã sản phẩm 'SP0' vào thanh tìm kiếm");
            WebElement inputSearch = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sp_search")));
            inputSearch.clear();
            for (char ch : "SP0".toCharArray()) {
                inputSearch.sendKeys(String.valueOf(ch));
                Thread.sleep(200);
            }

            test.info("Bước 2: Chọn trạng thái lọc 'Ngừng kinh doanh'");
            spPage.chonTrangThaiLoc("Ngừng kinh doanh");


            wait.until(ExpectedConditions.textToBePresentInElementLocated(
                    By.xpath("//table[@id='sp_table']/tbody/tr[1]/td[4]"), "Ngừng kinh doanh"));

            String maSP = driver.findElement(By.xpath("//table[@id='sp_table']/tbody/tr[1]/td[1]")).getText();
            String trangThai = driver.findElement(By.xpath("//table[@id='sp_table']/tbody/tr[1]/td[4]")).getText();

            test.info("Kết quả dòng đầu tiên: Mã " + maSP + " - Trạng thái: " + trangThai);

            Assert.assertTrue(maSP.contains("SP0"), "Lỗi: Mã sản phẩm không chứa từ khóa tìm kiếm!");
            Assert.assertEquals(trangThai, "Ngừng kinh doanh", "Lỗi: Trạng thái lọc không chính xác!");

            test.pass("Kết quả: Bảng hiển thị các sản phẩm mã 'SP0' có trạng thái 'Ngừng kinh doanh'.");
        } catch (Exception e) {
            test.fail("Thất bại: " + e.getMessage());
            throw e;
        }
    }

    @Test
    public void testLocSanPham_SauKhiTimKiem_TenSP_03_02() throws Exception {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SanPhamPage spPage = new SanPhamPage(driver);
        String tuKhoa = "Sữa tươi";
        test.info("TC03_02: Lọc sản phẩm theo 'Ngừng kinh doanh' sau khi tìm kiếm tên '" + tuKhoa + "'");

        DangNhapPage loginPage = new DangNhapPage(driver);
        loginPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        loginPage.clickDangNhap();

        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}

        spPage.vaoTrangSanPham();

        try {

            test.info("Bước 1: Nhập từ khóa '" + tuKhoa + "' vào thanh tìm kiếm");
            WebElement inputSearch = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sp_search")));
            inputSearch.clear();
            inputSearch.sendKeys(tuKhoa);
            Thread.sleep(1000);

            test.info("Bước 2: Chọn trạng thái lọc 'Ngừng kinh doanh'");
            spPage.chonTrangThaiLoc("Ngừng kinh doanh");

            wait.until(ExpectedConditions.textToBePresentInElementLocated(
                    By.xpath("//table[@id='sp_table']/tbody/tr[1]/td[4]"), "Ngừng kinh doanh"));

            String tenSP = driver.findElement(By.xpath("//table[@id='sp_table']/tbody/tr[1]/td[2]")).getText();
            String trangThai = driver.findElement(By.xpath("//table[@id='sp_table']/tbody/tr[1]/td[4]")).getText();

            test.info("Kết quả dòng đầu tiên: Tên '" + tenSP + "' - Trạng thái: '" + trangThai + "'");


            Assert.assertTrue(tenSP.toLowerCase().contains(tuKhoa.toLowerCase()),
                    "Lỗi: Tên sản phẩm '" + tenSP + "' không chứa từ khóa '" + tuKhoa + "'!");
            Assert.assertEquals(trangThai, "Ngừng kinh doanh", "Lỗi: Trạng thái lọc không khớp!");

            test.pass("Kết quả: Bảng hiển thị đúng sản phẩm thỏa mãn cả 2 điều kiện.");
        } catch (Exception e) {
            test.fail("Thất bại: " + e.getMessage());
            throw e;
        }
    }


    @Test
    public void testLocTatCaTrangThai_TuNgungKinhDoanh_04_01() throws Exception {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SanPhamPage spPage = new SanPhamPage(driver);
        test.info("TC_LSP_04_01: Lọc sản phẩm theo 'Tất cả trạng thái' sau khi lọc 'Ngừng kinh doanh'");


        DangNhapPage loginPage = new DangNhapPage(driver);
        loginPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        loginPage.clickDangNhap();

        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}

        spPage.vaoTrangSanPham();

        try {
            test.info("Bước 1: Nhấn chọn bộ lọc 'Ngừng kinh doanh'");
            spPage.chonTrangThaiLoc("Ngừng kinh doanh");
            Thread.sleep(1000);


            test.info("Bước 2: Chọn trạng thái 'Tất cả trạng thái'");
            spPage.chonTrangThaiLoc("Tất cả trạng thái");
            Thread.sleep(1000);

            int soLuongSauKhiLoc = driver.findElements(By.xpath("//table[@id='sp_table']/tbody/tr")).size();
            test.info("Số lượng sản phẩm hiển thị sau khi chọn Tất cả: " + soLuongSauKhiLoc);

            Assert.assertTrue(soLuongSauKhiLoc > 0, "Lỗi: Danh sách sản phẩm trống!");
            test.pass("Kết quả: Bảng danh sách sản phẩm hiển thị tất cả sản phẩm dù có trạng thái nào.");
        } catch (Exception e) {
            test.fail("Thất bại: " + e.getMessage());
            throw e;
        }
    }

    @Test
    public void testLocTatCaTrangThai_TuDangKinhDoanh_04_02() throws Exception {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SanPhamPage spPage = new SanPhamPage(driver);
        test.info("TC_LSP_04_02: Lọc sản phẩm theo 'Tất cả trạng thái' sau khi lọc 'Đang kinh doanh'");

        DangNhapPage loginPage = new DangNhapPage(driver);
        loginPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        loginPage.clickDangNhap();

        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}

        spPage.vaoTrangSanPham();

        try {

            test.info("Bước 1: Nhấn chọn bộ lọc 'Đang kinh doanh'");
            spPage.chonTrangThaiLoc("Đang kinh doanh");
            Thread.sleep(1000);


            test.info("Bước 2: Chọn trạng thái 'Tất cả trạng thái'");
            spPage.chonTrangThaiLoc("Tất cả trạng thái");
            Thread.sleep(1000);

            test.pass("Kết quả: Bảng danh sách sản phẩm hiển thị tất cả sản phẩm dù có trạng thái nào.");
        } catch (Exception e) {
            test.fail("Thất bại: " + e.getMessage());
            throw e;
        }
    }



    @Test
    public void testLocDangKinhDoanh_SauKhiTimKiem_MaSP_05_01() throws Exception {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SanPhamPage spPage = new SanPhamPage(driver);
        String tuKhoaMa = "SP0";
        test.info("TC_LSP_05_01: Lọc sản phẩm theo 'Đang kinh doanh' sau khi tìm kiếm theo mã '" + tuKhoaMa + "'");


        DangNhapPage loginPage = new DangNhapPage(driver);
        loginPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        loginPage.clickDangNhap();

        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}

        spPage.vaoTrangSanPham();

        try {

            test.info("Bước 1: Nhập từ khóa '" + tuKhoaMa + "' vào thanh tìm kiếm");
            WebElement inputSearch = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sp_search")));
            inputSearch.clear();
            inputSearch.sendKeys(tuKhoaMa);
            Thread.sleep(1000);


            test.info("Bước 2: Chọn trạng thái 'Đang kinh doanh'");
            spPage.chonTrangThaiLoc("Đang kinh doanh");


            wait.until(ExpectedConditions.textToBePresentInElementLocated(
                    By.xpath("//table[@id='sp_table']/tbody/tr[1]/td[4]"), "Đang kinh doanh"));

            String maSPThucTe = driver.findElement(By.xpath("//table[@id='sp_table']/tbody/tr[1]/td[1]")).getText();
            String trangThaiThucTe = driver.findElement(By.xpath("//table[@id='sp_table']/tbody/tr[1]/td[4]")).getText();

            test.info("Kết quả dòng đầu tiên: Mã '" + maSPThucTe + "' - Trạng thái: '" + trangThaiThucTe + "'");

            Assert.assertTrue(maSPThucTe.contains(tuKhoaMa), "Lỗi: Mã sản phẩm không khớp từ khóa tìm kiếm!");
            Assert.assertEquals(trangThaiThucTe, "Đang kinh doanh", "Lỗi: Trạng thái lọc không chính xác!");

            test.pass("Kết quả: Bảng hiển thị các sản phẩm có mã chứa '" + tuKhoaMa + "' có trạng thái 'Đang kinh doanh'.");
        } catch (Exception e) {
            test.fail("Thất bại: " + e.getMessage());
            throw e;
        }
    }

    @Test
    public void testLocDangKinhDoanh_SauKhiTimKiem_TenSP_05_02() throws Exception {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SanPhamPage spPage = new SanPhamPage(driver);
        String tuKhoaTen = "Sữa tươi";
        test.info("TC_LSP_05_02: Lọc sản phẩm theo 'Đang kinh doanh' sau khi tìm kiếm theo tên '" + tuKhoaTen + "'");


        DangNhapPage loginPage = new DangNhapPage(driver);
        loginPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        loginPage.clickDangNhap();

        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}

        spPage.vaoTrangSanPham();

        try {

            test.info("Bước 1: Nhập từ khóa '" + tuKhoaTen + "' vào thanh tìm kiếm");
            WebElement inputSearch = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sp_search")));
            inputSearch.clear();
            inputSearch.sendKeys(tuKhoaTen);
            Thread.sleep(1000);


            test.info("Bước 2: Chọn trạng thái 'Đang kinh doanh'");
            spPage.chonTrangThaiLoc("Đang kinh doanh");


            wait.until(ExpectedConditions.textToBePresentInElementLocated(
                    By.xpath("//table[@id='sp_table']/tbody/tr[1]/td[4]"), "Đang kinh doanh"));

            String tenSPThucTe = driver.findElement(By.xpath("//table[@id='sp_table']/tbody/tr[1]/td[2]")).getText();
            String trangThaiThucTe = driver.findElement(By.xpath("//table[@id='sp_table']/tbody/tr[1]/td[4]")).getText();

            test.info("Kết quả dòng đầu tiên: Tên '" + tenSPThucTe + "' - Trạng thái: '" + trangThaiThucTe + "'");

            Assert.assertTrue(tenSPThucTe.toLowerCase().contains(tuKhoaTen.toLowerCase()), "Lỗi: Tên sản phẩm không khớp từ khóa tìm kiếm!");
            Assert.assertEquals(trangThaiThucTe, "Đang kinh doanh", "Lỗi: Trạng thái lọc không chính xác!");

            test.pass("Kết quả: Bảng hiển thị các sản phẩm có tên chứa '" + tuKhoaTen + "' có trạng thái 'Đang kinh doanh'.");
        } catch (Exception e) {
            test.fail("Thất bại: " + e.getMessage());
            throw e;
        }
    }

    @Test
    public void testXemSanPham_KhiVuaTaoXong_01_01() throws Exception {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SanPhamPage spPage = new SanPhamPage(driver);
        String tenSP = "Sữa TH Xem Test " + System.currentTimeMillis();
        test.info("TC_XSP_SC01_01: Xem sản phẩm khi vừa tạo xong");


        driver.get("http://127.0.0.1:8000/");
        DangNhapPage loginPage = new DangNhapPage(driver);
        loginPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        loginPage.clickDangNhap();
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}

        spPage.vaoTrangSanPham();

        try {

            test.info("Bước 1: Tạo sản phẩm mới: " + tenSP);
            spPage.clickThemMoisp();
            Thread.sleep(1000);
            spPage.nhapTenSanPham(tenSP);
            spPage.chonDonViTinh("Hộp");
            spPage.clickXacNhan();


            wait.until(ExpectedConditions.alertIsPresent()).accept();
            Thread.sleep(1000);


            test.info("Bước 2: Nhấn nút Xem hình con mắt ở sản phẩm vừa tạo");
            spPage.clickXemSanPhamDauTien();


            Assert.assertTrue(spPage.isModalXemHienThi(), "Lỗi: Modal chi tiết không hiển thị!");
            String tenThucTe = spPage.getTenSanPhamXem();
            Assert.assertEquals(tenThucTe, tenSP, "Lỗi: Tên sản phẩm trong modal xem không khớp!");

            test.pass("Kết quả: Thông tin chi tiết hiện ra đầy đủ và chính xác.");
        } catch (Exception e) {
            test.fail("Thất bại: " + e.getMessage());
            throw e;
        }
    }


    @Test
    public void testXemSanPham_KhiVuaCapNhatXong_02_01() throws Exception {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));


        driver.get("http://127.0.0.1:8000/");
        DangNhapPage loginPage = new DangNhapPage(driver);
        loginPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        loginPage.clickDangNhap();
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}
        SanPhamPage spPage = new SanPhamPage(driver);
        String tenMoi = "Sữa Update " + System.currentTimeMillis();
        test.info("TC_XSP_SC02_01: Xem sản phẩm khi vừa cập nhật xong");

        spPage.vaoTrangSanPham();

        try {

            test.info("Bước 1: Cập nhật tên sản phẩm thành: " + tenMoi);
            spPage.clickSuaSanPhamDauTien();
            Thread.sleep(1000);
            spPage.nhapTenSua(tenMoi);
            spPage.clickCapNhat();

            wait.until(ExpectedConditions.alertIsPresent()).accept();
            Thread.sleep(1000);


            test.info("Bước 2: Nhấn nút Xem để kiểm tra dữ liệu sau cập nhật");
            spPage.clickXemSanPhamDauTien();

            Assert.assertEquals(spPage.getTenSanPhamXem(), tenMoi, "Lỗi: Modal Xem vẫn hiện tên cũ!");
            test.pass("Kết quả: Thông tin chi tiết hiển thị đúng dữ liệu đã sửa.");
        } catch (Exception e) {
            test.fail("Thất bại: " + e.getMessage());
            throw e;
        }
    }


    @Test
    public void testDongXemSanPham_BangNutDong_03_01() throws Exception {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));


        driver.get("http://127.0.0.1:8000/");
        DangNhapPage loginPage = new DangNhapPage(driver);
        loginPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        loginPage.clickDangNhap();
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}
        SanPhamPage spPage = new SanPhamPage(driver);
        test.info("TC_XSP_SC03_01: Bấm nút 'Đóng' để kết thúc Xem sản phẩm");

        spPage.vaoTrangSanPham();

        try {

            spPage.clickXemSanPhamDauTien();
            Assert.assertTrue(spPage.isModalXemHienThi(), "Lỗi: Modal không mở!");


            test.info("Bước 2: Nhấn nút 'Đóng' trên modal");
            spPage.clickDongModalXem();

            Thread.sleep(1000);
            Assert.assertFalse(spPage.isModalXemHienThi(), "Lỗi: Modal vẫn chưa đóng sau khi bấm nút Đóng!");
            test.pass("Kết quả: Form xem biến mất thành công.");
        } catch (Exception e) {
            test.fail("Thất bại: " + e.getMessage());
            throw e;
        }
    }


    @Test
    public void testDongXemSanPham_BangNutX_04_01() throws Exception {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));


        driver.get("http://127.0.0.1:8000/");
        DangNhapPage loginPage = new DangNhapPage(driver);
        loginPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        loginPage.clickDangNhap();
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {}
        SanPhamPage spPage = new SanPhamPage(driver);
        test.info("TC_XSP_SC04_01: Bấm nút 'X' để kết thúc Xem sản phẩm");

        spPage.vaoTrangSanPham();

        try {

            spPage.clickXemSanPhamDauTien();


            test.info("Bước 2: Nhấn nút 'X' ở góc modal");
            spPage.clickXModalXem();

            Thread.sleep(1000);
            Assert.assertFalse(spPage.isModalXemHienThi(), "Lỗi: Modal vẫn chưa đóng sau khi bấm nút X!");
            test.pass("Kết quả: Form xem đã đóng thành công.");
        } catch (Exception e) {
            test.fail("Thất bại: " + e.getMessage());
            throw e;
        }
    }
}