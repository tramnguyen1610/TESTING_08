from django.urls import path
from . import views

urlpatterns = [
    # Trang chủ & Đăng nhập
    path('', views.dang_nhap_view, name='dang_nhap'),
    path('dashboard/', views.dashboard_view, name='dashboard'),
    path('dang-xuat/', views.dang_xuat_view, name='dang_xuat'),

    # API
    path('api/dashboard/', views.api_dashboard_data, name='api_dashboard_data'),
    path('api/api_search_ncc/', views.api_search_ncc, name='api_search_ncc'),
    path('api/api_create_ncc/', views.api_create_ncc, name='api_create_ncc'),
    path('api/ncc/<str:ma_ncc>/', views.api_get_ncc, name='api_get_ncc'),
    path('api/ncc/<str:ma_ncc>/update/', views.api_update_ncc, name='api_update_ncc'),
    path('api/ncc/<str:ma_ncc>/delete/', views.api_delete_ncc, name='api_delete_ncc'),
    path('api/nhaphang/stats/', views.api_nhaphang_stats, name='api_nhaphang_stats'),
    path('api/nhaphang/search/', views.api_search_nhaphang, name='api_search_nhaphang'),
    path('api/nhaphang/create/', views.api_create_nhaphang, name='api_create_nhaphang'),
    path('api/nhaphang/<str:ma_phieu>/', views.api_get_nhaphang, name='api_get_nhaphang'),
    path('api/nhaphang/<str:ma_phieu>/update/', views.api_update_nhaphang, name='api_update_nhaphang'),
    path('api/nhaphang/<str:ma_phieu>/delete/', views.api_delete_nhaphang, name='api_delete_nhaphang'),
    path('api/sanpham/stats/', views.api_sanpham_stats, name='api_sanpham_stats'),
    path('api/sanpham/search/', views.api_search_sanpham, name='api_search_sanpham'),
    path('api/sanpham/create/', views.api_create_sanpham, name='api_create_sanpham'),
    path('api/sanpham/<str:ma_sp>/', views.api_get_sanpham, name='api_get_sanpham'),
    path('api/sanpham/<str:ma_sp>/update/', views.api_update_sanpham, name='api_update_sanpham'),
    path('api/sanpham/<str:ma_sp>/delete/', views.api_delete_sanpham, name='api_delete_sanpham'),
    path('api/lohang/stats/', views.api_lohang_stats, name='api_lohang_stats'),
    path('api/lohang/search/', views.api_search_lohang, name='api_search_lohang'),
    path('api/change-password/', views.api_change_password, name='api_change_password'),

    # Các trang quản lý (Đã đồng bộ tên name với templates)
    path('nha-cung-cap/', views.DanhSachNhaCungCapView.as_view(), name='danh_sach_ncc'),
    path('nhap-hang/', views.DanhSachPhieuNhapView.as_view(), name='danh_sach_phieu_nhap'),
    path('lo-hang/', views.DanhSachLoHangView.as_view(), name='danh_sach_lo_hang'),
    path('san-pham/', views.DanhSachSanPhamView.as_view(), name='danh_sach_sp'),
    path('cai-dat/', views.CaiDatView.as_view(), name='cai_dat'),
]