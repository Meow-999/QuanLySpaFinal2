package View;

import Model.DatLich;
import Model.DatLichChiTiet;
import Model.KhachHang;
import Model.DichVu;
import Model.Giuong;
import Model.NhanVien;
import Service.DatLichService;
import Service.KhachHangService;
import Service.DichVuService;
import Service.GiuongService;
import Service.NhanVienService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.DayOfWeek;
import java.time.YearMonth;
import java.util.List;
import java.util.*;
import View.KhachHangDialog;

public class QuanLyDatLichView extends JPanel {

    private DatLichService datLichService;
    private KhachHangService khachHangService;
    private DichVuService dichVuService;
    private GiuongService giuongService;
    private NhanVienService nhanVienService;
    // Components
    private JPanel calendarPanel;
    private JPanel timelinePanel;
    private JLabel lblThangNam;
    private JButton btnThangTruoc, btnThangSau;
    private JButton btnHomNay;
    private JButton btnThemKhachHang;
    private JButton btnHoanThanh;
    private LocalDate currentDate;
    private LocalDate selectedDate;
    private JComboBox<NhanVien> cbNhanVienDichVu;
    private JButton btnPhanCongNV;

    // Form components
    private JComboBox<KhachHang> cbKhachHang;
    private JComboBox<DichVu> cbDichVu;
    private JComboBox<Giuong> cbGiuong;
    private JTextField txtNgayDat;
    private JTextField txtGioDat;
    private JTextArea txtGhiChu;
    private JButton btnThem, btnSua, btnXoa, btnXacNhan, btnHuy, btnTimKhachHang;
    // Components for multiple services
    private JList<DichVu> listDichVu;
    private DefaultListModel<DichVu> listModelDichVu;
    private JButton btnThemDichVu, btnXoaDichVu;
    private Map<DichVu, NhanVien> phanCongNhanVien;

    // Thêm spinner cho số lượng người
    private JSpinner spinnerSoLuongNguoi;

    // Selected appointment
    private DatLich selectedAppointment;
    private Integer maGiuongCu; // THÊM FIELD NÀY
    private final Color COLOR_SELECTED_HIGHLIGHT = new Color(0x25, 0x7A, 0xB8); // Xanh dương đậm cho highlight
    private final Color COLOR_SELECTED_TEXT = Color.WHITE;
    private final Color COLOR_BACKGROUND = new Color(0x8C, 0xC9, 0x80);
    private final Color COLOR_PRIMARY = new Color(0x4D, 0x8A, 0x57);
    private final Color COLOR_SECONDARY = new Color(0x6c, 0x75, 0x7d);
    private final Color COLOR_TODAY = new Color(0x0d, 0x6e, 0xfd);
    private final Color COLOR_SELECTED = new Color(0x4D, 0x8A, 0x57);
    private final Color COLOR_EVENT = new Color(0xff, 0xe6, 0xe6);
    private final Color COLOR_LIST_BG = new Color(0x4D, 0x8A, 0x57);
    private final Color COLOR_LIST_FOREGROUND = Color.WHITE;

    public QuanLyDatLichView() {
        initServices();
        initUI();
        loadData();
        loadNhanVienChoDichVu();
        this.phanCongNhanVien = new HashMap<>();
        initListDichVuRenderer();
    }

    private void initServices() {
        this.datLichService = new DatLichService();
        this.khachHangService = new KhachHangService();
        this.dichVuService = new DichVuService();
        this.giuongService = new GiuongService();
        this.nhanVienService = new NhanVienService(); // THÊM SERVICE
    }

    private void initUI() {
        setLayout(new BorderLayout(5, 5));
        setBackground(COLOR_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        currentDate = LocalDate.now();
        selectedDate = LocalDate.now();

        // Header
        add(createHeaderPanel(), BorderLayout.NORTH);

        // Main content - Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400); // Giảm kích thước divider
        splitPane.setResizeWeight(0.4); // Cho phép điều chỉnh kích thước
        splitPane.setOneTouchExpandable(true); // Thêm nút mở rộng

        // Thiết lập kích thước tối thiểu
        JPanel leftPanel = createCalendarPanel();
        leftPanel.setMinimumSize(new Dimension(350, 350));
        JPanel rightPanel = createTimelinePanel();
        rightPanel.setMinimumSize(new Dimension(400, 350));

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);

        add(splitPane, BorderLayout.CENTER);

        // Form panel - đặt trong scroll pane để hỗ trợ màn hình nhỏ
        JScrollPane formScrollPane = new JScrollPane(createFormPanel());
        formScrollPane.setPreferredSize(new Dimension(800, 280));
        formScrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(formScrollPane, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_BACKGROUND);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel lblTitle = new JLabel("LỊCH HẸN THEO NGÀY");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18)); // Giảm kích thước font
        lblTitle.setForeground(Color.WHITE);

        // Navigation buttons
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        navPanel.setBackground(COLOR_BACKGROUND);

        btnHomNay = createStyledButton("Hôm nay", COLOR_PRIMARY);
        btnHomNay.setPreferredSize(new Dimension(80, 30)); // Kích thước cố định
        btnHomNay.addActionListener(e -> selectToday());

        navPanel.add(btnHomNay);

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(navPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createCalendarPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Lịch tháng"));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(350, 350)); // Giảm kích thước
        panel.setMinimumSize(new Dimension(320, 320));

        // Month navigation
        JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.setBackground(Color.WHITE);
        navPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        btnThangTruoc = createStyledButton("◀", COLOR_PRIMARY);
        btnThangTruoc.setPreferredSize(new Dimension(35, 25)); // Nhỏ hơn

        btnThangSau = createStyledButton("▶", COLOR_PRIMARY);
        btnThangSau.setPreferredSize(new Dimension(35, 25));

        btnThangTruoc.addActionListener(e -> previousMonth());
        btnThangSau.addActionListener(e -> nextMonth());

        lblThangNam = new JLabel("", JLabel.CENTER);
        lblThangNam.setFont(new Font("Arial", Font.BOLD, 14)); // Font nhỏ hơn

        navPanel.add(btnThangTruoc, BorderLayout.WEST);
        navPanel.add(lblThangNam, BorderLayout.CENTER);
        navPanel.add(btnThangSau, BorderLayout.EAST);

        // Calendar grid
        calendarPanel = new JPanel(new GridLayout(0, 7, 1, 1)); // Giảm khoảng cách
        calendarPanel.setBackground(Color.WHITE);

        panel.add(navPanel, BorderLayout.NORTH);
        panel.add(calendarPanel, BorderLayout.CENTER);

        updateCalendar();

        return panel;
    }

    private JPanel createTimelinePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Lịch hẹn ngày " + selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        panel.setBackground(COLOR_BACKGROUND);
        panel.setPreferredSize(new Dimension(450, 350));

        timelinePanel = new JPanel();
        timelinePanel.setLayout(new BoxLayout(timelinePanel, BoxLayout.Y_AXIS));
        timelinePanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(timelinePanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(430, 320));

        panel.add(scrollPane, BorderLayout.CENTER);

        updateTimeline();

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(COLOR_BACKGROUND);
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin đặt lịch"));
        formPanel.setVisible(true);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 3, 2, 3); // Giảm khoảng cách
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Row 0 - Khách hàng
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblKhachHang = new JLabel("Khách hàng *:");
        lblKhachHang.setForeground(Color.WHITE);
        lblKhachHang.setFont(new Font("Arial", Font.BOLD, 11)); // Font nhỏ hơn
        formPanel.add(lblKhachHang, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        JPanel khachHangPanel = new JPanel(new BorderLayout(3, 0));
        khachHangPanel.setBackground(COLOR_BACKGROUND);

        cbKhachHang = new JComboBox<>();
        cbKhachHang.setFont(new Font("Arial", Font.PLAIN, 11));
        cbKhachHang.setPreferredSize(new Dimension(150, 25)); // Nhỏ hơn
        khachHangPanel.add(cbKhachHang, BorderLayout.CENTER);

        // Panel chứa 2 nút
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 1, 0));
        buttonPanel.setBackground(COLOR_BACKGROUND);
        buttonPanel.setPreferredSize(new Dimension(60, 25)); // Nhỏ hơn

        btnThemKhachHang = createStyledButton("+", COLOR_PRIMARY);
        btnThemKhachHang.setToolTipText("Thêm khách hàng mới");
        btnThemKhachHang.setPreferredSize(new Dimension(25, 22));
        buttonPanel.add(btnThemKhachHang);

        btnTimKhachHang = createStyledButton("...", new Color(70, 130, 180));
        btnTimKhachHang.setToolTipText("Tìm kiếm khách hàng");
        btnTimKhachHang.setPreferredSize(new Dimension(25, 22));
        buttonPanel.add(btnTimKhachHang);

        khachHangPanel.add(buttonPanel, BorderLayout.EAST);
        formPanel.add(khachHangPanel, gbc);

        // Row 1 - Số lượng người
        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        JLabel lblSoLuongNguoi = new JLabel("Số người:");
        lblSoLuongNguoi.setForeground(Color.WHITE);
        lblSoLuongNguoi.setFont(new Font("Arial", Font.BOLD, 11));
        formPanel.add(lblSoLuongNguoi, gbc);

        gbc.gridx = 4;
        gbc.weightx = 0.3;
        spinnerSoLuongNguoi = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        spinnerSoLuongNguoi.setFont(new Font("Arial", Font.PLAIN, 11));
        spinnerSoLuongNguoi.setPreferredSize(new Dimension(50, 25));
        formPanel.add(spinnerSoLuongNguoi, gbc);

        // Row 2 - Dịch vụ
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        JLabel lblDichVu = new JLabel("Dịch vụ:");
        lblDichVu.setForeground(Color.WHITE);
        lblDichVu.setFont(new Font("Arial", Font.BOLD, 11));
        formPanel.add(lblDichVu, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        JPanel dichVuPanel = new JPanel(new BorderLayout(3, 0));
        cbDichVu = new JComboBox<>();
        cbDichVu.setFont(new Font("Arial", Font.PLAIN, 11));
        cbDichVu.setPreferredSize(new Dimension(150, 25));
        dichVuPanel.add(cbDichVu, BorderLayout.CENTER);

        btnThemDichVu = createStyledButton("+", COLOR_PRIMARY);
        btnThemDichVu.setPreferredSize(new Dimension(30, 22));
        dichVuPanel.add(btnThemDichVu, BorderLayout.EAST);

        formPanel.add(dichVuPanel, gbc);

        // Row 3 - Nhân viên thực hiện
        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        JLabel lblNhanVienDV = new JLabel("NV thực hiện:");
        lblNhanVienDV.setForeground(Color.WHITE);
        lblNhanVienDV.setFont(new Font("Arial", Font.BOLD, 11));
        formPanel.add(lblNhanVienDV, gbc);

        gbc.gridx = 4;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        JPanel nhanVienPanel = new JPanel(new BorderLayout(3, 0));
        cbNhanVienDichVu = new JComboBox<>();
        cbNhanVienDichVu.setFont(new Font("Arial", Font.PLAIN, 11));
        cbNhanVienDichVu.setPreferredSize(new Dimension(120, 25));
        nhanVienPanel.add(cbNhanVienDichVu, BorderLayout.CENTER);

        btnPhanCongNV = createStyledButton("PC", COLOR_PRIMARY);
        btnPhanCongNV.setPreferredSize(new Dimension(40, 22));
        btnPhanCongNV.setToolTipText("Phân công nhân viên");
        nhanVienPanel.add(btnPhanCongNV, BorderLayout.EAST);

        formPanel.add(nhanVienPanel, gbc);

        // Row 4 - Danh sách dịch vụ đã chọn
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        JLabel lblDSDichVu = new JLabel("DS dịch vụ:");
        lblDSDichVu.setForeground(Color.WHITE);
        lblDSDichVu.setFont(new Font("Arial", Font.BOLD, 11));
        formPanel.add(lblDSDichVu, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 4;
        gbc.weightx = 1.0;
        JPanel listPanel = new JPanel(new BorderLayout());
        listModelDichVu = new DefaultListModel<>();
        listDichVu = new JList<>(listModelDichVu);
        listDichVu.setFont(new Font("Arial", Font.PLAIN, 10)); // Font nhỏ hơn
        listDichVu.setBackground(COLOR_LIST_BG);
        listDichVu.setForeground(COLOR_LIST_FOREGROUND);
        listDichVu.setSelectionBackground(COLOR_LIST_BG.darker());
        listDichVu.setSelectionForeground(Color.WHITE);
        listDichVu.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

        JScrollPane scrollList = new JScrollPane(listDichVu);
        scrollList.setPreferredSize(new Dimension(200, 60)); // Nhỏ hơn
        scrollList.setBorder(BorderFactory.createLineBorder(COLOR_PRIMARY, 1));
        listPanel.add(scrollList, BorderLayout.CENTER);

        btnXoaDichVu = createStyledButton("Xóa", new Color(0xE7, 0x4C, 0x3C));
        btnXoaDichVu.setPreferredSize(new Dimension(50, 22));
        listPanel.add(btnXoaDichVu, BorderLayout.EAST);

        formPanel.add(listPanel, gbc);

        // Row 5 - Giường
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        JLabel lblGiuong = new JLabel("Giường:");
        lblGiuong.setForeground(Color.WHITE);
        lblGiuong.setFont(new Font("Arial", Font.BOLD, 11));
        formPanel.add(lblGiuong, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 0.5;
        cbGiuong = new JComboBox<>();
        cbGiuong.setFont(new Font("Arial", Font.PLAIN, 11));
        cbGiuong.setPreferredSize(new Dimension(120, 25));
        formPanel.add(cbGiuong, gbc);

        // Row 6 - Ngày đặt
        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        JLabel lblNgayDat = new JLabel("Ngày đặt *:");
        lblNgayDat.setForeground(Color.WHITE);
        lblNgayDat.setFont(new Font("Arial", Font.BOLD, 11));
        formPanel.add(lblNgayDat, gbc);

        gbc.gridx = 4;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        txtNgayDat = new JTextField();
        txtNgayDat.setFont(new Font("Arial", Font.PLAIN, 11));
        txtNgayDat.setPreferredSize(new Dimension(80, 25));
        formPanel.add(txtNgayDat, gbc);

        // Row 7 - Giờ đặt
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        JLabel lblGioDat = new JLabel("Giờ đặt *:");
        lblGioDat.setForeground(Color.WHITE);
        lblGioDat.setFont(new Font("Arial", Font.BOLD, 11));
        formPanel.add(lblGioDat, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 0.3;
        txtGioDat = new JTextField();
        txtGioDat.setFont(new Font("Arial", Font.PLAIN, 11));
        txtGioDat.setPreferredSize(new Dimension(80, 25));
        formPanel.add(txtGioDat, gbc);

// Row 8 - Ghi chú
        gbc.gridx = 0;  // THAY ĐỔI: Chuyển từ 3 về 0
        gbc.gridy = 5;  // THAY ĐỔI: Tăng gridy lên 5 (trước đó là 4)
        gbc.gridwidth = 1;  // THAY ĐỔI: Giảm gridwidth từ 2 xuống 1
        gbc.weightx = 0;
        JLabel lblGhiChu = new JLabel("Ghi chú:");
        lblGhiChu.setForeground(Color.WHITE);
        lblGhiChu.setFont(new Font("Arial", Font.BOLD, 11));
        formPanel.add(lblGhiChu, gbc);

        gbc.gridx = 1;  // THAY ĐỔI: Bắt đầu từ cột 1 thay vì 0
        gbc.gridy = 5;  // GIỮ NGUYÊN: Vẫn ở hàng 5
        gbc.gridwidth = 4;  // THAY ĐỔI: Mở rộng 4 cột thay vì 5
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        txtGhiChu = new JTextArea(2, 20);
        txtGhiChu.setFont(new Font("Arial", Font.PLAIN, 11));
        txtGhiChu.setLineWrap(true);
        JScrollPane scrollGhiChu = new JScrollPane(txtGhiChu);
        scrollGhiChu.setPreferredSize(new Dimension(400, 50));
        formPanel.add(scrollGhiChu, gbc);

        // Button row
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 5;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel formButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5)); // Giảm khoảng cách
        formButtonPanel.setBackground(COLOR_BACKGROUND);

        // Tạo các nút với kích thước nhỏ hơn
        Dimension smallButtonSize = new Dimension(75, 28);

        btnThem = createStyledButton("Thêm", COLOR_PRIMARY);
        btnThem.setPreferredSize(smallButtonSize);

        btnSua = createStyledButton("Sửa", COLOR_PRIMARY);
        btnSua.setPreferredSize(smallButtonSize);

        btnXoa = createStyledButton("Xóa", new Color(0xE7, 0x4C, 0x3C));
        btnXoa.setPreferredSize(smallButtonSize);

        btnXacNhan = createStyledButton("Xác nhận", new Color(0x2E, 0xCC, 0x71));
        btnXacNhan.setPreferredSize(new Dimension(85, 28));

        btnHuy = createStyledButton("Hủy lịch", new Color(0xE6, 0x7E, 0x22));
        btnHuy.setPreferredSize(new Dimension(80, 28));

        btnHoanThanh = createStyledButton("Hoàn thành", new Color(0x34, 0x98, 0xDB));
        btnHoanThanh.setPreferredSize(new Dimension(90, 28));

        formButtonPanel.add(btnThem);
        formButtonPanel.add(btnSua);
        formButtonPanel.add(btnXoa);
        formButtonPanel.add(btnXacNhan);
        formButtonPanel.add(btnHuy);
        formButtonPanel.add(btnHoanThanh);
        formPanel.add(formButtonPanel, gbc);

        return formPanel;
    }

    // THÊM PHƯƠNG THỨC LOAD NHÂN VIÊN
    public void loadNhanVienChoDichVu() {
        try {
            List<NhanVien> dsNhanVien = nhanVienService.getAllNhanVien();

            cbNhanVienDichVu.removeAllItems();
            cbNhanVienDichVu.addItem(new NhanVien()); // Item trống

            for (NhanVien nv : dsNhanVien) {
                cbNhanVienDichVu.addItem(nv);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách nhân viên: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // THÊM PHƯƠNG THỨC CẬP NHẬT HIỂN THỊ PHÂN CÔNG TRONG DANH SÁCH DỊCH VỤ
    public void capNhatHienThiPhanCong() {
        listDichVu.repaint(); // Vẽ lại để cập nhật hiển thị
    }

    // THÊM RENDERER TÙY CHỈNH CHO LIST DỊCH VỤ
    private void initListDichVuRenderer() {
        listDichVu.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value instanceof DichVu) {
                    DichVu dichVu = (DichVu) value;
                    StringBuilder displayText = new StringBuilder();
                    displayText.append("<html>");
                    displayText.append(dichVu.getTenDichVu());

                    // Thêm thông tin nhân viên phân công nếu có
                    NhanVien nhanVienPhanCong = phanCongNhanVien.get(dichVu);
                    if (nhanVienPhanCong != null && nhanVienPhanCong.getMaNhanVien() != null) {
                        displayText.append(" <br><font size='1' color='yellow'><b>NV: ")
                                .append(nhanVienPhanCong.getHoTen())
                                .append("</b></font>");
                    } else {
                        displayText.append(" <br><font size='1' color='gray'><i>Chưa PC NV</i></font>");
                    }

                    displayText.append("</html>");
                    label.setText(displayText.toString());
                }

                return label;
            }
        });
    }

    // THÊM PHƯƠNG THỨC ĐỂ LẤY VÀ THIẾT LẬP PHÂN CÔNG NHÂN VIÊN
    public Map<DichVu, NhanVien> getPhanCongNhanVien() {
        return phanCongNhanVien;
    }

    public void setPhanCongNhanVien(Map<DichVu, NhanVien> phanCongNhanVien) {
        this.phanCongNhanVien = phanCongNhanVien;
    }

    public void themPhanCongNhanVien(DichVu dichVu, NhanVien nhanVien) {
        this.phanCongNhanVien.put(dichVu, nhanVien);
        capNhatHienThiPhanCong(); // CẬP NHẬT HIỂN THỊ NGAY LẬP TỨC
    }

    public void xoaPhanCongNhanVien(DichVu dichVu) {
        this.phanCongNhanVien.remove(dichVu);
    }

    public void clearPhanCongNhanVien() {
        this.phanCongNhanVien.clear();
    }

    private void updateCalendar() {
        calendarPanel.removeAll();

        YearMonth yearMonth = YearMonth.from(currentDate);
        LocalDate firstOfMonth = yearMonth.atDay(1);
        LocalDate lastOfMonth = yearMonth.atEndOfMonth();

        // Update month year label
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
        lblThangNam.setText(yearMonth.format(formatter));

        // Day headers
        String[] dayNames = {"CN", "T2", "T3", "T4", "T5", "T6", "T7"};
        for (String dayName : dayNames) {
            JLabel lblDay = new JLabel(dayName, JLabel.CENTER);
            lblDay.setFont(new Font("Arial", Font.BOLD, 10)); // Font nhỏ hơn
            lblDay.setForeground(COLOR_SECONDARY);
            lblDay.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
            calendarPanel.add(lblDay);
        }

        // Fill in days before first of month
        DayOfWeek firstDayOfWeek = firstOfMonth.getDayOfWeek();
        for (int i = 0; i < firstDayOfWeek.getValue() % 7; i++) {
            calendarPanel.add(new JLabel(""));
        }

        // Add days of month
        for (int day = 1; day <= lastOfMonth.getDayOfMonth(); day++) {
            final LocalDate date = yearMonth.atDay(day);
            JButton btnDay = createDayButton(date);
            calendarPanel.add(btnDay);
        }

        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    private JButton createDayButton(LocalDate date) {
        JButton btn = new JButton(String.valueOf(date.getDayOfMonth()));
        btn.setFont(new Font("Arial", Font.PLAIN, 10)); // Font nhỏ hơn
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(30, 25)); // Kích thước nhỏ hơn

        // Style based on date
        if (date.equals(LocalDate.now())) {
            btn.setBackground(COLOR_TODAY);
            btn.setForeground(Color.WHITE);
        } else if (date.equals(selectedDate)) {
            btn.setBackground(COLOR_SELECTED);
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(Color.WHITE);
            btn.setForeground(Color.BLACK);
        }

        // Add event indicator if there are appointments
        try {
            List<DatLich> appointments = datLichService.getDatLichTheoNgay(date);
            if (!appointments.isEmpty()) {
                btn.setBorder(BorderFactory.createLineBorder(COLOR_PRIMARY, 1)); // Viền mỏng hơn
            }
        } catch (Exception e) {
            // Ignore errors for event indicators
        }

        btn.addActionListener(e -> selectDate(date));

        return btn;
    }

    // Phương thức để làm nổi bật lịch được chọn và cập nhật timeline
    public void highlightSelectedAppointment(DatLich appointment) {
        this.selectedAppointment = appointment;
        updateTimeline(); // Cập nhật để áp dụng highlight

        // Cập nhật form với thông tin lịch được chọn (nếu cần)
        if (appointment != null) {
            System.out.println("Đã chọn và highlight lịch: " + appointment.getMaLich() + " - "
                    + getTenKhachHang(appointment.getMaKhachHang()));
        }
    }

    public void setSelectedAppointment(DatLich appointment) {
        this.selectedAppointment = appointment;
        // Tự động cập nhật timeline để áp dụng highlight
        if (appointment != null) {
            updateTimeline();
        }
    }

    public void updateTimeline() {
        timelinePanel.removeAll();

        try {
            List<DatLich> appointments = datLichService.getDatLichTheoNgay(selectedDate);

            if (appointments.isEmpty()) {
                JLabel lblEmpty = new JLabel("Không có lịch hẹn nào cho ngày này", JLabel.CENTER);
                lblEmpty.setFont(new Font("Arial", Font.ITALIC, 12)); // Font nhỏ hơn
                lblEmpty.setForeground(COLOR_SECONDARY);
                timelinePanel.add(lblEmpty);
            } else {
                // Sort appointments by time
                appointments.sort((a, b) -> a.getGioDat().compareTo(b.getGioDat()));

                for (DatLich appointment : appointments) {
                    JPanel appointmentPanel = createAppointmentPanel(appointment);

                    // Làm nổi bật lịch đang được chọn
                    if (selectedAppointment != null
                            && selectedAppointment.getMaLich().equals(appointment.getMaLich())) {
                        appointmentPanel.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(Color.YELLOW, 2), // Viền mỏng hơn
                                BorderFactory.createEmptyBorder(5, 5, 5, 5) // Padding ít hơn
                        ));
                    }

                    timelinePanel.add(appointmentPanel);
                    timelinePanel.add(Box.createRigidArea(new Dimension(0, 3))); // Khoảng cách nhỏ hơn
                }
            }
        } catch (Exception e) {
            JLabel lblError = new JLabel("Lỗi khi tải lịch hẹn: " + e.getMessage(), JLabel.CENTER);
            lblError.setForeground(Color.RED);
            lblError.setFont(new Font("Arial", Font.PLAIN, 11));
            timelinePanel.add(lblError);
        }

        timelinePanel.revalidate();
        timelinePanel.repaint();

        // Update panel title
        Container parent = timelinePanel.getParent();
        while (parent != null && !(parent instanceof JPanel)) {
            parent = parent.getParent();
        }
        if (parent instanceof JPanel) {
            ((JPanel) parent).setBorder(BorderFactory.createTitledBorder(
                    "Lịch hẹn ngày " + selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            ));
        }
    }

    private void showAppointmentDetails(DatLich appointment) {
        // Tự động highlight lịch được click để xem chi tiết
        highlightSelectedAppointment(appointment);

        StringBuilder details = new StringBuilder();
        details.append("Chi tiết lịch hẹn:\n");
        details.append("Mã lịch: ").append(appointment.getMaLich()).append("\n");
        details.append("Khách hàng: ").append(getTenKhachHang(appointment.getMaKhachHang())).append("\n");
        KhachHang kh = khachHangService.getKhachHangById(appointment.getMaKhachHang());
        if (kh != null) {
            details.append("Điểm tích lũy: ").append(kh.getDiemTichLuy()).append(" điểm\n");
        }
        details.append("Số lượng người: ").append(appointment.getSoLuongNguoi()).append("\n");
        details.append("Thời gian: ").append(appointment.getGioDat().format(DateTimeFormatter.ofPattern("HH:mm"))).append("\n");
        details.append("Trạng thái: ").append(appointment.getTrangThai()).append("\n");

        if (appointment.getMaGiuong() != null) {
            details.append("Giường: ").append(getTenGiuong(appointment.getMaGiuong())).append("\n");
        }

        // THÊM THÔNG TIN NHÂN VIÊN THỰC HIỆN DỊCH VỤ
        if (appointment.hasDichVu()) {
            details.append("\nDịch vụ và nhân viên thực hiện:\n");
            for (int i = 0; i < appointment.getDanhSachDichVu().size(); i++) {
                DatLichChiTiet chiTiet = appointment.getDanhSachDichVu().get(i);
                details.append("• ").append(chiTiet.getDichVu().getTenDichVu());
                if (chiTiet.getNhanVien() != null) {
                    details.append(" - NV: ").append(chiTiet.getNhanVien().getHoTen());
                } else {
                    details.append(" - Chưa phân công NV");
                }
                details.append("\n");
            }
        }

        details.append("\nGhi chú: ").append(appointment.getGhiChu() != null ? appointment.getGhiChu() : "Không có");

        JOptionPane.showMessageDialog(this, details.toString(),
                "Chi tiết lịch hẹn #" + appointment.getMaLich(),
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private JPanel createAppointmentPanel(DatLich appointment) {
        JPanel panel = new JPanel(new BorderLayout(5, 3)); // Giảm khoảng cách
        panel.setPreferredSize(new Dimension(400, 60)); // Kích thước nhỏ hơn

        // KIỂM TRA XEM LỊCH NÀY CÓ ĐANG ĐƯỢC CHỌN KHÔNG
        boolean isSelected = selectedAppointment != null
                && selectedAppointment.getMaLich().equals(appointment.getMaLich());

        // SET MÀU NỀN DỰA TRÊN TRẠNG THÁI CHỌN
        if (isSelected) {
            panel.setBackground(COLOR_SELECTED_HIGHLIGHT);
        } else {
            panel.setBackground(new Color(0x8C, 0xC9, 0x80));
        }

        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(isSelected ? COLOR_SELECTED_HIGHLIGHT.darker() : COLOR_PRIMARY, 1), // Viền mỏng hơn
                BorderFactory.createEmptyBorder(5, 5, 5, 5) // Padding ít hơn
        ));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Time and status panel - LEFT
        JPanel leftPanel = new JPanel(new GridLayout(2, 1, 0, 2)); // Giảm khoảng cách
        leftPanel.setBackground(panel.getBackground());
        leftPanel.setPreferredSize(new Dimension(50, 40)); // Kích thước cố định

        JLabel lblTime = new JLabel(appointment.getGioDat().format(DateTimeFormatter.ofPattern("HH:mm")));
        lblTime.setFont(new Font("Arial", Font.BOLD, 12)); // Font nhỏ hơn
        lblTime.setForeground(isSelected ? COLOR_SELECTED_TEXT : Color.WHITE);
        lblTime.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblStatus = new JLabel(appointment.getTrangThai());
        lblStatus.setFont(new Font("Arial", Font.BOLD, 9)); // Font nhỏ hơn
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);

        // Color code status với màu sắc đẹp hơn
        if (isSelected) {
            lblStatus.setForeground(Color.YELLOW); // Màu vàng nổi bật khi được chọn
        } else {
            switch (appointment.getTrangThai()) {
                case "Đã xác nhận":
                    lblStatus.setForeground(new Color(0x2E, 0xCC, 0x71)); // Xanh lá sáng
                    break;
                case "Đã hủy":
                    lblStatus.setForeground(new Color(0xE7, 0x4C, 0x3C)); // Đỏ
                    break;
                case "Hoàn thành":
                    lblStatus.setForeground(new Color(0x34, 0x98, 0xDB)); // Xanh dương
                    break;
                case "Đang thực hiện":
                    lblStatus.setForeground(new Color(0xF3, 0x9C, 0x12)); // Vàng cam
                    break;
                default: // Chờ xác nhận
                    lblStatus.setForeground(new Color(0xF1, 0xC4, 0x0F)); // Vàng
            }
        }

        leftPanel.add(lblTime);
        leftPanel.add(lblStatus);

        // Customer and service info - CENTER
        JPanel centerPanel = new JPanel(new BorderLayout(0, 3)); // Giảm khoảng cách
        centerPanel.setBackground(panel.getBackground());

        String customerName = getTenKhachHang(appointment.getMaKhachHang());
        JLabel lblCustomer = new JLabel(customerName);
        lblCustomer.setFont(new Font("Arial", Font.BOLD, 11)); // Font nhỏ hơn
        lblCustomer.setForeground(isSelected ? COLOR_SELECTED_TEXT : Color.WHITE);

        // Service names với hiển thị thông tin nhân viên
        StringBuilder services = new StringBuilder();
        if (appointment.hasDichVu()) {
            for (int i = 0; i < Math.min(appointment.getDanhSachDichVu().size(), 2); i++) {
                if (i > 0) {
                    services.append(" • ");
                }
                DatLichChiTiet chiTiet = appointment.getDanhSachDichVu().get(i);
                services.append(chiTiet.getDichVu().getTenDichVu());
                if (chiTiet.getNhanVien() != null) {
                    services.append(" (NV: ").append(chiTiet.getNhanVien().getHoTen()).append(")");
                }
            }
            if (appointment.getDanhSachDichVu().size() > 2) {
                services.append(" • ...");
            }
        } else {
            services.append("Không có dịch vụ");
        }

        JLabel lblServices = new JLabel("<html><div style='width: 180px; font-size: 9px;'>" + services.toString() + "</div></html>"); // Width nhỏ hơn
        lblServices.setForeground(isSelected ? new Color(0xEC, 0xF0, 0xF1) : new Color(0xEC, 0xF0, 0xF1));

        centerPanel.add(lblCustomer, BorderLayout.NORTH);
        centerPanel.add(lblServices, BorderLayout.CENTER);

        // Bed info - RIGHT (nếu có)
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(panel.getBackground());
        rightPanel.setPreferredSize(new Dimension(80, 40)); // Kích thước cố định

        if (appointment.getMaGiuong() != null) {
            String tenGiuong = getTenGiuong(appointment.getMaGiuong());
            JLabel lblGiuong = new JLabel("Giường: " + tenGiuong);
            lblGiuong.setFont(new Font("Arial", Font.ITALIC, 9)); // Font nhỏ hơn
            lblGiuong.setForeground(isSelected ? new Color(0xEC, 0xF0, 0xF1) : new Color(0xEC, 0xF0, 0xF1));
            rightPanel.add(lblGiuong, BorderLayout.NORTH);
        }

        // Thêm hiển thị số lượng người vào panel bên phải
        JLabel lblSoLuongNguoi = new JLabel("Số người: " + appointment.getSoLuongNguoi());
        lblSoLuongNguoi.setFont(new Font("Arial", Font.ITALIC, 9)); // Font nhỏ hơn
        lblSoLuongNguoi.setForeground(isSelected ? new Color(0xEC, 0xF0, 0xF1) : new Color(0xEC, 0xF0, 0xF1));
        rightPanel.add(lblSoLuongNguoi, BorderLayout.CENTER);

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(rightPanel, BorderLayout.EAST);

        // Add hover effect - CHỈ ÁP DỤNG KHI KHÔNG ĐƯỢC CHỌN
        if (!isSelected) {
            panel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    panel.setBackground(COLOR_PRIMARY);
                    leftPanel.setBackground(COLOR_PRIMARY);
                    centerPanel.setBackground(COLOR_PRIMARY);
                    rightPanel.setBackground(COLOR_PRIMARY);
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    panel.setBackground(new Color(0x8C, 0xC9, 0x80));
                    leftPanel.setBackground(new Color(0x8C, 0xC9, 0x80));
                    centerPanel.setBackground(new Color(0x8C, 0xC9, 0x80));
                    rightPanel.setBackground(new Color(0x8C, 0xC9, 0x80));
                }

                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    setSelectedAppointment(appointment);
                    highlightSelectedAppointment(appointment);
                    showAppointmentDetails(appointment);
                }
            });
        } else {
            // Khi đã được chọn, vẫn cho phép click để xem chi tiết
            panel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    showAppointmentDetails(appointment);
                }
            });
        }

        return panel;
    }

    private void selectDate(LocalDate date) {
        selectedDate = date;
        updateCalendar();
        updateTimeline();

        // Auto-fill date in form
        txtNgayDat.setText(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }

    private void selectToday() {
        selectDate(LocalDate.now());
    }

    private void previousMonth() {
        currentDate = currentDate.minusMonths(1);
        updateCalendar();
    }

    private void nextMonth() {
        currentDate = currentDate.plusMonths(1);
        updateCalendar();
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 10)); // Font nhỏ hơn
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8)); // Giảm padding
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

    private void loadData() {
        loadComboboxData();
        updateTimeline();

        // Auto-fill today's date
        txtNgayDat.setText(selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }

    private void loadComboboxData() {
        try {
            // Load khách hàng
            cbKhachHang.removeAllItems();
            List<KhachHang> khachHangs = khachHangService.getAllKhachHang();
            for (KhachHang kh : khachHangs) {
                cbKhachHang.addItem(kh);
            }

            // Load dịch vụ
            cbDichVu.removeAllItems();
            cbDichVu.addItem(new DichVu()); // Item trống
            List<DichVu> dichVus = dichVuService.getAllDichVu();
            for (DichVu dv : dichVus) {
                cbDichVu.addItem(dv);
            }

            // Load giường - CHỈ HIỂN THỊ GIƯỜNG CÓ THỂ ĐẶT
            cbGiuong.removeAllItems();
            cbGiuong.addItem(new Giuong()); // Item trống

            // Sử dụng phương thức mới để lấy giường khả dụng
            List<Giuong> availableGiuongs = giuongService.getGiuongAvailableForBooking();
            for (Giuong g : availableGiuongs) {
                cbGiuong.addItem(g);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu combobox: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refreshGiuongComboBox() {
        loadComboboxData(); // Tải lại toàn bộ dữ liệu combobox
    }

    private String getTenKhachHang(Integer maKhachHang) {
        if (maKhachHang == null) {
            return "Không xác định";
        }
        try {
            KhachHang kh = khachHangService.getKhachHangById(maKhachHang);
            return kh != null ? kh.getHoTen() : "Không xác định";
        } catch (Exception e) {
            return "Lỗi";
        }
    }

    private String getTenGiuong(Integer maGiuong) {
        if (maGiuong == null) {
            return "Không xác định";
        }
        try {
            Giuong g = giuongService.getGiuongById(maGiuong);
            return g != null ? g.getSoHieu() : "Không xác định";
        } catch (Exception e) {
            return "Lỗi";
        }
    }

    // Getter methods for controller (giữ nguyên)
    public JComboBox<KhachHang> getCbKhachHang() {
        return cbKhachHang;
    }

    public JComboBox<DichVu> getCbDichVu() {
        return cbDichVu;
    }

    public Integer getMaGiuongCu() {
        return maGiuongCu;
    }

    public JButton getBtnThemKhachHang() {
        return btnThemKhachHang;
    }

    public void setMaGiuongCu(Integer maGiuongCu) {
        this.maGiuongCu = maGiuongCu;
    }

    public JComboBox<Giuong> getCbGiuong() {
        return cbGiuong;
    }

    public JComboBox<NhanVien> getCbNhanVienDichVu() {
        return cbNhanVienDichVu;
    }

    public JButton getBtnPhanCongNV() {
        return btnPhanCongNV;
    }

    public JTextField getTxtNgayDat() {
        return txtNgayDat;
    }

    public JTextField getTxtGioDat() {
        return txtGioDat;
    }

    public JTextArea getTxtGhiChu() {
        return txtGhiChu;
    }

    public JButton getBtnThem() {
        return btnThem;
    }

    public JButton getBtnSua() {
        return btnSua;
    }

    public JButton getBtnXoa() {
        return btnXoa;
    }

    public JButton getBtnXacNhan() {
        return btnXacNhan;
    }

    public JButton getBtnHuy() {
        return btnHuy;
    }

    public JButton getBtnThemDichVu() {
        return btnThemDichVu;
    }

    public JButton getBtnXoaDichVu() {
        return btnXoaDichVu;
    }

    public JButton getBtnHoanThanh() {
        return btnHoanThanh;
    }

    public JList<DichVu> getListDichVu() {
        return listDichVu;
    }

    public DefaultListModel<DichVu> getListModelDichVu() {
        return listModelDichVu;
    }

    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    public DatLich getSelectedAppointment() {
        return selectedAppointment;
    }

    public JSpinner getSpinnerSoLuongNguoi() {
        return spinnerSoLuongNguoi;
    }

    public JButton getBtnTimKhachHang() {
        return btnTimKhachHang;
    }
}
