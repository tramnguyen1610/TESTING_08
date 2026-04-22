from django.contrib import admin
from .models import NhaCungCap, SanPham, LoHang, PhieuNhap, ChiTietNhap

# Register your models here.
@admin.register(NhaCungCap)
class NhaCungCapAdmin(admin.ModelAdmin):
    list_display = ('MaNCC', 'TenNCC', 'SDT_NCC', 'DChi_NCC')
    search_fields = ('MaNCC', 'TenNCC', 'SDT_NCC') # Thêm thanh tìm kiếm

@admin.register(SanPham)
class SanPhamAdmin(admin.ModelAdmin):
    list_display = ('MaSP', 'TenSP', 'DVT', 'TrangThai')
    list_filter = ('TrangThai',) # Thêm bộ lọc bên phải
    search_fields = ('MaSP', 'TenSP')

@admin.register(LoHang)
class LoHangAdmin(admin.ModelAdmin):
    list_display = ('MaLH', 'MaSP', 'SoLuong', 'HSD', 'TrangThai')
    list_filter = ('TrangThai',)
    search_fields = ('MaLH',)

@admin.register(PhieuNhap)
class PhieuNhapAdmin(admin.ModelAdmin):
    list_display = ('MaPhieu', 'MaNCC', 'NgayNhap', 'TongTien', 'TrangThai')
    list_filter = ('TrangThai', 'NgayNhap')
    search_fields = ('MaPhieu',)

@admin.register(ChiTietNhap)
class ChiTietNhapAdmin(admin.ModelAdmin):
    list_display = ('MaPhieu', 'MaLH', 'SoLuongNhap', 'DonGiaNhap', 'ThanhTien')