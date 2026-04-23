from django.shortcuts import render, redirect
from django.contrib.auth import authenticate, login, logout
from django.urls import reverse_lazy
from django.views.generic import ListView, DetailView, TemplateView
from .models import NhaCungCap, SanPham, LoHang, PhieuNhap, ChiTietNhap
from django.db.models import Sum, Q
from datetime import date, timedelta
from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
import json, re, random
from django.core.paginator import Paginator
from django.contrib.auth import update_session_auth_hash
from django.contrib.auth.signals import user_logged_in
from django.contrib.auth.models import update_last_login

user_logged_in.disconnect(update_last_login, dispatch_uid='update_last_login')


def dang_nhap_view(request):
    if request.user.is_authenticated: return redirect('dashboard')
    if request.method == 'POST':
        user = authenticate(request, username=request.POST.get('username'), password=request.POST.get('password'))
        if user:
            login(request, user)
            return redirect('dashboard')
        return render(request, 'DangNhap.html', {'error': 'Sai tên đăng nhập hoặc mật khẩu!'})
    return render(request, 'DangNhap.html')


def dang_xuat_view(request):
    logout(request)
    return redirect('dang_nhap')


def api_dashboard_data(request):
    tong_ncc = NhaCungCap.objects.filter(TrangThai='Hợp tác').count()
    don_nhap_hom_nay = PhieuNhap.objects.filter(NgayNhap=date.today()).count()
    tong_chi_phi = PhieuNhap.objects.aggregate(Sum('TongTien'))['TongTien__sum'] or 0
    moc_30_ngay = date.today() + timedelta(days=30)
    lo_hang_canh_bao = LoHang.objects.filter(HSD__lte=moc_30_ngay).order_by('HSD')

    canh_bao_data = []
    for lo in lo_hang_canh_bao:
        # Liên kết: Tìm số lượng trong ChiTietNhap
        so_luong = ChiTietNhap.objects.filter(MaLH=lo).aggregate(Sum('SoLuongNhap'))['SoLuongNhap__sum'] or 0
        canh_bao_data.append({
            'TenSP': lo.MaSP.TenSP if lo.MaSP else 'Sản phẩm',
            'MaLH': lo.MaLH,
            'HSD': lo.HSD.strftime('%Y-%m-%d'),
            'SoLuong': so_luong  # Đã thay số 0 bằng dữ liệu thật
        })

    return JsonResponse(
        {'tong_ncc': tong_ncc, 'don_nhap_hom_nay': don_nhap_hom_nay, 'tong_chi_phi': float(tong_chi_phi),
         'so_luong_canh_bao': lo_hang_canh_bao.count(), 'canh_bao_lo_hang': canh_bao_data})


# --- NHÀ CUNG CẤP ---
def get_next_ma_ncc(request):
    last_ncc = NhaCungCap.objects.all().order_by('MaNCC').last()
    if not last_ncc: return JsonResponse({'next_id': 'NCC001'})
    match = re.search(r'(\d+)', last_ncc.MaNCC)
    return JsonResponse({'next_id': f"NCC{int(match.group(1)) + 1:03d}" if match else "NCC001"})


def api_search_ncc(request):
    q = request.GET.get('q', '')
    page = request.GET.get('page', 1)
    nccs = NhaCungCap.objects.filter(Q(TenNCC__icontains=q) | Q(MaNCC__icontains=q)).order_by('-pk')
    paginator = Paginator(nccs, 5)
    page_obj = paginator.get_page(page)
    results = [{'MaNCC': n.MaNCC, 'TenNCC': n.TenNCC, 'SDT': n.SDT_NCC, 'DiaChi': n.DChi_NCC,
                'TrangThai': getattr(n, 'TrangThai', 'Hợp tác')} for n in page_obj]
    return JsonResponse({'results': results, 'current_page': page_obj.number, 'total_pages': paginator.num_pages,
                         'has_next': page_obj.has_next(), 'has_previous': page_obj.has_previous()})


@csrf_exempt
def api_create_ncc(request):
    if request.method == 'POST':
        data = json.loads(request.body)
        try:
            NhaCungCap.objects.create(MaNCC=data.get('ma_ncc'), TenNCC=data.get('ten_ncc'), SDT_NCC=data.get('sdt'),
                                      DChi_NCC=data.get('dia_chi'), TrangThai='Hợp tác')
            return JsonResponse({'status': 'success'})
        except Exception as e:
            return JsonResponse({'status': 'error', 'message': str(e)})


def api_get_ncc(request, ma_ncc):
    try:
        n = NhaCungCap.objects.get(MaNCC=ma_ncc)
        return JsonResponse({'status': 'success',
                             'data': {'MaNCC': n.MaNCC, 'TenNCC': n.TenNCC, 'SDT': n.SDT_NCC, 'DiaChi': n.DChi_NCC,
                                      'TrangThai': getattr(n, 'TrangThai', 'Hợp tác')}})
    except NhaCungCap.DoesNotExist:
        return JsonResponse({'status': 'error', 'message': 'Lỗi!'})


@csrf_exempt
def api_update_ncc(request, ma_ncc):
    if request.method == 'POST':
        data = json.loads(request.body)
        try:
            n = NhaCungCap.objects.get(MaNCC=ma_ncc)
            n.TenNCC = data.get('ten_ncc', n.TenNCC);
            n.SDT_NCC = data.get('sdt', n.SDT_NCC);
            n.DChi_NCC = data.get('dia_chi', n.DChi_NCC);
            n.TrangThai = data.get('trang_thai', n.TrangThai)
            n.save()
            return JsonResponse({'status': 'success'})
        except Exception as e:
            return JsonResponse({'status': 'error', 'message': str(e)})


# --- NHẬP HÀNG ---
def get_next_ma_phieu(request):
    """API tự động sinh mã Phiếu Nhập (PN001 -> PN002)"""
    last_pn = PhieuNhap.objects.all().order_by('MaPhieu').last()
    if not last_pn: return JsonResponse({'next_id': 'PN001'})
    match = re.search(r'(\d+)', last_pn.MaPhieu)
    return JsonResponse({'next_id': f"PN{int(match.group(1)) + 1:03d}" if match else "PN001"})


def get_next_ma_lo(request):
    """API tự động sinh mã Lô Hàng (LH001 -> LH002)"""
    last_lo = LoHang.objects.all().order_by('MaLH').last()
    if not last_lo: return JsonResponse({'next_id': 'LH001'})
    match = re.search(r'(\d+)', last_lo.MaLH)
    return JsonResponse({'next_id': f"LH{int(match.group(1)) + 1:03d}" if match else "LH001"})


def api_nhaphang_stats(request):
    nhap = PhieuNhap.objects.filter(TrangThai='Nháp').count()
    hoan_thanh = PhieuNhap.objects.filter(TrangThai='Hoàn thành').count()
    canh_bao = LoHang.objects.filter(HSD__lte=date.today() + timedelta(days=30)).count()
    return JsonResponse({'nhap': nhap, 'hoan_thanh': hoan_thanh, 'canh_bao': canh_bao})


def api_search_nhaphang(request):
    q = request.GET.get('q', '');
    status = request.GET.get('status', '');
    page = request.GET.get('page', 1)
    phieus = PhieuNhap.objects.all().order_by('-pk')
    if q: phieus = phieus.filter(Q(MaPhieu__icontains=q) | Q(MaNCC__TenNCC__icontains=q))
    if status and status != 'Tất cả trạng thái': phieus = phieus.filter(TrangThai=status)
    paginator = Paginator(phieus, 5)
    page_obj = paginator.get_page(page)
    results = []
    for pn in page_obj:
        ctn = ChiTietNhap.objects.filter(MaPhieu=pn).first()
        results.append({
            'MaPhieu': pn.MaPhieu, 'MaNCC': pn.MaNCC.MaNCC if pn.MaNCC else "N/A",
            'MaLo': ctn.MaLH.MaLH if ctn and ctn.MaLH else "N/A",
            'NgayNhap': pn.NgayNhap.strftime('%d/%m/%Y'),
            'SoLuong': ctn.SoLuongNhap if ctn else 0,
            'ThanhTien': float(ctn.ThanhTien) if ctn else 0,
            'TongTien': float(pn.TongTien), 'TrangThai': pn.TrangThai
        })
    return JsonResponse({'results': results, 'total_items': paginator.count, 'current_page': page_obj.number,
                         'total_pages': paginator.num_pages})


def api_get_nhaphang(request, ma_phieu):
    try:
        pn = PhieuNhap.objects.get(MaPhieu=ma_phieu)
        chi_tiets = ChiTietNhap.objects.filter(MaPhieu=pn)
        sp_list = [
            {'MaLH': ct.MaLH.MaLH if ct.MaLH else '', 'TenSP': ct.MaLH.MaSP.TenSP if ct.MaLH and ct.MaLH.MaSP else '',
             'SoLuong': ct.SoLuongNhap, 'DonGia': float(ct.DonGiaNhap), 'ThanhTien': float(ct.ThanhTien)} for ct in
            chi_tiets]
        return JsonResponse({'status': 'success',
                             'data': {'MaPhieu': pn.MaPhieu, 'MaNCC': pn.MaNCC.MaNCC if pn.MaNCC else '',
                                      'NgayNhap': pn.NgayNhap.strftime('%Y-%m-%d'), 'TongTien': float(pn.TongTien),
                                      'TrangThai': pn.TrangThai, 'SanPham': sp_list}})
    except PhieuNhap.DoesNotExist:
        return JsonResponse({'status': 'error', 'message': 'Không tìm thấy!'})


@csrf_exempt
def api_update_nhaphang(request, ma_phieu):
    if request.method == 'POST':
        data = json.loads(request.body)
        try:
            pn = PhieuNhap.objects.get(MaPhieu=ma_phieu)
            ncc = NhaCungCap.objects.filter(MaNCC=data.get('ma_ncc')).first()
            if ncc: pn.MaNCC = ncc
            if data.get('ngay_nhap'): pn.NgayNhap = data.get('ngay_nhap')
            pn.save()
            return JsonResponse({'status': 'success'})
        except Exception as e:
            return JsonResponse({'status': 'error', 'message': str(e)})


@csrf_exempt
def api_create_nhaphang(request):
    if request.method == 'POST':
        data = json.loads(request.body)
        try:
            ncc = NhaCungCap.objects.filter(MaNCC=data.get('ma_ncc')).first()
            if not ncc: return JsonResponse({'status': 'error', 'message': 'Mã NCC không tồn tại!'})

            # Sử dụng Mã Phiếu lấy từ giao diện
            pn = PhieuNhap.objects.create(MaPhieu=data.get('ma_phieu'), MaNCC=ncc, NgayNhap=data.get('ngay_nhap'),
                                          TrangThai="Hoàn thành", TongTien=0)

            tong_tien = 0
            for sp in data.get('san_phams', []):
                # Tìm hoặc tạo Sản phẩm
                sp_obj, _ = SanPham.objects.get_or_create(TenSP=sp.get('ten'),
                                                          defaults={'MaSP': f"SP{random.randint(1000, 9999)}",
                                                                    'DVT': 'Khác', 'TrangThai': 'Đang kinh doanh'})
                # Tạo Lô hàng
                lh = LoHang.objects.create(MaLH=sp.get('ma_lo'), MaSP=sp_obj, HSD=sp.get('hsd'),
                                           TrangThai="Bình thường")
                # Tạo Chi tiết
                tt = int(sp.get('sl')) * float(sp.get('gia'))
                ChiTietNhap.objects.create(MaPhieu=pn, MaLH=lh, SoLuongNhap=int(sp.get('sl')),
                                           DonGiaNhap=float(sp.get('gia')), ThanhTien=tt)
                tong_tien += tt

            pn.TongTien = tong_tien
            pn.save()
            return JsonResponse({'status': 'success'})
        except Exception as e:
            return JsonResponse({'status': 'error', 'message': str(e)})


@csrf_exempt
def api_delete_nhaphang(request, ma_phieu):
    if request.method == 'POST':
        try:
            pn = PhieuNhap.objects.get(MaPhieu=ma_phieu)
            if pn.TrangThai == 'Hoàn thành': return JsonResponse(
                {'status': 'error', 'message': 'Phiếu đã hoàn thành, không thể xóa!'})
            pn.delete()
            return JsonResponse({'status': 'success'})
        except Exception as e:
            return JsonResponse({'status': 'error', 'message': str(e)})


# --- SẢN PHẨM & LÔ HÀNG ---
def api_sanpham_stats(request):
    return JsonResponse(
        {'thuc_pham': SanPham.objects.filter(Q(TenSP__icontains='bánh') | Q(TenSP__icontains='sữa')).count(),
         'gia_dung': 10, 'dang_kinh_doanh': SanPham.objects.filter(TrangThai='Đang kinh doanh').count(),
         'ngung_kinh_doanh': SanPham.objects.filter(TrangThai='Ngừng kinh doanh').count()})


def api_search_sanpham(request):
    sps = SanPham.objects.all().order_by('-pk')
    paginator = Paginator(sps, 5)
    page_obj = paginator.get_page(request.GET.get('page', 1))
    return JsonResponse(
        {'results': [{'MaSP': s.MaSP, 'TenSP': s.TenSP, 'DVT': s.DVT, 'TrangThai': s.TrangThai} for s in page_obj],
         'current_page': page_obj.number, 'total_pages': paginator.num_pages, 'has_next': page_obj.has_next(),
         'has_previous': page_obj.has_previous()})


@csrf_exempt
def api_create_sanpham(request):
    if request.method == 'POST':
        data = json.loads(request.body)
        SanPham.objects.create(MaSP=data.get('ma_sp'), TenSP=data.get('ten_sp'), DVT=data.get('dvt'),
                               TrangThai="Đang kinh doanh")
        return JsonResponse({'status': 'success'})


def api_get_sanpham(request, ma_sp):
    sp = SanPham.objects.get(MaSP=ma_sp)
    return JsonResponse({'status': 'success',
                         'data': {'MaSP': sp.MaSP, 'TenSP': sp.TenSP, 'DVT': sp.DVT, 'TrangThai': sp.TrangThai,
                                  'GiaNhap': 0, 'TonKho': 0, 'TenNCC': 'N/A'}})


@csrf_exempt
def api_update_sanpham(request, ma_sp):
    if request.method == 'POST':
        data = json.loads(request.body)
        sp = SanPham.objects.get(MaSP=ma_sp);
        sp.TenSP = data.get('ten_sp');
        sp.DVT = data.get('dvt');
        sp.TrangThai = data.get('trang_thai');
        sp.save()
        return JsonResponse({'status': 'success'})


@csrf_exempt
def api_delete_sanpham(request, ma_sp):
    if request.method == 'POST':
        SanPham.objects.get(MaSP=ma_sp).delete()
        return JsonResponse({'status': 'success'})


def api_lohang_stats(request):
    today = date.today()
    moc_30_ngay = today + timedelta(days=30)
    lo_hang_canh_bao = LoHang.objects.filter(HSD__lte=moc_30_ngay).order_by('HSD')

    danh_sach_lo = []
    for lo in lo_hang_canh_bao[:5]:
        # Liên kết: Tìm số lượng trong ChiTietNhap
        so_luong = ChiTietNhap.objects.filter(MaLH=lo).aggregate(Sum('SoLuongNhap'))['SoLuongNhap__sum'] or 0
        danh_sach_lo.append({
            'TenSP': lo.MaSP.TenSP if lo.MaSP else 'N/A',
            'MaLH': lo.MaLH,
            'HSD': lo.HSD.strftime('%Y-%m-%d'),
            'SoLuong': so_luong  # Đã thay số 0 bằng dữ liệu thật
        })

    return JsonResponse({'so_luong_canh_bao': lo_hang_canh_bao.count(), 'canh_bao_lo_hang': danh_sach_lo})


def api_search_lohang(request):
    query = request.GET.get('q', '')
    status = request.GET.get('status', '')
    page_number = request.GET.get('page', 1)

    los = LoHang.objects.all().order_by('-pk')
    if query:
        los = los.filter(Q(MaLH__icontains=query) | Q(MaSP__TenSP__icontains=query))

    today = date.today()
    moc_30_ngay = today + timedelta(days=30)
    if status == 'Bình thường':
        los = los.filter(HSD__gt=moc_30_ngay)
    elif status == 'Sắp hết hạn':
        los = los.filter(HSD__lte=moc_30_ngay, HSD__gte=today)
    elif status == 'Đã hết hạn':
        los = los.filter(HSD__lt=today)

    paginator = Paginator(los, 5)
    page_obj = paginator.get_page(page_number)

    results = []
    for lo in page_obj:
        # Tính toán lại trạng thái màu sắc động
        trang_thai = "Bình thường"
        if lo.HSD:
            if lo.HSD < today:
                trang_thai = "Đã hết hạn"
            elif lo.HSD <= moc_30_ngay:
                trang_thai = "Sắp hết hạn"

        # LẤY SỐ LƯỢNG VÀ NGÀY NHẬP TỪ BẢNG CHI TIẾT NHẬP & PHIẾU NHẬP
        ctn = ChiTietNhap.objects.filter(MaLH=lo).first()
        so_luong = ctn.SoLuongNhap if ctn else 0
        ngay_nhap = ctn.MaPhieu.NgayNhap.strftime('%d/%m/%Y') if ctn and ctn.MaPhieu else 'N/A'

        results.append({
            'MaLH': lo.MaLH,
            'TenSP': lo.MaSP.TenSP if lo.MaSP else 'N/A',
            'SoLuong': so_luong,  # Link số lượng
            'NgayNhap': ngay_nhap,  # Link ngày nhập
            'HSD': lo.HSD.strftime('%d/%m/%Y'),
            'TrangThai': trang_thai
        })

    return JsonResponse({
        'results': results,
        'total_items': paginator.count,
        'current_page': page_obj.number,
        'total_pages': paginator.num_pages,
        'has_next': page_obj.has_next(),
        'has_previous': page_obj.has_previous()
    })


@csrf_exempt
def api_change_password(request):
    if request.method == 'POST':
        data = json.loads(request.body)
        user = request.user
        if user.check_password(data.get('old_pw')):
            user.set_password(data.get('new_pw'))
            user.save();
            update_session_auth_hash(request, user)
            return JsonResponse({'status': 'success'})
        return JsonResponse({'status': 'error', 'message': 'Mật khẩu cũ sai'})


class DanhSachNhaCungCapView(ListView): model = NhaCungCap; template_name = 'NhaCungCap.html'
class DanhSachPhieuNhapView(ListView): model = PhieuNhap; template_name = 'NhapHang.html'
class DanhSachLoHangView(ListView): model = LoHang; template_name = 'LoHang.html'
class DanhSachSanPhamView(ListView): model = SanPham; template_name = 'SanPham.html'
class CaiDatView(TemplateView): template_name = 'CaiDat.html'
def get_object(self):
    return None
def dashboard_view(request): return render(request, 'Dashboard.html')