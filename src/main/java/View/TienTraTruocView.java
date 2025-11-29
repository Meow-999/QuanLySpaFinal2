package View;

import Model.KhachHang;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

public class TienTraTruocView extends JPanel {

    // Màu sắc thân thiện và chuyên nghiệp
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
    private JTextField txtSoTienThem;
    private JLabel lblSoDuHienTai;
    private JLabel lblTongTienTraTruoc;
    private JTable tblDanhSachTraTruoc;
    private DefaultTableModel tableModel;

    // Các nút
    private JButton btnThemKhachHang;
    private JButton btnTimKhachHang;
    private JButton btnNapTien;
    private JButton btnTaoTaiKhoan;
    private JButton btnLamMoi;
    private JButton btnSuaSoDu;

    public TienTraTruocView() {
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

        // Panel nội dung chính
        JPanel contentPanel = createContentPanel();
        mainContainer.add(contentPanel, BorderLayout.CENTER);

        add(mainContainer, BorderLayout.CENTER);
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel lblTitle = new JLabel("QUẢN LÝ TIỀN TRẢ TRƯỚC");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(COLOR_PRIMARY);
        panel.add(lblTitle, BorderLayout.WEST);
        
        return panel;
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_BACKGROUND);

        // Panel thông tin - bố cục grid 2x3
        JPanel infoPanel = createInfoPanel();
        panel.add(infoPanel, BorderLayout.NORTH);

        // Panel danh sách
        JPanel tablePanel = createTablePanel();
        panel.add(tablePanel, BorderLayout.CENTER);

        // Panel tổng quan
        JPanel bottomPanel = createBottomPanel();
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 10, 10));
        panel.setBackground(COLOR_BACKGROUND);
        panel.setPreferredSize(new Dimension(0, 180));

        // Hàng 1, Cột 1: Khách hàng
        panel.add(createKhachHangPanel());
        
        // Hàng 1, Cột 2: Số dư hiện tại
        panel.add(createSoDuPanel());
        
        // Hàng 1, Cột 3: Nút Tạo TK
        panel.add(createTaoTaiKhoanPanel());

        // Hàng 2, Cột 1: Số tiền nạp thêm
        panel.add(createNapTienPanel());
        
        // Hàng 2, Cột 2: Nút Nạp Tiền
        panel.add(createNapTienButtonPanel());
        
        // Hàng 2, Cột 3: Nút Sửa số dư
        panel.add(createSuaSoDuPanel());

        return panel;
    }

    private JPanel createKhachHangPanel() {
        JPanel panel = createCardPanel();
        panel.setLayout(new BorderLayout(8, 8));

        JLabel lblTitle = new JLabel("KHÁCH HÀNG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
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

        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        inputPanel.setBackground(COLOR_CARD);
        inputPanel.add(cboKhachHang, BorderLayout.CENTER);
        inputPanel.add(buttonPanel, BorderLayout.EAST);

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(inputPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSoDuPanel() {
        JPanel panel = createCardPanel();
        panel.setLayout(new BorderLayout(8, 8));

        JLabel lblTitle = new JLabel("SỐ DƯ HIỆN TẠI");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitle.setForeground(COLOR_TEXT_SECONDARY);

        lblSoDuHienTai = new JLabel("0 VND", JLabel.CENTER);
        lblSoDuHienTai.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSoDuHienTai.setForeground(COLOR_ACCENT);
        lblSoDuHienTai.setOpaque(true);
        lblSoDuHienTai.setBackground(new Color(255, 245, 245));
        lblSoDuHienTai.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_ACCENT, 1),
                BorderFactory.createEmptyBorder(10, 5, 10, 5)
        ));

        JLabel lblNote = new JLabel("(Số dư khả dụng)", JLabel.CENTER);
        lblNote.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        lblNote.setForeground(COLOR_TEXT_SECONDARY);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(COLOR_CARD);
        contentPanel.add(lblSoDuHienTai, BorderLayout.CENTER);
        contentPanel.add(lblNote, BorderLayout.SOUTH);

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTaoTaiKhoanPanel() {
        JPanel panel = createCardPanel();
        panel.setLayout(new BorderLayout());
        
        btnTaoTaiKhoan = createPrimaryButton("TẠO TÀI KHOẢN");
        btnTaoTaiKhoan.setEnabled(false);
        panel.add(btnTaoTaiKhoan, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createNapTienPanel() {
        JPanel panel = createCardPanel();
        panel.setLayout(new BorderLayout(8, 8));

        JLabel lblTitle = new JLabel("SỐ TIỀN NẠP THÊM");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitle.setForeground(COLOR_TEXT_SECONDARY);

        txtSoTienThem = new JTextField();
        styleTextField(txtSoTienThem);
        txtSoTienThem.setHorizontalAlignment(JTextField.RIGHT);

        JLabel lblCurrency = new JLabel("VND");
        lblCurrency.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblCurrency.setForeground(COLOR_TEXT_SECONDARY);

        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        inputPanel.setBackground(COLOR_CARD);
        inputPanel.add(txtSoTienThem, BorderLayout.CENTER);
        inputPanel.add(lblCurrency, BorderLayout.EAST);

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(inputPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createNapTienButtonPanel() {
        JPanel panel = createCardPanel();
        panel.setLayout(new BorderLayout());
        
        btnNapTien = createSecondaryButton("NẠP TIỀN");
        btnNapTien.setEnabled(false);
        panel.add(btnNapTien, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createSuaSoDuPanel() {
        JPanel panel = createCardPanel();
        panel.setLayout(new BorderLayout());
        
        btnSuaSoDu = createWarningButton("SỬA SỐ DƯ");
        btnSuaSoDu.setEnabled(false);
        panel.add(btnSuaSoDu, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BACKGROUND);

        JLabel lblTitle = new JLabel("DANH SÁCH TÀI KHOẢN TRẢ TRƯỚC");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(COLOR_PRIMARY);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Tạo bảng
        String[] columns = {"STT", "Mã KH", "Tên khách hàng", "Số điện thoại", "Số dư hiện tại", "Ngày cập nhật"};
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

        tblDanhSachTraTruoc = new JTable(tableModel);
        styleTable(tblDanhSachTraTruoc);

        JScrollPane scrollPane = new JScrollPane(tblDanhSachTraTruoc);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_BORDER));
        scrollPane.setPreferredSize(new Dimension(0, 300));

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Panel tổng tiền
        JPanel totalPanel = createTotalPanel();

        // Panel nút chức năng
        JPanel buttonPanel = createFunctionButtonPanel();

        panel.add(totalPanel, BorderLayout.WEST);
        panel.add(buttonPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createTotalPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setBackground(COLOR_BACKGROUND);

        JLabel lblTitle = new JLabel("TỔNG TIỀN TRẢ TRƯỚC:");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(COLOR_TEXT_PRIMARY);

        lblTongTienTraTruoc = new JLabel("0 VND");
        lblTongTienTraTruoc.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTongTienTraTruoc.setForeground(COLOR_ACCENT);

        panel.add(lblTitle);
        panel.add(lblTongTienTraTruoc);

        return panel;
    }

    private JPanel createFunctionButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setBackground(COLOR_BACKGROUND);

        btnLamMoi = createSecondaryButton("LÀM MỚI");

        panel.add(btnLamMoi);

        return panel;
    }

    private JButton createWarningButton(String text) {
        return createButton(text, new Color(255, 193, 7), Color.BLACK);
    }

    // Các phương thức tạo component với style
    private JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(COLOR_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        return panel;
    }

    private void styleTextField(JTextField textField) {
        textField.setBackground(Color.WHITE);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    }

    private void styleTable(JTable table) {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBackground(COLOR_PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setShowGrid(true);
        table.setGridColor(COLOR_BORDER);

        // Highlight màu cho hàng được chọn
        table.setSelectionBackground(new Color(220, 237, 200));
        table.setSelectionForeground(COLOR_TEXT_PRIMARY);

        // Đặt kích thước cột
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(80);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(150);
        table.getColumnModel().getColumn(5).setPreferredWidth(120);
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
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setToolTipText(tooltip);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(button.getBackground().darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.getText().equals("+")) {
                    button.setBackground(COLOR_SECONDARY);
                } else {
                    button.setBackground(new Color(70, 130, 180));
                }
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
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.darker());
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(backgroundColor.darker().darker(), 1),
                        BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(backgroundColor.darker(), 1),
                        BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
            }
        });

        return button;
    }

    private void setupUI() {
        setPreferredSize(new Dimension(1100, 650));
    }

    // Getter methods
    public JComboBox<KhachHang> getCboKhachHang() {
        return cboKhachHang;
    }

    public JTextField getTxtSoTienThem() {
        return txtSoTienThem;
    }

    public JLabel getLblSoDuHienTai() {
        return lblSoDuHienTai;
    }

    public JLabel getLblTongTienTraTruoc() {
        return lblTongTienTraTruoc;
    }

    public JTable getTblDanhSachTraTruoc() {
        return tblDanhSachTraTruoc;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JButton getBtnThemKhachHang() {
        return btnThemKhachHang;
    }

    public JButton getBtnSuaSoDu() {
        return btnSuaSoDu;
    }

    public JButton getBtnTimKhachHang() {
        return btnTimKhachHang;
    }

    public JButton getBtnNapTien() {
        return btnNapTien;
    }

    public JButton getBtnTaoTaiKhoan() {
        return btnTaoTaiKhoan;
    }

    public JButton getBtnLamMoi() {
        return btnLamMoi;
    }

    // Phương thức hỗ trợ
    public void themKhachHangVaoComboBox(KhachHang khachHang) {
        DefaultComboBoxModel<KhachHang> model = (DefaultComboBoxModel<KhachHang>) cboKhachHang.getModel();
        model.addElement(khachHang);
    }

    public void capNhatComboBoxKhachHang() {
        cboKhachHang.revalidate();
        cboKhachHang.repaint();
    }

    public void xoaTatCaDuLieu() {
        tableModel.setRowCount(0);
    }

    public int getSoLuongTaiKhoan() {
        return tableModel.getRowCount();
    }
}