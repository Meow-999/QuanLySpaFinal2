package Service;

import Model.HoaDon;
import Model.ChiTietHoaDon;
import Model.DichVu;
import Repository.HoaDonRepository;
import Data.DataConnection;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class HoaDonService {

    private final HoaDonRepository repository;
    private final DichVuService dichVuService;
    private final TienTraTruocService tienTraTruocService;

    public HoaDonService() {
        this.repository = new HoaDonRepository();
        this.dichVuService = new DichVuService();
        this.tienTraTruocService = new TienTraTruocService();
    }

    public Integer addHoaDonAndReturnId(HoaDon hoaDon) {
        Connection conn = null;
        try {
            conn = DataConnection.getConnection();
            conn.setAutoCommit(false);

            Integer maHoaDon = repository.insertAndReturnId(hoaDon);

            if (maHoaDon != null && maHoaDon > 0) {
                boolean taoChiTietSuccess = repository.taoChiTietTienDichVuTuDong(maHoaDon);

                if (taoChiTietSuccess) {
                    conn.commit();
                    System.out.println("✅ Đã tạo ChiTietTienDichVuCuaNhanVien cho hóa đơn: " + maHoaDon);

                    debugChiTietTienDichVu(maHoaDon);
                    return maHoaDon;
                } else {
                    conn.rollback();
                    System.err.println("❌ Lỗi khi tạo ChiTietTienDichVuCuaNhanVien");
                    return null;
                }
            } else {
                conn.rollback();
                System.err.println("❌ Lỗi khi lưu hóa đơn chính");
                return null;
            }

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            System.err.println("❌ Lỗi khi thêm hóa đơn: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi thêm hóa đơn: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean addHoaDon(HoaDon hoaDon) {
        Integer maHoaDon = addHoaDonAndReturnId(hoaDon);
        return maHoaDon != null && maHoaDon > 0;
    }

    public boolean thanhToanVoiTienTraTruoc(int maHoaDon, int maKhachHang, BigDecimal tongTien) {
        try {
            boolean success = tienTraTruocService.xuLyThanhToanHoaDon(
                    maKhachHang, maHoaDon, tongTien
            );

            if (success) {
                System.out.println("✅ Thanh toán thành công với tiền trả trước cho hóa đơn: " + maHoaDon);
            } else {
                System.out.println("⚠️ Khách hàng không có tài khoản trả trước, chuyển sang thanh toán thường");
            }

            return success;
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi thanh toán với tiền trả trước: " + e.getMessage());
            return false;
        }
    }

    private void debugChiTietTienDichVu(int maHoaDon) {
        try {
            String sql = "SELECT COUNT(*) as count FROM ChiTietTienDichVuCuaNhanVien ct "
                    + "INNER JOIN ChiTietHoaDon cthd ON ct.MaCTHD = cthd.MaCTHD "
                    + "WHERE cthd.MaHoaDon = ?";

            try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, maHoaDon);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    int count = rs.getInt("count");
                    System.out.println("🔍 Đã tạo " + count + " ChiTietTienDichVuCuaNhanVien cho hóa đơn " + maHoaDon);

                    if (count == 0) {
                        debugKhongTaoDuocChiTiet(maHoaDon);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi debug chi tiết tiền dịch vụ: " + e.getMessage());
        }
    }

    private void debugKhongTaoDuocChiTiet(int maHoaDon) {
        try {
            System.out.println("🔍 DEBUG chi tiết cho hóa đơn " + maHoaDon + ":");

            String sqlChiTiet = "SELECT cthd.*, dv.TenDichVu, nv.HoTen as TenNhanVien, dv.MaLoaiDV "
                    + "FROM ChiTietHoaDon cthd "
                    + "LEFT JOIN DichVu dv ON cthd.MaDichVu = dv.MaDichVu "
                    + "LEFT JOIN NhanVien nv ON cthd.MaNhanVien = nv.MaNhanVien "
                    + "WHERE cthd.MaHoaDon = ?";

            try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sqlChiTiet)) {

                stmt.setInt(1, maHoaDon);
                ResultSet rs = stmt.executeQuery();

                boolean hasChiTiet = false;
                while (rs.next()) {
                    hasChiTiet = true;
                    int maDichVu = rs.getInt("MaDichVu");
                    Integer maNhanVien = rs.getInt("MaNhanVien");
                    int maLoaiDV = rs.getInt("MaLoaiDV");

                    System.out.println("  - Dịch vụ: " + rs.getString("TenDichVu")
                            + " (Mã DV: " + maDichVu + ", Loại DV: " + maLoaiDV + ")"
                            + ", NV: " + (maNhanVien > 0 ? maNhanVien : "NULL"));

                    if (maNhanVien > 0) {
                        checkPhanTramDichVu(maLoaiDV, maNhanVien);
                    }
                }

                if (!hasChiTiet) {
                    System.out.println("  ❌ Không có chi tiết hóa đơn nào!");
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi debug chi tiết: " + e.getMessage());
        }
    }

    private void checkPhanTramDichVu(int maLoaiDV, int maNhanVien) {
        try {
            String sql = "SELECT * FROM PhanTramDichVu WHERE MaLoaiDV = ? AND MaNhanVien = ?";

            try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, maLoaiDV);
                stmt.setInt(2, maNhanVien);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    System.out.println("    ✅ Có PhanTramDichVu: " + rs.getBigDecimal("TiLePhanTram") + "%");
                } else {
                    System.err.println("    ❌ KHÔNG có PhanTramDichVu cho LoaiDV " + maLoaiDV + " và NV " + maNhanVien);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi kiểm tra PhanTramDichVu: " + e.getMessage());
        }
    }

    public HoaDon createHoaDonFromDatLich(Map<String, Object> datLichInfo) {
        try {
            HoaDon hoaDon = new HoaDon();

            hoaDon.setMaKhachHang((Integer) datLichInfo.get("maKhachHang"));
            hoaDon.setNgayLap(LocalDateTime.now());
            hoaDon.setGhiChu("Hóa đơn từ lịch hẹn - Giường: " + datLichInfo.get("soHieuGiuong"));

            if (datLichInfo.get("maNhanVienLap") != null) {
                hoaDon.setMaNhanVienLap((Integer) datLichInfo.get("maNhanVienLap"));
                System.out.println("✅ Đã set mã NV lập hóa đơn: " + hoaDon.getMaNhanVienLap());
            }

            BigDecimal tongTien = BigDecimal.ZERO;
            List<ChiTietHoaDon> chiTietList = new ArrayList<>();

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> dichVuList = (List<Map<String, Object>>) datLichInfo.get("dichVu");

            if (dichVuList != null && !dichVuList.isEmpty()) {
                for (Map<String, Object> dichVuInfo : dichVuList) {
                    ChiTietHoaDon chiTiet = new ChiTietHoaDon();
                    chiTiet.setMaDichVu((Integer) dichVuInfo.get("maDichVu"));
                    chiTiet.setSoLuong(1);

                    BigDecimal donGia = (BigDecimal) dichVuInfo.get("gia");
                    chiTiet.setDonGia(donGia);

                    if (dichVuInfo.get("maNhanVien") != null) {
                        chiTiet.setMaNhanVien((Integer) dichVuInfo.get("maNhanVien"));
                        System.out.println("✅ Đã gán mã NV " + chiTiet.getMaNhanVien() + " cho dịch vụ " + chiTiet.getMaDichVu());
                    } else {
                        System.err.println("⚠️ Cảnh báo: Dịch vụ " + chiTiet.getMaDichVu() + " không có mã nhân viên!");
                        if (hoaDon.getMaNhanVienLap() != null) {
                            chiTiet.setMaNhanVien(hoaDon.getMaNhanVienLap());
                            System.out.println("✅ Đã gán mã NV lập hóa đơn làm mặc định: " + chiTiet.getMaNhanVien());
                        }
                    }

                    chiTiet.recalculateThanhTien();

                    tongTien = tongTien.add(chiTiet.getThanhTien());
                    chiTietList.add(chiTiet);
                }
            }

            BigDecimal phiGiuong = calculatePhiGiuong((Integer) datLichInfo.get("soLuongNguoi"));
            if (phiGiuong.compareTo(BigDecimal.ZERO) > 0) {
                ChiTietHoaDon chiTietGiuong = new ChiTietHoaDon();
                chiTietGiuong.setMaDichVu(999);
                chiTietGiuong.setSoLuong(1);
                chiTietGiuong.setDonGia(phiGiuong);
                chiTietGiuong.recalculateThanhTien();
                chiTietGiuong.setDichVu(createDichVuGiuong(phiGiuong));

                chiTietList.add(chiTietGiuong);
                tongTien = tongTien.add(chiTietGiuong.getThanhTien());
            }

            hoaDon.setTongTien(tongTien);
            hoaDon.setChiTietHoaDon(chiTietList);

            System.out.println("✅ Đã tạo hóa đơn với " + chiTietList.size() + " dịch vụ, tổng tiền: " + tongTien);
            return hoaDon;

        } catch (Exception e) {
            System.err.println("❌ Lỗi khi tạo hóa đơn từ lịch hẹn: " + e.getMessage());
            throw new RuntimeException("Lỗi khi tạo hóa đơn từ lịch hẹn: " + e.getMessage(), e);
        }
    }

    public Integer taoHoaDonTuDatLichAndReturnId(Map<String, Object> datLichInfo) {
        Connection conn = null;
        try {
            conn = DataConnection.getConnection();
            conn.setAutoCommit(false);

            HoaDon hoaDon = createHoaDonFromDatLich(datLichInfo);

            Integer maHoaDon = addHoaDonAndReturnId(hoaDon);

            if (maHoaDon != null && maHoaDon > 0) {
                conn.commit();
                System.out.println("✅ Tạo hóa đơn thành công từ lịch hẹn: " + maHoaDon);

                logHoaDonInfo(hoaDon, datLichInfo);
                return maHoaDon;
            } else {
                conn.rollback();
                System.err.println("❌ Lỗi khi lưu hóa đơn chính");
                return null;
            }

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            System.err.println("❌ Lỗi khi tạo hóa đơn từ lịch hẹn: " + e.getMessage());
            return null;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean taoHoaDonTuDatLich(Map<String, Object> datLichInfo) {
        Integer maHoaDon = taoHoaDonTuDatLichAndReturnId(datLichInfo);
        return maHoaDon != null && maHoaDon > 0;
    }

    public List<HoaDon> getAllHoaDon() {
        try {
            return repository.getAll();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy danh sách hóa đơn: " + e.getMessage(), e);
        }
    }

    public HoaDon getHoaDonById(int maHoaDon) {
        try {
            return repository.getById(maHoaDon);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy thông tin hóa đơn: " + e.getMessage(), e);
        }
    }

    public List<HoaDon> getHoaDonByMaKhachHang(int maKhachHang) {
        try {
            return repository.getByMaKhachHang(maKhachHang);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy hóa đơn theo khách hàng: " + e.getMessage(), e);
        }
    }

    public List<HoaDon> getHoaDonTheoKhoangThoiGian(LocalDateTime tuNgay, LocalDateTime denNgay) {
        try {
            return repository.getHoaDonTheoKhoangThoiGian(tuNgay, denNgay);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy hóa đơn theo khoảng thời gian: " + e.getMessage(), e);
        }
    }

    public boolean updateHoaDon(HoaDon hoaDon) {
        try {
            return repository.update(hoaDon);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi cập nhật hóa đơn: " + e.getMessage(), e);
        }
    }

    public boolean deleteHoaDon(int maHoaDon) {
        try {
            return repository.delete(maHoaDon);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xóa hóa đơn: " + e.getMessage(), e);
        }
    }

    public BigDecimal getTongDoanhThuTheoThang(int thang, int nam) {
        try {
            return repository.getTongDoanhThuTheoThang(thang, nam);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy tổng doanh thu: " + e.getMessage(), e);
        }
    }

    private BigDecimal calculatePhiGiuong(Integer soLuongNguoi) {
        if (soLuongNguoi == null || soLuongNguoi == 1) {
            return BigDecimal.ZERO;
        }
        BigDecimal phiCoBan = new BigDecimal("50000");
        return phiCoBan.multiply(BigDecimal.valueOf(soLuongNguoi - 1));
    }

    private DichVu createDichVuGiuong(BigDecimal phiGiuong) {
        DichVu dichVu = new DichVu();
        dichVu.setMaDichVu(999);
        dichVu.setTenDichVu("Phí giường thêm");
        dichVu.setGia(phiGiuong);
        return dichVu;
    }

    private void logHoaDonInfo(HoaDon hoaDon, Map<String, Object> datLichInfo) {
        System.out.println("=== THÔNG TIN HÓA ĐƠN ===");
        System.out.println("Mã hóa đơn: " + hoaDon.getMaHoaDon());
        System.out.println("Mã khách hàng: " + hoaDon.getMaKhachHang());
        System.out.println("Mã NV lập: " + hoaDon.getMaNhanVienLap());
        System.out.println("Tổng tiền: " + hoaDon.getTongTien());

        if (hoaDon.hasChiTiet()) {
            System.out.println("Chi tiết dịch vụ:");
            for (ChiTietHoaDon chiTiet : hoaDon.getChiTietHoaDon()) {
                String tenDichVu = chiTiet.getDichVu() != null ? chiTiet.getDichVu().getTenDichVu() : "Không xác định";
                System.out.println("  - " + tenDichVu + " (NV: " + chiTiet.getMaNhanVien() + "): "
                        + chiTiet.getDonGia() + " x " + chiTiet.getSoLuong() + " = " + chiTiet.getThanhTien());
            }
        }
        System.out.println("========================");
    }

    public BigDecimal tinhTongTienTuChiTiet(List<ChiTietHoaDon> chiTietList) {
        if (chiTietList == null || chiTietList.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return chiTietList.stream()
                .map(ChiTietHoaDon::getThanhTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}