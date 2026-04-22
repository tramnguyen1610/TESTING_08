from django.shortcuts import render, redirect
from django.contrib.auth import authenticate, login, logout
from django.contrib.auth.decorators import login_required
from django.urls import reverse_lazy
from django.views.generic import ListView, DetailView, CreateView
from .models import NhaCungCap, SanPham, LoHang, PhieuNhap, ChiTietNhap
from django.db.models import Sum
from datetime import date, timedelta
from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
import json
from django.core.paginator import Paginator
from django.db.models import Q
from django.contrib.auth import update_session_auth_hash

# --- 1. XỬ LÝ ĐĂNG NHẬP & ĐĂNG XUẤT ---
def dang_nhap_view(request):
    # SỬA: Dùng 'dashboard' (tên url) thay vì 'Dashboard.html'
    if request.user.is_authenticated:
        return redirect('dashboard')

    if request.method == 'POST':
        u = request.POST.get('username')
        p = request.POST.get('password')

        user = authenticate(request, username=u, password=p)
        if user is not None:
            login(request, user)
            return redirect('dashboard')
        else:
            return render(request, 'DangNhap.html', {'error': 'Sai tên đăng nhập hoặc mật khẩu!'})

    return render(request, 'DangNhap.html')


def dang_xuat_view(request):
    logout(request)
    # SỬA: Đảm bảo chuyển hướng về đúng tên URL đăng nhập
    return redirect('dang_nhap')


# --- 2. TRANG CHỦ (DASHBOARD) ---
# SỬA: URL name phải khớp với urls.py
@login_required(login_url='dang_nhap')
def dashboard_view(request):
    # TỐI ƯU: Chỉ cần render giao diện.
    # Dữ liệu thống kê sẽ được load ngầm qua API (hàm api_dashboard_data) bằng Javascript
    return render(request, 'Dashboard.html')


# --- 3. QUẢN LÝ DỮ LIỆU (CLASS-BASED VIEWS) ---

class DanhSachNhaCungCapView(ListView):
    model = NhaCungCap
    template_name = 'NhaCungCap.html'
    context_object_name = 'danh_sach_ncc'

class TaoNhaCungCapView(CreateView):
    model = NhaCungCap
    fields = ['MaNCC', 'TenNCC', 'DChi_NCC', 'SDT_NCC']
    template_name = 'tao_nha_cung_cap.html'
    success_url = reverse_lazy('danh_sach_ncc')


class DanhSachSanPhamView(ListView):
    model = SanPham
    template_name = 'SanPham.html'
    context_object_name = 'danh_sach_sp'

class TaoSanPhamView(CreateView):
    model = SanPham
    fields = ['MaSP', 'TenSP', 'DVT', 'TrangThai']
    template_name = 'tao_san_pham.html'
    success_url = reverse_lazy('danh_sach_sp')


class DanhSachLoHangView(ListView):
    model = LoHang
    template_name = 'LoHang.html'
    context_object_name = 'danh_sach_lo_hang'


class DanhSachPhieuNhapView(ListView):
    model = PhieuNhap
    template_name = 'NhapHang.html'
    context_object_name = 'danh_sach_phieu_nhap'

class ChiTietPhieuNhapView(DetailView):
    model = PhieuNhap
    template_name = 'chi_tiet_phieu_nhap.html'
    context_object_name = 'phieu_nhap'

class CaiDatView(ListView):
    model = SanPham
    template_name = 'CaiDat.html'

# --- 4. API CHO DASHBOARD ---

def api_dashboard_data(request):
    """API trả về dữ liệu JSON cho Dashboard"""
    today = date.today()

    # 1. Số lượng nhà cung cấp
    tong_ncc = NhaCungCap.objects.count()

    # 2. Số đơn nhập hôm nay
    don_nhap_hom_nay = PhieuNhap.objects.filter(NgayNhap=today).count()

    # 3. Tổng chi phí nhập (lấy tổng cột TongTien)
    tong_chi_phi = PhieuNhap.objects.aggregate(Sum('TongTien'))['TongTien__sum'] or 0

    # 4. Tính toán Cảnh báo hết hạn (Giới hạn trong 30 ngày gần nhất)
    moc_30_ngay = today + timedelta(days=30)

    lo_hang_canh_bao = LoHang.objects.filter(
        HSD__lte=moc_30_ngay
    ).order_by('HSD')

    so_luong_canh_bao = lo_hang_canh_bao.count()

    danh_sach_lo = []
    for lo in lo_hang_canh_bao[:3]:
        danh_sach_lo.append({
            'TenSP': lo.MaSP.TenSP,
            'MaLH': lo.MaLH,
            'HSD': lo.HSD.strftime('%Y-%m-%d'),
            'SoLuong': lo.SoLuong
        })

    return JsonResponse({
        'tong_ncc': tong_ncc,
        'don_nhap_hom_nay': don_nhap_hom_nay,
        'tong_chi_phi': float(tong_chi_phi),
        'so_luong_canh_bao': so_luong_canh_bao,
        'canh_bao_lo_hang': danh_sach_lo
    })


# API Tìm kiếm Nhà cung cấp (Hỗ trợ tìm kiếm linh hoạt)
def api_search_ncc(request):
    query = request.GET.get('q', '')
    page_number = request.GET.get('page', 1) # Lấy số trang từ client gửi lên

    # SỬA LỖI Ở ĐÂY: Đổi '-id' thành '-pk' hoặc '-MaNCC'
    nccs = NhaCungCap.objects.filter(
        Q(TenNCC__icontains=query) | Q(MaNCC__icontains=query)
    ).order_by('-pk') # <--- Sửa dòng này

    # 2. Phân trang: 5 nhà cung cấp mỗi trang
    paginator = Paginator(nccs, 5)
    page_obj = paginator.get_page(page_number)

    results = []
    for ncc in page_obj:
        results.append({
            'MaNCC': ncc.MaNCC,
            'TenNCC': ncc.TenNCC,
            'SDT': ncc.SDT_NCC,
            'DiaChi': ncc.DChi_NCC,
        })

    # 3. Trả về thêm thông tin phân trang
    return JsonResponse({
        'results': results,
        'current_page': page_obj.number,
        'total_pages': paginator.num_pages,
        'has_next': page_obj.has_next(),
        'has_previous': page_obj.has_previous(),
    })


@csrf_exempt
def api_create_ncc(request):
    if request.method == 'POST':
        data = json.loads(request.body)
        try:
            NhaCungCap.objects.create(
                MaNCC=data['ma_ncc'],
                TenNCC=data['ten_ncc'],
                SDT_NCC=data['sdt'],
                DChi_NCC=data['dia_chi']
            )
            return JsonResponse({'status': 'success'})
        except Exception as e:
            return JsonResponse({'status': 'error', 'message': str(e)})


def api_get_ncc(request, ma_ncc):
    """API lấy thông tin chi tiết 1 nhà cung cấp"""
    try:
        ncc = NhaCungCap.objects.get(MaNCC=ma_ncc)
        return JsonResponse({
            'status': 'success',
            'data': {
                'MaNCC': ncc.MaNCC,
                'TenNCC': ncc.TenNCC,
                'SDT': ncc.SDT_NCC,
                'DiaChi': ncc.DChi_NCC,
                # Nếu model chưa có các trường dưới, nó sẽ trả về rỗng để giao diện không lỗi
                'TenNganHang': getattr(ncc, 'TenNganHang', ''),
                'STK': getattr(ncc, 'STK', ''),
            }
        })
    except NhaCungCap.DoesNotExist:
        return JsonResponse({'status': 'error', 'message': 'Không tìm thấy nhà cung cấp!'})


@csrf_exempt
def api_update_ncc(request, ma_ncc):
    """API cập nhật thông tin nhà cung cấp"""
    if request.method == 'POST':
        data = json.loads(request.body)
        try:
            ncc = NhaCungCap.objects.get(MaNCC=ma_ncc)
            ncc.TenNCC = data.get('ten_ncc', ncc.TenNCC)
            ncc.SDT_NCC = data.get('sdt', ncc.SDT_NCC)
            ncc.DChi_NCC = data.get('dia_chi', ncc.DChi_NCC)
            # Cập nhật các trường ngân hàng nếu model của bạn đã có
            if hasattr(ncc, 'TenNganHang'): ncc.TenNganHang = data.get('ten_ngan_hang', '')
            if hasattr(ncc, 'STK'): ncc.STK = data.get('stk', '')

            ncc.save()
            return JsonResponse({'status': 'success'})
        except Exception as e:
            return JsonResponse({'status': 'error', 'message': str(e)})

@csrf_exempt
def api_delete_ncc(request, ma_ncc):
    """API xóa nhà cung cấp khỏi cơ sở dữ liệu"""
    if request.method == 'POST':
        try:
            # Tìm nhà cung cấp trong CSDL
            ncc = NhaCungCap.objects.get(MaNCC=ma_ncc)
            # Xóa khỏi CSDL
            ncc.delete()
            return JsonResponse({'status': 'success'})
        except NhaCungCap.DoesNotExist:
            return JsonResponse({'status': 'error', 'message': 'Không tìm thấy nhà cung cấp!'})
        except Exception as e:
            return JsonResponse({'status': 'error', 'message': str(e)})


# ==========================================
# CÁC API CHO QUẢN LÝ NHẬP HÀNG
# ==========================================

def api_nhaphang_stats(request):
    """API trả về 4 thẻ thống kê cho Nhập hàng"""
    cho_kiem_tra = PhieuNhap.objects.filter(TrangThai='Chờ kiểm tra').count()

    # SỬA: Đổi 'Đang kiểm tra' thành 'Đang xử lý'
    dang_xu_ly = PhieuNhap.objects.filter(TrangThai='Đang xử lý').count()

    hoan_thanh = PhieuNhap.objects.filter(TrangThai='Hoàn thành').count()

    today = date.today()
    moc_30_ngay = today + timedelta(days=30)
    canh_bao = LoHang.objects.filter(HSD__lte=moc_30_ngay).count()

    return JsonResponse({
        'cho_kiem_tra': cho_kiem_tra,
        'dang_xu_ly': dang_xu_ly,  # Đổi key trả về
        'hoan_thanh': hoan_thanh,
        'canh_bao': canh_bao
    })


def api_search_nhaphang(request):
    """API tìm kiếm và phân trang phiếu nhập"""
    query = request.GET.get('q', '')
    status = request.GET.get('status', '')
    page_number = request.GET.get('page', 1)

    # 1. SỬA ĐỔI SẮP XẾP: Dùng '-pk' để phiếu mới tạo luôn nằm trên cùng
    phieus = PhieuNhap.objects.all().order_by('-pk')

    if query:
        phieus = phieus.filter(Q(MaPhieu__icontains=query) | Q(MaNCC__TenNCC__icontains=query))
    if status and status != 'Tất cả trạng thái':
        phieus = phieus.filter(TrangThai=status)

    # 2. SỬA ĐỔI PHÂN TRANG: Đổi từ 8 xuống 5 dòng mỗi trang
    paginator = Paginator(phieus, 5)
    page_obj = paginator.get_page(page_number)

    results = []
    for pn in page_obj:
        # Lấy sản phẩm đầu tiên đại diện (nếu có)
        ctn_first = ChiTietNhap.objects.filter(MaPhieu=pn).first()
        so_luong = ctn_first.SoLuongNhap if ctn_first else 0
        ma_lo = ctn_first.MaLH.MaLH if ctn_first and ctn_first.MaLH else "N/A"

        results.append({
            'MaPhieu': pn.MaPhieu,
            'MaNCC': pn.MaNCC.MaNCC if pn.MaNCC else "N/A",
            'MaLo': ma_lo,
            'NgayNhap': pn.NgayNhap.strftime('%d/%m/%Y'),
            'SoLuong': so_luong,
            'ThanhTien': float(pn.TongTien) if pn.TongTien else 0,
            'TongTien': float(pn.TongTien) if pn.TongTien else 0,
            'TrangThai': pn.TrangThai
        })

    return JsonResponse({
        'results': results,
        'current_page': page_obj.number,
        'total_pages': paginator.num_pages,
        'has_next': page_obj.has_next(),
        'has_previous': page_obj.has_previous(),
    })


def api_get_nhaphang(request, ma_phieu):
    """Lấy chi tiết phiếu nhập kèm danh sách sản phẩm"""
    try:
        pn = PhieuNhap.objects.get(MaPhieu=ma_phieu)
        ctns = ChiTietNhap.objects.filter(MaPhieu=pn)
        ds_san_pham = []
        for ctn in ctns:
            ds_san_pham.append({
                'MaLH': ctn.MaLH.MaLH if ctn.MaLH else '',
                'TenSP': ctn.MaLH.MaSP.TenSP if ctn.MaLH and ctn.MaLH.MaSP else 'N/A',
                'SoLuong': ctn.SoLuongNhap,
                'DonGia': float(ctn.DonGiaNhap),
                'ThanhTien': float(ctn.SoLuongNhap * ctn.DonGiaNhap)
            })

        return JsonResponse({
            'status': 'success',
            'data': {
                'MaPhieu': pn.MaPhieu,
                'MaNCC': pn.MaNCC.MaNCC if pn.MaNCC else "",
                'NgayNhap': pn.NgayNhap.strftime('%Y-%m-%d'),
                'TongTien': float(pn.TongTien) if pn.TongTien else 0,
                'SanPham': ds_san_pham
            }
        })
    except PhieuNhap.DoesNotExist:
        return JsonResponse({'status': 'error', 'message': 'Không tìm thấy!'})


@csrf_exempt
def api_create_nhaphang(request):
    """Tạo phiếu nhập và các chi tiết sản phẩm liên quan"""
    if request.method == 'POST':
        data = json.loads(request.body)
        try:
            # 1. Tìm Nhà Cung Cấp
            ncc = NhaCungCap.objects.filter(MaNCC=data.get('ma_ncc')).first()
            if not ncc:
                return JsonResponse({'status': 'error', 'message': 'Mã nhà cung cấp không tồn tại!'})

            # 2. Tạo Phiếu Nhập
            pn = PhieuNhap.objects.create(
                MaPhieu=f"PN-{random.randint(1000, 9999)}",  # Tự động sinh mã
                MaNCC=ncc,
                NgayNhap=data.get('ngay_nhap'),
                TrangThai="Đã tạo",
                TongTien=0
            )

            # 3. Tạo Chi tiết & Tính tổng tiền
            tong_tien = 0
            san_phams = data.get('san_phams', [])
            for sp in san_phams:
                # Demo: Lưu tạm vào chi tiết (Trong thực tế cần query MaSP -> tạo LoHang -> ChiTietNhap)
                # Tạm tính logic để không lỗi database
                thanh_tien = int(sp['sl']) * float(sp['gia'])
                tong_tien += thanh_tien

            pn.TongTien = tong_tien
            pn.save()

            return JsonResponse({'status': 'success'})
        except Exception as e:
            return JsonResponse({'status': 'error', 'message': str(e)})


@csrf_exempt
def api_delete_nhaphang(request, ma_phieu):
    """Xóa phiếu nhập (Chặn Hoàn Thành)"""
    if request.method == 'POST':
        try:
            pn = PhieuNhap.objects.get(MaPhieu=ma_phieu)
            if pn.TrangThai == 'Hoàn thành':
                return JsonResponse({'status': 'error', 'message': 'Phiếu nhập đã hoàn thành, không thể xóa!'})

            # Xóa chi tiết trước (nếu không setup CASCADE)
            ChiTietNhap.objects.filter(MaPhieu=pn).delete()
            pn.delete()
            return JsonResponse({'status': 'success'})
        except PhieuNhap.DoesNotExist:
            return JsonResponse({'status': 'error', 'message': 'Không tìm thấy phiếu nhập!'})


@csrf_exempt
def api_update_nhaphang(request, ma_phieu):
    """API Cập nhật (Sửa) thông tin phiếu nhập"""
    if request.method == 'POST':
        data = json.loads(request.body)
        try:
            pn = PhieuNhap.objects.get(MaPhieu=ma_phieu)

            # Cập nhật thông tin cơ bản
            ma_ncc = data.get('ma_ncc')
            if ma_ncc:
                ncc = NhaCungCap.objects.filter(MaNCC=ma_ncc).first()
                if ncc: pn.MaNCC = ncc

            if data.get('ngay_nhap'):
                pn.NgayNhap = data.get('ngay_nhap')

            # Ghi chú: Để hoàn thiện, phần này sẽ cần vòng lặp để cập nhật từng
            # ChiTietNhap (Xóa cái cũ, thêm cái mới) từ danh sách sp gửi lên.
            # Tạm thời lưu thông tin phiếu.
            pn.save()
            return JsonResponse({'status': 'success'})
        except PhieuNhap.DoesNotExist:
            return JsonResponse({'status': 'error', 'message': 'Không tìm thấy phiếu nhập!'})
        except Exception as e:
            return JsonResponse({'status': 'error', 'message': str(e)})


# ==========================================
# CÁC API CHO QUẢN LÝ SẢN PHẨM
# ==========================================

def api_sanpham_stats(request):
    """API trả về 4 thẻ thống kê cho Sản phẩm (Tự động phân loại thực phẩm/gia dụng)"""
    dang_kinh_doanh = SanPham.objects.filter(TrangThai='Đang kinh doanh').count()
    ngung_kinh_doanh = SanPham.objects.filter(TrangThai='Ngừng kinh doanh').count()

    # 1. Danh sách từ khóa nhận diện Thực phẩm (Ăn/Uống được)
    food_keywords = [
        'bánh', 'kẹo', 'nước', 'sữa', 'trứng', 'mì', 'dầu ăn', 'gạo',
        'coca', 'pepsi', 'xúc xích', 'thịt', 'cá', 'rau', 'củ', 'quả',
        'bia', 'rượu', 'trà', 'cafe', 'cà phê', 'mứt', 'đường', 'muối',
        'mắm', 'tương', 'bột ngọt', 'hạt nêm', 'chinsu', 'cholimex', 'thực phẩm'
    ]

    # 2. Danh sách từ khóa loại trừ
    # (Tránh trường hợp "Nước giặt", "Sữa tắm" bị nhận nhầm là đồ ăn do có chữ "Nước", "Sữa")
    exclude_keywords = [
        'nước giặt', 'nước xả', 'nước rửa chén', 'nước lau sàn', 'nước tẩy',
        'sữa tắm', 'sữa rửa mặt', 'dầu gội', 'kem đánh răng', 'bột giặt'
    ]

    # 3. Tạo câu lệnh quét Database tự động
    food_query = Q()
    for kw in food_keywords:
        food_query |= Q(TenSP__icontains=kw)

    exclude_query = Q()
    for kw in exclude_keywords:
        exclude_query |= Q(TenSP__icontains=kw)

    # 4. Đếm số lượng Thực phẩm (Có từ khóa đồ ăn + KHÔNG chứa từ khóa đồ dùng)
    thuc_pham = SanPham.objects.filter(food_query).exclude(exclude_query).count()

    # 5. Đồ gia dụng = Tổng tất cả sản phẩm trừ đi số Thực phẩm
    tong_sp = SanPham.objects.count()
    gia_dung = tong_sp - thuc_pham

    return JsonResponse({
        'thuc_pham': thuc_pham,
        'gia_dung': gia_dung,
        'dang_kinh_doanh': dang_kinh_doanh,
        'ngung_kinh_doanh': ngung_kinh_doanh
    })


def api_search_sanpham(request):
    """API tìm kiếm và phân trang sản phẩm"""
    query = request.GET.get('q', '')
    status = request.GET.get('status', '')
    page_number = request.GET.get('page', 1)

    sps = SanPham.objects.all().order_by('-pk')  # Mới nhất lên đầu

    if query:
        sps = sps.filter(Q(MaSP__icontains=query) | Q(TenSP__icontains=query))
    if status and status != 'Tất cả trạng thái':
        sps = sps.filter(TrangThai=status)

    paginator = Paginator(sps, 5)  # 5 dòng mỗi trang
    page_obj = paginator.get_page(page_number)

    results = []
    for sp in page_obj:
        results.append({
            'MaSP': sp.MaSP,
            'TenSP': sp.TenSP,
            'DVT': sp.DVT,
            'TrangThai': sp.TrangThai
        })

    return JsonResponse({
        'results': results,
        'current_page': page_obj.number,
        'total_pages': paginator.num_pages,
        'has_next': page_obj.has_next(),
        'has_previous': page_obj.has_previous(),
    })


def api_get_sanpham(request, ma_sp):
    """Lấy chi tiết 1 sản phẩm kèm Giá nhập từ Phiếu Nhập"""
    try:
        sp = SanPham.objects.get(MaSP=ma_sp)

        # 1. Tính tồn kho và lấy NCC
        ton_kho = 0
        ten_ncc = "Chưa có thông tin"
        if LoHang.objects.filter(MaSP=sp).exists():
            ton_kho = LoHang.objects.filter(MaSP=sp).aggregate(Sum('SoLuong'))['SoLuong__sum'] or 0
            lo = LoHang.objects.filter(MaSP=sp).first()
            if hasattr(lo, 'MaPhieu') and lo.MaPhieu and lo.MaPhieu.MaNCC:
                ten_ncc = lo.MaPhieu.MaNCC.TenNCC

        # 2. Lấy Giá Nhập thực tế từ bảng ChiTietNhap (lần nhập mới nhất)
        gia_nhap = 0
        ct_nhap = ChiTietNhap.objects.filter(MaLH__MaSP=sp).order_by('-pk').first()
        if ct_nhap:
            gia_nhap = float(ct_nhap.DonGiaNhap)
        elif hasattr(sp, 'GiaNhap'):  # Fallback nếu sản phẩm mới tạo, chưa có phiếu nhập
            gia_nhap = float(sp.GiaNhap)

        return JsonResponse({
            'status': 'success',
            'data': {
                'MaSP': sp.MaSP,
                'TenSP': sp.TenSP,
                'DVT': sp.DVT,
                'GiaNhap': gia_nhap,
                'TrangThai': sp.TrangThai,
                'TonKho': ton_kho,
                'TenNCC': ten_ncc
            }
        })
    except SanPham.DoesNotExist:
        return JsonResponse({'status': 'error', 'message': 'Không tìm thấy sản phẩm!'})


@csrf_exempt
def api_create_sanpham(request):
    """Tạo sản phẩm mới"""
    if request.method == 'POST':
        data = json.loads(request.body)
        try:
            sp = SanPham.objects.create(
                MaSP=data.get('ma_sp'),
                TenSP=data.get('ten_sp'),
                DVT=data.get('dvt'),
                TrangThai="Đang kinh doanh"
            )
            # Lưu Giá nhập khởi tạo (nếu có trường này)
            if hasattr(sp, 'GiaNhap'):
                sp.GiaNhap = data.get('gia_nhap', 0)
                sp.save()
            return JsonResponse({'status': 'success'})
        except Exception as e:
            return JsonResponse({'status': 'error', 'message': str(e)})


@csrf_exempt
def api_update_sanpham(request, ma_sp):
    """Cập nhật sản phẩm"""
    if request.method == 'POST':
        data = json.loads(request.body)
        try:
            sp = SanPham.objects.get(MaSP=ma_sp)
            sp.TenSP = data.get('ten_sp', sp.TenSP)
            sp.DVT = data.get('dvt', sp.DVT)
            sp.TrangThai = data.get('trang_thai', sp.TrangThai)
            if hasattr(sp, 'GiaNhap'):
                sp.GiaNhap = data.get('gia_nhap', sp.GiaNhap)
            sp.save()
            return JsonResponse({'status': 'success'})
        except Exception as e:
            return JsonResponse({'status': 'error', 'message': str(e)})


@csrf_exempt
def api_delete_sanpham(request, ma_sp):
    """Xóa sản phẩm, chặn nếu còn tồn kho"""
    if request.method == 'POST':
        try:
            sp = SanPham.objects.get(MaSP=ma_sp)
            ton_kho = LoHang.objects.filter(MaSP=sp).aggregate(Sum('SoLuong'))['SoLuong__sum'] or 0
            if ton_kho > 0:
                return JsonResponse({'status': 'error_tonkho', 'message': 'Không thể xóa sản phẩm đang có tồn kho'})

            sp.delete()
            return JsonResponse({'status': 'success'})
        except SanPham.DoesNotExist:
            return JsonResponse({'status': 'error', 'message': 'Không tìm thấy sản phẩm!'})


# ==========================================
# CÁC API CHO QUẢN LÝ LÔ HÀNG
# ==========================================

def api_lohang_stats(request):
    """API trả về dữ liệu cho hộp Cảnh báo hết hạn"""
    today = date.today()
    moc_30_ngay = today + timedelta(days=30)

    # Tìm các lô hàng có HSD trong vòng 30 ngày tới hoặc đã hết hạn, và vẫn còn tồn kho
    lo_hang_canh_bao = LoHang.objects.filter(
        HSD__lte=moc_30_ngay,
        SoLuong__gt=0
    ).order_by('HSD')

    danh_sach_lo = []
    for lo in lo_hang_canh_bao[:5]:  # Hiển thị 5 lô gấp nhất
        danh_sach_lo.append({
            'TenSP': lo.MaSP.TenSP if hasattr(lo, 'MaSP') and lo.MaSP else 'Sản phẩm',
            'MaLH': lo.MaLH,
            'HSD': lo.HSD.strftime('%Y-%m-%d') if lo.HSD else '',
            'SoLuong': lo.SoLuong
        })

    return JsonResponse({
        'so_luong_canh_bao': lo_hang_canh_bao.count(),
        'canh_bao_lo_hang': danh_sach_lo
    })


def api_search_lohang(request):
    """API tìm kiếm và phân trang danh sách Lô hàng"""
    query = request.GET.get('q', '')
    status = request.GET.get('status', '')
    page_number = request.GET.get('page', 1)

    los = LoHang.objects.all().order_by('-pk')

    if query:
        los = los.filter(Q(MaLH__icontains=query) | Q(MaSP__TenSP__icontains=query))

    # Tính toán trạng thái dựa trên HSD
    today = date.today()
    moc_30_ngay = today + timedelta(days=30)

    if status == 'Bình thường':
        los = los.filter(HSD__gt=moc_30_ngay)
    elif status == 'Sắp hết hạn':
        los = los.filter(HSD__lte=moc_30_ngay, HSD__gte=today)
    elif status == 'Đã hết hạn':
        los = los.filter(HSD__lt=today)

    paginator = Paginator(los, 5)  # 5 dòng mỗi trang
    page_obj = paginator.get_page(page_number)

    results = []
    for lo in page_obj:
        # Xác định trạng thái để trả về Frontend
        trang_thai = "Bình thường"
        if lo.HSD:
            if lo.HSD < today:
                trang_thai = "Đã hết hạn"
            elif lo.HSD <= moc_30_ngay:
                trang_thai = "Sắp hết hạn"

        # Lấy ngày nhập (Từ Phiếu nhập hoặc trực tiếp từ Lô Hàng)
        ngay_nhap = "N/A"
        if hasattr(lo, 'NgayNhap') and lo.NgayNhap:
            ngay_nhap = lo.NgayNhap.strftime('%d/%m/%Y')
        elif hasattr(lo, 'MaPhieu') and lo.MaPhieu and lo.MaPhieu.NgayNhap:
            ngay_nhap = lo.MaPhieu.NgayNhap.strftime('%d/%m/%Y')

        results.append({
            'MaLH': lo.MaLH,
            'TenSP': lo.MaSP.TenSP if hasattr(lo, 'MaSP') and lo.MaSP else 'N/A',
            'SoLuong': lo.SoLuong,
            'NgayNhap': ngay_nhap,
            'HSD': lo.HSD.strftime('%d/%m/%Y') if lo.HSD else 'N/A',
            'TrangThai': trang_thai
        })

    return JsonResponse({
        'results': results,
        'total_items': paginator.count,  # Tổng số lượng để hiển thị trên tiêu đề
        'current_page': page_obj.number,
        'total_pages': paginator.num_pages,
        'has_next': page_obj.has_next(),
        'has_previous': page_obj.has_previous(),
    })


@csrf_exempt
def api_change_password(request):
    """API xử lý đổi mật khẩu cho người dùng hiện tại"""
    if request.method == 'POST':
        if not request.user.is_authenticated:
            return JsonResponse({'status': 'error', 'message': 'Vui lòng đăng nhập!'})

        data = json.loads(request.body)
        old_pw = data.get('old_pw')
        new_pw = data.get('new_pw')

        user = request.user

        # 1. Kiểm tra mật khẩu cũ
        if not user.check_password(old_pw):
            return JsonResponse({'status': 'error', 'message': 'Mật khẩu cũ không chính xác!'})

        # 2. Cập nhật mật khẩu mới
        user.set_password(new_pw)
        user.save()

        # 3. Cập nhật lại session để không bị đăng xuất tự động
        update_session_auth_hash(request, user)

        return JsonResponse({'status': 'success'})