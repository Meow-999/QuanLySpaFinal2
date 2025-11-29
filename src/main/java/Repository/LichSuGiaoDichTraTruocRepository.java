package Repository;

import Data.DataConnection;
import Model.LichSuGiaoDichTraTruoc;

import java.sql.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LichSuGiaoDichTraTruocRepository {

    public List<LichSuGiaoDichTraTruoc> getAll() throws SQLException {
        List<LichSuGiaoDichTraTruoc> list = new ArrayList<>();
        String sql = "SELECT ls.*, kh.HoTen as TenKhachHang, hd.MaHoaDon as SoHoaDon " +
                    "FROM LichSuGiaoDichTraTruoc ls " +
                    "INNER JOIN KhachHang kh ON ls.MaKhachHang = kh.MaKhachHang " +
                    "LEFT JOIN HoaDon hd ON ls.MaHoaDon = hd.MaHoaDon " +
                    "ORDER BY ls.NgayGiaoDich DESC";

        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                LichSuGiaoDichTraTruoc lichSu = mapResultSetToLichSu(rs);
                list.add(lichSu);
            }
        }
        return list;
    }

    public List<LichSuGiaoDichTraTruoc> getByMaKhachHang(int maKhachHang) throws SQLException {
        List<LichSuGiaoDichTraTruoc> list = new ArrayList<>();
        String sql = "SELECT ls.*, kh.HoTen as TenKhachHang, hd.MaHoaDon as SoHoaDon " +
                    "FROM LichSuGiaoDichTraTruoc ls " +
                    "INNER JOIN KhachHang kh ON ls.MaKhachHang = kh.MaKhachHang " +
                    "LEFT JOIN HoaDon hd ON ls.MaHoaDon = hd.MaHoaDon " +
                    "WHERE ls.MaKhachHang = ? " +
                    "ORDER BY ls.NgayGiaoDich DESC";

        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maKhachHang);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LichSuGiaoDichTraTruoc lichSu = mapResultSetToLichSu(rs);
                    list.add(lichSu);
                }
            }
        }
        return list;
    }

    public boolean insert(LichSuGiaoDichTraTruoc lichSu) throws SQLException {
        String sql = "INSERT INTO LichSuGiaoDichTraTruoc (MaKhachHang, MaTTT, MaHoaDon, NgayGiaoDich, " +
                    "SoTienTang, SoTienGiam, TongTien, TienPhaiTra, GhiChu) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, lichSu.getMaKhachHang());
            
            if (lichSu.getMaTTT() != null) {
                stmt.setInt(2, lichSu.getMaTTT());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            
            if (lichSu.getMaHoaDon() != null) {
                stmt.setInt(3, lichSu.getMaHoaDon());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            
            stmt.setTimestamp(4, Timestamp.valueOf(lichSu.getNgayGiaoDich()));
            
            if (lichSu.getSoTienTang() != null) {
                stmt.setBigDecimal(5, lichSu.getSoTienTang());
            } else {
                stmt.setNull(5, Types.DECIMAL);
            }
            
            if (lichSu.getSoTienGiam() != null) {
                stmt.setBigDecimal(6, lichSu.getSoTienGiam());
            } else {
                stmt.setNull(6, Types.DECIMAL);
            }
            
            if (lichSu.getTongTien() != null) {
                stmt.setBigDecimal(7, lichSu.getTongTien());
            } else {
                stmt.setNull(7, Types.DECIMAL);
            }
            
            if (lichSu.getTienPhaiTra() != null) {
                stmt.setBigDecimal(8, lichSu.getTienPhaiTra());
            } else {
                stmt.setNull(8, Types.DECIMAL);
            }
            
            stmt.setString(9, lichSu.getGhiChu());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    private LichSuGiaoDichTraTruoc mapResultSetToLichSu(ResultSet rs) throws SQLException {
        LichSuGiaoDichTraTruoc lichSu = new LichSuGiaoDichTraTruoc();
        lichSu.setMaLichSu(rs.getInt("MaLichSu"));
        lichSu.setMaKhachHang(rs.getInt("MaKhachHang"));
        
        int maTTT = rs.getInt("MaTTT");
        if (!rs.wasNull()) {
            lichSu.setMaTTT(maTTT);
        }
        
        int maHoaDon = rs.getInt("MaHoaDon");
        if (!rs.wasNull()) {
            lichSu.setMaHoaDon(maHoaDon);
        }
        
        Timestamp ngayGiaoDich = rs.getTimestamp("NgayGiaoDich");
        if (ngayGiaoDich != null) {
            lichSu.setNgayGiaoDich(ngayGiaoDich.toLocalDateTime());
        }
        
        BigDecimal soTienTang = rs.getBigDecimal("SoTienTang");
        if (!rs.wasNull()) {
            lichSu.setSoTienTang(soTienTang);
        }
        
        BigDecimal soTienGiam = rs.getBigDecimal("SoTienGiam");
        if (!rs.wasNull()) {
            lichSu.setSoTienGiam(soTienGiam);
        }
        
        BigDecimal tongTien = rs.getBigDecimal("TongTien");
        if (!rs.wasNull()) {
            lichSu.setTongTien(tongTien);
        }
        
        BigDecimal tienPhaiTra = rs.getBigDecimal("TienPhaiTra");
        if (!rs.wasNull()) {
            lichSu.setTienPhaiTra(tienPhaiTra);
        }
        
        lichSu.setGhiChu(rs.getString("GhiChu"));
        lichSu.setTenKhachHang(rs.getString("TenKhachHang"));
        lichSu.setSoHoaDon(rs.getString("SoHoaDon"));
        
        return lichSu;
    }
}