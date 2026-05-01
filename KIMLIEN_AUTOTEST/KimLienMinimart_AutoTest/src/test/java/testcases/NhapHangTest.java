package testcases;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import base.BaseTest;
import base.TestListener;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.DangNhapPage;
import pages.NhapHangPage;

import java.util.List;

@Listeners(TestListener.class)
public class NhapHangTest extends BaseTest {
    NhapHangPage nhapHangPage;
    DangNhapPage dangNhapPage;

    @BeforeMethod
    public void setUpTest() {
        dangNhapPage = new DangNhapPage(driver);
        nhapHangPage = new NhapHangPage(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        dangNhapPage.nhapThongTinDangNhap("kimlien", "kimlien123");
        dangNhapPage.clickDangNhap();

        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();

        wait.until(ExpectedConditions.urlContains("/dashboard/"));

        driver.get("http://127.0.0.1:8000/nhap-hang/");
    }

    @Test(description = "FUN_TPN_SC01_01: Tạo phiếu nhập thành công với 1 lô hàng")
    public void testTaoPhieuNhapThanhCong_1LoHang() {
        nhapHangPage.clickTaoPhieuMoi();

        String maPhieuTuSinh = nhapHangPage.layMaPhieuTuDong();
        System.out.println("-> Đang chạy test cho Mã phiếu: " + maPhieuTuSinh);

        nhapHangPage.nhapNgay("2026-05-24");
        nhapHangPage.chonNhaCungCap("Công ty CP Acecook Việt Nam");

        nhapHangPage.chonSanPham("Mì Hảo Hảo tôm chua cay");
        nhapHangPage.nhapThongTinLoHang("500", "2027-10-03", "3000");
        nhapHangPage.clickThemLoHang();

        nhapHangPage.clickLuuPhieuNhap();
        String thongBao = nhapHangPage.xacNhanThongBaoAlert();
        Assert.assertEquals(thongBao, "Tạo phiếu thành công!", "Lỗi: Popup thông báo sai!");

        List<String> duLieuBang = nhapHangPage.layDuLieuPhieuTuBang(maPhieuTuSinh);

        Assert.assertEquals(duLieuBang.get(0), maPhieuTuSinh, "Mã phiếu không hiển thị đúng!");
        Assert.assertEquals(duLieuBang.get(4), "Chờ kiểm tra", "Trạng thái phiếu mới tạo không nằm ở Chờ kiểm tra!");
    }

    @Test(description = "FUN_TPN_SC01_02 + FUN_TPN_SC01_03: Tạo phiếu nhập với nhiều lô hàng + Xóa bớt 1 lô trước khi lưu")
    public void testTaoPhieuNhap_NhieuLoHang_XoaBo1Lo() {
        nhapHangPage.clickTaoPhieuMoi();

        String maPhieuTuSinh = nhapHangPage.layMaPhieuTuDong();
        System.out.println("-> Đang chạy test cho Mã phiếu: " + maPhieuTuSinh);

        nhapHangPage.nhapNgay("2026-05-24");
        nhapHangPage.chonNhaCungCap("Công ty CP Acecook Việt Nam");

        // Thêm lô hàng 1: 400 * 3.000 = 1.200.000
        nhapHangPage.chonSanPham("Mì Hảo Hảo tôm chua cay");
        nhapHangPage.nhapThongTinLoHang("400", "2027-03-03", "3000");
        nhapHangPage.clickThemLoHang();

        // Thêm lô hàng 2: 400 * 12.000 = 4.800.000
        nhapHangPage.chonSanPham("Xúc xích Vissan tiệt trùng");
        nhapHangPage.nhapThongTinLoHang("400", "2027-03-20", "12000");
        nhapHangPage.clickThemLoHang();

        //Xóa lô hàng 2: -4.800.000
        WebElement btnXoa = driver.findElement(By.xpath("//tbody[@id='temp_sp_list']/tr[2]//button"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnXoa);

        nhapHangPage.clickLuuPhieuNhap();
        String thongBao = nhapHangPage.xacNhanThongBaoAlert();
        Assert.assertEquals(thongBao, "Tạo phiếu thành công!", "Lỗi: Popup thông báo sai!");

        List<String> duLieuBang = nhapHangPage.layDuLieuPhieuTuBang(maPhieuTuSinh);
        Assert.assertEquals(duLieuBang.get(3), "1.200.000đ", "Tổng tiền phiếu nhập nhiều lô hàng bị tính sai!");
        Assert.assertEquals(duLieuBang.get(0), maPhieuTuSinh, "Mã phiếu không hiển thị đúng!");
        Assert.assertEquals(duLieuBang.get(4), "Chờ kiểm tra", "Trạng thái phiếu mới tạo không nằm ở Chờ kiểm tra!");
    }

    @Test(description = "FUN_TPN_SC02_01: Bỏ trống Ngày nhập")
    public void testBoTrongNgayNhap() {
        nhapHangPage.clickTaoPhieuMoi();

        nhapHangPage.chonNhaCungCap("Công ty TNHH Vina Acecook");

        nhapHangPage.chonSanPham("Mì Hảo Hảo tôm chua cay");
        nhapHangPage.nhapThongTinLoHang("100", "2027-11-03", "12000");
        nhapHangPage.clickThemLoHang();

        nhapHangPage.clickLuuPhieuNhap();

        String thongBaoLoi = nhapHangPage.xacNhanThongBaoAlert();
        Assert.assertEquals(thongBaoLoi, "Vui lòng nhập đủ thông tin Phiếu nhập!", "Hệ thống không chặn lỗi bỏ trống Ngày nhập!");
    }

    @Test(description = "FUN_TPN_SC03_01: Bỏ trống danh sách lô hàng")
    public void testDanhsachLohangTrong() {
        nhapHangPage.clickTaoPhieuMoi();

        nhapHangPage.nhapNgay("2026-06-24");
        nhapHangPage.chonNhaCungCap("Công ty TNHH Vina Acecook");

        nhapHangPage.clickLuuPhieuNhap();

        String thongBaoLoi = nhapHangPage.xacNhanThongBaoAlert();
        Assert.assertEquals(thongBaoLoi, "Vui lòng thêm sản phẩm!", "Hệ thống không chặn lỗi bỏ trống danh sách lô hàng!");
    }

    @Test(description = "FUN_TPN_SC04_01: Bỏ trống Tên sản phẩm khi thêm lô")
    public void testBoTrongTenSanPhamKhiThemLo() {
        nhapHangPage.clickTaoPhieuMoi();

        nhapHangPage.nhapThongTinLoHang("100", "2027-11-03", "12000");
        nhapHangPage.clickThemLoHang();

        String thongBaoLoi = nhapHangPage.xacNhanThongBaoAlert();
        Assert.assertEquals(thongBaoLoi, "Vui lòng nhập đủ thông tin Lô hàng!", "Hệ thống không chặn lỗi bỏ trống Tên sản phẩm!");
    }

    @Test(description = "FUN_TPN_SC04_02: Nhập Số lượng và Đơn giá <= 0")
    public void testNhapSoLuongVaDonGiaAm() {
        nhapHangPage.clickTaoPhieuMoi();

        nhapHangPage.chonSanPham("Mì Hảo Hảo tôm chua cay");
        nhapHangPage.nhapThongTinLoHang("-5", "2026-10-24", "-10000");
        nhapHangPage.clickThemLoHang();

        String thongBaoLoi = nhapHangPage.xacNhanThongBaoAlert();
        Assert.assertEquals(thongBaoLoi, "Số lượng và Đơn giá phải lớn hơn 0!", "Hệ thống không chặn được giá trị âm!");
    }

    @Test(description = "FUN_TPN_SC05_01: Hủy tạo phiếu bằng nút Hủy")
    public void testHuyTaoPhieuBangNutHuy() {
        nhapHangPage.clickTaoPhieuMoi();
        String maPhieu = nhapHangPage.layMaPhieuTuDong();

        nhapHangPage.nhapNgay("2026-04-24");
        nhapHangPage.chonNhaCungCap("Công ty CP Acecook Việt Nam");
        nhapHangPage.chonSanPham("Mì Hảo Hảo tôm chua cay");
        nhapHangPage.nhapThongTinLoHang("200", "2027-05-03", "2500");
        nhapHangPage.clickThemLoHang();

        WebElement btnHuy = driver.findElement(By.xpath("//div[@id='addModal']//button[contains(text(),'Hủy')]"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnHuy);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("addModal")));

        Assert.assertTrue(driver.findElements(By.xpath("//table[@id='pn_table']/tbody/tr[td[1][text()='" + maPhieu + "']]")).isEmpty(),
                "Lỗi: Phiếu nhập vẫn xuất hiện trong bảng dù đã nhấn Hủy!");
    }

    @Test(description = "FUN_SPN_SC01_01: Kiểm tra tải dữ liệu cũ lên form sửa")
    public void testKiemTraDuLieuCuTrenFormSua() {
        String maPhieuCanSua = nhapHangPage.layMaPhieuChoKiemTraDauTien();
        Assert.assertNotNull(maPhieuCanSua, "Không tìm thấy phiếu nhập nào có trạng thái 'Chờ kiểm tra' để test!");

        System.out.println("-> Đang mở form sửa cho Mã phiếu: " + maPhieuCanSua);
        nhapHangPage.clickNutSuaCuaPhieu(maPhieuCanSua);

        String maPhieuTrenForm = nhapHangPage.layMaPhieuDangSua();
        Assert.assertEquals(maPhieuTrenForm, maPhieuCanSua, "Mã phiếu tải lên form sửa không khớp với phiếu đã chọn!");

        WebElement btnHuy = driver.findElement(By.xpath("//div[@id='editModal']//button[contains(text(),'Hủy')]"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnHuy);
    }

    @Test(description = "FUN_SPN_SC02_01: Chỉnh sửa các trường thông tin chung (Ngày, NCC, Trạng thái)")
    public void testChinhSuaThongTinChung() throws InterruptedException {
        String maPhieuCanSua = nhapHangPage.layMaPhieuChoKiemTraDauTien();
        Assert.assertNotNull(maPhieuCanSua, "Không tìm thấy phiếu nhập nào có trạng thái 'Chờ kiểm tra' để test!");

        nhapHangPage.clickNutSuaCuaPhieu(maPhieuCanSua);

        nhapHangPage.suaNgayNhap("2026-10-11");

        nhapHangPage.suaNhaCungCap("Công ty TNHH Quốc Tế Unilever VN");

        nhapHangPage.doiTrangThai("Hoàn thành");

        nhapHangPage.clickCapNhatPhieuNhap();

        String thongBao = nhapHangPage.xacNhanThongBaoAlert();
        Assert.assertEquals(thongBao, "Cập nhật thành công!", "Lỗi: Popup thông báo cập nhật sai!");

        Thread.sleep(1500);

        List<String> duLieuBang = nhapHangPage.layDuLieuPhieuTuBang(maPhieuCanSua);
        Assert.assertEquals(duLieuBang.get(2), "11/10/2026", "Ngày nhập chưa được cập nhật trên bảng!");
        Assert.assertEquals(duLieuBang.get(4), "Hoàn thành", "Trạng thái chưa được chuyển sang Hoàn thành!");
    }

    @Test(description = "FUN_SPN_SC02_03: Thêm lô hàng mới và xóa lô hàng cũ trong form sửa")
    public void testChinhSuaThemXoaLoHang() {
        String maPhieuCanSua = nhapHangPage.layMaPhieuChoKiemTraDauTien();
        Assert.assertNotNull(maPhieuCanSua, "Không tìm thấy phiếu nhập nào có trạng thái 'Chờ kiểm tra' để test!");

        nhapHangPage.clickNutSuaCuaPhieu(maPhieuCanSua);

        nhapHangPage.xoaLoHangTrongFormSua(1);

        nhapHangPage.themLoHangMoiTrongFormSua("Mì Hảo Hảo tôm chua cay", "100", "2027-01-01", "5000");

        nhapHangPage.clickCapNhatPhieuNhap();

        String thongBao = nhapHangPage.xacNhanThongBaoAlert();
        Assert.assertEquals(thongBao, "Cập nhật thành công!", "Lỗi: Popup thông báo cập nhật sai!");
    }
    @Test(description = "FUN_SPN_SC05_01: Xóa toàn bộ danh sách lô hàng khi sửa")
    public void testXoaToanBoLoHangKhiSua() {
        String maPhieuCanSua = nhapHangPage.layMaPhieuChoKiemTraDauTien();
        Assert.assertNotNull(maPhieuCanSua, "Không tìm thấy phiếu nhập nào có trạng thái 'Chờ kiểm tra' để test!");

        nhapHangPage.clickNutSuaCuaPhieu(maPhieuCanSua);

        List<WebElement> dsThungRac = driver.findElements(By.xpath("//tbody[@id='edit_sp_list']/tr/td[last()]"));
        for (int i = dsThungRac.size(); i >= 1; i--) {
            nhapHangPage.xoaLoHangTrongFormSua(i);
        }

        nhapHangPage.clickCapNhatPhieuNhap();

        String thongBaoLoi = nhapHangPage.xacNhanThongBaoAlert();
        Assert.assertEquals(thongBaoLoi, "Vui lòng thêm sản phẩm!", "Hệ thống không chặn lỗi khi lưu phiếu không có lô hàng!");
    }

}