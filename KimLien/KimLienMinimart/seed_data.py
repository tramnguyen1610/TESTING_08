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

    # --- 1. DỮ LIỆU NHÀ CUNG CẤP (15 dòng) ---
    print("Đang tạo 15 Nhà cung cấp...")
    ncc_data = [
        {"MaNCC": "NCC001", "TenNCC": "Công ty CP Sữa Việt Nam (Vinamilk)", "DChi": "Q.7, TP.HCM", "SDT": "02854155555"},
        {"MaNCC": "NCC002", "TenNCC": "Công ty CP Hàng Tiêu Dùng Masan", "DChi": "Q.1, TP.HCM", "SDT": "02862563862"},
        {"MaNCC": "NCC003", "TenNCC": "Công ty TNHH Nước Giải Khát Suntory Pepsico", "DChi": "Q.1, TP.HCM", "SDT": "19003127"},
        {"MaNCC": "NCC004", "TenNCC": "Công ty TNHH Nước Giải Khát Coca-Cola VN", "DChi": "Thủ Đức, TP.HCM", "SDT": "02838961000"},
        {"MaNCC": "NCC005", "TenNCC": "Công ty TNHH Quốc Tế Unilever VN", "DChi": "Q.7, TP.HCM", "SDT": "02854135686"},
        {"MaNCC": "NCC006", "TenNCC": "Công ty TNHH P&G Việt Nam", "DChi": "Bình Dương", "SDT": "18006988"},
        {"MaNCC": "NCC007", "TenNCC": "Công ty TNHH Nestlé Việt Nam", "DChi": "Đồng Nai", "SDT": "18006699"},
        {"MaNCC": "NCC008", "TenNCC": "Công ty CP Acecook Việt Nam", "DChi": "Tân Phú, TP.HCM", "SDT": "02838154064"},
        {"MaNCC": "NCC009", "TenNCC": "Công ty TNHH Thực Phẩm Orion Vina", "DChi": "Bình Dương", "SDT": "02743560231"},
        {"MaNCC": "NCC010", "TenNCC": "Công ty CP Tập Đoàn KIDO", "DChi": "Q.1, TP.HCM", "SDT": "02838270468"},
        {"MaNCC": "NCC011", "TenNCC": "Công ty CP Việt Nam Kỹ Nghệ Súc Sản (Vissan)", "DChi": "Bình Thạnh, TP.HCM", "SDT": "02835533999"},
        {"MaNCC": "NCC012", "TenNCC": "Công ty CP Chuỗi Thực Phẩm TH", "DChi": "Nghệ An", "SDT": "1800545440"},
        {"MaNCC": "NCC013", "Tổng Công ty Cổ phần Bia - Rượu - Nước giải khát Sài Gòn (Sabeco)": "Công ty CP Chuỗi Thực Phẩm TH", "DChi": "Q.5, TP.HCM", "SDT": "19001000"},
        {"MaNCC": "NCC014", "TenNCC": "Công ty TNHH Nhà Máy Bia Heineken VN", "DChi": "Q.1, TP.HCM", "SDT": "02838222755"},
        {"MaNCC": "NCC015", "TenNCC": "Công ty CP Thực phẩm Cholimex", "DChi": "Bình Chánh, TP.HCM", "SDT": "02837653389"},
    ]
    ncc_objects = []
    for data in ncc_data:
        # Sửa lỗi key ở NCC013 nếu bị (chắc chắn dùng TenNCC)
        ten_ncc = data.get("TenNCC", "Tổng Công ty CP Bia - Rượu - NGK Sài Gòn (Sabeco)")
        ncc = NhaCungCap.objects.create(MaNCC=data["MaNCC"], TenNCC=ten_ncc, DChi_NCC=data["DChi"], SDT_NCC=data["SDT"])
        ncc_objects.append(ncc)

    # --- 2. DỮ LIỆU SẢN PHẨM (15 dòng) ---
    print("Đang tạo 15 Sản phẩm...")
    sp_data = [
        {"MaSP": "SP001", "TenSP": "Sữa tươi Vinamilk 1L", "DVT": "Hộp", "TT": "Đang kinh doanh"},
        {"MaSP": "SP002", "TenSP": "Mì Hảo Hảo Tôm Chua Cay", "DVT": "Gói", "TT": "Đang kinh doanh"},
        {"MaSP": "SP003", "TenSP": "Nước tương Chinsu 250ml", "DVT": "Chai", "TT": "Đang kinh doanh"},
        {"MaSP": "SP004", "TenSP": "Nước ngọt Pepsi 320ml", "DVT": "Lon", "TT": "Đang kinh doanh"},
        {"MaSP": "SP005", "TenSP": "Nước ngọt Coca-Cola 320ml", "DVT": "Lon", "TT": "Đang kinh doanh"},
        {"MaSP": "SP006", "TenSP": "Nước giặt OMO Matic 3.6kg", "DVT": "Túi", "TT": "Đang kinh doanh"},
        {"MaSP": "SP007", "TenSP": "Dầu gội Head & Shoulders 850ml", "DVT": "Chai", "TT": "Ngừng kinh doanh"},
        {"MaSP": "SP008", "TenSP": "Cà phê hòa tan Nescafe 3in1", "DVT": "Hộp", "TT": "Đang kinh doanh"},
        {"MaSP": "SP009", "TenSP": "Mì ly Modern Lẩu Thái", "DVT": "Ly", "TT": "Đang kinh doanh"},
        {"MaSP": "SP010", "TenSP": "Bánh Chocopie hộp 12 cái", "DVT": "Hộp", "TT": "Đang kinh doanh"},
        {"MaSP": "SP011", "TenSP": "Kem Merino Đậu Xanh", "DVT": "Cây", "TT": "Hết hàng"},
        {"MaSP": "SP012", "TenSP": "Xúc xích Vissan Heo Tiệt Trùng", "DVT": "Gói", "TT": "Đang kinh doanh"},
        {"MaSP": "SP013", "TenSP": "Sữa chua uống TH True Yogurt", "DVT": "Chai", "TT": "Đang kinh doanh"},
        {"MaSP": "SP014", "TenSP": "Bia Tiger Bạc (Crystal) 330ml", "DVT": "Lon", "TT": "Đang kinh doanh"},
        {"MaSP": "SP015", "TenSP": "Tương ớt Cholimex 270g", "DVT": "Chai", "TT": "Đang kinh doanh"},
    ]
    sp_objects = []
    for data in sp_data:
        sp = SanPham.objects.create(MaSP=data["MaSP"], TenSP=data["TenSP"], DVT=data["DVT"], TrangThai=data["TT"])
        sp_objects.append(sp)

    # --- 3. DỮ LIỆU LÔ HÀNG (15 dòng) ---
    print("Đang tạo 15 Lô hàng (Có đúng 3 sản phẩm sắp hết hạn trong 30 ngày)...")
    lh_objects = []
    today = date.today()
    for i in range(1, 16):
        sp = sp_objects[i - 1]

        # ĐẢM BẢO ĐÚNG 3 LÔ ĐẦU TIÊN LÀ SẮP HẾT HẠN
        if i in [1, 2, 3]:
            # Chỉ tạo ngày trong khoảng từ 1 đến 28 ngày tới (nhỏ hơn 30 ngày)
            days_offset = random.randint(1, 28)
            tt_lohang = "Sắp hết hạn"
        else:
            # Các lô còn lại cách từ 40 ngày đến 365 ngày (chắc chắn an toàn > 30 ngày)
            days_offset = random.randint(40, 365)
            tt_lohang = "Bình thường"

        hsd = today + timedelta(days=days_offset)

        lh = LoHang.objects.create(
            MaLH=f"LH0{i:02d}",
            MaSP=sp,
            HSD=hsd,
            SoLuong=random.choice([20, 50, 100, 200, 500]),
            TrangThai=tt_lohang
        )
        lh_objects.append(lh)

    # --- 4 & 5. DỮ LIỆU PHIẾU NHẬP & CHI TIẾT NHẬP (15 dòng) ---
    print("Đang tạo 15 Phiếu nhập & Chi tiết nhập...")
    for i in range(1, 16):
        ncc = ncc_objects[i - 1]
        lh = lh_objects[i - 1]

        ngay_nhap = today - timedelta(days=random.randint(10, 300))

        pn = PhieuNhap.objects.create(
            MaPhieu=f"PN-24-0{i:02d}",
            MaNCC=ncc,
            NgayNhap=ngay_nhap,
            TrangThai=random.choice(["Hoàn thành", "Chờ kiểm tra", "Đã tạo", "Đang xử lý"]),
            TongTien=0
        )

        sl_nhap = random.choice([50, 100, 200, 500, 1000])
        don_gia = float(random.choice([15000, 25000, 50000, 120000, 350000]))

        ctn = ChiTietNhap.objects.create(
            MaPhieu=pn,
            MaLH=lh,
            SoLuongNhap=sl_nhap,
            DonGiaNhap=don_gia
        )

        pn.TongTien = ctn.ThanhTien
        pn.save()

    print("Đã tạo dữ liệu mẫu thành công! 🎉 (Gồm đúng 3 sản phẩm sắp hết hạn)")

if __name__ == '__main__':
    seed_data()