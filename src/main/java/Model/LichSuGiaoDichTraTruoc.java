package Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LichSuGiaoDichTraTruoc {

    private Integer maLichSu;
    private Integer maKhachHang;
    private Integer maTTT;
    private Integer maHoaDon;
    private LocalDateTime ngayGiaoDich;
    private BigDecimal soTienTang;
    private BigDecimal soTienGiam;
    private BigDecimal tongTien;
    private BigDecimal tienPhaiTra;
    private String ghiChu;

    // Thông tin join
    private String tenKhachHang;
    private String soHoaDon;

    public LichSuGiaoDichTraTruoc() {
    }

    public LichSuGiaoDichTraTruoc(Integer maKhachHang, Integer maHoaDon,
            BigDecimal soTienTang, BigDecimal soTienGiam,
            String ghiChu) {
        this.maKhachHang = maKhachHang;
        this.maHoaDon = maHoaDon;
        this.soTienTang = soTienTang;
        this.soTienGiam = soTienGiam;
        this.ghiChu = ghiChu;
        this.ngayGiaoDich = LocalDateTime.now();
        this.tinhToanTien();
    }

    private void tinhToanTien() {
        if (soTienTang == null) {
            soTienTang = BigDecimal.ZERO;
        }
        if (soTienGiam == null) {
            soTienGiam = BigDecimal.ZERO;
        }

        this.tongTien = soTienTang.subtract(soTienGiam);

        if (tongTien.compareTo(BigDecimal.ZERO) < 0) {
            this.tienPhaiTra = tongTien.abs();
        } else {
            this.tienPhaiTra = BigDecimal.ZERO;
        }
    }

    // Getters and Setters
    public Integer getMaLichSu() {
        return maLichSu;
    }

    public void setMaLichSu(Integer maLichSu) {
        this.maLichSu = maLichSu;
    }

    public Integer getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(Integer maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public Integer getMaTTT() {
        return maTTT;
    }

    public void setMaTTT(Integer maTTT) {
        this.maTTT = maTTT;
    }

    public Integer getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(Integer maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public LocalDateTime getNgayGiaoDich() {
        return ngayGiaoDich;
    }

    public void setNgayGiaoDich(LocalDateTime ngayGiaoDich) {
        this.ngayGiaoDich = ngayGiaoDich;
    }

    public BigDecimal getSoTienTang() {
        return soTienTang;
    }

    public void setSoTienTang(BigDecimal soTienTang) {
        this.soTienTang = soTienTang;
        this.tinhToanTien();
    }

    public BigDecimal getSoTienGiam() {
        return soTienGiam;
    }

    public void setSoTienGiam(BigDecimal soTienGiam) {
        this.soTienGiam = soTienGiam;
        this.tinhToanTien();
    }

    public BigDecimal getTongTien() {
        return tongTien;
    }

    public void setTongTien(BigDecimal tongTien) {
        this.tongTien = tongTien;
    }

    public BigDecimal getTienPhaiTra() {
        return tienPhaiTra;
    }

    public void setTienPhaiTra(BigDecimal tienPhaiTra) {
        this.tienPhaiTra = tienPhaiTra;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public String getTenKhachHang() {
        return tenKhachHang;
    }

    public void setTenKhachHang(String tenKhachHang) {
        this.tenKhachHang = tenKhachHang;
    }

    public String getSoHoaDon() {
        return soHoaDon;
    }

    public void setSoHoaDon(String soHoaDon) {
        this.soHoaDon = soHoaDon;
    }
}
