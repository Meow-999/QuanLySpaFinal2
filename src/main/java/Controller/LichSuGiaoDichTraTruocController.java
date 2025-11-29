package Controller;

import Model.*;
import Service.*;
import View.LichSuGiaoDichTraTruocView;
import View.TimKhachHangView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LichSuGiaoDichTraTruocController {

    private LichSuGiaoDichTraTruocView view;
    private LichSuGiaoDichTraTruocService lichSuService;
    private KhachHangService khachHangService;

    private NumberFormat currencyFormat;
    private DateTimeFormatter dateFormatter;

    public LichSuGiaoDichTraTruocController(LichSuGiaoDichTraTruocView view) {
        this.view = view;
        this.lichSuService = new LichSuGiaoDichTraTruocService();
        this.khachHangService = new KhachHangService();

        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        this.dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        initController();
        loadDuLieuBanDau();
    }

    private void initController() {
        view.getCboKhachHang().addActionListener(e -> handleLocDuLieu());
        view.getBtnTimKhachHang().addActionListener(e -> handleTimKiemKhachHang());
        view.getBtnLoc().addActionListener(e -> handleLocDuLieu());
        view.getBtnLamMoi().addActionListener(e -> handleLamMoi());
        
        // Add date change listeners for immediate filtering (optional)
        view.getDateChooserTuNgay().addPropertyChangeListener("date", e -> handleLocDuLieu());
        view.getDateChooserDenNgay().addPropertyChangeListener("date", e -> handleLocDuLieu());
    }

    private void loadDuLieuBanDau() {
        loadKhachHang();
        thietLapNgayMacDinh();
        loadLichSuGiaoDich(null, null, null);
    }

    private void loadKhachHang() {
        try {
            List<KhachHang> dsKhachHang = khachHangService.getAllKhachHang();
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            model.addElement("-- Tất cả khách hàng --");

            for (KhachHang kh : dsKhachHang) {
                model.addElement(kh.getHoTen() + " - " + kh.getSoDienThoai());
            }

            view.getCboKhachHang().setModel(model);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi khi tải danh sách khách hàng: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void thietLapNgayMacDinh() {
        LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate today = LocalDate.now();
        
        // Set default dates to JDateChooser
        view.getDateChooserTuNgay().setDate(Date.from(firstDayOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        view.getDateChooserDenNgay().setDate(Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    private void handleTimKiemKhachHang() {
        TimKhachHangView timKhachHangView = new TimKhachHangView((Frame) SwingUtilities.getWindowAncestor(view));
        TimKhachHangController timKhachHangController = new TimKhachHangController(timKhachHangView, this);
        timKhachHangView.setVisible(true);
    }

    public void capNhatKhachHangDuocChon(Integer maKhachHang, String tenKhachHang, String soDienThoai) {
        try {
            DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) view.getCboKhachHang().getModel();
            for (int i = 0; i < model.getSize(); i++) {
                String item = model.getElementAt(i);
                if (item.contains(soDienThoai)) {
                    view.getCboKhachHang().setSelectedIndex(i);
                    handleLocDuLieu();
                    
                    JOptionPane.showMessageDialog(view, 
                        "Đã chọn khách hàng: " + tenKhachHang, 
                        "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, 
                "Lỗi khi cập nhật khách hàng: " + e.getMessage(), 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleLocDuLieu() {
        try {
            Integer maKhachHang = null;
            LocalDate tuNgay = null;
            LocalDate denNgay = null;

            int selectedIndex = view.getCboKhachHang().getSelectedIndex();
            if (selectedIndex > 0) {
                String selected = (String) view.getCboKhachHang().getSelectedItem();
                String soDienThoai = selected.split(" - ")[1];
                KhachHang kh = khachHangService.getKhachHangBySoDienThoai(soDienThoai);
                if (kh != null) {
                    maKhachHang = kh.getMaKhachHang();
                }
            }

            // Get dates from JDateChooser
            Date tuNgayDate = view.getDateChooserTuNgay().getDate();
            Date denNgayDate = view.getDateChooserDenNgay().getDate();

            if (tuNgayDate != null) {
                tuNgay = tuNgayDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            }

            if (denNgayDate != null) {
                denNgay = denNgayDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            }

            if (tuNgay != null && denNgay != null && tuNgay.isAfter(denNgay)) {
                JOptionPane.showMessageDialog(view, "Ngày bắt đầu không thể sau ngày kết thúc",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            loadLichSuGiaoDich(maKhachHang, tuNgay, denNgay);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi khi lọc dữ liệu: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadLichSuGiaoDich(Integer maKhachHang, LocalDate tuNgay, LocalDate denNgay) {
        try {
            List<LichSuGiaoDichTraTruoc> dsLichSu;
            
            if (maKhachHang != null) {
                dsLichSu = lichSuService.getLichSuByMaKhachHang(maKhachHang);
            } else {
                dsLichSu = lichSuService.getAllLichSu();
            }

            DefaultTableModel model = view.getTableModel();
            model.setRowCount(0);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            for (LichSuGiaoDichTraTruoc lichSu : dsLichSu) {
                if (tuNgay != null && lichSu.getNgayGiaoDich().toLocalDate().isBefore(tuNgay)) {
                    continue;
                }
                if (denNgay != null && lichSu.getNgayGiaoDich().toLocalDate().isAfter(denNgay)) {
                    continue;
                }

                int stt = model.getRowCount() + 1;
                model.addRow(new Object[]{
                    stt,
                    lichSu.getMaKhachHang(),
                    lichSu.getTenKhachHang(),
                    lichSu.getNgayGiaoDich().format(formatter),
                    lichSu.getSoTienTang() != null ? currencyFormat.format(lichSu.getSoTienTang()) : "0 VND",
                    lichSu.getSoTienGiam() != null ? currencyFormat.format(lichSu.getSoTienGiam()) : "0 VND",
                    lichSu.getTongTien() != null ? currencyFormat.format(lichSu.getTongTien()) : "0 VND",
                    lichSu.getTienPhaiTra() != null ? currencyFormat.format(lichSu.getTienPhaiTra()) : "0 VND",
                    lichSu.getGhiChu()
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi khi tải lịch sử giao dịch: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleLamMoi() {
        int confirm = JOptionPane.showConfirmDialog(
                view,
                "Bạn có chắc muốn làm mới form?",
                "Xác nhận làm mới",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            view.getCboKhachHang().setSelectedIndex(0);
            thietLapNgayMacDinh();
            loadLichSuGiaoDich(null, null, null);
        }
    }

   
}