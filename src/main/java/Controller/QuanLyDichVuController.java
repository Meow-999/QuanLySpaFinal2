package Controller;

import View.QuanLyDichVuView;
import Model.DichVu;
import Model.LoaiDichVu;
import Service.DichVuService;
import Service.LoaiDichVuService;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;
import java.util.List;

public class QuanLyDichVuController {

    private QuanLyDichVuView view;
    private DichVuService dichVuService;
    private LoaiDichVuService loaiDichVuService;

    // Màu sắc
    private final Color COLOR_BACKGROUND = new Color(0x8C, 0xC9, 0x80);
    private final Color COLOR_BUTTON = new Color(0x4D, 0x8A, 0x57);
    private final Color COLOR_TEXT = Color.WHITE;

    public QuanLyDichVuController(QuanLyDichVuView view) {
        this.view = view;
        this.dichVuService = new DichVuService();
        this.loaiDichVuService = new LoaiDichVuService();

        initController();
        loadAllDichVu();
        loadLoaiDichVuToComboBox();
    }

    private void initController() {
        view.getBtnThem().addActionListener(e -> themDichVu());
        view.getBtnSua().addActionListener(e -> suaDichVu());
        view.getBtnXoa().addActionListener(e -> xoaDichVu());
        view.getBtnLamMoi().addActionListener(e -> lamMoi());
        view.getBtnTimKiem().addActionListener(e -> timKiemDichVu());
        view.getBtnLoaiDichVu().addActionListener(e -> showQuanLyLoaiDichVu());
    }

    private void lamMoi() {
        loadAllDichVu();
        view.getTxtTimKiem().setText("");
        view.getCboLoaiFilter().setSelectedIndex(0);
    }

    public void loadLoaiDichVuToComboBox() {
        try {
            List<LoaiDichVu> listLoaiDV = loaiDichVuService.getAllLoaiDichVu();
            JComboBox<String> cboLoaiFilter = view.getCboLoaiFilter();
            cboLoaiFilter.removeAllItems();
            cboLoaiFilter.addItem("Tất cả");

            for (LoaiDichVu loaiDV : listLoaiDV) {
                cboLoaiFilter.addItem(loaiDV.getTenLoaiDV());
            }
        } catch (Exception e) {
            hienThiThongBao("Lỗi khi tải loại dịch vụ: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadAllDichVu() {
        try {
            List<DichVu> listDichVu = dichVuService.getAllDichVu();
            displayDichVuOnTable(listDichVu);
        } catch (Exception e) {
            hienThiThongBao("Lỗi khi tải danh sách dịch vụ: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayDichVuOnTable(List<DichVu> listDichVu) {
        DefaultTableModel model = view.getModel();
        model.setRowCount(0);

        for (DichVu dv : listDichVu) {
            model.addRow(new Object[]{
                dv.getMaDichVu(),
                dv.getTenDichVu(),
                formatCurrency(dv.getGia()),
                formatThoiGian(dv.getThoiGian()),
                getTenLoaiDichVu(dv.getMaLoaiDV()),
                dv.getGhiChu()
            });
        }
    }

    private String formatThoiGian(Integer thoiGian) {
        if (thoiGian == null) {
            return "Không xác định";
        }
        return thoiGian + " phút";
    }

    private String getTenLoaiDichVu(Integer maLoaiDV) {
        if (maLoaiDV == null) {
            return "Chưa phân loại";
        }

        try {
            LoaiDichVu loaiDV = loaiDichVuService.getLoaiDichVuById(maLoaiDV);
            return loaiDV.getTenLoaiDV();
        } catch (Exception e) {
            return "Không xác định";
        }
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "0";
        }
        return String.format("%,d VNĐ", amount.longValue());
    }

    private void showQuanLyLoaiDichVu() {
        try {
            QuanLyLoaiDichVuController loaiDichVuController = new QuanLyLoaiDichVuController(this);
            loaiDichVuController.showView();
        } catch (Exception e) {
            hienThiThongBao("Lỗi khi mở quản lý loại dịch vụ: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ========== THÊM DỊCH VỤ ==========
    private void themDichVu() {
        try {
            JDialog dialog = createDialog("Thêm Dịch Vụ Mới", 500, 500);
            JPanel mainPanel = createMainPanel();

            JPanel formPanel = new JPanel(new BorderLayout(10, 10));
            formPanel.setBackground(COLOR_BACKGROUND);

            JPanel basicInfoPanel = new JPanel(new GridLayout(4, 2, 10, 10));
            basicInfoPanel.setBackground(COLOR_BACKGROUND);

            JTextField txtTenDV = new JTextField();
            JTextField txtGia = new JTextField();
            JTextField txtThoiGian = new JTextField();
            JComboBox<String> cboLoaiDV = new JComboBox<>();

            List<LoaiDichVu> listLoaiDV = loaiDichVuService.getAllLoaiDichVu();
            cboLoaiDV.addItem("-- Chọn loại dịch vụ --");
            for (LoaiDichVu loai : listLoaiDV) {
                cboLoaiDV.addItem(loai.getTenLoaiDV());
            }

            basicInfoPanel.add(createStyledLabel("Tên dịch vụ:"));
            basicInfoPanel.add(txtTenDV);
            basicInfoPanel.add(createStyledLabel("Giá dịch vụ:"));
            basicInfoPanel.add(txtGia);
            basicInfoPanel.add(createStyledLabel("Thời gian (phút):"));
            basicInfoPanel.add(txtThoiGian);
            basicInfoPanel.add(createStyledLabel("Loại dịch vụ:"));
            basicInfoPanel.add(cboLoaiDV);

            JPanel ghiChuPanel = new JPanel(new BorderLayout(5, 5));
            ghiChuPanel.setBackground(COLOR_BACKGROUND);

            JLabel lblGhiChu = createStyledLabel("Ghi chú:");
            JTextArea txtGhiChu = new JTextArea(6, 30);
            txtGhiChu.setLineWrap(true);
            txtGhiChu.setWrapStyleWord(true);
            JScrollPane scrollGhiChu = new JScrollPane(txtGhiChu);
            scrollGhiChu.setPreferredSize(new Dimension(400, 120));

            ghiChuPanel.add(lblGhiChu, BorderLayout.NORTH);
            ghiChuPanel.add(scrollGhiChu, BorderLayout.CENTER);

            formPanel.add(basicInfoPanel, BorderLayout.NORTH);
            formPanel.add(ghiChuPanel, BorderLayout.CENTER);

            JPanel buttonPanel = createButtonPanel();
            JButton btnThem = createStyledButton("Thêm", COLOR_BUTTON);
            JButton btnHuy = createStyledButton("Hủy", COLOR_BUTTON);

            btnThem.addActionListener(e -> handleThemDichVu(dialog, txtTenDV, txtGia, txtThoiGian, cboLoaiDV, txtGhiChu, listLoaiDV));
            btnHuy.addActionListener(e -> dialog.dispose());

            buttonPanel.add(btnThem);
            buttonPanel.add(btnHuy);

            mainPanel.add(formPanel, BorderLayout.CENTER);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            dialog.add(mainPanel);
            dialog.setVisible(true);

        } catch (Exception e) {
            hienThiThongBao("Lỗi khi thêm dịch vụ: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleThemDichVu(JDialog dialog, JTextField txtTenDV, JTextField txtGia,
            JTextField txtThoiGian, JComboBox<String> cboLoaiDV, JTextArea txtGhiChu,
            List<LoaiDichVu> listLoaiDV) {

        if (txtTenDV.getText().trim().isEmpty()) {
            hienThiThongBao("Tên dịch vụ không được để trống", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        BigDecimal gia;
        try {
            gia = new BigDecimal(txtGia.getText().replaceAll("[^\\d]", ""));
        } catch (NumberFormatException ex) {
            hienThiThongBao("Giá dịch vụ không hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Integer thoiGian = null;
        if (!txtThoiGian.getText().trim().isEmpty()) {
            try {
                thoiGian = Integer.parseInt(txtThoiGian.getText().trim());
                if (thoiGian <= 0) {
                    hienThiThongBao("Thời gian phải lớn hơn 0", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                hienThiThongBao("Thời gian không hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        Integer maLoaiDV = null;
        if (cboLoaiDV.getSelectedIndex() > 0) {
            maLoaiDV = listLoaiDV.get(cboLoaiDV.getSelectedIndex() - 1).getMaLoaiDV();
        }

        DichVu dichVu = new DichVu(
                txtTenDV.getText().trim(),
                gia,
                thoiGian,
                maLoaiDV,
                txtGhiChu.getText().trim()
        );

        boolean success = dichVuService.addDichVu(dichVu);
        if (success) {
            hienThiThongBao("Thêm dịch vụ thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadAllDichVu();
            dialog.dispose();
        } else {
            hienThiThongBao("Thêm dịch vụ thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ========== SỬA DỊCH VỤ ==========
    private void suaDichVu() {
        int selectedRow = view.getTblDichVu().getSelectedRow();
        if (selectedRow == -1) {
            hienThiThongBao("Vui lòng chọn một dịch vụ để sửa", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Lấy mã dịch vụ an toàn
            Object maDichVuObj = view.getModel().getValueAt(selectedRow, 0);
            int maDichVu;
            
            if (maDichVuObj instanceof Integer) {
                maDichVu = (Integer) maDichVuObj;
            } else if (maDichVuObj instanceof String) {
                String maStr = ((String) maDichVuObj).replaceAll("[^0-9]", "");
                maDichVu = Integer.parseInt(maStr);
            } else {
                maDichVu = Integer.parseInt(maDichVuObj.toString());
            }
            
            String tenDichVu = (String) view.getModel().getValueAt(selectedRow, 1);

            // 🎯 SỬA LỖI XÁC NHẬN Ở ĐÂY
            boolean confirmed = hienThiXacNhan("Bạn có chắc muốn sửa dịch vụ '" + tenDichVu + "' không?");
            
            if (confirmed) {
                DichVu dichVu = dichVuService.getDichVuById(maDichVu);
                if (dichVu == null) {
                    hienThiThongBao("Không tìm thấy dịch vụ cần sửa", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                showEditDichVuForm(dichVu);
            }

        } catch (Exception e) {
            hienThiThongBao("Lỗi khi sửa dịch vụ: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void showEditDichVuForm(DichVu dichVu) {
    try {
        JDialog dialog = createDialog("Sửa Dịch Vụ", 500, 500);
        JPanel mainPanel = createMainPanel();

        JPanel formPanel = new JPanel(new BorderLayout(10, 10));
        formPanel.setBackground(COLOR_BACKGROUND);

        JPanel basicInfoPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        basicInfoPanel.setBackground(COLOR_BACKGROUND);

        JTextField txtTenDV = new JTextField(dichVu.getTenDichVu());
        // SỬA Ở ĐÂY: Hiển thị giá không có phần thập phân
        JTextField txtGia = new JTextField(String.valueOf(dichVu.getGia().longValue()));
        JTextField txtThoiGian = new JTextField(dichVu.getThoiGian() != null ? dichVu.getThoiGian().toString() : "");
        JComboBox<String> cboLoaiDV = new JComboBox<>();

        List<LoaiDichVu> listLoaiDV = loaiDichVuService.getAllLoaiDichVu();
        cboLoaiDV.addItem("-- Chọn loại dịch vụ --");

        int selectedIndex = 0;
        for (int i = 0; i < listLoaiDV.size(); i++) {
            LoaiDichVu loai = listLoaiDV.get(i);
            cboLoaiDV.addItem(loai.getTenLoaiDV());

            if (dichVu.getMaLoaiDV() != null && dichVu.getMaLoaiDV().equals(loai.getMaLoaiDV())) {
                selectedIndex = i + 1;
            }
        }
        cboLoaiDV.setSelectedIndex(selectedIndex);

        basicInfoPanel.add(createStyledLabel("Tên dịch vụ:"));
        basicInfoPanel.add(txtTenDV);
        basicInfoPanel.add(createStyledLabel("Giá dịch vụ:"));
        basicInfoPanel.add(txtGia);
        basicInfoPanel.add(createStyledLabel("Thời gian (phút):"));
        basicInfoPanel.add(txtThoiGian);
        basicInfoPanel.add(createStyledLabel("Loại dịch vụ:"));
        basicInfoPanel.add(cboLoaiDV);

        JPanel ghiChuPanel = new JPanel(new BorderLayout(5, 5));
        ghiChuPanel.setBackground(COLOR_BACKGROUND);

        JLabel lblGhiChu = createStyledLabel("Ghi chú:");
        JTextArea txtGhiChu = new JTextArea(6, 30);
        txtGhiChu.setText(dichVu.getGhiChu() != null ? dichVu.getGhiChu() : "");
        txtGhiChu.setLineWrap(true);
        txtGhiChu.setWrapStyleWord(true);
        JScrollPane scrollGhiChu = new JScrollPane(txtGhiChu);
        scrollGhiChu.setPreferredSize(new Dimension(400, 120));

        ghiChuPanel.add(lblGhiChu, BorderLayout.NORTH);
        ghiChuPanel.add(scrollGhiChu, BorderLayout.CENTER);

        formPanel.add(basicInfoPanel, BorderLayout.NORTH);
        formPanel.add(ghiChuPanel, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        JButton btnCapNhat = createStyledButton("Cập nhật", COLOR_BUTTON);
        JButton btnHuy = createStyledButton("Hủy", COLOR_BUTTON);

        btnCapNhat.addActionListener(e -> handleCapNhatDichVu(dialog, dichVu, txtTenDV, txtGia, txtThoiGian, cboLoaiDV, txtGhiChu, listLoaiDV));
        btnHuy.addActionListener(e -> dialog.dispose());

        buttonPanel.add(btnCapNhat);
        buttonPanel.add(btnHuy);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);

    } catch (Exception e) {
        hienThiThongBao("Lỗi khi hiển thị form sửa dịch vụ: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}

    private void handleCapNhatDichVu(JDialog dialog, DichVu dichVu, JTextField txtTenDV,
            JTextField txtGia, JTextField txtThoiGian, JComboBox<String> cboLoaiDV, JTextArea txtGhiChu,
            List<LoaiDichVu> listLoaiDV) {

        if (txtTenDV.getText().trim().isEmpty()) {
            hienThiThongBao("Tên dịch vụ không được để trống", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        BigDecimal gia;
        try {
            gia = new BigDecimal(txtGia.getText().replaceAll("[^\\d]", ""));
        } catch (NumberFormatException ex) {
            hienThiThongBao("Giá dịch vụ không hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Integer thoiGian = null;
        if (!txtThoiGian.getText().trim().isEmpty()) {
            try {
                thoiGian = Integer.parseInt(txtThoiGian.getText().trim());
                if (thoiGian <= 0) {
                    hienThiThongBao("Thời gian phải lớn hơn 0", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                hienThiThongBao("Thời gian không hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        dichVu.setTenDichVu(txtTenDV.getText().trim());
        dichVu.setGia(gia);
        dichVu.setThoiGian(thoiGian);
        dichVu.setGhiChu(txtGhiChu.getText().trim());

        if (cboLoaiDV.getSelectedIndex() > 0) {
            dichVu.setMaLoaiDV(listLoaiDV.get(cboLoaiDV.getSelectedIndex() - 1).getMaLoaiDV());
        } else {
            dichVu.setMaLoaiDV(null);
        }

        try {
            boolean success = dichVuService.updateDichVu(dichVu);
            if (success) {
                hienThiThongBao("Cập nhật dịch vụ thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadAllDichVu();
                dialog.dispose();
            } else {
                hienThiThongBao("Cập nhật dịch vụ thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            hienThiThongBao("Lỗi khi cập nhật dịch vụ: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ========== XÓA DỊCH VỤ ==========
    private void xoaDichVu() {
        int selectedRow = view.getTblDichVu().getSelectedRow();
        if (selectedRow == -1) {
            hienThiThongBao("Vui lòng chọn một dịch vụ để xóa", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Lấy mã dịch vụ an toàn
            Object maDichVuObj = view.getModel().getValueAt(selectedRow, 0);
            int maDichVu;
            
            if (maDichVuObj instanceof Integer) {
                maDichVu = (Integer) maDichVuObj;
            } else if (maDichVuObj instanceof String) {
                String maStr = ((String) maDichVuObj).replaceAll("[^0-9]", "");
                maDichVu = Integer.parseInt(maStr);
            } else {
                maDichVu = Integer.parseInt(maDichVuObj.toString());
            }
            
            String tenDichVu = (String) view.getModel().getValueAt(selectedRow, 1);

            // 🎯 SỬA LỖI XÁC NHẬN Ở ĐÂY
            boolean confirmed = hienThiXacNhan("Bạn có chắc muốn xóa dịch vụ '" + tenDichVu + "' không?");
            
            if (confirmed) {
                boolean success = dichVuService.deleteDichVu(maDichVu);
                if (success) {
                    hienThiThongBao("Xóa dịch vụ thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadAllDichVu();
                } else {
                    hienThiThongBao("Xóa dịch vụ thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (Exception e) {
            hienThiThongBao("Lỗi khi xóa dịch vụ: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void timKiemDichVu() {
        try {
            String tuKhoa = view.getTxtTimKiem().getText().trim();
            String loaiFilter = (String) view.getCboLoaiFilter().getSelectedItem();

            List<DichVu> ketQua = !tuKhoa.isEmpty()
                    ? dichVuService.searchDichVuByTen(tuKhoa)
                    : dichVuService.getAllDichVu();

            if (!"Tất cả".equals(loaiFilter)) {
                ketQua.removeIf(dv -> !loaiFilter.equals(getTenLoaiDichVu(dv.getMaLoaiDV())));
            }

            displayDichVuOnTable(ketQua);

        } catch (Exception e) {
            hienThiThongBao("Lỗi khi tìm kiếm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ========== PHƯƠNG THỨC HIỂN THỊ THÔNG BÁO ==========
    
    // 🎯 SỬA LỖI QUAN TRỌNG: Phương thức xác nhận đơn giản và hiệu quả
    private boolean hienThiXacNhan(String message) {
        int result = JOptionPane.showConfirmDialog(
            view, 
            message, 
            "Xác nhận", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        return result == JOptionPane.YES_OPTION;
    }

    private void hienThiThongBao(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(view, message, title, messageType);
    }

    // ========== PHƯƠNG THỨC HỖ TRỢ TẠO GIAO DIỆN ==========
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(COLOR_TEXT);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });

        return button;
    }

    private JDialog createDialog(String title, int width, int height) {
        JDialog dialog = new JDialog();
        dialog.setTitle(title);
        dialog.setSize(width, height);
        dialog.setLocationRelativeTo(view);
        dialog.setModal(true);
        return dialog;
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(COLOR_BACKGROUND);
        return panel;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(COLOR_TEXT);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        return label;
    }
}