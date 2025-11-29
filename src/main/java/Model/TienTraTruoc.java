package Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TienTraTruoc {

    private Integer maTTT;
    private Integer maKhachHang;
    private BigDecimal tienThem;
    private BigDecimal soDuHienTai;
    private LocalDateTime ngayCapNhat;

    // Thông tin khách hàng (join)
    private String tenKhachHang;
    private String soDienThoai;

    public TienTraTruoc() {
    }

    public TienTraTruoc(Integer maKhachHang, BigDecimal soDuHienTai) {
        this.maKhachHang = maKhachHang;
        this.soDuHienTai = soDuHienTai;
        this.ngayCapNhat = LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getMaTTT() {
        return maTTT;
    }

    public void setMaTTT(Integer maTTT) {
        this.maTTT = maTTT;
    }

    public Integer getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(Integer maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public BigDecimal getTienThem() {
        return tienThem;
    }

    public void setTienThem(BigDecimal tienThem) {
        this.tienThem = tienThem;
    }

    public BigDecimal getSoDuHienTai() {
        return soDuHienTai;
    }

    public void setSoDuHienTai(BigDecimal soDuHienTai) {
        this.soDuHienTai = soDuHienTai;
    }

    public LocalDateTime getNgayCapNhat() {
        return ngayCapNhat;
    }

    public void setNgayCapNhat(LocalDateTime ngayCapNhat) {
        this.ngayCapNhat = ngayCapNhat;
    }

    public String getTenKhachHang() {
        return tenKhachHang;
    }

    public void setTenKhachHang(String tenKhachHang) {
        this.tenKhachHang = tenKhachHang;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }
}
