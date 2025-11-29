package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

public class LichSuGiaoDichTraTruocView extends JPanel {

    // Màu sắc thân thiện và chuyên nghiệp
    private final Color COLOR_PRIMARY = new Color(74, 138, 87);     // Xanh lá chính #4A8A57
    private final Color COLOR_SECONDARY = new Color(108, 187, 126); // Xanh lá nhạt #6CBB7E
    private final Color COLOR_ACCENT = new Color(255, 107, 107);    // Đỏ cam #FF6B6B
    private final Color COLOR_BACKGROUND = new Color(248, 250, 252); // Nền trắng xám #8FAFC
    private final Color COLOR_CARD = Color.WHITE;                   // Nền card trắng
    private final Color COLOR_TEXT_PRIMARY = new Color(51, 51, 51);  // Chữ đậm #333333
    private final Color COLOR_TEXT_SECONDARY = new Color(102, 102, 102); // Chữ nhạt #666666
    private final Color COLOR_BORDER = new Color(222, 226, 230);    // Viền xám #DEE2E6

    // Các thành phần giao diện
    private JComboBox<String> cboKhachHang;
    private JTable tblLichSuGiaoDich;
    private DefaultTableModel tableModel;

    // Các nút
    private JButton btnTimKhachHang;
    private JButton btnLoc;
    private JButton btnLamMoi;
    
    // JDateChooser components
    private com.toedter.calendar.JDateChooser dateChooserTuNgay;
    private com.toedter.calendar.JDateChooser dateChooserDenNgay;

    public LichSuGiaoDichTraTruocView() {
        initComponents();
        setupUI();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        setBackground(COLOR_BACKGROUND);

        // Panel chính với padding lớn hơn
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(COLOR_BACKGROUND);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Panel tiêu đề - CHỈ CÒN TIÊU ĐỀ CHÍNH
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
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));

        JLabel lblTitle = new JLabel("LỊCH SỬ GIAO DỊCH TRẢ TRƯỚC");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(COLOR_PRIMARY);

        panel.add(lblTitle, BorderLayout.WEST);

        return panel;
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(COLOR_BACKGROUND);

        // Panel bộ lọc - TO HƠN do có thêm không gian
        JPanel filterPanel = createFilterPanel();
        panel.add(filterPanel, BorderLayout.NORTH);

        // Panel danh sách lịch sử giao dịch
        JPanel tablePanel = createTablePanel();
        panel.add(tablePanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 20));
        panel.setBackground(COLOR_BACKGROUND);
        panel.setPreferredSize(new Dimension(0, 250));

        // Hàng 1: Khách hàng - CHIẾM TOÀN BỘ HÀNG ĐẦU TIÊN
        JPanel row1 = new JPanel(new BorderLayout(15, 0));
        row1.setBackground(COLOR_BACKGROUND);
        row1.add(createKhachHangPanel(), BorderLayout.CENTER);

        // Hàng 2: Thời gian và nút
        JPanel row2 = new JPanel(new GridLayout(1, 4, 15, 0));
        row2.setBackground(COLOR_BACKGROUND);
        row2.add(createTuNgayPanel());
        row2.add(createDenNgayPanel());
        row2.add(createLocPanel());

        panel.add(row1);
        panel.add(row2);

        return panel;
    }

    private JPanel createKhachHangPanel() {
        JPanel panel = createCardPanel();
        panel.setLayout(new BorderLayout(15, 10));

        JLabel lblTitle = new JLabel("KHÁCH HÀNG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(COLOR_TEXT_SECONDARY);

        cboKhachHang = new JComboBox<>();
        styleComboBox(cboKhachHang);
        cboKhachHang.setPreferredSize(new Dimension(400, 50));
        cboKhachHang.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        btnTimKhachHang = createIconButton("Tìm kiếm khách hàng", "Tìm kiếm");
        btnTimKhachHang.setBackground(new Color(70, 130, 180));
        btnTimKhachHang.setPreferredSize(new Dimension(180, 50));
        btnTimKhachHang.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JPanel inputPanel = new JPanel(new BorderLayout(15, 0));
        inputPanel.setBackground(COLOR_CARD);
        inputPanel.add(cboKhachHang, BorderLayout.CENTER);
        inputPanel.add(btnTimKhachHang, BorderLayout.EAST);

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(inputPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTuNgayPanel() {
        JPanel panel = createCardPanel();
        panel.setLayout(new BorderLayout(10, 10));

        JLabel lblTitle = new JLabel("TỪ NGÀY");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(COLOR_TEXT_SECONDARY);

        dateChooserTuNgay = new com.toedter.calendar.JDateChooser();
        styleDateChooser(dateChooserTuNgay);
        dateChooserTuNgay.setDateFormatString("dd/MM/yyyy");
        dateChooserTuNgay.setPreferredSize(new Dimension(200, 45));

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(dateChooserTuNgay, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDenNgayPanel() {
        JPanel panel = createCardPanel();
        panel.setLayout(new BorderLayout(10, 10));

        JLabel lblTitle = new JLabel("ĐẾN NGÀY");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(COLOR_TEXT_SECONDARY);

        dateChooserDenNgay = new com.toedter.calendar.JDateChooser();
        styleDateChooser(dateChooserDenNgay);
        dateChooserDenNgay.setDateFormatString("dd/MM/yyyy");
        dateChooserDenNgay.setPreferredSize(new Dimension(200, 45));

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(dateChooserDenNgay, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createLocPanel() {
        JPanel panel = createCardPanel();
        panel.setLayout(new GridLayout(2, 1, 10, 10));

        btnLoc = createPrimaryButton("LỌC DỮ LIỆU");
        btnLamMoi = createSecondaryButton("LÀM MỚI");

        btnLoc.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLamMoi.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        Dimension buttonSize = new Dimension(0, 45); // Giảm chiều cao từ 50px xuống 45px
        btnLoc.setPreferredSize(buttonSize);
        btnLamMoi.setPreferredSize(buttonSize);

        panel.add(btnLoc);
        panel.add(btnLamMoi);

        return panel;
    }


    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BACKGROUND);

        JLabel lblTitle = new JLabel("LỊCH SỬ GIAO DỊCH");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(COLOR_PRIMARY);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // Tạo bảng với các trường to hơn
        String[] columns = {"STT", "Mã KH", "Tên khách hàng", "Ngày giao dịch", "Số tiền tăng", "Số tiền giảm", "Tổng tiền", "Tiền phải trả", "Ghi chú"};
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

        tblLichSuGiaoDich = new JTable(tableModel);
        styleTable(tblLichSuGiaoDich);

        JScrollPane scrollPane = new JScrollPane(tblLichSuGiaoDich);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 2));
        scrollPane.setPreferredSize(new Dimension(0, 0));

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // Các phương thức tạo component với style
    private JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(COLOR_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15) // Giảm padding từ 20px xuống 15px
        ));
        return panel;
    }

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12) // Giảm padding từ 12,15,12,15 xuống 10,12,10,12
        ));
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 15));
    }

    private void styleDateChooser(com.toedter.calendar.JDateChooser dateChooser) {
        dateChooser.setBackground(Color.WHITE);
        dateChooser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateChooser.getCalendarButton().setBackground(COLOR_SECONDARY);
        dateChooser.getCalendarButton().setForeground(Color.WHITE);
        dateChooser.getCalendarButton().setFont(new Font("Segoe UI", Font.BOLD, 14));
        dateChooser.getCalendarButton().setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12)); // Giảm padding
        dateChooser.getCalendarButton().setCursor(new Cursor(Cursor.HAND_CURSOR));
        dateChooser.getCalendarButton().setText("Chọn ngày");
        
        // Style the text field part
        JTextField dateTextField = ((JTextField) dateChooser.getDateEditor().getUiComponent());
        dateTextField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12) // Giảm padding
        ));
        dateTextField.setBackground(Color.WHITE);
        dateTextField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }

    private void styleTable(JTable table) {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBackground(COLOR_PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setShowGrid(true);
        table.setGridColor(COLOR_BORDER);

        // Đặt kích thước cột
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(300);
        table.getColumnModel().getColumn(3).setPreferredWidth(180);
        table.getColumnModel().getColumn(4).setPreferredWidth(150);
        table.getColumnModel().getColumn(5).setPreferredWidth(150);
        table.getColumnModel().getColumn(6).setPreferredWidth(150);
        table.getColumnModel().getColumn(7).setPreferredWidth(150);
        table.getColumnModel().getColumn(8).setPreferredWidth(300);
    }

    private JButton createPrimaryButton(String text) {
        JButton button = createButton(text, COLOR_PRIMARY, Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return button;
    }

    private JButton createSecondaryButton(String text) {
        JButton button = createButton(text, new Color(108, 117, 125), Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return button;
    }

    private JButton createIconButton(String text, String tooltip) {
        JButton button = new JButton(text);
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15)); // Giảm padding từ 12,20,12,20 xuống 10,15,10,15
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setToolTipText(tooltip);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(button.getBackground().darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 130, 180));
            }
        });

        return button;
    }

    private JButton createButton(String text, Color backgroundColor, Color textColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(textColor);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(backgroundColor.darker(), 1),
                BorderFactory.createEmptyBorder(10, 18, 10, 18) // Giảm padding từ 15,25,15,25 xuống 10,18,10,18
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.darker());
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(backgroundColor.darker().darker(), 1),
                        BorderFactory.createEmptyBorder(10, 18, 10, 18)
                ));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(backgroundColor.darker(), 1),
                        BorderFactory.createEmptyBorder(10, 18, 10, 18)
                ));
            }
        });

        return button;
    }

    private void setupUI() {
        setPreferredSize(new Dimension(1400, 800));
    }

    // Getter methods
    public JComboBox<String> getCboKhachHang() {
        return cboKhachHang;
    }

    public com.toedter.calendar.JDateChooser getDateChooserTuNgay() {
        return dateChooserTuNgay;
    }

    public com.toedter.calendar.JDateChooser getDateChooserDenNgay() {
        return dateChooserDenNgay;
    }

    public JTable getTblLichSuGiaoDich() {
        return tblLichSuGiaoDich;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JButton getBtnTimKhachHang() {
        return btnTimKhachHang;
    }

    public JButton getBtnLoc() {
        return btnLoc;
    }

    public JButton getBtnLamMoi() {
        return btnLamMoi;
    }

    // Phương thức hỗ trợ
    public void themKhachHangVaoComboBox(String khachHangInfo) {
        DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) cboKhachHang.getModel();
        model.addElement(khachHangInfo);
    }

    public void xoaTatCaDuLieu() {
        tableModel.setRowCount(0);
    }

    public int getSoLuongGiaoDich() {
        return tableModel.getRowCount();
    }
}