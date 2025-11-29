package Controller;

import Data.DataConnection;
import Model.*;
import Service.*;
import ShareInfo.Auth;
import View.DatDichVuView;
import View.KhachHangDialog;
import View.TimKhachHangView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Date;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Desktop;

// iText imports
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.Font;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DatDichVuController {

    private DatDichVuView view;
    private KhachHangService khachHangService;
    private DichVuService dichVuService;
    private NhanVienService nhanVienService;
    private HoaDonService hoaDonService;
    private boolean cheDoChinhSua = false;
    private Integer maHoaDonChinhSua = null;

    private NumberFormat currencyFormat;
    private KhachHang khachHangHienTai;
    private BigDecimal tongTien;

    public DatDichVuController(DatDichVuView view) {
        this.view = view;
        this.khachHangService = new KhachHangService();
        this.dichVuService = new DichVuService();
        this.nhanVienService = new NhanVienService();
        this.hoaDonService = new HoaDonService();

        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        this.tongTien = BigDecimal.ZERO;

        initController();
        loadDuLieuBanDau();
    }

    public void khoiTaoCheDoChinhSua(Integer maHoaDon) {
        this.cheDoChinhSua = true;
        this.maHoaDonChinhSua = maHoaDon;
        loadDuLieuHoaDonChinhSua(maHoaDon);
        // Trong khoiTaoCheDoChinhSua method của controller
        view.setCheDoChinhSua(true, maHoaDon);
    }

    private void initController() {
        // Sự kiện cho combobox khách hàng
        view.getCboKhachHang().addActionListener(e -> handleChonKhachHang());

        // Sự kiện cho combobox dịch vụ
        view.getCboDichVu().addActionListener(e -> handleChonDichVu());

        // Sự kiện cho nút thêm khách hàng
        view.getBtnThemKhachHang().addActionListener(e -> handleThemKhachHang());

        // Sự kiện cho nút thêm dịch vụ vào danh sách
        view.getBtnThemDichVu().addActionListener(e -> handleThemDichVuVaoDanhSach());

        // Sự kiện cho nút xóa dịch vụ
        view.getBtnXoaDichVu().addActionListener(e -> handleXoaDichVu());

        // Sự kiện cho nút đổi điểm
        view.getBtnDoiDiem().addActionListener(e -> handleDoiDiem());

        // Sự kiện cho nút in hóa đơn
        view.getBtnInHoaDon().addActionListener(e -> handleInHoaDon());

        // Sự kiện cho nút làm mới
        view.getBtnLamMoi().addActionListener(e -> handleLamMoi());

        view.getBtnTimKhachHang().addActionListener(e -> handleTimKiemKhachHang());
    }

    private void handleTimKiemKhachHang() {
        TimKhachHangView timKhachHangView = new TimKhachHangView((Frame) SwingUtilities.getWindowAncestor(view));
        TimKhachHangController timKhachHangController = new TimKhachHangController(timKhachHangView, this);
        timKhachHangView.setVisible(true);
    }

    public void capNhatKhachHangDuocChon(Integer maKhachHang, String tenKhachHang, String soDienThoai) {
        try {
            // Tìm khách hàng trong combobox
            DefaultComboBoxModel<KhachHang> model = (DefaultComboBoxModel<KhachHang>) view.getCboKhachHang().getModel();

            for (int i = 0; i < model.getSize(); i++) {
                KhachHang kh = model.getElementAt(i);
                if (kh.getMaKhachHang() != null && kh.getMaKhachHang().equals(maKhachHang)) {
                    view.getCboKhachHang().setSelectedIndex(i);

                    // Cập nhật thông tin khách hàng hiện tại
                    khachHangHienTai = kh;
                    if (khachHangHienTai != null) {
                        view.getLblDiemTichLuy().setText(khachHangHienTai.getDiemTichLuy() + " điểm");
                    }

                    JOptionPane.showMessageDialog(view,
                            "Đã chọn khách hàng: " + tenKhachHang,
                            "Thành công",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            }

            // Nếu không tìm thấy trong combobox, thêm mới và chọn
            KhachHang khachHangMoi = khachHangService.getKhachHangById(maKhachHang);
            if (khachHangMoi != null) {
                model.addElement(khachHangMoi);
                view.getCboKhachHang().setSelectedItem(khachHangMoi);

                // Cập nhật thông tin khách hàng hiện tại
                khachHangHienTai = khachHangMoi;
                view.getLblDiemTichLuy().setText(khachHangHienTai.getDiemTichLuy() + " điểm");

                JOptionPane.showMessageDialog(view,
                        "Đã thêm và chọn khách hàng: " + tenKhachHang,
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view,
                    "Lỗi khi cập nhật khách hàng: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadDuLieuHoaDonChinhSua(Integer maHoaDon) {
        try {
            HoaDon hoaDon = hoaDonService.getHoaDonById(maHoaDon);
            if (hoaDon != null) {
                // Tải thông tin khách hàng
                khachHangHienTai = khachHangService.getKhachHangById(hoaDon.getMaKhachHang());
                if (khachHangHienTai != null) {
                    // Tìm và chọn khách hàng trong combobox
                    DefaultComboBoxModel<KhachHang> model = (DefaultComboBoxModel<KhachHang>) view.getCboKhachHang().getModel();
                    for (int i = 0; i < model.getSize(); i++) {
                        KhachHang kh = model.getElementAt(i);
                        if (kh.getMaKhachHang() != null && kh.getMaKhachHang().equals(khachHangHienTai.getMaKhachHang())) {
                            view.getCboKhachHang().setSelectedIndex(i);
                            break;
                        }
                    }
                    view.getLblDiemTichLuy().setText(khachHangHienTai.getDiemTichLuy() + " điểm");
                }

                // Tải chi tiết hóa đơn vào bảng
                List<ChiTietHoaDon> chiTietList = hoaDon.getChiTietHoaDon();
                DefaultTableModel model = view.getTableModel();
                model.setRowCount(0); // Xóa dữ liệu cũ

                tongTien = BigDecimal.ZERO;

                for (ChiTietHoaDon chiTiet : chiTietList) {
                    DichVu dichVu = dichVuService.getDichVuById(chiTiet.getMaDichVu());
                    if (dichVu != null) {
                        int stt = model.getRowCount() + 1;
                        int soLuong = chiTiet.getSoLuong();
                        BigDecimal thanhTien = chiTiet.getDonGia().multiply(BigDecimal.valueOf(soLuong));

                        // Lấy thông tin nhân viên từ hóa đơn chính
                        String tenNhanVien = "Chưa xác định";
                        if (hoaDon.getMaNhanVienLap() != null) {
                            NhanVien nv = nhanVienService.getNhanVienById(hoaDon.getMaNhanVienLap());
                            if (nv != null) {
                                tenNhanVien = nv.getHoTen();
                            }
                        }

                        model.addRow(new Object[]{
                            stt,
                            dichVu.getTenDichVu(),
                            dichVu.getThoiGian() + " phút",
                            currencyFormat.format(chiTiet.getDonGia()),
                            soLuong,
                            tenNhanVien,
                            currencyFormat.format(thanhTien)
                        });

                        tongTien = tongTien.add(thanhTien);
                    }
                }

                capNhatTongTien();

                // Cập nhật combobox nhân viên
                if (hoaDon.getMaNhanVienLap() != null) {
                    NhanVien nv = nhanVienService.getNhanVienById(hoaDon.getMaNhanVienLap());
                    if (nv != null) {
                        for (int i = 0; i < view.getCboNhanVien().getItemCount(); i++) {
                            String item = view.getCboNhanVien().getItemAt(i);
                            if (item.contains(nv.getHoTen())) {
                                view.getCboNhanVien().setSelectedIndex(i);
                                break;
                            }
                        }
                    }
                }

                // Cập nhật tiêu đề để biết đang ở chế độ chỉnh sửa
                view.setCheDoChinhSua(true, maHoaDon);

                JOptionPane.showMessageDialog(view,
                        "Đã tải hóa đơn #" + maHoaDon + " để chỉnh sửa.\nTổng tiền hiện tại: " + currencyFormat.format(tongTien),
                        "Chế độ chỉnh sửa",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(view, "Không tìm thấy hóa đơn với mã: " + maHoaDon,
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi khi tải hóa đơn: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadDuLieuBanDau() {
        loadKhachHang();
        loadDichVu();
        loadNhanVien();
        capNhatTongTien();
    }

    private void loadKhachHang() {
        try {
            List<KhachHang> khachHangs = khachHangService.getAllKhachHang();
            view.getCboKhachHang().removeAllItems();
            view.getCboKhachHang().addItem(new KhachHang());

            for (KhachHang kh : khachHangs) {
                view.getCboKhachHang().addItem(kh);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi khi tải danh sách khách hàng: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadDichVu() {
        try {
            List<DichVu> dsDichVu = dichVuService.getAllDichVu();
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            model.addElement("-- Chọn dịch vụ --");

            for (DichVu dv : dsDichVu) {
                model.addElement(dv.getTenDichVu() + " - " + currencyFormat.format(dv.getGia()));
            }

            view.getCboDichVu().setModel(model);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi khi tải danh sách dịch vụ: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadNhanVien() {
        try {
            List<NhanVien> dsNhanVien = nhanVienService.getAllNhanVien();
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            model.addElement("-- Chọn nhân viên --");

            for (NhanVien nv : dsNhanVien) {
                model.addElement(nv.getHoTen() + " - " + nv.getChucVu() + " - " + nv.getMaNhanVien());
            }

            view.getCboNhanVien().setModel(model);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi khi tải danh sách nhân viên: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

  private void handleChonKhachHang() {
    KhachHang selectedKhachHang = (KhachHang) view.getCboKhachHang().getSelectedItem();
    
    if (selectedKhachHang != null && selectedKhachHang.getMaKhachHang() != null) {
        try {
            khachHangHienTai = selectedKhachHang;
            if (khachHangHienTai != null) {
                view.getLblDiemTichLuy().setText(khachHangHienTai.getDiemTichLuy() + " điểm");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi khi lấy thông tin khách hàng: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    } else {
        // Nếu chọn "-- Chọn khách hàng --" hoặc item rỗng
        khachHangHienTai = null;
        view.getLblDiemTichLuy().setText("0 điểm");
    }
}
    private void handleChonDichVu() {
        int selectedIndex = view.getCboDichVu().getSelectedIndex();
        if (selectedIndex > 0) {
            try {
                String selected = (String) view.getCboDichVu().getSelectedItem();
                String tenDichVu = selected.split(" - ")[0];

                List<DichVu> dsDichVu = dichVuService.getAllDichVu();
                for (DichVu dv : dsDichVu) {
                    if (dv.getTenDichVu().equals(tenDichVu)) {
                        view.getTxtThoiGian().setText(dv.getThoiGian() + " phút");
                        break;
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Lỗi khi lấy thông tin dịch vụ: " + e.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleThemKhachHang() {
        KhachHangDialog dialog = new KhachHangDialog((Frame) SwingUtilities.getWindowAncestor(view));
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            if (validateKhachHangForm(dialog.getHoTen(), dialog.getSoDienThoai())) {
                try {
                    KhachHang khachHangMoi = new KhachHang();
                    khachHangMoi.setHoTen(dialog.getHoTen());
                    khachHangMoi.setSoDienThoai(dialog.getSoDienThoai());

                    // Xử lý ngày sinh
                    java.util.Date ngaySinhUtil = dialog.getNgaySinh();
                    if (ngaySinhUtil != null) {
                        java.time.LocalDate ngaySinh = ngaySinhUtil.toInstant()
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate();
                        khachHangMoi.setNgaySinh(ngaySinh);
                    }

                    khachHangMoi.setLoaiKhach(dialog.getLoaiKhach());
                    khachHangMoi.setGhiChu(dialog.getGhiChu());
                    khachHangMoi.setDiemTichLuy(0);
                    khachHangMoi.setNgayTao(java.time.LocalDateTime.now());

                    boolean success = khachHangService.addKhachHang(khachHangMoi);
                    if (success) {
                        JOptionPane.showMessageDialog(view, "Thêm khách hàng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        loadKhachHang(); // Tải lại danh sách khách hàng

                        // Tự động chọn khách hàng vừa thêm
                        DefaultComboBoxModel<KhachHang> model = (DefaultComboBoxModel<KhachHang>) view.getCboKhachHang().getModel();
                        for (int i = 0; i < model.getSize(); i++) {
                            KhachHang kh = model.getElementAt(i);
                            if (kh.getMaKhachHang() != null
                                    && kh.getSoDienThoai() != null
                                    && kh.getSoDienThoai().equals(dialog.getSoDienThoai())) {
                                view.getCboKhachHang().setSelectedIndex(i);
                                break;
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(view, "Thêm khách hàng thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(view, "Lỗi khi thêm khách hàng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        }
    }

    // Trong phương thức validateKhachHangForm()
    private boolean validateKhachHangForm(String hoTen, String soDienThoai) {
        if (hoTen.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập họ tên khách hàng", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Số điện thoại không bắt buộc, chỉ validate nếu có nhập
        if (!soDienThoai.isEmpty() && !soDienThoai.matches("\\d{10,11}")) {
            JOptionPane.showMessageDialog(view, "Số điện thoại không hợp lệ (10-11 số) hoặc để trống", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Kiểm tra số điện thoại đã tồn tại chưa (chỉ khi có nhập)
        if (!soDienThoai.isEmpty()) {
            try {
                KhachHang khachHang = khachHangService.getKhachHangBySoDienThoai(soDienThoai);
                if (khachHang != null) {
                    JOptionPane.showMessageDialog(view, "Số điện thoại đã tồn tại trong hệ thống", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } catch (Exception e) {
                // Bỏ qua lỗi nếu không tìm thấy
            }
        }

        return true;
    }

    private void handleThemDichVuVaoDanhSach() {
        if (!kiemTraDuLieuHopLe()) {
            return;
        }

        try {
            String selectedDichVu = (String) view.getCboDichVu().getSelectedItem();
            String selectedNhanVien = (String) view.getCboNhanVien().getSelectedItem();

            String tenDichVu = selectedDichVu.split(" - ")[0];
            String tenNhanVien = selectedNhanVien.split(" - ")[0];
            String thoiGian = view.getTxtThoiGian().getText();

            // Lấy giá từ dịch vụ
            BigDecimal donGia = BigDecimal.ZERO;
            List<DichVu> dsDichVu = dichVuService.getAllDichVu();
            for (DichVu dv : dsDichVu) {
                if (dv.getTenDichVu().equals(tenDichVu)) {
                    donGia = dv.getGia();
                    break;
                }
            }

            int soLuong = Integer.parseInt(view.getTxtSoLuongNguoi().getText());
            BigDecimal thanhTien = donGia.multiply(BigDecimal.valueOf(soLuong));

            // Thêm vào bảng
            DefaultTableModel model = view.getTableModel();
            int stt = model.getRowCount() + 1;
            model.addRow(new Object[]{
                stt,
                tenDichVu,
                thoiGian,
                currencyFormat.format(donGia),
                soLuong,
                tenNhanVien,
                currencyFormat.format(thanhTien)
            });

            // Cập nhật tổng tiền
            tongTien = tongTien.add(thanhTien);
            capNhatTongTien();

            // Reset các trường nhập
            view.getCboDichVu().setSelectedIndex(0);
            view.getTxtThoiGian().setText("");
            view.getTxtSoLuongNguoi().setText("1");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi khi thêm dịch vụ: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleXoaDichVu() {
        int selectedRow = view.getTblDichVuDaChon().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn dịch vụ cần xóa",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Lấy thành tiền từ dòng được chọn
            String thanhTienStr = (String) view.getTableModel().getValueAt(selectedRow, 6);
            BigDecimal thanhTien = new BigDecimal(thanhTienStr.replaceAll("[^\\d]", ""));

            // Xóa dòng
            view.getTableModel().removeRow(selectedRow);

            // Cập nhật tổng tiền
            tongTien = tongTien.subtract(thanhTien);
            capNhatTongTien();

            // Cập nhật lại STT
            capNhatSTT();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi khi xóa dịch vụ: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDoiDiem() {
        if (khachHangHienTai == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn khách hàng trước khi đổi điểm",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int diemHienCo = khachHangHienTai.getDiemTichLuy();

        // KIỂM TRA TỐI THIỂU 10 ĐIỂM
        if (diemHienCo < 10) {
            JOptionPane.showMessageDialog(view,
                    "Cần tối thiểu 10 điểm để đổi 1 vé gọi đầu!\n"
                    + "Số điểm hiện có: " + diemHienCo + " điểm\n"
                    + "Còn thiếu: " + (10 - diemHienCo) + " điểm",
                    "Không đủ điểm", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // TÍNH SỐ VÉ TỐI ĐA CÓ THỂ ĐỔI
        int soVeToiDa = diemHienCo / 10;

        String input = JOptionPane.showInputDialog(
                view,
                "Số điểm hiện có: " + diemHienCo + " điểm\n"
                + "Tỷ lệ đổi: 10 điểm = 1 vé gọi đầu\n"
                + "Số vé tối đa có thể đổi: " + soVeToiDa + " vé\n"
                + "Nhập số vé muốn đổi:",
                "Đổi điểm tích lũy",
                JOptionPane.QUESTION_MESSAGE
        );

        if (input != null && !input.trim().isEmpty()) {
            try {
                int soVeMuonDoi = Integer.parseInt(input.trim());

                if (soVeMuonDoi <= 0) {
                    JOptionPane.showMessageDialog(view, "Số vé phải lớn hơn 0",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // TÍNH SỐ ĐIỂM CẦN ĐỔI
                int diemCanDoi = soVeMuonDoi * 10;

                if (diemCanDoi > diemHienCo) {
                    JOptionPane.showMessageDialog(view,
                            "Không đủ điểm để đổi!\n"
                            + "Điểm cần: " + diemCanDoi + " điểm\n"
                            + "Điểm hiện có: " + diemHienCo + " điểm\n"
                            + "Số vé tối đa có thể đổi: " + soVeToiDa + " vé",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Thêm dịch vụ "Vé gọi đầu" vào danh sách
                DefaultTableModel model = view.getTableModel();
                int stt = model.getRowCount() + 1;
                model.addRow(new Object[]{
                    stt,
                    "Vé gọi đầu (đổi " + diemCanDoi + " điểm)",
                    "0 phút",
                    currencyFormat.format(0),
                    soVeMuonDoi, // Số lượng vé
                    getNhanVienInfoFromTable(),
                    currencyFormat.format(0)
                });

                // Cập nhật điểm tích lũy TRONG DATABASE
                int diemMoi = diemHienCo - diemCanDoi;
                khachHangHienTai.setDiemTichLuy(diemMoi);

                // QUAN TRỌNG: Cập nhật vào database
                boolean updateSuccess = khachHangService.updateKhachHang(khachHangHienTai);

                if (updateSuccess) {
                    // Cập nhật trên giao diện
                    view.getLblDiemTichLuy().setText(diemMoi + " điểm");

                    JOptionPane.showMessageDialog(view,
                            "Đổi điểm thành công!\n"
                            + "Đã đổi " + diemCanDoi + " điểm để nhận " + soVeMuonDoi + " vé gọi đầu\n"
                            + "Số điểm còn lại: " + diemMoi + " điểm",
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(view,
                            "Lỗi khi cập nhật điểm tích lũy trong database!",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    // Rollback: xóa dòng vừa thêm nếu cập nhật database thất bại
                    model.removeRow(model.getRowCount() - 1);
                }

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(view, "Số vé không hợp lệ",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Lỗi khi đổi điểm: " + e.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleInHoaDon() {
        if (view.getTableModel().getRowCount() == 0) {
            JOptionPane.showMessageDialog(view, "Vui lòng thêm dịch vụ trước khi in hóa đơn",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (khachHangHienTai == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn khách hàng trước khi in hóa đơn",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String message;
            String title;
            if (cheDoChinhSua) {
                message = "Bạn có muốn CẬP NHẬT hóa đơn #" + maHoaDonChinhSua + "?\n"
                        + "Khách hàng: " + getTenKhachHangHienTai() + "\n"
                        + "Tổng tiền mới: " + currencyFormat.format(tongTien) + "\n"
                        + "Số dịch vụ: " + view.getTableModel().getRowCount();
                title = "Xác nhận cập nhật hóa đơn";
            } else {
                message = "Bạn có muốn in hóa đơn PDF?\n"
                        + "Khách hàng: " + getTenKhachHangHienTai() + "\n"
                        + "Tổng tiền: " + currencyFormat.format(tongTien) + "\n"
                        + "Số dịch vụ: " + view.getTableModel().getRowCount();
                title = "Xác nhận in hóa đơn PDF";
            }

            int confirm = JOptionPane.showConfirmDialog(
                    view,
                    message,
                    title,
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            boolean success;
            if (cheDoChinhSua) {
                // Cập nhật hóa đơn hiện có
                success = capNhatHoaDonTrongDatabase();
            } else {
                // Tạo hóa đơn mới
                success = luuHoaDonVaoDatabase();
            }

            if (!success) {
                JOptionPane.showMessageDialog(view, "Lỗi khi lưu hóa đơn vào cơ sở dữ liệu!",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Tạo và in hóa đơn PDF
            boolean pdfSuccess = inHoaDonPDF();

            if (pdfSuccess) {
                // Cập nhật điểm tích lũy cho khách hàng (100.000 VND = 1 điểm)
                int diemThuong = tongTien.divideToIntegralValue(BigDecimal.valueOf(100000)).intValue();
                if (diemThuong > 0) {
                    int diemMoi = khachHangHienTai.getDiemTichLuy() + diemThuong;
                    khachHangHienTai.setDiemTichLuy(diemMoi);
                    khachHangService.updateKhachHang(khachHangHienTai);
                    view.getLblDiemTichLuy().setText(diemMoi + " điểm");

                    JOptionPane.showMessageDialog(view,
                            (cheDoChinhSua ? "Cập nhật hóa đơn" : "In hóa đơn") + " thành công!\n"
                            + "Khách hàng được thưởng " + diemThuong + " điểm tích lũy!",
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(view,
                            (cheDoChinhSua ? "Cập nhật hóa đơn" : "In hóa đơn") + " thành công!",
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                }

                // Reset form sau khi in/cập nhật hóa đơn
                handleLamMoi();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi khi xử lý hóa đơn: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean capNhatHoaDonTrongDatabase() {
        try {
            // 1. Lấy hóa đơn hiện có
            HoaDon hoaDon = hoaDonService.getHoaDonById(maHoaDonChinhSua);
            if (hoaDon == null) {
                JOptionPane.showMessageDialog(view, "Không tìm thấy hóa đơn để cập nhật!",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // 2. Cập nhật thông tin hóa đơn
            hoaDon.setTongTien(tongTien);
            hoaDon.setNgayLap(java.time.LocalDateTime.now());

            // Lấy mã nhân viên từ combobox
            String selectedNhanVien = (String) view.getCboNhanVien().getSelectedItem();
            if (selectedNhanVien != null && !selectedNhanVien.equals("-- Chọn nhân viên --")) {
                Integer maNhanVien = getMaNhanVienTheoTen(selectedNhanVien.split(" - ")[0]);
                hoaDon.setMaNhanVienLap(maNhanVien);
            }

            hoaDon.setGhiChu("Hóa đơn dịch vụ spa - Đã cập nhật - "
                    + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));

            // 3. Tạo danh sách chi tiết hóa đơn mới
            List<ChiTietHoaDon> chiTietList = new ArrayList<>();
            DefaultTableModel model = view.getTableModel();

            for (int i = 0; i < model.getRowCount(); i++) {
                String tenDichVu = model.getValueAt(i, 1).toString();

                // Bỏ qua dịch vụ "Vé gọi đầu" khi lưu database
                if (tenDichVu.contains("Vé gọi đầu")) {
                    continue;
                }

                int soLuong = Integer.parseInt(model.getValueAt(i, 4).toString());

                // Lấy đơn giá từ chuỗi format (ví dụ: "100,000 VND")
                String donGiaStr = model.getValueAt(i, 3).toString().replaceAll("[^\\d]", "");
                BigDecimal donGia = new BigDecimal(donGiaStr);

                // Tìm mã dịch vụ theo tên
                Integer maDichVu = getMaDichVuTheoTen(tenDichVu);
                if (maDichVu != null) {
                    ChiTietHoaDon chiTiet = new ChiTietHoaDon();
                    chiTiet.setMaDichVu(maDichVu);
                    chiTiet.setSoLuong(soLuong);
                    chiTiet.setDonGia(donGia);

                    // THÊM MÃ NHÂN VIÊN TỪ BẢNG
                    String tenNhanVien = model.getValueAt(i, 5).toString();
                    Integer maNhanVien = getMaNhanVienTheoTen(tenNhanVien);
                    chiTiet.setMaNhanVien(maNhanVien);

                    chiTietList.add(chiTiet);
                }
            }

            // 4. Đặt danh sách chi tiết mới vào hóa đơn
            hoaDon.setChiTietHoaDon(chiTietList);

            // 5. Cập nhật hóa đơn trong database (sử dụng phương thức update đã có)
            boolean success = hoaDonService.updateHoaDon(hoaDon);

            if (success) {
                System.out.println("Đã cập nhật hóa đơn #" + maHoaDonChinhSua + " thành công!");
                return true;
            } else {
                System.out.println("Lỗi khi cập nhật hóa đơn #" + maHoaDonChinhSua + "!");
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Lỗi khi cập nhật hóa đơn: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

// PHƯƠNG THỨC LƯU HÓA ĐƠN VÀO DATABASE
// PHƯƠNG THỨC LƯU HÓA ĐƠN VÀO DATABASE
    private boolean luuHoaDonVaoDatabase() {
        try {
            // 1. Tạo đối tượng HoaDon
            HoaDon hoaDon = new HoaDon();
            hoaDon.setMaKhachHang(khachHangHienTai.getMaKhachHang());
            hoaDon.setNgayLap(java.time.LocalDateTime.now());
            hoaDon.setTongTien(tongTien);

            // Lấy mã nhân viên từ combobox
            String selectedNhanVien = (String) view.getCboNhanVien().getSelectedItem();
            if (selectedNhanVien != null && !selectedNhanVien.equals("-- Chọn nhân viên --")) {
                Integer maNhanVien = getMaNhanVienTheoTen(selectedNhanVien.split(" - ")[0]);
                hoaDon.setMaNhanVienLap(maNhanVien);
            }

            hoaDon.setGhiChu("Hóa đơn dịch vụ spa - " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));

            // 2. Tạo danh sách chi tiết hóa đơn
            List<ChiTietHoaDon> chiTietList = new ArrayList<>();
            DefaultTableModel model = view.getTableModel();

            for (int i = 0; i < model.getRowCount(); i++) {
                String tenDichVu = model.getValueAt(i, 1).toString();

                // Bỏ qua dịch vụ "Vé gọi đầu" khi lưu database
                if (tenDichVu.contains("Vé gọi đầu")) {
                    continue;
                }

                int soLuong = Integer.parseInt(model.getValueAt(i, 4).toString());

                // Lấy đơn giá từ chuỗi format (ví dụ: "100,000 VND")
                String donGiaStr = model.getValueAt(i, 3).toString().replaceAll("[^\\d]", "");
                BigDecimal donGia = new BigDecimal(donGiaStr);

                // Tìm mã dịch vụ theo tên
                Integer maDichVu = getMaDichVuTheoTen(tenDichVu);
                if (maDichVu != null) {
                    ChiTietHoaDon chiTiet = new ChiTietHoaDon();
                    chiTiet.setMaDichVu(maDichVu);
                    chiTiet.setSoLuong(soLuong);
                    chiTiet.setDonGia(donGia);

                    // THÊM MÃ NHÂN VIÊN TỪ BẢNG
                    String tenNhanVien = model.getValueAt(i, 5).toString();
                    Integer maNhanVien = getMaNhanVienTheoTen(tenNhanVien);
                    chiTiet.setMaNhanVien(maNhanVien);

                    chiTietList.add(chiTiet);
                }
            }

            // Đặt danh sách chi tiết vào hóa đơn
            hoaDon.setChiTietHoaDon(chiTietList);

            // 3. Lưu hóa đơn vào database thông qua service
            boolean success = hoaDonService.addHoaDon(hoaDon);

            if (success) {
                System.out.println("Đã lưu hóa đơn vào database thành công! Mã hóa đơn: " + hoaDon.getMaHoaDon());
                return true;
            } else {
                System.out.println("Lỗi khi lưu hóa đơn vào database!");
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Lỗi khi lưu hóa đơn: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

// PHƯƠNG THỨC HỖ TRỢ - LẤY MÃ NHÂN VIÊN THEO TÊN
    private Integer getMaNhanVienTheoTen(String tenNhanVien) {
        try {
            List<NhanVien> dsNhanVien = nhanVienService.getAllNhanVien();
            for (NhanVien nv : dsNhanVien) {
                if (nv.getHoTen().equals(tenNhanVien)) {
                    return nv.getMaNhanVien();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1; // Trả về mã mặc định nếu không tìm thấy
    }

// PHƯƠNG THỨC HỖ TRỢ - LẤY MÃ DỊCH VỤ THEO TÊN
    private Integer getMaDichVuTheoTen(String tenDichVu) {
        try {
            List<DichVu> dsDichVu = dichVuService.getAllDichVu();
            for (DichVu dv : dsDichVu) {
                if (dv.getTenDichVu().equals(tenDichVu)) {
                    return dv.getMaDichVu();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

// Phương thức in hóa đơn PDF chi tiết - ĐÃ SỬA ĐỂ GIỐNG VỚI QUẢN LÝ ĐẶT LỊCH
    private boolean inHoaDonPDF() {
        FileOutputStream fos = null;
        try {
            // Tạo đường dẫn động đến thư mục bill
            String billDir = DataConnection.getBillPath();

            // Tạo thư mục nếu chưa tồn tại
            File directory = new File(billDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Tạo tên file
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String fileName = "HoaDon_DichVu_" + sdf.format(new Date()) + ".pdf";
            String filePath = billDir + File.separator + fileName;
            fos = new FileOutputStream(filePath);

            Document doc = new Document();
            PdfWriter writer = PdfWriter.getInstance(doc, fos);
            doc.open();

            // Sử dụng font Unicode để hỗ trợ tiếng Việt
            BaseFont baseFont = createBaseFont();

            // Tạo fonts
            Font fontNormal = new Font(baseFont, 12, Font.NORMAL);
            Font fontBold = new Font(baseFont, 12, Font.BOLD);
            Font fontTitle = new Font(baseFont, 18, Font.BOLD);
            Font fontHeader = new Font(baseFont, 10, Font.BOLD);
            Font fontSmall = new Font(baseFont, 10, Font.NORMAL);

            // Tiêu đề
            Paragraph title = new Paragraph("HOÁ ĐƠN DỊCH VỤ", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            doc.add(title);

            // Thông tin cửa hàng
            Paragraph storeInfo = new Paragraph("SWEET HOME", fontBold);
            storeInfo.setAlignment(Element.ALIGN_CENTER);
            doc.add(storeInfo);

            Paragraph storeAddress = new Paragraph("43 Đ. Lý Tự Trọng, P, Ninh Kiều, Cần Thơ 94100, Việt Nam", fontSmall);
            storeAddress.setAlignment(Element.ALIGN_CENTER);
            doc.add(storeAddress);

            Paragraph storePhone = new Paragraph("Điện thoại: 097 3791 643", fontSmall);
            storePhone.setAlignment(Element.ALIGN_CENTER);
            doc.add(storePhone);

            doc.add(new Paragraph(" "));
//            doc.add(new Paragraph("---------------------------------------------", fontNormal));
            doc.add(new Paragraph(" "));

            // Thông tin hóa đơn
            doc.add(new Paragraph("Ngày lập: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()), fontNormal));
            doc.add(new Paragraph("Khách hàng: " + getTenKhachHangHienTai(), fontNormal));

            // Thêm thông tin liên hệ khách hàng
            if (khachHangHienTai.getSoDienThoai() != null && !khachHangHienTai.getSoDienThoai().isEmpty()) {
                doc.add(new Paragraph("SĐT: " + khachHangHienTai.getSoDienThoai(), fontNormal));
            }

            // Hiển thị điểm tích lũy hiện tại
//            doc.add(new Paragraph("Điểm tích lũy hiện tại: " + khachHangHienTai.getDiemTichLuy() + " điểm", fontNormal));
//            doc.add(new Paragraph("Lưu ý: Cần tối thiểu 10 điểm để đổi vé gọi đầu", fontSmall));
            doc.add(new Paragraph("---------------------------------------------", fontNormal));

            // Bảng dịch vụ
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            float[] columnWidths = {0.8f, 3f, 2f, 1.5f, 2f, 2f};
            table.setWidths(columnWidths);

            // Header bảng
            table.addCell(new Phrase("STT", fontHeader));
            table.addCell(new Phrase("Tên dịch vụ", fontHeader));
            table.addCell(new Phrase("Thời gian", fontHeader));
            table.addCell(new Phrase("Số lượng", fontHeader));
            table.addCell(new Phrase("Đơn giá", fontHeader));
            table.addCell(new Phrase("Thành tiền", fontHeader));

            // Tính tổng tiền CHỈ từ các dịch vụ (không bao gồm vé gọi đầu)
            int stt = 1;
            BigDecimal tongTienDichVu = BigDecimal.ZERO;
            DefaultTableModel model = view.getTableModel();

            for (int i = 0; i < model.getRowCount(); i++) {
                String tenDichVu = model.getValueAt(i, 1).toString();

                // Bỏ qua dịch vụ "Vé gọi đầu" khi tính tổng tiền
                if (tenDichVu.contains("Vé gọi đầu")) {
                    continue;
                }

                String thoiGian = model.getValueAt(i, 2).toString();
                int soLuong = Integer.parseInt(model.getValueAt(i, 4).toString());

                // Lấy đơn giá từ chuỗi format (ví dụ: "100,000 VND")
                String donGiaStr = model.getValueAt(i, 3).toString().replaceAll("[^\\d]", "");
                BigDecimal donGia = new BigDecimal(donGiaStr);

                BigDecimal thanhTien = donGia.multiply(BigDecimal.valueOf(soLuong));
                tongTienDichVu = tongTienDichVu.add(thanhTien);

                // Thêm các cell với STT
                table.addCell(new Phrase(String.valueOf(stt++), fontNormal));
                table.addCell(new Phrase(tenDichVu, fontNormal));
                table.addCell(new Phrase(thoiGian, fontNormal));
                table.addCell(new Phrase(String.valueOf(soLuong), fontNormal));
                table.addCell(new Phrase(String.format("%,.0f", donGia) + " VND", fontNormal));
                table.addCell(new Phrase(String.format("%,.0f", thanhTien.doubleValue()) + " VND", fontNormal));
            }

            doc.add(table);
//            doc.add(new Paragraph("/-------------------------------------", fontNormal));

            // XỬ LÝ TIỀN TRẢ TRƯỚC VÀ HIỂN THỊ THÔNG TIN TÀI CHÍNH
            BigDecimal soDuHienTai = BigDecimal.ZERO;
            BigDecimal tienDaThanhToan = BigDecimal.ZERO;
            BigDecimal tienPhaiTra = tongTienDichVu;
            boolean coTaiKhoanTraTruoc = false;
            Integer maHoaDonHienTai = null;

            try {
                // Kiểm tra xem khách hàng có tài khoản trả trước không
                TienTraTruocService tienTraTruocService = new TienTraTruocService();
                TienTraTruoc ttt = tienTraTruocService.getTienTraTruocByMaKhachHang(khachHangHienTai.getMaKhachHang());

                if (ttt != null && ttt.getSoDuHienTai() != null && ttt.getSoDuHienTai().compareTo(BigDecimal.ZERO) > 0) {
                    coTaiKhoanTraTruoc = true;
                    soDuHienTai = ttt.getSoDuHienTai();

                    // Lấy mã hóa đơn hiện tại
                    maHoaDonHienTai = getMaHoaDonHienTai();

                    if (maHoaDonHienTai != null) {
                        // XỬ LÝ THANH TOÁN TRẢ TRƯỚC TRONG DATABASE
                        boolean thanhToanSuccess = tienTraTruocService.xuLyThanhToanHoaDon(
                                khachHangHienTai.getMaKhachHang(),
                                maHoaDonHienTai,
                                tongTienDichVu
                        );

                        if (thanhToanSuccess) {
                            // Cập nhật lại thông tin sau khi thanh toán
                            ttt = tienTraTruocService.getTienTraTruocByMaKhachHang(khachHangHienTai.getMaKhachHang());
                            BigDecimal soDuMoi = ttt != null ? ttt.getSoDuHienTai() : BigDecimal.ZERO;

                            // Tính toán các khoản tiền
                            tienDaThanhToan = soDuHienTai.subtract(soDuMoi);
                            tienPhaiTra = tongTienDichVu.subtract(tienDaThanhToan);

                            if (tienPhaiTra.compareTo(BigDecimal.ZERO) < 0) {
                                tienPhaiTra = BigDecimal.ZERO;
                            }

                            System.out.println("✅ Đã xử lý thanh toán trả trước:");
                            System.out.println("   - Tổng tiền dịch vụ: " + tongTienDichVu);
                            System.out.println("   - Số dư ban đầu: " + soDuHienTai);
                            System.out.println("   - Số dư mới: " + soDuMoi);
                            System.out.println("   - Tiền đã thanh toán: " + tienDaThanhToan);
                            System.out.println("   - Tiền phải trả: " + tienPhaiTra);
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Lỗi khi xử lý thông tin trả trước: " + e.getMessage());
                e.printStackTrace();
            }

            // HIỂN THỊ THÔNG TIN TÀI CHÍNH TRONG HÓA ĐƠN
            doc.add(new Paragraph(String.format("Tổng tiền dịch vụ: %s VND",
                    String.format("%,.0f", tongTienDichVu.doubleValue())), fontNormal));

            if (coTaiKhoanTraTruoc) {
                doc.add(new Paragraph(String.format("Số dư tài khoản: %s VND",
                        String.format("%,.0f", soDuHienTai.doubleValue())), fontNormal));

                doc.add(new Paragraph(String.format("Tiền đã thanh toán: %s VND",
                        String.format("%,.0f", tienDaThanhToan.doubleValue())), fontNormal));

                if (tienPhaiTra.compareTo(BigDecimal.ZERO) == 0) {
                    doc.add(new Paragraph("Tiền phải trả: 0 VND - ĐÃ THANH TOÁN ĐỦ",
                            new Font(baseFont, 14, Font.NORMAL)));
                } else {
                    doc.add(new Paragraph(String.format("Tiền phải trả: %s VND",
                            String.format("%,.0f", tienPhaiTra.doubleValue())), fontNormal));
                }
            } else {
                doc.add(new Paragraph(String.format("Tổng cộng: %s VND",
                        String.format("%,.0f", tongTienDichVu.doubleValue())), fontNormal));
            }

            // Hiển thị điểm tích lũy được thưởng
            int diemThuong = tongTienDichVu.divideToIntegralValue(BigDecimal.valueOf(100000)).intValue();
            if (diemThuong > 0) {
//                doc.add(new Paragraph("Điểm tích lũy được thưởng: +" + diemThuong + " điểm", fontBold));
            }

            // Thêm QR Code thanh toán (chỉ hiển thị nếu còn tiền phải trả)
            if (tienPhaiTra.compareTo(BigDecimal.ZERO) > 0) {
                try {
                    String bankBin = "970431";
                    String accountNumber = "0973791643";
                    String accountName = "NGUYEN DIEM THAO NGUYEN";
                    String addInfo = "Thanh toán dịch vụ SPA - " + getTenKhachHangHienTai();

                    String qrUrl = "https://img.vietqr.io/image/"
                            + bankBin + "-" + accountNumber
                            + "-compact.png?amount=" + tienPhaiTra
                            + "&addInfo=" + URLEncoder.encode(addInfo, StandardCharsets.UTF_8)
                            + "&accountName=" + URLEncoder.encode(accountName, StandardCharsets.UTF_8);

                    BufferedImage qrBufferedImage = ImageIO.read(new URL(qrUrl));

                    // Lưu QR code vào thư mục bill
                    String qrFileName = "VietQR_DichVu_" + System.currentTimeMillis() + ".png";
                    String qrPath = billDir + File.separator + qrFileName;
                    ImageIO.write(qrBufferedImage, "PNG", new File(qrPath));

                    com.itextpdf.text.Image qrImage = com.itextpdf.text.Image.getInstance(qrPath);
                    qrImage.scaleToFit(120, 120);
                    qrImage.setAlignment(Element.ALIGN_CENTER);

                    doc.add(new Paragraph("\nMã QR thanh toán:", fontBold));
                    doc.add(qrImage);

                    doc.add(new Paragraph("Ngân hàng: EximBank", fontNormal));
                    doc.add(new Paragraph("Chủ tài khoản: " + accountName, fontNormal));
                    doc.add(new Paragraph("Số tài khoản: " + accountNumber, fontNormal));

                    // Xóa file QR tạm
                    new File(qrPath).delete();
                } catch (Exception e) {
                    System.err.println("Không thể tạo QR thanh toán: " + e.getMessage());
                    doc.add(new Paragraph("\nQuý khách vui lòng thanh toán trực tiếp tại quầy.", fontNormal));
                }
            } else if (coTaiKhoanTraTruoc) {
                doc.add(new Paragraph("\nĐÃ THANH TOÁN TOÀN BỘ BẰNG TÀI KHOẢN TRẢ TRƯỚC",
                        new Font(baseFont, 14, Font.ITALIC)));
            }

            // Thêm ghi chú
            String ghiChu = "Hóa đơn dịch vụ spa - " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
            if (coTaiKhoanTraTruoc) {
                ghiChu += " - Đã thanh toán " + String.format("%,.0f", tienDaThanhToan.doubleValue()) + " VND qua tài khoản trả trước";
            }
            doc.add(new Paragraph("\nGhi chú: " + ghiChu, fontNormal));

            doc.add(new Paragraph("\nSweet home - Thân khỏe - Tâm an", fontBold));
            doc.add(new Paragraph("Cám ơn khách thương đã tin và ủng hộ Sweet home. Hẹn gặp lại!", fontNormal));

            doc.close();

            // Mở file PDF
            JOptionPane.showMessageDialog(view, "Đã in hóa đơn thành công!");
            try {
                Desktop.getDesktop().open(new File(filePath));
            } catch (Exception e) {
                System.err.println("Không thể mở file PDF: " + e.getMessage());
            }

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Lỗi khi in hóa đơn PDF: " + e.getMessage());
            return false;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
// PHƯƠNG THỨC LẤY MÃ HÓA ĐƠN HIỆN TẠI

    private Integer getMaHoaDonHienTai() {
        try {
            if (cheDoChinhSua && maHoaDonChinhSua != null) {
                // Trong chế độ chỉnh sửa, trả về mã hóa đơn đang chỉnh sửa
                return maHoaDonChinhSua;
            } else {
                // Trong chế độ tạo mới, tìm hóa đơn mới nhất của khách hàng
                List<HoaDon> hoaDonList = hoaDonService.getHoaDonByMaKhachHang(khachHangHienTai.getMaKhachHang());
                if (hoaDonList != null && !hoaDonList.isEmpty()) {
                    // Sắp xếp theo ngày lập giảm dần, lấy hóa đơn mới nhất
                    hoaDonList.sort((h1, h2) -> h2.getNgayLap().compareTo(h1.getNgayLap()));
                    return hoaDonList.get(0).getMaHoaDon();
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy mã hóa đơn hiện tại: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
// PHƯƠNG THỨC HỖ TRỢ - TÍNH SỐ DƯ TỪ LỊCH SỬ GIAO DỊCH

    private BigDecimal calculateSoDuFromLichSu(List<LichSuGiaoDichTraTruoc> lichSuList, Integer maKhachHang) {
        BigDecimal soDu = BigDecimal.ZERO;

        for (LichSuGiaoDichTraTruoc lichSu : lichSuList) {
            if (lichSu.getMaKhachHang().equals(maKhachHang)) {
                // Số tiền tăng (nạp tiền) làm tăng số dư
                if (lichSu.getSoTienTang() != null) {
                    soDu = soDu.add(lichSu.getSoTienTang());
                }
                // Số tiền giảm (thanh toán) làm giảm số dư
                if (lichSu.getSoTienGiam() != null) {
                    soDu = soDu.subtract(lichSu.getSoTienGiam());
                }
            }
        }

        return soDu.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : soDu;
    }
// Phương thức lấy thông tin nhân viên từ bảng dịch vụ

    private String getNhanVienInfoFromTable() {
        DefaultTableModel model = view.getTableModel();
        if (model.getRowCount() > 0) {
            // Lấy nhân viên từ dòng đầu tiên
            Object nhanVienObj = model.getValueAt(0, 5); // Cột nhân viên là cột thứ 5
            if (nhanVienObj != null) {
                String nhanVienStr = nhanVienObj.toString();

                // Nếu có nhiều nhân viên khác nhau, liệt kê tất cả
                if (model.getRowCount() > 1) {
                    Set<String> nhanVienSet = new HashSet<>();
                    nhanVienSet.add(nhanVienStr);

                    for (int i = 1; i < model.getRowCount(); i++) {
                        Object nvObj = model.getValueAt(i, 5);
                        if (nvObj != null) {
                            nhanVienSet.add(nvObj.toString());
                        }
                    }

                    if (nhanVienSet.size() == 1) {
                        return nhanVienStr;
                    } else {
                        return String.join(", ", nhanVienSet);
                    }
                }

                return nhanVienStr;
            }
        }
        return "Chưa xác định";
    }

    // Phương thức hỗ trợ tạo BaseFont
    private BaseFont createBaseFont() throws Exception {
        try {
            return BaseFont.createFont("c:/windows/fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        } catch (Exception e1) {
            try {
                return BaseFont.createFont("c:/windows/fonts/times.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            } catch (Exception e2) {
                try {
                    return BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.EMBEDDED);
                } catch (Exception e3) {
                    return BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
                }
            }
        }
    }

    // Phương thức hỗ trợ tạo bảng dịch vụ
// Phương thức hỗ trợ tạo bảng dịch vụ - ĐÃ SỬA LỖI
    private PdfPTable createServiceTable(Font fontNormal, Font fontHeader) throws DocumentException {
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        float[] columnWidths = {1f, 3f, 1.5f, 1.5f, 2f};
        table.setWidths(columnWidths);

        // Header bảng
        table.addCell(new Phrase("STT", fontHeader));
        table.addCell(new Phrase("Tên dịch vụ", fontHeader));
        table.addCell(new Phrase("Số lượng", fontHeader));
        table.addCell(new Phrase("Đơn giá", fontHeader));
        table.addCell(new Phrase("Thành tiền", fontHeader));

        // Thêm dữ liệu dịch vụ - SỬA LỖI ÉP KIỂU
        DefaultTableModel model = view.getTableModel();

        for (int i = 0; i < model.getRowCount(); i++) {
            int stt = i + 1;

            // Lấy dữ liệu từ bảng và chuyển đổi an toàn sang String
            Object tenDichVuObj = model.getValueAt(i, 1);
            Object soLuongObj = model.getValueAt(i, 4);
            Object donGiaObj = model.getValueAt(i, 3);
            Object thanhTienObj = model.getValueAt(i, 6);

            // Chuyển đổi an toàn sang String
            String tenDichVu = convertToString(tenDichVuObj);
            String soLuong = convertToString(soLuongObj);
            String donGiaStr = convertToString(donGiaObj);
            String thanhTienStr = convertToString(thanhTienObj);

            table.addCell(new Phrase(String.valueOf(stt), fontNormal));
            table.addCell(new Phrase(tenDichVu, fontNormal));
            table.addCell(new Phrase(soLuong, fontNormal));
            table.addCell(new Phrase(donGiaStr, fontNormal));
            table.addCell(new Phrase(thanhTienStr, fontNormal));
        }

        return table;
    }

// Phương thức hỗ trợ chuyển đổi an toàn sang String
    private String convertToString(Object obj) {
        if (obj == null) {
            return "";
        }
        return obj.toString();
    }

    // Phương thức hỗ trợ thêm QR Code
    private void addQRCodeToDocument(Document doc, Font fontBold, Font fontSmall) throws Exception {
        try {
            String bankBin = "970431";
            String accountNumber = "0973791643";
            String accountName = "NGUYEN DIEM THAO NGUYEN";
            String addInfo = "Thanh toán dịch vụ SPA - " + getTenKhachHangHienTai();

            String qrUrl = "https://img.vietqr.io/image/"
                    + bankBin + "-" + accountNumber
                    + "-compact.png?amount=" + tongTien
                    + "&addInfo=" + URLEncoder.encode(addInfo, StandardCharsets.UTF_8)
                    + "&accountName=" + URLEncoder.encode(accountName, StandardCharsets.UTF_8);

            BufferedImage qrBufferedImage = ImageIO.read(new URL(qrUrl));
            String qrPath = "VietQR_DichVu_" + System.currentTimeMillis() + ".png";
            ImageIO.write(qrBufferedImage, "PNG", new File(qrPath));

            com.itextpdf.text.Image qrImage = com.itextpdf.text.Image.getInstance(qrPath);
            qrImage.scaleToFit(120, 120);
            qrImage.setAlignment(Element.ALIGN_CENTER);

            doc.add(new Paragraph("Mã QR thanh toán:", fontBold));
            doc.add(qrImage);

            doc.add(new Paragraph("Ngân hàng: EximBank", fontSmall));
            doc.add(new Paragraph("Chủ tài khoản: " + accountName, fontSmall));
            doc.add(new Paragraph("Số tài khoản: " + accountNumber, fontSmall));

            // Xóa file QR tạm
            new File(qrPath).delete();

        } catch (Exception e) {
            System.err.println("Không thể tạo QR thanh toán: " + e.getMessage());
            doc.add(new Paragraph("Quý khách vui lòng thanh toán trực tiếp tại quầy.", fontSmall));
        }
    }

    // Phương thức hỗ trợ - Lấy tên khách hàng hiện tại
    private String getTenKhachHangHienTai() {
        if (khachHangHienTai != null) {
            return khachHangHienTai.getHoTen();
        }
        return "Không xác định";
    }

    private void handleLamMoi() {
        int confirm = JOptionPane.showConfirmDialog(
                view,
                "Bạn có chắc muốn làm mới form? Tất cả dữ liệu chưa lưu sẽ bị mất.",
                "Xác nhận làm mới",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            view.getTableModel().setRowCount(0);
            view.getCboKhachHang().setSelectedIndex(0);
            view.getCboDichVu().setSelectedIndex(0);
            view.getCboNhanVien().setSelectedIndex(0);
            view.getTxtSoLuongNguoi().setText("1");
            view.getTxtThoiGian().setText("");
            tongTien = BigDecimal.ZERO;
            capNhatTongTien();
            khachHangHienTai = null;
            view.getLblDiemTichLuy().setText("0 điểm");

            // Reset chế độ chỉnh sửa
            this.cheDoChinhSua = false;
            this.maHoaDonChinhSua = null;
            view.setCheDoChinhSua(false, null);
        }
    }

    private boolean kiemTraDuLieuHopLe() {
        if (view.getCboKhachHang().getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn khách hàng",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (view.getCboDichVu().getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn dịch vụ",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (view.getCboNhanVien().getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn nhân viên thực hiện",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            int soLuong = Integer.parseInt(view.getTxtSoLuongNguoi().getText());
            if (soLuong <= 0) {
                JOptionPane.showMessageDialog(view, "Số lượng người phải lớn hơn 0",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(view, "Số lượng người không hợp lệ",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void capNhatTongTien() {
        view.getLblTongTien().setText(currencyFormat.format(tongTien));
    }

    private void capNhatSTT() {
        DefaultTableModel model = view.getTableModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(i + 1, i, 0);
        }
    }
}
