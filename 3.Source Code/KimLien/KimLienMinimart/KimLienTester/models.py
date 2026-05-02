from django.db import models

# Create your models here.
class NhaCungCap(models.Model):
    MaNCC = models.CharField(max_length=50, primary_key=True)
    TenNCC = models.CharField(max_length=255)
    DChi_NCC = models.TextField()
    SDT_NCC = models.CharField(max_length=20)
    TrangThai = models.CharField(max_length=20, default='Hợp tác')

    class Meta:
        verbose_name = "Nhà cung cấp"
        verbose_name_plural = "Nhà cung cấp"

    def __str__(self):
        return self.TenNCC

class SanPham(models.Model):
    MaSP = models.CharField(max_length=50, primary_key=True)
    TenSP = models.CharField(max_length=255)
    DVT = models.CharField(max_length=50)
    TrangThai = models.CharField(max_length=50)

    class Meta:
        verbose_name = "Sản phẩm"
        verbose_name_plural = "Sản phẩm"

    def __str__(self):
        return self.TenSP

class LoHang(models.Model):
    MaLH = models.CharField(max_length=50, primary_key=True)
    MaSP = models.ForeignKey(SanPham, on_delete=models.CASCADE, db_column='MaSP')
    HSD = models.DateField()
    TrangThai = models.CharField(max_length=50)

    class Meta:
        verbose_name = "Lô hàng"
        verbose_name_plural = "Lô hàng"

    def __str__(self):
        return f"{self.MaLH} - {self.MaSP.TenSP}"

class PhieuNhap(models.Model):
    MaPhieu = models.CharField(max_length=50, primary_key=True)
    NgayNhap = models.DateField()
    TongTien = models.DecimalField(max_digits=12, decimal_places=2, default=0)
    TrangThai = models.CharField(max_length=50)
    MaNCC = models.ForeignKey(NhaCungCap, on_delete=models.CASCADE, db_column='MaNCC')

    class Meta:
        verbose_name = "Phiếu nhập"
        verbose_name_plural = "Phiếu nhập"

    def __str__(self):
        return self.MaPhieu

class ChiTietNhap(models.Model):
    MaPhieu = models.ForeignKey(PhieuNhap, on_delete=models.CASCADE, db_column='MaPhieu')
    MaLH = models.ForeignKey(LoHang, on_delete=models.CASCADE, db_column='MaLH')
    SoLuongNhap = models.IntegerField()
    DonGiaNhap = models.DecimalField(max_digits=10, decimal_places=2)
    ThanhTien = models.DecimalField(max_digits=12, decimal_places=2)

    class Meta:
        verbose_name = "Chi tiết nhập"
        verbose_name_plural = "Chi tiết nhập"

    def save(self, *args, **kwargs):
        # Tự động tính Thành tiền = Số lượng nhập * Đơn giá nhập trước khi lưu
        self.ThanhTien = self.SoLuongNhap * self.DonGiaNhap
        super().save(*args, **kwargs)

    def __str__(self):
        return f"Phiếu: {self.MaPhieu.MaPhieu} - Lô: {self.MaLH.MaLH}"