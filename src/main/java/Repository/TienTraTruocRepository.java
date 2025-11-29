package Repository;

import Data.DataConnection;
import Model.TienTraTruoc;

import java.sql.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TienTraTruocRepository {

    public List<TienTraTruoc> getAll() throws SQLException {
        List<TienTraTruoc> list = new ArrayList<>();
        String sql = "SELECT ttt.*, kh.HoTen as TenKhachHang, kh.SoDienThoai " +
                    "FROM TienTraTruoc ttt " +
                    "INNER JOIN KhachHang kh ON ttt.MaKhachHang = kh.MaKhachHang " +
                    "ORDER BY ttt.NgayCapNhat DESC";

        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                TienTraTruoc tienTraTruoc = mapResultSetToTienTraTruoc(rs);
                list.add(tienTraTruoc);
            }
        }
        return list;
    }

    public TienTraTruoc getByMaKhachHang(int maKhachHang) throws SQLException {
        String sql = "SELECT ttt.*, kh.HoTen as TenKhachHang, kh.SoDienThoai " +
                    "FROM TienTraTruoc ttt " +
                    "INNER JOIN KhachHang kh ON ttt.MaKhachHang = kh.MaKhachHang " +
                    "WHERE ttt.MaKhachHang = ?";

        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maKhachHang);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTienTraTruoc(rs);
                }
            }
        }
        return null;
    }

    public boolean insert(TienTraTruoc tienTraTruoc) throws SQLException {
        String sql = "INSERT INTO TienTraTruoc (MaKhachHang, TienThem, SoDuHienTai, NgayCapNhat) " +
                    "VALUES (?, ?, ?, ?)";

        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, tienTraTruoc.getMaKhachHang());
            
            if (tienTraTruoc.getTienThem() != null) {
                stmt.setBigDecimal(2, tienTraTruoc.getTienThem());
            } else {
                stmt.setNull(2, Types.DECIMAL);
            }
            
            stmt.setBigDecimal(3, tienTraTruoc.getSoDuHienTai());
            stmt.setTimestamp(4, Timestamp.valueOf(tienTraTruoc.getNgayCapNhat()));

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public boolean updateSoDu(TienTraTruoc tienTraTruoc) throws SQLException {
        String sql = "UPDATE TienTraTruoc SET SoDuHienTai = ?, NgayCapNhat = ? WHERE MaKhachHang = ?";

        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, tienTraTruoc.getSoDuHienTai());
            stmt.setTimestamp(2, Timestamp.valueOf(tienTraTruoc.getNgayCapNhat()));
            stmt.setInt(3, tienTraTruoc.getMaKhachHang());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public boolean themTien(int maKhachHang, BigDecimal soTienThem) throws SQLException {
        String sql = "UPDATE TienTraTruoc SET TienThem = ?, SoDuHienTai = SoDuHienTai + ?, NgayCapNhat = ? WHERE MaKhachHang = ?";

        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, soTienThem);
            stmt.setBigDecimal(2, soTienThem);
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(4, maKhachHang);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public boolean existsByMaKhachHang(int maKhachHang) throws SQLException {
        String sql = "SELECT 1 FROM TienTraTruoc WHERE MaKhachHang = ?";

        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maKhachHang);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private TienTraTruoc mapResultSetToTienTraTruoc(ResultSet rs) throws SQLException {
        TienTraTruoc tienTraTruoc = new TienTraTruoc();
        tienTraTruoc.setMaTTT(rs.getInt("MaTTT"));
        tienTraTruoc.setMaKhachHang(rs.getInt("MaKhachHang"));
        
        BigDecimal tienThem = rs.getBigDecimal("TienThem");
        if (!rs.wasNull()) {
            tienTraTruoc.setTienThem(tienThem);
        }
        
        tienTraTruoc.setSoDuHienTai(rs.getBigDecimal("SoDuHienTai"));
        
        Timestamp ngayCapNhat = rs.getTimestamp("NgayCapNhat");
        if (ngayCapNhat != null) {
            tienTraTruoc.setNgayCapNhat(ngayCapNhat.toLocalDateTime());
        }
        
        tienTraTruoc.setTenKhachHang(rs.getString("TenKhachHang"));
        tienTraTruoc.setSoDienThoai(rs.getString("SoDienThoai"));
        
        return tienTraTruoc;
    }
   
public boolean capNhatSoDu(int maKhachHang, BigDecimal soDuMoi) throws SQLException {
    String sql = "UPDATE TienTraTruoc SET SoDuHienTai = ?, TienThem = ?, NgayCapNhat = ? WHERE MaKhachHang = ?";

    try (Connection conn = DataConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setBigDecimal(1, soDuMoi);
        stmt.setNull(2, Types.DECIMAL); // Đặt TienThem thành null khi chỉnh sửa số dư
        stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
        stmt.setInt(4, maKhachHang);

        int affectedRows = stmt.executeUpdate();
        return affectedRows > 0;
    }
}
}