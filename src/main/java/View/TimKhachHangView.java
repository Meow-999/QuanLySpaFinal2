package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

public class TimKhachHangView extends JDialog {
    private final Color COLOR_PRIMARY = new Color(74, 138, 87);
    private final Color COLOR_BACKGROUND = new Color(0x8C, 0xC9, 0x80);
    private final Color COLOR_CARD = Color.WHITE;
    private final Color COLOR_BORDER = new Color(222, 226, 230);
    
    private JTextField txtTen;
    private JTextField txtSoDienThoai;
    private JTable tblKhachHang;
    private DefaultTableModel tableModel;
    private JButton btnTimKiem;
    private JButton btnChon;
    private JButton btnHuy;
    
    private Integer maKhachHangDuocChon;
    private String tenKhachHangDuocChon;
    private String soDienThoaiDuocChon;
    
    public TimKhachHangView(Frame parent) {
        super(parent, "Tìm Kiếm Khách Hàng", true);
        initComponents();
        setupUI();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(COLOR_BACKGROUND);
        setSize(600, 500);
        setLocationRelativeTo(getParent());
        
        // Panel tiêu đề
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);
        
        // Panel tìm kiếm
        JPanel searchPanel = createSearchPanel();
        add(searchPanel, BorderLayout.CENTER);
        
        // Panel nút
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel lblTitle = new JLabel("TÌM KIẾM KHÁCH HÀNG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(COLOR_PRIMARY);
        
        JLabel lblSubtitle = new JLabel("Tìm kiếm theo tên hoặc số điện thoại");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(Color.GRAY);
        
        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setBackground(COLOR_BACKGROUND);
        textPanel.add(lblTitle);
        textPanel.add(lblSubtitle);
        
        panel.add(textPanel, BorderLayout.WEST);
        
        return panel;
    }
    
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        
        // Panel điều kiện tìm kiếm
        JPanel conditionPanel = createConditionPanel();
        panel.add(conditionPanel, BorderLayout.NORTH);
        
        // Panel bảng kết quả
        JPanel tablePanel = createTablePanel();
        panel.add(tablePanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createConditionPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel lblTen = new JLabel("Tên khách hàng:");
        lblTen.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        txtTen = new JTextField();
        styleTextField(txtTen);
        
        JLabel lblSoDienThoai = new JLabel("Số điện thoại:");
        lblSoDienThoai.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        txtSoDienThoai = new JTextField();
        styleTextField(txtSoDienThoai);
        
        panel.add(lblTen);
        panel.add(txtTen);
        panel.add(lblSoDienThoai);
        panel.add(txtSoDienThoai);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BACKGROUND);
        
        // Tiêu đề bảng
        JLabel lblTitle = new JLabel("KẾT QUẢ TÌM KIẾM");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(COLOR_PRIMARY);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        // Bảng kết quả
        String[] columns = {"Mã KH", "Tên khách hàng", "Số điện thoại"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tblKhachHang = new JTable(tableModel);
        styleTable(tblKhachHang);
        
        JScrollPane scrollPane = new JScrollPane(tblKhachHang);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_BORDER));
        
        // Panel nút tìm kiếm
        btnTimKiem = createPrimaryButton("TÌM KIẾM");
        
        JPanel searchButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchButtonPanel.setBackground(COLOR_BACKGROUND);
        searchButtonPanel.add(btnTimKiem);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(COLOR_BACKGROUND);
        topPanel.add(lblTitle, BorderLayout.WEST);
        topPanel.add(searchButtonPanel, BorderLayout.EAST);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        
        btnChon = createPrimaryButton("CHỌN");
        btnChon.setEnabled(false);
        
        btnHuy = createSecondaryButton("HỦY");
        
        panel.add(btnChon);
        panel.add(btnHuy);
        
        return panel;
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
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        table.setShowGrid(true);
        table.setGridColor(COLOR_BORDER);
        
        // Đặt kích thước cột
        table.getColumnModel().getColumn(0).setPreferredWidth(80);  // Mã KH
        table.getColumnModel().getColumn(1).setPreferredWidth(200); // Tên
        table.getColumnModel().getColumn(2).setPreferredWidth(120); // SĐT
    }
    
    private JButton createPrimaryButton(String text) {
        return createButton(text, COLOR_PRIMARY, Color.WHITE);
    }
    
    private JButton createSecondaryButton(String text) {
        return createButton(text, new Color(108, 117, 125), Color.WHITE);
    }
    
    private JButton createButton(String text, Color backgroundColor, Color textColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(textColor);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(backgroundColor.darker(), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
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
    
    private void setupUI() {
        // Thêm listener cho bảng để bật/tắt nút Chọn
        tblKhachHang.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = tblKhachHang.getSelectedRow() != -1;
            btnChon.setEnabled(hasSelection);
        });
    }
    
    // Getter methods
    public String getTen() {
        return txtTen.getText().trim();
    }
    
    public String getSoDienThoai() {
        return txtSoDienThoai.getText().trim();
    }
    
    public JTable getTblKhachHang() {
        return tblKhachHang;
    }
    
    public DefaultTableModel getTableModel() {
        return tableModel;
    }
    
    public JButton getBtnTimKiem() {
        return btnTimKiem;
    }
    
    public JButton getBtnChon() {
        return btnChon;
    }
    
    public JButton getBtnHuy() {
        return btnHuy;
    }
    
    public Integer getMaKhachHangDuocChon() {
        return maKhachHangDuocChon;
    }
    
    public String getTenKhachHangDuocChon() {
        return tenKhachHangDuocChon;
    }
    
    public String getSoDienThoaiDuocChon() {
        return soDienThoaiDuocChon;
    }
    
    // Phương thức để chọn khách hàng từ bảng
    public void chonKhachHangHienTai() {
        int selectedRow = tblKhachHang.getSelectedRow();
        if (selectedRow != -1) {
            maKhachHangDuocChon = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
            tenKhachHangDuocChon = tableModel.getValueAt(selectedRow, 1).toString();
            soDienThoaiDuocChon = tableModel.getValueAt(selectedRow, 2).toString();
        }
    }
    
    // Phương thức thêm dữ liệu vào bảng
    public void themKhachHangVaoBang(Object[] rowData) {
        tableModel.addRow(rowData);
    }
    
    // Phương thức xóa tất cả dữ liệu trong bảng
    public void xoaTatCaDuLieu() {
        tableModel.setRowCount(0);
    }
    
    // Phương thức kiểm tra có dữ liệu tìm kiếm không
    public boolean coDuLieuTimKiem() {
        return !getTen().isEmpty() || !getSoDienThoai().isEmpty();
    }
}