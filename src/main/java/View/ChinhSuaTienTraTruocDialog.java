// ChinhSuaTienTraTruocDialog.java
package View;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class ChinhSuaTienTraTruocDialog extends JDialog {

    private boolean confirmed = false;
    private JTextField txtSoDuMoi;
    private JLabel lblSoDuCu;
    private JLabel lblKhachHang;

    private NumberFormat currencyFormat;
    private DecimalFormat decimalFormat;
    private BigDecimal soDuHienTai;
    private String tenKhachHang;

    public ChinhSuaTienTraTruocDialog(Frame parent, String tenKhachHang, BigDecimal soDuHienTai) {
        super(parent, "Chỉnh sửa số dư trả trước", true);
        this.tenKhachHang = tenKhachHang;
        this.soDuHienTai = soDuHienTai;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        this.decimalFormat = new DecimalFormat("#,##0"); // Định dạng không có số thập phân

        initComponents();
        setupUI();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setPreferredSize(new Dimension(400, 400));

        // Panel chính
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Panel thông tin
        JPanel infoPanel = createInfoPanel();
        mainPanel.add(infoPanel, BorderLayout.NORTH);

        // Panel chỉnh sửa
        JPanel editPanel = createEditPanel();
        mainPanel.add(editPanel, BorderLayout.CENTER);

        // Panel nút
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Thông tin khách hàng"));

        lblKhachHang = new JLabel("Khách hàng: " + tenKhachHang);
        lblKhachHang.setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Sử dụng decimalFormat để bỏ phần .000
        lblSoDuCu = new JLabel("Số dư hiện tại: " + decimalFormat.format(soDuHienTai) + " đ");
        lblSoDuCu.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSoDuCu.setForeground(new Color(74, 138, 87));

        panel.add(lblKhachHang);
        panel.add(lblSoDuCu);

        return panel;
    }

    private JPanel createEditPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Chỉnh sửa số dư"));

        JLabel lblSoDuMoiTitle = new JLabel("Số dư mới:");
        lblSoDuMoiTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));

        txtSoDuMoi = new JTextField();
        // Hiển thị số dư hiện tại không có .000
        txtSoDuMoi.setText(decimalFormat.format(soDuHienTai));
        txtSoDuMoi.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSoDuMoi.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        panel.add(lblSoDuMoiTitle);
        panel.add(txtSoDuMoi);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        JButton btnHuy = new JButton("Hủy");
        btnHuy.addActionListener(e -> {
            confirmed = false;
            dispose();
        });

        JButton btnXacNhan = new JButton("Xác nhận");
        btnXacNhan.setBackground(new Color(74, 138, 87));
        btnXacNhan.setForeground(Color.WHITE);
        btnXacNhan.addActionListener(e -> {
            if (validateInput()) {
                confirmed = true;
                dispose();
            }
        });

        panel.add(btnHuy);
        panel.add(btnXacNhan);

        return panel;
    }

    private boolean validateInput() {
        try {
            String soTienStr = txtSoDuMoi.getText().trim();
            if (soTienStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập số dư mới", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Loại bỏ dấu phân cách hàng nghìn trước khi chuyển đổi
            soTienStr = soTienStr.replaceAll("\\.", "").replaceAll(",", "");
            BigDecimal soDuMoi = new BigDecimal(soTienStr);

            if (soDuMoi.compareTo(BigDecimal.ZERO) < 0) {
                JOptionPane.showMessageDialog(this, "Số dư không được âm", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            return true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số tiền không hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void setupUI() {
        pack();
        setLocationRelativeTo(getParent());
        setResizable(false);
    }

    // Getter methods
    public boolean isConfirmed() {
        return confirmed;
    }

    public BigDecimal getSoDuMoi() {
        try {
            String soTienStr = txtSoDuMoi.getText().trim();
            // Loại bỏ dấu phân cách hàng nghìn trước khi chuyển đổi
            soTienStr = soTienStr.replaceAll("\\.", "").replaceAll(",", "");
            return new BigDecimal(soTienStr);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    public String getLyDoChinhSua() {
        return "Từ " + decimalFormat.format(soDuHienTai) + " đ thành " + "\t\n" + decimalFormat.format(getSoDuMoi()) + " đ";
    }
}
