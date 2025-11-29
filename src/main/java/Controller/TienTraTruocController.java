package Controller;

import Model.*;
import Service.*;
import View.ChinhSuaTienTraTruocDialog;
import View.TienTraTruocView;
import View.KhachHangDialog;
import View.LichSuGiaoDichTraTruocView;
import View.TimKhachHangView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class TienTraTruocController {

    private TienTraTruocView view;
    private TienTraTruocService tienTraTruocService;
    private KhachHangService khachHangService;
    private LichSuGiaoDichTraTruocService lichSuService;

    private NumberFormat currencyFormat;
    private KhachHang khachHangHienTai;
    private TienTraTruoc tienTraTruocHienTai;
    private BigDecimal tongTienTraTruoc;

    public TienTraTruocController(TienTraTruocView view) {
        this.view = view;
        this.tienTraTruocService = new TienTraTruocService();
        this.khachHangService = new KhachHangService();
        this.lichSuService = new LichSuGiaoDichTraTruocService();

        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        this.tongTienTraTruoc = BigDecimal.ZERO;

        initController();
        loadDuLieuBanDau();
    }

    private void initController() {
        // Sự kiện cho combobox khách hàng
        view.getCboKhachHang().addActionListener(e -> handleChonKhachHang());

        // Sự kiện cho nút thêm khách hàng
        view.getBtnThemKhachHang().addActionListener(e -> handleThemKhachHang());

        // Sự kiện cho nút tìm khách hàng
        view.getBtnTimKhachHang().addActionListener(e -> handleTimKiemKhachHang());

        // Sự kiện cho nút tạo tài khoản
        view.getBtnTaoTaiKhoan().addActionListener(e -> handleTaoTaiKhoan());

        // Sự kiện cho nút nạp tiền
        view.getBtnNapTien().addActionListener(e -> handleNapTien());

        // Sự kiện cho nút làm mới
        view.getBtnLamMoi().addActionListener(e -> handleLamMoi());

        // Sự kiện cho nút xem lịch sử
        view.getTblDanhSachTraTruoc().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                handleChonTuBang();
            }
        });
        view.getBtnSuaSoDu().addActionListener(e -> handleChinhSuaSoDu());
    }

    private void loadDuLieuBanDau() {
        loadKhachHang();
        loadDanhSachTraTruoc();
        capNhatTongTienTraTruoc();
    }

    private void handleChonTuBang() {
        try {
            JTable table = view.getTblDanhSachTraTruoc();
            int selectedRow = table.getSelectedRow();
            
            if (selectedRow >= 0) {
                // Lấy mã khách hàng từ cột 1 (index 1)
                String maKhachHangStr = table.getValueAt(selectedRow, 1).toString();
                Integer maKhachHang = Integer.parseInt(maKhachHangStr);
                
                // Lấy thông tin khách hàng từ service
                khachHangHienTai = khachHangService.getKhachHangById(maKhachHang);
                
                if (khachHangHienTai != null) {
                    // Tìm và chọn khách hàng trong combobox
                    DefaultComboBoxModel<KhachHang> model = (DefaultComboBoxModel<KhachHang>) view.getCboKhachHang().getModel();
                    
                    for (int i = 0; i < model.getSize(); i++) {
                        KhachHang kh = model.getElementAt(i);
                        if (kh.getMaKhachHang() != null && kh.getMaKhachHang().equals(maKhachHang)) {
                            view.getCboKhachHang().setSelectedIndex(i);
                            break;
                        }
                    }
                    
                    // Cập nhật thông tin tài khoản trả trước
                    tienTraTruocHienTai = tienTraTruocService.getTienTraTruocByMaKhachHang(maKhachHang);
                    if (tienTraTruocHienTai != null) {
                        view.getLblSoDuHienTai().setText(currencyFormat.format(tienTraTruocHienTai.getSoDuHienTai()));
                        view.getBtnTaoTaiKhoan().setEnabled(false);
                        view.getBtnNapTien().setEnabled(true);
                        view.getBtnSuaSoDu().setEnabled(true);
                    } else {
                        view.getLblSoDuHienTai().setText("0 VND");
                        view.getBtnTaoTaiKhoan().setEnabled(true);
                        view.getBtnNapTien().setEnabled(false);
                        view.getBtnSuaSoDu().setEnabled(false);
                    }
                    
                    // Focus vào ô nhập số tiền để tiện sử dụng
                    view.getTxtSoTienThem().requestFocus();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, 
                "Lỗi khi chọn khách hàng từ bảng: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadKhachHang() {
        try {
            List<KhachHang> khachHangs = khachHangService.getAllKhachHang();
            view.getCboKhachHang().removeAllItems();
            view.getCboKhachHang().addItem(new KhachHang()); // Item rỗng cho "-- Chọn khách hàng --"

            for (KhachHang kh : khachHangs) {
                view.getCboKhachHang().addItem(kh);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi khi tải danh sách khách hàng: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadDanhSachTraTruoc() {
        try {
            List<TienTraTruoc> dsTraTruoc = tienTraTruocService.getAllTienTraTruoc();
            DefaultTableModel model = view.getTableModel();
            model.setRowCount(0);

            tongTienTraTruoc = BigDecimal.ZERO;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            for (TienTraTruoc ttt : dsTraTruoc) {
                int stt = model.getRowCount() + 1;
                model.addRow(new Object[]{
                    stt,
                    ttt.getMaKhachHang(),
                    ttt.getTenKhachHang(),
                    ttt.getSoDienThoai(),
                    currencyFormat.format(ttt.getSoDuHienTai()),
                    ttt.getNgayCapNhat().format(formatter)
                });

                tongTienTraTruoc = tongTienTraTruoc.add(ttt.getSoDuHienTai());
            }

            capNhatTongTienTraTruoc();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi khi tải danh sách trả trước: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleChonKhachHang() {
        KhachHang selectedKhachHang = (KhachHang) view.getCboKhachHang().getSelectedItem();
        
        if (selectedKhachHang != null && selectedKhachHang.getMaKhachHang() != null) {
            try {
                khachHangHienTai = selectedKhachHang;
                
                // Kiểm tra xem khách hàng đã có tài khoản trả trước chưa
                tienTraTruocHienTai = tienTraTruocService.getTienTraTruocByMaKhachHang(khachHangHienTai.getMaKhachHang());
                
                if (tienTraTruocHienTai != null) {
                    view.getLblSoDuHienTai().setText(currencyFormat.format(tienTraTruocHienTai.getSoDuHienTai()));
                    view.getBtnTaoTaiKhoan().setEnabled(false);
                    view.getBtnNapTien().setEnabled(true);
                    view.getBtnSuaSoDu().setEnabled(true);
                } else {
                    view.getLblSoDuHienTai().setText("0 VND");
                    view.getBtnTaoTaiKhoan().setEnabled(true);
                    view.getBtnNapTien().setEnabled(false);
                    view.getBtnSuaSoDu().setEnabled(false);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Lỗi khi lấy thông tin khách hàng: " + e.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            resetThongTinKhachHang();
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
                        tienTraTruocHienTai = tienTraTruocService.getTienTraTruocByMaKhachHang(maKhachHang);
                        if (tienTraTruocHienTai != null) {
                            view.getLblSoDuHienTai().setText(currencyFormat.format(tienTraTruocHienTai.getSoDuHienTai()));
                        } else {
                            view.getLblSoDuHienTai().setText("0 VND");
                        }
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
                tienTraTruocHienTai = tienTraTruocService.getTienTraTruocByMaKhachHang(maKhachHang);
                if (tienTraTruocHienTai != null) {
                    view.getLblSoDuHienTai().setText(currencyFormat.format(tienTraTruocHienTai.getSoDuHienTai()));
                } else {
                    view.getLblSoDuHienTai().setText("0 VND");
                }

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

    private void handleTaoTaiKhoan() {
        if (khachHangHienTai == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn khách hàng trước khi tạo tài khoản",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Kiểm tra xem khách hàng đã có tài khoản chưa
            if (tienTraTruocService.kiemTraTonTai(khachHangHienTai.getMaKhachHang())) {
                JOptionPane.showMessageDialog(view, "Khách hàng đã có tài khoản trả trước!",
                        "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Tạo tài khoản trả trước mới
            TienTraTruoc taiKhoanMoi = new TienTraTruoc();
            taiKhoanMoi.setMaKhachHang(khachHangHienTai.getMaKhachHang());
            taiKhoanMoi.setTenKhachHang(khachHangHienTai.getHoTen());
            taiKhoanMoi.setSoDienThoai(khachHangHienTai.getSoDienThoai());
            taiKhoanMoi.setSoDuHienTai(BigDecimal.ZERO);
            taiKhoanMoi.setNgayCapNhat(java.time.LocalDateTime.now());

            boolean success = tienTraTruocService.taoTaiKhoanTraTruoc(taiKhoanMoi);
            if (success) {
                JOptionPane.showMessageDialog(view, "Tạo tài khoản trả trước thành công!",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                
                // Cập nhật giao diện
                tienTraTruocHienTai = taiKhoanMoi;
                view.getLblSoDuHienTai().setText(currencyFormat.format(tienTraTruocHienTai.getSoDuHienTai()));
                view.getBtnTaoTaiKhoan().setEnabled(false);
                view.getBtnNapTien().setEnabled(true);
                view.getBtnSuaSoDu().setEnabled(true);
                loadDanhSachTraTruoc();
            } else {
                JOptionPane.showMessageDialog(view, "Tạo tài khoản trả trước thất bại!",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi khi tạo tài khoản: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleNapTien() {
        if (khachHangHienTai == null || tienTraTruocHienTai == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn khách hàng có tài khoản trả trước",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String soTienStr = view.getTxtSoTienThem().getText().trim();
            if (soTienStr.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Vui lòng nhập số tiền cần nạp",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            BigDecimal soTienThem = new BigDecimal(soTienStr.replaceAll("[^\\d]", ""));
            if (soTienThem.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(view, "Số tiền nạp phải lớn hơn 0",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    view,
                    "Xác nhận nạp " + currencyFormat.format(soTienThem) + " cho khách hàng " + khachHangHienTai.getHoTen() + "?",
                    "Xác nhận nạp tiền",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = tienTraTruocService.themTien(khachHangHienTai.getMaKhachHang(), soTienThem);
                if (success) {
                    // Cập nhật thông tin tài khoản
                    tienTraTruocHienTai = tienTraTruocService.getTienTraTruocByMaKhachHang(khachHangHienTai.getMaKhachHang());
                    
                    JOptionPane.showMessageDialog(view, 
                            "Nạp tiền thành công!\n" +
                            "Số tiền: " + currencyFormat.format(soTienThem) + "\n" +
                            "Số dư mới: " + currencyFormat.format(tienTraTruocHienTai.getSoDuHienTai()),
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);

                    // Cập nhật giao diện
                    view.getTxtSoTienThem().setText("");
                    view.getLblSoDuHienTai().setText(currencyFormat.format(tienTraTruocHienTai.getSoDuHienTai()));
                    loadDanhSachTraTruoc();
                } else {
                    JOptionPane.showMessageDialog(view, "Nạp tiền thất bại!",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(view, "Số tiền không hợp lệ",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi khi nạp tiền: " + e.getMessage(),
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
            resetThongTinKhachHang();
            view.getTxtSoTienThem().setText("");
            loadDanhSachTraTruoc();
        }
    }

    private void resetThongTinKhachHang() {
        khachHangHienTai = null;
        tienTraTruocHienTai = null;
        view.getLblSoDuHienTai().setText("0 VND");
        view.getBtnTaoTaiKhoan().setEnabled(false);
        view.getBtnNapTien().setEnabled(false);
        view.getBtnSuaSoDu().setEnabled(false);
        view.getCboKhachHang().setSelectedIndex(0);
    }

    private void capNhatTongTienTraTruoc() {
        view.getLblTongTienTraTruoc().setText(currencyFormat.format(tongTienTraTruoc));
    }

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

    private void handleChinhSuaSoDu() {
        if (khachHangHienTai == null || tienTraTruocHienTai == null) {
            JOptionPane.showMessageDialog(view, 
                "Vui lòng chọn khách hàng có tài khoản trả trước để chỉnh sửa",
                "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Hiển thị dialog chỉnh sửa
            ChinhSuaTienTraTruocDialog dialog = new ChinhSuaTienTraTruocDialog(
                (Frame) SwingUtilities.getWindowAncestor(view),
                khachHangHienTai.getHoTen(),
                tienTraTruocHienTai.getSoDuHienTai()
            );
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                BigDecimal soDuMoi = dialog.getSoDuMoi();
                String lyDo = dialog.getLyDoChinhSua();

                int confirm = JOptionPane.showConfirmDialog(
                    view,
                    "Xác nhận thay đổi số dư từ " + 
                    currencyFormat.format(tienTraTruocHienTai.getSoDuHienTai()) + 
                    " thành " + currencyFormat.format(soDuMoi) + "?",
                    "Xác nhận chỉnh sửa",
                    JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    boolean success = tienTraTruocService.chinhSuaSoDu(
                        khachHangHienTai.getMaKhachHang(), 
                        soDuMoi, 
                        lyDo
                    );

                    if (success) {
                        JOptionPane.showMessageDialog(view,
                            "Chỉnh sửa số dư thành công!\n" +
                            "Số dư mới: " + currencyFormat.format(soDuMoi),
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);

                        // Cập nhật giao diện
                        tienTraTruocHienTai.setSoDuHienTai(soDuMoi);
                        view.getLblSoDuHienTai().setText(currencyFormat.format(soDuMoi));
                        loadDanhSachTraTruoc();
                    } else {
                        JOptionPane.showMessageDialog(view,
                            "Chỉnh sửa số dư thất bại!",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view,
                "Lỗi khi chỉnh sửa số dư: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}