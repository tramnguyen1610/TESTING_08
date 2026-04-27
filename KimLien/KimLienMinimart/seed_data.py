import os
import django
from datetime import date, timedelta
import random

# 1. CẤU HÌNH MÔI TRƯỜNG DJANGO
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'KimLienMinimart.settings')
django.setup()

from KimLienTester.models import NhaCungCap, SanPham, LoHang, PhieuNhap, ChiTietNhap


def seed_data():
    print("Đang dọn dẹp dữ liệu cũ...")
    ChiTietNhap.objects.all().delete()
    PhieuNhap.objects.all().delete()
    LoHang.objects.all().delete()
    SanPham.objects.all().delete()
    NhaCungCap.objects.all().delete()

    today = date.today()

    # --- 1. DỮ LIỆU NHÀ CUNG CẤP (15 dòng) ---
    print("Đang tạo 15 Nhà cung cấp...")
    ncc_data = [
        {"MaNCC": "NCC001", "TenNCC": "Công ty CP Sữa Việt Nam (Vinamilk)", "DChi": "Q.7, TP.HCM",
         "SDT": "02854155555"},
        {"MaNCC": "NCC002", "TenNCC": "Công ty CP Hàng Tiêu Dùng Masan", "DChi": "Q.1, TP.HCM", "SDT": "02862563862"},
        {"MaNCC": "NCC003", "TenNCC": "Công ty TNHH Nước Giải Khát Suntory Pepsico", "DChi": "Q.1, TP.HCM",
         "SDT": "19003127"},
        {"MaNCC": "NCC004", "TenNCC": "Công ty TNHH Nước Giải Khát Coca-Cola VN", "DChi": "Thủ Đức, TP.HCM",
         "SDT": "02838961000"},
        {"MaNCC": "NCC005", "TenNCC": "Công ty TNHH Quốc Tế Unilever VN", "DChi": "Q.7, TP.HCM", "SDT": "02854135686"},
        {"MaNCC": "NCC006", "TenNCC": "Công ty CP Tập Đoàn KIDO", "DChi": "Tân Bình, TP.HCM", "SDT": "02838153833"},
        {"MaNCC": "NCC007", "TenNCC": "Công ty CP Việt Nam Kỹ Nghệ Súc Sản (Vissan)", "DChi": "Bình Thạnh, TP.HCM",
         "SDT": "02835533999"},
        {"MaNCC": "NCC008", "TenNCC": "Công ty CP Thực Phẩm CJ Cầu Tre", "DChi": "Tân Phú, TP.HCM",
         "SDT": "02838150424"},
        {"MaNCC": "NCC009", "TenNCC": "Công ty CP Acecook Việt Nam", "DChi": "Tân Phú, TP.HCM", "SDT": "02838154064"},
        {"MaNCC": "NCC010", "TenNCC": "Công ty CP Tập Đoàn Trung Nguyên", "DChi": "Q.1, TP.HCM", "SDT": "19006061"},
        {"MaNCC": "NCC011", "TenNCC": "Công ty TNHH FrieslandCampina Việt Nam", "DChi": "Bình Dương",
         "SDT": "18001545"},
        {"MaNCC": "NCC012", "TenNCC": "Công ty TNHH URC Việt Nam", "DChi": "Bình Dương", "SDT": "02743767191"},
        {"MaNCC": "NCC013", "TenNCC": "Công ty CP Mondelez Kinh Đô", "DChi": "Hưng Yên", "SDT": "19001889"},
        {"MaNCC": "NCC014", "TenNCC": "Công ty CP Hàng Tiêu Dùng Cholimex", "DChi": "Bình Chánh, TP.HCM",
         "SDT": "02837653389"},
        {"MaNCC": "NCC015", "TenNCC": "Công ty TNHH Vina Acecook", "DChi": "Tân Phú, TP.HCM", "SDT": "02838154064"},
    ]

    ncc_objects = []
    for data in ncc_data:
        # 80% là Hợp tác, 20% là Ngừng hợp tác
        trang_thai = "Hợp tác" if random.random() < 0.8 else "Ngừng hợp tác"
        ncc = NhaCungCap.objects.create(
            MaNCC=data["MaNCC"],
            TenNCC=data["TenNCC"],
            DChi_NCC=data["DChi"],
            SDT_NCC=data["SDT"],
            TrangThai=trang_thai
        )
        ncc_objects.append(ncc)

    # --- 2. DỮ LIỆU SẢN PHẨM (15 dòng) ---
    print("Đang tạo 15 Sản phẩm...")
    sp_data = [
        {"MaSP": "SP001", "TenSP": "Sữa tươi Vinamilk 1L", "DVT": "Hộp", "TT": "Đang kinh doanh"},
        {"MaSP": "SP002", "TenSP": "Nước mắm Nam Ngư 500ml", "DVT": "Chai", "TT": "Đang kinh doanh"},
        {"MaSP": "SP003", "TenSP": "Mì Hảo Hảo tôm chua cay", "DVT": "Gói", "TT": "Đang kinh doanh"},
        {"MaSP": "SP004", "TenSP": "Nước giải khát Coca-Cola 330ml", "DVT": "Lon", "TT": "Đang kinh doanh"},
        {"MaSP": "SP005", "TenSP": "Dầu gội Clear Men 650g", "DVT": "Chai", "TT": "Đang kinh doanh"},
        {"MaSP": "SP006", "TenSP": "Nước giặt OMO Matic 3.6kg", "DVT": "Túi", "TT": "Ngừng kinh doanh"},
        {"MaSP": "SP007", "TenSP": "Bánh quy Cosy Marie 400g", "DVT": "Hộp", "TT": "Đang kinh doanh"},
        {"MaSP": "SP008", "TenSP": "Xúc xích Vissan tiệt trùng", "DVT": "Gói", "TT": "Đang kinh doanh"},
        {"MaSP": "SP009", "TenSP": "Nước tương Chinsu 250ml", "DVT": "Chai", "TT": "Đang kinh doanh"},
        {"MaSP": "SP010", "TenSP": "Cà phê G7 3in1", "DVT": "Hộp", "TT": "Đang kinh doanh"},
        {"MaSP": "SP011", "TenSP": "Sữa chua uống Yomost", "DVT": "Lốc", "TT": "Đang kinh doanh"},
        {"MaSP": "SP012", "TenSP": "Trà xanh Không Độ", "DVT": "Chai", "TT": "Đang kinh doanh"},
        {"MaSP": "SP013", "TenSP": "Bánh mì Sandwich Kinh Đô", "DVT": "Gói", "TT": "Đang kinh doanh"},
        {"MaSP": "SP014", "TenSP": "Tương ớt Cholimex 250ml", "DVT": "Chai", "TT": "Đang kinh doanh"},
        {"MaSP": "SP015", "TenSP": "Miến trộn Hảo Hảo", "DVT": "Ly", "TT": "Ngừng kinh doanh"},
    ]

    sp_objects = []
    for data in sp_data:
        sp = SanPham.objects.create(
            MaSP=data["MaSP"],
            TenSP=data["TenSP"],
            DVT=data["DVT"],
            TrangThai=data["TT"]
        )
        sp_objects.append(sp)

    # --- 3. DỮ LIỆU LÔ HÀNG (15 dòng) ---
    print("Đang tạo 15 Lô hàng...")
    lh_objects = []
    for i in range(1, 16):
        sp = sp_objects[i - 1]

        # Logic tạo HSD ngẫu nhiên
        if i in [2, 7, 12]:
            days_offset = random.randint(-10, -1)
            tt_lohang = "Đã hết hạn"
        elif i in [4, 9, 14]:
            days_offset = random.randint(5, 20)
            tt_lohang = "Sắp hết hạn"
        else:
            days_offset = random.randint(40, 365)
            tt_lohang = "Bình thường"

        hsd = today + timedelta(days=days_offset)

        # Đã XÓA trường SoLuong ở đây theo Model mới
        lh = LoHang.objects.create(
            MaLH=f"LH0{i:02d}",
            MaSP=sp,
            HSD=hsd,
            TrangThai=tt_lohang
        )
        lh_objects.append(lh)

    # --- 4 & 5. DỮ LIỆU PHIẾU NHẬP & CHI TIẾT NHẬP (15 dòng) ---
    print("Đang tạo 15 Phiếu nhập & Chi tiết nhập...")
    for i in range(1, 16):
        ncc = ncc_objects[i - 1]
        lh = lh_objects[i - 1]

        ngay_nhap = today - timedelta(days=random.randint(1, 100))

        # Chỉ sử dụng trạng thái Chờ kiểm tra hoặc Hoàn thành
        trang_thai_phieu = random.choice(["Chờ kiểm tra", "Hoàn thành"])

        pn = PhieuNhap.objects.create(
            MaPhieu=f"PN0{i:02d}",
            MaNCC=ncc,
            NgayNhap=ngay_nhap,
            TrangThai=trang_thai_phieu,
            TongTien=0  # Sẽ tính toán sau
        )

        sl_nhap = random.choice([20, 50, 100, 200, 500])
        don_gia = random.choice([15000, 25000, 50000, 120000, 200000])
        thanh_tien = sl_nhap * don_gia

        ChiTietNhap.objects.create(
            MaPhieu=pn,
            MaLH=lh,
            SoLuongNhap=sl_nhap,
            DonGiaNhap=don_gia,
            ThanhTien=thanh_tien
        )

        # Cập nhật tổng tiền cho phiếu nhập
        pn.TongTien = thanh_tien
        pn.save()

    print("==================================================")
    print("Khởi tạo dữ liệu thành công! Đã chèn 15 dòng cho mỗi bảng.")
    print("==================================================")


if __name__ == '__main__':
    seed_data()