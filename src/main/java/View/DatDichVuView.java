package View;

import Model.KhachHang;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

public class DatDichVuView extends JPanel {

    // Màu sắc mới - thân thiện và chuyên nghiệp
    private final Color COLOR_PRIMARY = new Color(74, 138, 87);     // Xanh lá chính #4A8A57
    private final Color COLOR_SECONDARY = new Color(108, 187, 126); // Xanh lá nhạt #6CBB7E
    private final Color COLOR_ACCENT = new Color(255, 107, 107);    // Đỏ cam #FF6B6B
    private final Color COLOR_BACKGROUND = new Color(248, 250, 252); // Nền trắng xám #F8FAFC
    private final Color COLOR_CARD = Color.WHITE;                   // Nền card trắng
    private final Color COLOR_TEXT_PRIMARY = new Color(51, 51, 51);  // Chữ đậm #333333
    private final Color COLOR_TEXT_SECONDARY = new Color(102, 102, 102); // Chữ nhạt #666666
    private final Color COLOR_BORDER = new Color(222, 226, 230);    // Viền xám #DEE2E6

    // Các thành phần giao diện
    private JComboBox<KhachHang> cboKhachHang;
    private JComboBox<String> cboDichVu;
    private JComboBox<String> cboNhanVien;
    private JTextField txtSoLuongNguoi;
    private JTextField txtThoiGian;
    private JLabel lblDiemTichLuy;
    private JLabel lblTongTien;
    private JTable tblDichVuDaChon;
    private DefaultTableModel tableModel;

    // Các nút
    private JButton btnThemKhachHang;
    private JButton btnThemDichVu;
    private JButton btnXoaDichVu;
    private JButton btnDoiDiem;
    private JButton btnInHoaDon;
    private JButton btnLamMoi;
    private JButton btnTimKhachHang;

    public DatDichVuView() {
        initComponents();
        setupUI();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        setBackground(COLOR_BACKGROUND);

        // Panel chính với padding
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(COLOR_BACKGROUND);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Panel tiêu đề
        JPanel titlePanel = createTitlePanel();
        mainContainer.add(titlePanel, BorderLayout.NORTH);

        // Panel nội dung chính - SỬA ĐỔI: Sử dụng BoxLayout để dễ responsive
        JPanel contentPanel = createContentPanel();
        mainContainer.add(contentPanel, BorderLayout.CENTER);

        add(mainContainer, BorderLayout.CENTER);
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        JLabel lblTitle = new JLabel("ĐẶT DỊCH VỤ SPA");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(COLOR_PRIMARY);

        return panel;
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_BACKGROUND);

        // Panel thông tin đặt dịch vụ
        JPanel infoPanel = createInfoPanel();
        panel.add(infoPanel);

        // Thêm khoảng cách
        panel.add(Box.createRigidArea(new Dimension(0, 5)));

        // Panel danh sách dịch vụ đã chọn - SỬA ĐỔI: Chiếm nhiều không gian hơn
        JPanel tablePanel = createTablePanel();
        panel.add(tablePanel);

        // Thêm khoảng cách
        panel.add(Box.createRigidArea(new Dimension(0, 5)));

        // Panel thông tin thanh toán và nút
        JPanel bottomPanel = createBottomPanel();
        panel.add(bottomPanel);

        return panel;
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_BACKGROUND);

        // Hàng 1: Khách hàng và Dịch vụ - SỬA ĐỔI: Sử dụng BoxLayout ngang
        JPanel row1 = new JPanel();
        row1.setLayout(new BoxLayout(row1, BoxLayout.X_AXIS));
        row1.setBackground(COLOR_BACKGROUND);

        JPanel khachHangPanel = createKhachHangPanel();
        khachHangPanel.setPreferredSize(new Dimension(500, 80));
        row1.add(khachHangPanel);

        row1.add(Box.createRigidArea(new Dimension(10, 0)));

        JPanel dichVuPanel = createDichVuPanel();
        dichVuPanel.setPreferredSize(new Dimension(500, 80));
        row1.add(dichVuPanel);

        // Hàng 2: Thông tin chi tiết - SỬA ĐỔI: Sử dụng GridLayout linh hoạt
        JPanel row2 = new JPanel(new GridLayout(1, 4, 10, 0));
        row2.setBackground(COLOR_BACKGROUND);
        row2.add(createSoLuongPanel());
        row2.add(createThoiGianPanel());
        row2.add(createNhanVienPanel());
        row2.add(createDiemTichLuyPanel());

        panel.add(row1);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(row2);

        return panel;
    }

    private JPanel createKhachHangPanel() {
        JPanel panel = createCardPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel lblTitle = new JLabel("KHÁCH HÀNG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(COLOR_TEXT_SECONDARY);

        // Khởi tạo combobox với KhachHang
        cboKhachHang = new JComboBox<>();

        // Style combobox
        cboKhachHang.setBackground(Color.WHITE);
        cboKhachHang.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        cboKhachHang.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Renderer để hiển thị thông tin khách hàng đẹp
        cboKhachHang.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value instanceof KhachHang) {
                    KhachHang kh = (KhachHang) value;
                    if (kh.getMaKhachHang() == null) {
                        setText("-- Chọn khách hàng --");
                    } else {
                        String sdt = (kh.getSoDienThoai() != null && !kh.getSoDienThoai().isEmpty())
                                ? kh.getSoDienThoai() : "[Chưa có SĐT]";
                        setText(kh.getHoTen() + " - " + sdt);
                    }
                }
                return this;
            }
        });

        // Tạo các nút
        btnThemKhachHang = createIconButton("+", "Thêm khách hàng mới");
        btnThemKhachHang.setBackground(COLOR_SECONDARY);
        btnThemKhachHang.setPreferredSize(new Dimension(40, 35));

        btnTimKhachHang = createIconButton("...", "Tìm kiếm khách hàng");
        btnTimKhachHang.setBackground(new Color(70, 130, 180));
        btnTimKhachHang.setPreferredSize(new Dimension(40, 35));

        // Panel chứa 2 nút
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        buttonPanel.setBackground(COLOR_CARD);
        buttonPanel.setPreferredSize(new Dimension(90, 35));
        buttonPanel.add(btnThemKhachHang);
        buttonPanel.add(btnTimKhachHang);

        // Thiết lập layout với GridBagConstraints
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Chiếm 2 cột
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 8, 0); // Bottom padding
        panel.add(lblTitle, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1; // Reset về 1 cột
        gbc.weightx = 0.8; // Combobox chiếm 80%
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 5); // Right padding
        panel.add(cboKhachHang, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.2; // Nút chiếm 20%
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(0, 0, 0, 0); // No padding
        panel.add(buttonPanel, gbc);

        return panel;
    }

    private JPanel createDichVuPanel() {
        JPanel panel = createCardPanel();
        panel.setLayout(new BorderLayout()); // THÊM DÒNG NÀY

        JLabel lblTitle = new JLabel("DỊCH VỤ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(COLOR_TEXT_SECONDARY);

        // KHỞI TẠO COMBOBOX VỚI DỮ LIỆU MẪU
        cboDichVu = new JComboBox<>(new String[]{"-- Chọn dịch vụ --", "Massage thư giãn", "Chăm sóc da", "Tắm trắng", "Phun xăm"});
        styleComboBox(cboDichVu);
        cboDichVu.setPreferredSize(new Dimension(300, 35)); // SET KÍCH THƯỚC

        btnThemDichVu = createPrimaryButton("THÊM");
        btnThemDichVu.setPreferredSize(new Dimension(120, 35)); // Làm nút nhỏ hơn

        // SỬA LẠI LAYOUT
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(COLOR_CARD);
        titlePanel.add(lblTitle);

        JPanel comboPanel = new JPanel(new BorderLayout(5, 0));
        comboPanel.setBackground(COLOR_CARD);
        comboPanel.add(cboDichVu, BorderLayout.CENTER);
        comboPanel.add(btnThemDichVu, BorderLayout.EAST);

        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(comboPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSoLuongPanel() {
        JPanel panel = createCardPanel();

        JLabel lblTitle = new JLabel("SỐ LƯỢNG NGƯỜI");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(COLOR_TEXT_SECONDARY);

        txtSoLuongNguoi = new JTextField("1");
        styleTextField(txtSoLuongNguoi);
        txtSoLuongNguoi.setHorizontalAlignment(JTextField.CENTER);

        panel.add(lblTitle);
        panel.add(txtSoLuongNguoi);

        return panel;
    }

    private JPanel createThoiGianPanel() {
        JPanel panel = createCardPanel();

        JLabel lblTitle = new JLabel("THỜI GIAN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(COLOR_TEXT_SECONDARY);

        txtThoiGian = new JTextField();
        styleTextField(txtThoiGian);
        txtThoiGian.setEditable(false);
        txtThoiGian.setBackground(new Color(245, 245, 245));

        panel.add(lblTitle);
        panel.add(txtThoiGian);

        return panel;
    }

    private JPanel createNhanVienPanel() {
        JPanel panel = createCardPanel();

        JLabel lblTitle = new JLabel("NHÂN VIÊN THỰC HIỆN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(COLOR_TEXT_SECONDARY);

        cboNhanVien = new JComboBox<>();
        styleComboBox(cboNhanVien);

        panel.add(lblTitle);
        panel.add(cboNhanVien);

        return panel;
    }

    private JPanel createDiemTichLuyPanel() {
        JPanel panel = createCardPanel();
        panel.setLayout(new GridLayout(3, 1, 2, 2)); // Sửa thành 3 dòng

        JLabel lblTitle = new JLabel("ĐIỂM TÍCH LŨY");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(COLOR_TEXT_SECONDARY);

        lblDiemTichLuy = new JLabel("0 điểm", JLabel.CENTER);
        lblDiemTichLuy.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblDiemTichLuy.setForeground(COLOR_ACCENT);
        lblDiemTichLuy.setOpaque(true);
        lblDiemTichLuy.setBackground(new Color(255, 245, 245));
        lblDiemTichLuy.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_ACCENT, 1),
                BorderFactory.createEmptyBorder(8, 5, 8, 5)
        ));

        // THÊM LABEL THÔNG BÁO
        JLabel lblNote = new JLabel("(Tối thiểu 10 điểm)", JLabel.CENTER);
        lblNote.setFont(new Font("Segoe UI", Font.ITALIC, 9));
        lblNote.setForeground(COLOR_TEXT_SECONDARY);

        panel.add(lblTitle);
        panel.add(lblDiemTichLuy);
        panel.add(lblNote);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BACKGROUND);

        // Giảm kích thước bảng
        panel.setPreferredSize(new Dimension(1000, 250)); // Giảm từ 400 xuống 250
        panel.setMinimumSize(new Dimension(800, 200));    // Giảm từ 300 xuống 200
        panel.setMaximumSize(new Dimension(1200, 300));   // Giảm từ 500 xuống 300

        JLabel lblTitle = new JLabel("DANH SÁCH DỊCH VỤ ĐÃ CHỌN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(COLOR_PRIMARY);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0)); // Giảm padding

        // Tạo bảng với kích thước cột tối ưu hơn
        String[] columns = {"STT", "Tên dịch vụ", "Thời gian", "Đơn giá", "Số lượng", "Nhân viên", "Thành tiền"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }
        };

        tblDichVuDaChon = new JTable(tableModel);
        styleTable(tblDichVuDaChon);

        JScrollPane scrollPane = new JScrollPane(tblDichVuDaChon);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_BORDER));
        scrollPane.setPreferredSize(new Dimension(1000, 200)); // Giảm chiều cao
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BACKGROUND);

        // Panel tổng tiền
        JPanel totalPanel = createTotalPanel();

        // Panel nút chức năng
        JPanel buttonPanel = createButtonPanel();

        panel.add(totalPanel, BorderLayout.WEST);
        panel.add(buttonPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createTotalPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(COLOR_BACKGROUND);

        JLabel lblTitle = new JLabel("TỔNG TIỀN:");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(COLOR_TEXT_PRIMARY);

        lblTongTien = new JLabel("0 VND");
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTongTien.setForeground(COLOR_ACCENT);

        panel.add(lblTitle);
        panel.add(Box.createRigidArea(new Dimension(10, 0)));
        panel.add(lblTongTien);
        panel.add(Box.createRigidArea(new Dimension(30, 0)));

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setBackground(COLOR_BACKGROUND);

        btnXoaDichVu = createSecondaryButton("XÓA DỊCH VỤ");
        btnDoiDiem = createSecondaryButton("ĐỔI ĐIỂM");
        btnInHoaDon = createPrimaryButton("IN HÓA ĐƠN PDF");
        btnLamMoi = createSecondaryButton("LÀM MỚI");

        panel.add(btnXoaDichVu);
        panel.add(btnDoiDiem);
        btnDoiDiem.setToolTipText("Tối thiểu 10 điểm mới được đổi");
        panel.add(btnInHoaDon);
        panel.add(btnLamMoi);

        return panel;
    }

    // Các phương thức tạo component với style
    private JPanel createCardPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5));
        panel.setBackground(COLOR_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        return panel;
    }

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }

    private void styleTextField(JTextField textField) {
        textField.setBackground(Color.WHITE);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }

    private void styleTable(JTable table) {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBackground(COLOR_PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11)); // Giảm font size
        table.setRowHeight(25); // Giảm chiều cao dòng từ 30 xuống 25
        table.setFont(new Font("Segoe UI", Font.PLAIN, 10)); // Giảm font size
        table.setShowGrid(true);
        table.setGridColor(COLOR_BORDER);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Tối ưu kích thước cột cho màn hình nhỏ
        table.getColumnModel().getColumn(0).setPreferredWidth(35);   // STT (giảm)
        table.getColumnModel().getColumn(1).setPreferredWidth(150);  // Tên dịch vụ (giảm)
        table.getColumnModel().getColumn(2).setPreferredWidth(60);   // Thời gian (giảm)
        table.getColumnModel().getColumn(3).setPreferredWidth(80);   // Đơn giá (giảm)
        table.getColumnModel().getColumn(4).setPreferredWidth(60);   // Số lượng (giảm)
        table.getColumnModel().getColumn(5).setPreferredWidth(100);  // Nhân viên (giảm)
        table.getColumnModel().getColumn(6).setPreferredWidth(90);   // Thành tiền (giảm)
    }

    private JButton createPrimaryButton(String text) {
        return createButton(text, COLOR_PRIMARY, Color.WHITE);
    }

    private JButton createSecondaryButton(String text) {
        return createButton(text, new Color(108, 117, 125), Color.WHITE);
    }

    private JButton createIconButton(String text, String tooltip) {
        JButton button = new JButton(text);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setToolTipText(tooltip);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(button.getBackground().darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(COLOR_SECONDARY);
            }
        });

        return button;
    }

    private JButton createButton(String text, Color backgroundColor, Color textColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(textColor);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(backgroundColor.darker(), 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.darker());
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(backgroundColor.darker().darker(), 1),
                        BorderFactory.createEmptyBorder(10, 20, 10, 20)
                ));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(backgroundColor.darker(), 1),
                        BorderFactory.createEmptyBorder(10, 20, 10, 20)
                ));
            }
        });

        return button;
    }

    private void setupUI() {
        // SỬA ĐỔI: Kích thước linh hoạt hơn
        setPreferredSize(new Dimension(1100, 600));
        setMinimumSize(new Dimension(900, 400));
    }

    // Getter methods (giữ nguyên)
    public JComboBox<KhachHang> getCboKhachHang() {
        return cboKhachHang;
    }

    public JComboBox<String> getCboDichVu() {
        return cboDichVu;
    }

    public JComboBox<String> getCboNhanVien() {
        return cboNhanVien;
    }

    public JTextField getTxtSoLuongNguoi() {
        return txtSoLuongNguoi;
    }

    public JTextField getTxtThoiGian() {
        return txtThoiGian;
    }

    public JLabel getLblDiemTichLuy() {
        return lblDiemTichLuy;
    }

    public JLabel getLblTongTien() {
        return lblTongTien;
    }

    public JTable getTblDichVuDaChon() {
        return tblDichVuDaChon;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JButton getBtnThemKhachHang() {
        return btnThemKhachHang;
    }

    public JButton getBtnThemDichVu() {
        return btnThemDichVu;
    }

    public JButton getBtnXoaDichVu() {
        return btnXoaDichVu;
    }

    public JButton getBtnDoiDiem() {
        return btnDoiDiem;
    }

    public JButton getBtnInHoaDon() {
        return btnInHoaDon;
    }

    public JButton getBtnLamMoi() {
        return btnLamMoi;
    }

    public JButton getBtnTimKhachHang() {
        return btnTimKhachHang;
    }

    // Phương thức hỗ trợ (giữ nguyên)
    public void themKhachHangVaoComboBox(KhachHang khachHang) {
        DefaultComboBoxModel<KhachHang> model = (DefaultComboBoxModel<KhachHang>) cboKhachHang.getModel();
        model.addElement(khachHang);
    }

    public void setCheDoChinhSua(boolean cheDoChinhSua, Integer maHoaDon) {
        if (cheDoChinhSua && maHoaDon != null) {
            // Tìm và cập nhật tiêu đề
            JPanel mainContainer = (JPanel) getComponent(0);
            JPanel titlePanel = (JPanel) mainContainer.getComponent(0);
            JPanel textPanel = (JPanel) titlePanel.getComponent(0);
            JLabel lblTitle = (JLabel) textPanel.getComponent(0);

            lblTitle.setText("SỬA HÓA ĐƠN DỊCH VỤ #" + maHoaDon);
            lblTitle.setForeground(new Color(255, 107, 107)); // Màu đỏ để phân biệt

            // Có thể cập nhật subtitle nếu muốn
            JLabel lblSubtitle = (JLabel) textPanel.getComponent(1);
            lblSubtitle.setText("Chế độ chỉnh sửa - Cẩn thận khi thay đổi thông tin");
        } else {
            // Khôi phục về trạng thái ban đầu
            JPanel mainContainer = (JPanel) getComponent(0);
            JPanel titlePanel = (JPanel) mainContainer.getComponent(0);
            JPanel textPanel = (JPanel) titlePanel.getComponent(0);
            JLabel lblTitle = (JLabel) textPanel.getComponent(0);

            lblTitle.setText("ĐẶT DỊCH VỤ SPA");
            lblTitle.setForeground(new Color(74, 138, 87)); // Màu xanh ban đầu

            JLabel lblSubtitle = (JLabel) textPanel.getComponent(1);
            lblSubtitle.setText("Quản lý đặt dịch vụ và tích điểm khách hàng");
        }

        revalidate();
        repaint();
    }

    public void capNhatComboBoxKhachHang() {
        cboKhachHang.revalidate();
        cboKhachHang.repaint();
    }

    public void xoaTatCaDichVu() {
        tableModel.setRowCount(0);
    }

    public int getSoLuongDichVuDaChon() {
        return tableModel.getRowCount();
    }
}
