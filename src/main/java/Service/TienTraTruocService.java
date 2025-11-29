package Service;

import Repository.TienTraTruocRepository;
import Repository.LichSuGiaoDichTraTruocRepository;
import Model.TienTraTruoc;
import Model.LichSuGiaoDichTraTruoc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class TienTraTruocService {

    private final TienTraTruocRepository repository;
    private final LichSuGiaoDichTraTruocRepository lichSuRepository;

    public TienTraTruocService() {
        this.repository = new TienTraTruocRepository();
        this.lichSuRepository = new LichSuGiaoDichTraTruocRepository();
    }

    public List<TienTraTruoc> getAllTienTraTruoc() {
        try {
            return repository.getAll();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy danh sách tiền trả trước: " + e.getMessage(), e);
        }
    }

    public TienTraTruoc getTienTraTruocByMaKhachHang(int maKhachHang) {
        try {
            return repository.getByMaKhachHang(maKhachHang);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy thông tin tiền trả trước: " + e.getMessage(), e);
        }
    }

    public boolean taoTaiKhoanTraTruoc(TienTraTruoc tienTraTruoc) {
        try {
            if (repository.existsByMaKhachHang(tienTraTruoc.getMaKhachHang())) {
                throw new RuntimeException("Khách hàng đã có tài khoản trả trước");
            }

            return repository.insert(tienTraTruoc);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo tài khoản trả trước: " + e.getMessage(), e);
        }
    }

    public boolean themTien(int maKhachHang, BigDecimal soTienThem) {
        try {
            if (!repository.existsByMaKhachHang(maKhachHang)) {
                throw new RuntimeException("Khách hàng chưa có tài khoản trả trước");
            }

            boolean success = repository.themTien(maKhachHang, soTienThem);
            
            if (success) {
                TienTraTruoc ttt = repository.getByMaKhachHang(maKhachHang);
                LichSuGiaoDichTraTruoc lichSu = new LichSuGiaoDichTraTruoc(
                    maKhachHang, null, soTienThem, BigDecimal.ZERO, "Nạp thêm tiền"
                );
                lichSu.setMaTTT(ttt.getMaTTT());
                lichSuRepository.insert(lichSu);
            }
            
            return success;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi thêm tiền: " + e.getMessage(), e);
        }
    }

   public boolean xuLyThanhToanHoaDon(int maKhachHang, int maHoaDon, BigDecimal tongTienHoaDon) {
    try {
        TienTraTruoc ttt = repository.getByMaKhachHang(maKhachHang);
        if (ttt == null) {
            return false;
        }

        BigDecimal soDuHienTai = ttt.getSoDuHienTai();
        BigDecimal soTienGiam = tongTienHoaDon;
        BigDecimal soDuConLai = soDuHienTai.subtract(soTienGiam);
        BigDecimal tienPhaiTra = BigDecimal.ZERO;

        // 🔥 SỬA LOGIC TÍNH TOÁN Ở ĐÂY
        if (soDuConLai.compareTo(BigDecimal.ZERO) >= 0) {
            // Số dư đủ để thanh toán toàn bộ
            ttt.setSoDuHienTai(soDuConLai);
            tienPhaiTra = BigDecimal.ZERO;
        } else {
            // Số dư không đủ, chỉ trừ phần có thể
            ttt.setSoDuHienTai(BigDecimal.ZERO);
            tienPhaiTra = soDuConLai.abs(); // Số tiền còn phải trả thêm
        }

        ttt.setNgayCapNhat(LocalDateTime.now());

        boolean updateSuccess = repository.updateSoDu(ttt);
        
        if (updateSuccess) {
            LichSuGiaoDichTraTruoc lichSu = new LichSuGiaoDichTraTruoc();
            lichSu.setMaKhachHang(maKhachHang);
            lichSu.setMaTTT(ttt.getMaTTT());
            lichSu.setMaHoaDon(maHoaDon);
            lichSu.setNgayGiaoDich(LocalDateTime.now());
            lichSu.setSoTienTang(BigDecimal.ZERO);
            lichSu.setSoTienGiam(soTienGiam);
            lichSu.setTongTien(ttt.getSoDuHienTai()); // Số dư mới
            lichSu.setTienPhaiTra(tienPhaiTra);
            lichSu.setGhiChu("Thanh toán hóa đơn #" + maHoaDon);

            lichSuRepository.insert(lichSu);
            
            // 🔥 THÊM LOG GỠ LỖI
            System.out.println("✅ Thanh toán thành công:");
            System.out.println("   - Số dư ban đầu: " + soDuHienTai);
            System.out.println("   - Tổng hóa đơn: " + tongTienHoaDon);
            System.out.println("   - Số dư còn lại: " + ttt.getSoDuHienTai());
            System.out.println("   - Tiền phải trả thêm: " + tienPhaiTra);
        }

        return updateSuccess;
    } catch (Exception e) {
        System.err.println("❌ Lỗi khi xử lý thanh toán: " + e.getMessage());
        e.printStackTrace();
        throw new RuntimeException("Lỗi khi xử lý thanh toán: " + e.getMessage(), e);
    }
}

    public boolean kiemTraTonTai(int maKhachHang) {
        try {
            return repository.existsByMaKhachHang(maKhachHang);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi kiểm tra tồn tại: " + e.getMessage(), e);
        }
    }
    public boolean chinhSuaSoDu(int maKhachHang, BigDecimal soDuMoi, String lyDo) {
    try {
        if (!repository.existsByMaKhachHang(maKhachHang)) {
            throw new RuntimeException("Khách hàng chưa có tài khoản trả trước");
        }

        TienTraTruoc ttt = repository.getByMaKhachHang(maKhachHang);
        BigDecimal soDuCu = ttt.getSoDuHienTai();
        
        boolean success = repository.capNhatSoDu(maKhachHang, soDuMoi);
        
        if (success) {
            // Ghi log lịch sử
            LichSuGiaoDichTraTruoc lichSu = new LichSuGiaoDichTraTruoc();
            lichSu.setMaKhachHang(maKhachHang);
            lichSu.setMaTTT(ttt.getMaTTT());
            lichSu.setNgayGiaoDich(LocalDateTime.now());
            
            if (soDuMoi.compareTo(soDuCu) > 0) {
                // Tăng số dư
                BigDecimal soTienTang = soDuMoi.subtract(soDuCu);
                lichSu.setSoTienTang(soTienTang);
                lichSu.setSoTienGiam(BigDecimal.ZERO);
            } else {
                // Giảm số dư
                BigDecimal soTienGiam = soDuCu.subtract(soDuMoi);
                lichSu.setSoTienTang(BigDecimal.ZERO);
                lichSu.setSoTienGiam(soTienGiam);
            }
            
            lichSu.setTongTien(soDuMoi);
            lichSu.setGhiChu(lyDo);
            
            lichSuRepository.insert(lichSu);
        }
        
        return success;
    } catch (Exception e) {
        throw new RuntimeException("Lỗi khi chỉnh sửa số dư: " + e.getMessage(), e);
    }
}
}