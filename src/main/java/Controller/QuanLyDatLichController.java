package Controller;

import Data.DataConnection;
import Model.ChiTietHoaDon;
import View.QuanLyDatLichView;
import Service.DatLichService;
import Service.KhachHangService;
import Service.DichVuService;
import Service.GiuongService;
import Model.DatLich;
import Model.DatLichChiTiet;
import Model.KhachHang;
import Model.DichVu;
import Model.Giuong;
import Model.HoaDon;
import Model.NhanVien;
import Service.HoaDonService;
import Service.NhanVienService;
import Service.TienTraTruocService;
import ShareInfo.Auth;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;
import View.KhachHangDialog;
import View.TimKhachHangView;
import java.awt.Frame;
import java.awt.Window;

public class QuanLyDatLichController implements ActionListener {

    private NhanVienService nhanVienService;
    private QuanLyDatLichView view;
    private DatLichService datLichService;
    private KhachHangService khachHangService;
    private DichVuService dichVuService;
    private GiuongService giuongService;
    private TienTraTruocService tienTraTruocService;
    private HoaDonService hoaDonService;

    private boolean isEditMode = false;
    private int currentEditId = -1;

    public QuanLyDatLichController(QuanLyDatLichView view) {
        this.view = view;
        this.datLichService = new DatLichService();
        this.khachHangService = new KhachHangService();
        this.dichVuService = new DichVuService();
        this.giuongService = new GiuongService();
        this.nhanVienService = new NhanVienService();
        this.tienTraTruocService = new TienTraTruocService();
        this.hoaDonService = new HoaDonService();
        setupEventListeners();
    }

    private void setupEventListeners() {
        view.getBtnThem().addActionListener(this);
        view.getBtnSua().addActionListener(this);
        view.getBtnXoa().addActionListener(this);
        view.getBtnXacNhan().addActionListener(this);
        view.getBtnHuy().addActionListener(this);
        view.getBtnThemDichVu().addActionListener(this);
        view.getBtnXoaDichVu().addActionListener(this);
        view.getBtnHoanThanh().addActionListener(this);
        view.getBtnPhanCongNV().addActionListener(this);
        view.getBtnThemKhachHang().addActionListener(this);
        view.getBtnTimKhachHang().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == view.getBtnThem()) {
            handleThemMoi();
        } else if (source == view.getBtnSua()) {
            handleSua();
        } else if (source == view.getBtnXoa()) {
            handleXoa();
        } else if (source == view.getBtnXacNhan()) {
            handleXacNhan();
        } else if (source == view.getBtnHuy()) {
            handleHuyLich();
        } else if (source == view.getBtnThemDichVu()) {
            handleThemDichVu();
        } else if (source == view.getBtnXoaDichVu()) {
            handleXoaDichVu();
        } else if (source == view.getBtnHoanThanh()) {
            handleHoanThanh();
        } else if (source == view.getBtnPhanCongNV()) {
            handlePhanCongNhanVien();
        } else if (source == view.getBtnThemKhachHang()) {
            handleThemKhachHang();
        } else if (source == view.getBtnTimKhachHang()) {
            handleTimKiemKhachHang();
        }
    }

    private void handleTimKiemKhachHang() {
        try {
            Window parentWindow = SwingUtilities.getWindowAncestor(view);
            TimKhachHangView timKhachHangView = new TimKhachHangView((Frame) parentWindow);
            TimKhachHangController timKhachHangController = new TimKhachHangController(timKhachHangView, this);
            timKhachHangView.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view,
                    "Lỗi khi mở cửa sổ tìm kiếm khách hàng: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void capNhatKhachHangDuocChon(Integer maKhachHang, String tenKhachHang, String soDienThoai) {
        try {
            // Tìm khách hàng trong combobox
            for (int i = 0; i < view.getCbKhachHang().getItemCount(); i++) {
                KhachHang kh = view.getCbKhachHang().getItemAt(i);
                if (kh.getMaKhachHang() != null && kh.getMaKhachHang().equals(maKhachHang)) {
                    view.getCbKhachHang().setSelectedIndex(i);

                    JOptionPane.showMessageDialog(view,
                            "Đã chọn khách hàng: " + tenKhachHang,
                            "Thành công",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            }

            // Nếu không tìm thấy, tải lại danh sách và chọn
            loadKhachHangComboBox();

            for (int i = 0; i < view.getCbKhachHang().getItemCount(); i++) {
                KhachHang kh = view.getCbKhachHang().getItemAt(i);
                if (kh.getMaKhachHang() != null && kh.getMaKhachHang().equals(maKhachHang)) {
                    view.getCbKhachHang().setSelectedIndex(i);
                    break;
                }
            }

            JOptionPane.showMessageDialog(view,
                    "Đã chọn khách hàng: " + tenKhachHang,
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view,
                    "Lỗi khi cập nhật khách hàng: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handlePhanCongNhanVien() {
        int selectedIndex = view.getListDichVu().getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn dịch vụ trong danh sách để phân công nhân viên",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        NhanVien selectedNhanVien = (NhanVien) view.getCbNhanVienDichVu().getSelectedItem();
        if (selectedNhanVien == null || selectedNhanVien.getMaNhanVien() == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn nhân viên",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DichVu selectedDichVu = view.getListModelDichVu().getElementAt(selectedIndex);
        view.themPhanCongNhanVien(selectedDichVu, selectedNhanVien);
    }

    private void handleThemKhachHang() {    
        Window parentWindow = SwingUtilities.getWindowAncestor(view);
        KhachHangDialog dialog = new KhachHangDialog((Frame) parentWindow);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            if (validateKhachHangForm(dialog.getHoTen(), dialog.getSoDienThoai())) {
                try {
                    KhachHang khachHangMoi = new KhachHang();
                    khachHangMoi.setHoTen(dialog.getHoTen());
                    khachHangMoi.setSoDienThoai(dialog.getSoDienThoai());

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
                        JOptionPane.showMessageDialog(view, "Thêm khách hàng thành công!",
                                "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        loadKhachHangComboBox();

                        for (int i = 0; i < view.getCbKhachHang().getItemCount(); i++) {
                            KhachHang kh = view.getCbKhachHang().getItemAt(i);
                            if (kh.getSoDienThoai() != null
                                    && kh.getSoDienThoai().equals(dialog.getSoDienThoai())) {
                                view.getCbKhachHang().setSelectedIndex(i);
                                break;
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(view, "Thêm khách hàng thất bại!",
                                "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(view, "Lỗi khi thêm khách hàng: " + ex.getMessage(),
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        }
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

    private void loadKhachHangComboBox() {
        try {
            List<KhachHang> khachHangs = khachHangService.getAllKhachHang();
            view.getCbKhachHang().removeAllItems(); 
            view.getCbKhachHang().addItem(new KhachHang());

            for (KhachHang kh : khachHangs) {
                view.getCbKhachHang().addItem(kh);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi khi tải danh sách khách hàng: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleThemMoi() {
        try {
            DatLich datLich = validateAndGetFormData();
            if (datLich == null) {
                return;
            }

            boolean success;
            if (isEditMode && currentEditId != -1) {
                Integer maGiuongCu = view.getMaGiuongCu();
                Integer maGiuongMoi = datLich.getMaGiuong();

                success = datLichService.updateDatLich(datLich);
                if (success) {
                    handleCapNhatGiuongKhiSua(maGiuongCu, maGiuongMoi);
                    isEditMode = false;
                    currentEditId = -1;
                    view.setMaGiuongCu(null);

                    DatLich updatedAppointment = datLichService.getDatLichById(datLich.getMaLich());
                    if (updatedAppointment != null) {
                        view.highlightSelectedAppointment(updatedAppointment);
                    }
                }
            } else {
                success = datLichService.addDatLich(datLich);
                if (success) {
                    if (datLich.getMaGiuong() != null) {
                        giuongService.updateTrangThai(datLich.getMaGiuong(), "Đã đặt");
                    }

                    DatLich newAppointment = findNewlyAddedAppointment(datLich);
                    if (newAppointment != null) {
                        view.highlightSelectedAppointment(newAppointment);
                    }
                }
            }

            if (success) {
                clearForm();
                view.updateTimeline();
            } else {
                JOptionPane.showMessageDialog(view,
                        isEditMode ? "Cập nhật lịch hẹn thất bại" : "Thêm lịch hẹn thất bại",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private DatLich findNewlyAddedAppointment(DatLich datLich) {
        try {
            List<DatLich> appointments = datLichService.getDatLichTheoNgay(datLich.getNgayDat());

            for (DatLich appointment : appointments) {
                if (appointment.getMaKhachHang().equals(datLich.getMaKhachHang())
                        && appointment.getGioDat().equals(datLich.getGioDat())
                        && ((appointment.getMaGiuong() == null && datLich.getMaGiuong() == null)
                        || (appointment.getMaGiuong() != null && appointment.getMaGiuong().equals(datLich.getMaGiuong())))) {
                    return appointment;
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi tìm lịch vừa thêm: " + e.getMessage());
        }
        return null;
    }

    private void handleSua() {
        DatLich selectedAppointment = view.getSelectedAppointment();
        if (selectedAppointment == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn lịch hẹn để sửa", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Integer maGiuongCu = selectedAppointment.getMaGiuong();
            isEditMode = true;
            currentEditId = selectedAppointment.getMaLich();
            view.setMaGiuongCu(maGiuongCu);
            fillFormData(selectedAppointment);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Lỗi khi sửa lịch hẹn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleCapNhatGiuongKhiSua(Integer maGiuongCu, Integer maGiuongMoi) {
        try {
            if (maGiuongCu != null && maGiuongMoi != null && !maGiuongCu.equals(maGiuongMoi)) {
                giuongService.updateTrangThai(maGiuongCu, "Trống");
                giuongService.updateTrangThai(maGiuongMoi, "Đã đặt");
                System.out.println("Đã cập nhật trạng thái giường: " + maGiuongCu + " -> Trống, " + maGiuongMoi + " -> Đã đặt");
            } else if (maGiuongCu != null && maGiuongMoi == null) {
                giuongService.updateTrangThai(maGiuongCu, "Trống");
                System.out.println("Đã cập nhật trạng thái giường: " + maGiuongCu + " -> Trống (xóa giường)");
            } else if (maGiuongCu == null && maGiuongMoi != null) {
                giuongService.updateTrangThai(maGiuongMoi, "Đã đặt");
                System.out.println("Đã cập nhật trạng thái giường: " + maGiuongMoi + " -> Đã đặt (thêm giường)");
            } else {
                System.out.println("Không có thay đổi giường");
            }

            view.refreshGiuongComboBox();

        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật trạng thái giường: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleXoa() {
        DatLich selectedAppointment = view.getSelectedAppointment();
        if (selectedAppointment == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn lịch hẹn để xóa", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int confirm = JOptionPane.showConfirmDialog(view,
                    "Bạn có chắc chắn muốn xóa lịch hẹn này?\nKhách hàng: "
                    + khachHangService.getKhachHangById(selectedAppointment.getMaKhachHang()).getHoTen()
                    + "\nThời gian: " + selectedAppointment.getNgayDat().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " "
                    + selectedAppointment.getGioDat().format(DateTimeFormatter.ofPattern("HH:mm")),
                    "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = datLichService.deleteDatLich(selectedAppointment.getMaLich());

                if (success && selectedAppointment.getMaGiuong() != null) {
                    giuongService.updateTrangThai(selectedAppointment.getMaGiuong(), "Trống");
                    view.refreshGiuongComboBox();
                }

                if (success) {
                    JOptionPane.showMessageDialog(view, "Xóa lịch hẹn thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    view.updateTimeline();
                } else {
                    JOptionPane.showMessageDialog(view, "Xóa lịch hẹn thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Lỗi khi xóa lịch hẹn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleHoanThanh() {
        DatLich selectedAppointment = view.getSelectedAppointment();
        if (selectedAppointment == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn lịch hẹn để hoàn thành", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (!selectedAppointment.isDaXacNhan() && !selectedAppointment.isDangThucHien()) {
                JOptionPane.showMessageDialog(view, "Chỉ có thể hoàn thành lịch hẹn đã được xác nhận hoặc đang thực hiện", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            BigDecimal tongTien = tinhTongTienHoaDon(selectedAppointment);

            // XỬ LÝ TIỀN TRẢ TRƯỚC GIỐNG BÊN ĐẶT DỊCH VỤ
            BigDecimal soDuHienTai = BigDecimal.ZERO;
            BigDecimal tienDaThanhToan = BigDecimal.ZERO;
            BigDecimal tienPhaiTra = tongTien;
            boolean coTaiKhoanTraTruoc = false;
            Integer maHoaDonHienTai = null;

            try {
                // Kiểm tra xem khách hàng có tài khoản trả trước không
                TienTraTruocService tienTraTruocService = new TienTraTruocService();
                Model.TienTraTruoc ttt = tienTraTruocService.getTienTraTruocByMaKhachHang(selectedAppointment.getMaKhachHang());

                if (ttt != null && ttt.getSoDuHienTai() != null && ttt.getSoDuHienTai().compareTo(BigDecimal.ZERO) > 0) {
                    coTaiKhoanTraTruoc = true;
                    soDuHienTai = ttt.getSoDuHienTai();

                    // Lấy mã hóa đơn hiện tại
                    maHoaDonHienTai = getMaHoaDonHienTai(selectedAppointment);

                    if (maHoaDonHienTai != null) {
                        // XỬ LÝ THANH TOÁN TRẢ TRƯỚC TRONG DATABASE
                        boolean thanhToanSuccess = tienTraTruocService.xuLyThanhToanHoaDon(
                                selectedAppointment.getMaKhachHang(),
                                maHoaDonHienTai,
                                tongTien
                        );

                        if (thanhToanSuccess) {
                            // Cập nhật lại thông tin sau khi thanh toán
                            ttt = tienTraTruocService.getTienTraTruocByMaKhachHang(selectedAppointment.getMaKhachHang());
                            BigDecimal soDuMoi = ttt != null ? ttt.getSoDuHienTai() : BigDecimal.ZERO;

                            // Tính toán các khoản tiền
                            tienDaThanhToan = soDuHienTai.subtract(soDuMoi);
                            tienPhaiTra = tongTien.subtract(tienDaThanhToan);

                            if (tienPhaiTra.compareTo(BigDecimal.ZERO) < 0) {
                                tienPhaiTra = BigDecimal.ZERO;
                            }

                            System.out.println("✅ Đã xử lý thanh toán trả trước:");
                            System.out.println("   - Tổng tiền dịch vụ: " + tongTien);
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

            int diemThuong = tongTien.divideToIntegralValue(BigDecimal.valueOf(100000)).intValue();

            // Thông báo xác nhận với thông tin tiền trả trước
            String thongBaoTraTruoc = "";
            if (coTaiKhoanTraTruoc) {
                thongBaoTraTruoc = "\n💳 THÔNG TIN THANH TOÁN TRẢ TRƯỚC:"
                        + "\n   Số dư hiện tại: " + String.format("%,.0f", soDuHienTai) + " VND"
                        + "\n   Tổng tiền: " + String.format("%,.0f", tongTien) + " VND"
                        + "\n   Tiền đã thanh toán: " + String.format("%,.0f", tienDaThanhToan) + " VND"
                        + "\n   Tiền phải trả: " + String.format("%,.0f", tienPhaiTra) + " VND";
            }

            int confirm = JOptionPane.showConfirmDialog(view,
                    "Hoàn thành lịch hẹn này?\nKhách hàng: "
                    + khachHangService.getKhachHangById(selectedAppointment.getMaKhachHang()).getHoTen()
                    + "\nThời gian: " + selectedAppointment.getGioDat().format(DateTimeFormatter.ofPattern("HH:mm"))
                    + "\nTổng tiền: " + String.format("%,.0f", tongTien) + " VND"
                    + (diemThuong > 0 ? "\nĐiểm tích lũy: +" + diemThuong + " điểm" : "")
                    + thongBaoTraTruoc
                    + "\nGiường: " + (selectedAppointment.getMaGiuong() != null
                    ? giuongService.getGiuongById(selectedAppointment.getMaGiuong()).getSoHieu() : "Không có")
                    + "\n\nSau khi hoàn thành sẽ:\n- Lưu hóa đơn\n- In PDF hóa đơn\n- Xóa form"
                    + (selectedAppointment.getMaGiuong() != null ? "\n- Giường sẽ được chuyển về trạng thái 'Trống'" : ""),
                    "Xác nhận hoàn thành", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = datLichService.updateTrangThai(selectedAppointment.getMaLich(), "Hoàn thành");

                if (success) {
                    if (selectedAppointment.getMaGiuong() != null) {
                        giuongService.updateTrangThai(selectedAppointment.getMaGiuong(), "Trống");

                        Giuong giuong = giuongService.getGiuongById(selectedAppointment.getMaGiuong());
                        if (giuong != null) {
                            System.out.println("Đã cập nhật trạng thái giường " + giuong.getSoHieu()
                                    + " từ '" + giuong.getTrangThai() + "' -> 'Trống' (do hoàn thành lịch)");
                        }

                        view.refreshGiuongComboBox();
                    }

                    // Lưu hóa đơn
                    Integer maHoaDon = luuHoaDonAndReturnId(selectedAppointment, coTaiKhoanTraTruoc);

                    if (maHoaDon != null && maHoaDon > 0) {
                        if (diemThuong > 0) {
                            capNhatDiemTichLuy(selectedAppointment.getMaKhachHang(), diemThuong);
                        }

                        inHoaDonPDF(selectedAppointment, maHoaDon, tongTien, coTaiKhoanTraTruoc, soDuHienTai, tienDaThanhToan, tienPhaiTra);

                        DatLich updatedAppointment = datLichService.getDatLichById(selectedAppointment.getMaLich());
                        if (updatedAppointment != null) {
                            view.highlightSelectedAppointment(updatedAppointment);
                        }

                        clearForm();
                        view.updateTimeline();

                    } else {
                        JOptionPane.showMessageDialog(view,
                                "Hoàn thành lịch hẹn nhưng LỖI khi lưu hóa đơn!"
                                + (selectedAppointment.getMaGiuong() != null ? "\nTuy nhiên giường đã được chuyển về trạng thái 'Trống'" : ""),
                                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(view, "Cập nhật trạng thái lịch hẹn thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Lỗi khi hoàn thành lịch hẹn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Integer getMaHoaDonHienTai(DatLich datLich) {
        try {
            // Tìm hóa đơn mới nhất của khách hàng
            List<HoaDon> hoaDonList = hoaDonService.getHoaDonByMaKhachHang(datLich.getMaKhachHang());
            if (hoaDonList != null && !hoaDonList.isEmpty()) {
                // Sắp xếp theo ngày lập giảm dần, lấy hóa đơn mới nhất
                hoaDonList.sort((h1, h2) -> h2.getNgayLap().compareTo(h1.getNgayLap()));
                return hoaDonList.get(0).getMaHoaDon();
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy mã hóa đơn hiện tại: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private Integer luuHoaDonAndReturnId(DatLich datLich, boolean coTaiKhoanTraTruoc) {
        try {
            KhachHang khachHang = khachHangService.getKhachHangById(datLich.getMaKhachHang());
            BigDecimal tongTien = tinhTongTienHoaDon(datLich);

            HoaDon hoaDon = new HoaDon();
            hoaDon.setMaKhachHang(datLich.getMaKhachHang());
            hoaDon.setNgayLap(java.time.LocalDateTime.now());
            hoaDon.setTongTien(tongTien);

            String ghiChu = "Hóa đơn từ lịch hẹn #" + datLich.getMaLich();
            if (coTaiKhoanTraTruoc) {
                ghiChu += " - Có thể thanh toán bằng tiền trả trước";
            }
            hoaDon.setGhiChu(ghiChu);

            List<ChiTietHoaDon> chiTietList = new ArrayList<>();

            if (datLich.hasDichVu()) {
                for (DatLichChiTiet chiTiet : datLich.getDanhSachDichVu()) {
                    if (chiTiet.getDichVu() != null) {
                        ChiTietHoaDon cthd = new ChiTietHoaDon();
                        cthd.setMaDichVu(chiTiet.getMaDichVu());
                        cthd.setSoLuong(1);
                        cthd.setDonGia(chiTiet.getDichVu().getGia());
                        cthd.setMaNhanVien(chiTiet.getMaNhanVien());
                        cthd.recalculateThanhTien();
                        chiTietList.add(cthd);
                    }
                }
            }

            hoaDon.setChiTietHoaDon(chiTietList);

            Integer maHoaDon = hoaDonService.addHoaDonAndReturnId(hoaDon);

            if (maHoaDon != null && maHoaDon > 0) {
                System.out.println("✅ Đã lưu hóa đơn thành công: " + maHoaDon);
                return maHoaDon;
            } else {
                System.err.println("❌ Lỗi khi lưu hóa đơn");
                return null;
            }

        } catch (Exception e) {
            System.err.println("❌ Lỗi khi lưu hóa đơn: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private void capNhatDiemTichLuy(Integer maKhachHang, int diemThuong) {
        try {
            KhachHang khachHang = khachHangService.getKhachHangById(maKhachHang);
            if (khachHang != null) {
                int diemHienTai = khachHang.getDiemTichLuy();
                int diemMoi = diemHienTai + diemThuong;
                khachHang.setDiemTichLuy(diemMoi);
                khachHangService.updateKhachHang(khachHang);

                System.out.println("Đã cập nhật điểm tích lũy cho khách hàng " + khachHang.getHoTen()
                        + ": " + diemHienTai + " + " + diemThuong + " = " + diemMoi + " điểm");
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật điểm tích lũy: " + e.getMessage());
        }
    }

    private BigDecimal tinhTongTienHoaDon(DatLich datLich) {
        BigDecimal tongTien = BigDecimal.ZERO;

        if (datLich.hasDichVu()) {
            for (DatLichChiTiet chiTiet : datLich.getDanhSachDichVu()) {
                if (chiTiet.getDichVu() != null && chiTiet.getDichVu().getGia() != null) {
                    tongTien = tongTien.add(chiTiet.getDichVu().getGia());
                }
            }
        }

        return tongTien;
    }

    private void inHoaDonPDF(DatLich datLich, Integer maHoaDon, BigDecimal tongTien,
            boolean coTaiKhoanTraTruoc, BigDecimal soDuHienTai,
            BigDecimal tienDaThanhToan, BigDecimal tienPhaiTra) {
        try {
            KhachHang khachHang = khachHangService.getKhachHangById(datLich.getMaKhachHang());

            int confirm = JOptionPane.showConfirmDialog(
                    view,
                    "Bạn có muốn in hóa đơn PDF?\n"
                    + "Khách hàng: " + khachHang.getHoTen() + "\n"
                    + "Tổng tiền: " + String.format("%,.0f", tongTien) + " VND\n"
                    + "Số dịch vụ: " + (datLich.hasDichVu() ? datLich.getDanhSachDichVu().size() : 0)
                    + (coTaiKhoanTraTruoc ? "\n💳 CÓ THỂ THANH TOÁN BẰNG TIỀN TRẢ TRƯỚC" : ""),
                    "Xác nhận in hóa đơn PDF",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            inHoaDonPDFDetail(datLich, khachHang, tongTien, maHoaDon, coTaiKhoanTraTruoc, soDuHienTai, tienDaThanhToan, tienPhaiTra);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view, "Lỗi khi in hóa đơn!");
        }
    }

    public void inHoaDonPDFDetail(DatLich datLich, KhachHang khachHang, BigDecimal tongTien,
            Integer maHoaDon, boolean coTaiKhoanTraTruoc,
            BigDecimal soDuHienTai, BigDecimal tienDaThanhToan,
            BigDecimal tienPhaiTra) {
        FileOutputStream fos = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String fileName = "HoaDon_DatLich_" + datLich.getMaLich() + "_" + sdf.format(new Date()) + ".pdf";

            // Tạo đường dẫn động đến thư mục bill
            String billDirectory = DataConnection.getBillPath();

            File dir = new File(billDirectory);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String filePath = billDirectory + File.separator + fileName;
            fos = new FileOutputStream(filePath);

            Document doc = new Document();
            PdfWriter writer = PdfWriter.getInstance(doc, fos);
            doc.open();

            BaseFont baseFont;
            try {
                baseFont = BaseFont.createFont("c:/windows/fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            } catch (Exception e1) {
                try {
                    baseFont = BaseFont.createFont("c:/windows/fonts/times.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                } catch (Exception e2) {
                    try {
                        baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.EMBEDDED);
                    } catch (Exception e3) {
                        baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
                    }
                }
            }

            Font fontNormal = new Font(baseFont, 12);
            Font fontBold = new Font(baseFont, 12, Font.BOLD);
            Font fontTitle = new Font(baseFont, 18, Font.BOLD);
            Font fontHeader = new Font(baseFont, 10, Font.BOLD);
            Font fontSmall = new Font(baseFont, 10, Font.NORMAL);

            Paragraph title = new Paragraph("HOÁ ĐƠN DỊCH VỤ", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            doc.add(title);

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
            doc.add(new Paragraph("Ngày lập: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()), fontNormal));
            doc.add(new Paragraph("Khách hàng: " + khachHang.getHoTen(), fontNormal));

            if (khachHang.getSoDienThoai() != null && !khachHang.getSoDienThoai().isEmpty()) {
                doc.add(new Paragraph("SĐT: " + khachHang.getSoDienThoai(), fontNormal));
            }

            // Hiển thị thông tin tiền trả trước GIỐNG BÊN ĐẶT DỊCH VỤ
            if (coTaiKhoanTraTruoc) {
                doc.add(new Paragraph("Số dư tài khoản: " + String.format("%,.0f", soDuHienTai) + " VND", fontNormal));

                doc.add(new Paragraph("Tiền đã thanh toán: " + String.format("%,.0f", tienDaThanhToan) + " VND", fontNormal));

                if (tienPhaiTra.compareTo(BigDecimal.ZERO) == 0) {
                    doc.add(new Paragraph("Tiền phải trả: 0 VND - ĐÃ THANH TOÁN ĐỦ",
                            new Font(baseFont, 14, Font.NORMAL)));
                } else {
                    doc.add(new Paragraph("Tiền phải trả: " + String.format("%,.0f", tienPhaiTra) + " VND", fontBold));
                }
            }

//            doc.add(new Paragraph("Điểm tích lũy hiện tại: " + khachHang.getDiemTichLuy() + " điểm", fontNormal));
//            doc.add(new Paragraph("Lưu ý: Cần tối thiểu 10 điểm để đổi vé gọi đầu", fontSmall));
            doc.add(new Paragraph("---------------------------------------------", fontNormal));

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            float[] columnWidths = {0.8f, 3f, 2f, 1.5f, 2f, 2f};
            table.setWidths(columnWidths);

            table.addCell(new Phrase("STT", fontHeader));
            table.addCell(new Phrase("Tên dịch vụ", fontHeader));
            table.addCell(new Phrase("Thời gian", fontHeader));
            table.addCell(new Phrase("Số lượng", fontHeader));
            table.addCell(new Phrase("Đơn giá", fontHeader));
            table.addCell(new Phrase("Thành tiền", fontHeader));

            int stt = 1;
            BigDecimal tongCong = BigDecimal.ZERO;

            if (datLich.hasDichVu()) {
                for (DatLichChiTiet chiTiet : datLich.getDanhSachDichVu()) {
                    DichVu dichVu = chiTiet.getDichVu();
                    if (dichVu != null) {
                        String tenDV = dichVu.getTenDichVu();
                        BigDecimal donGia = dichVu.getGia();
                        BigDecimal thanhTien = donGia != null ? donGia : BigDecimal.ZERO;
                        tongCong = tongCong.add(thanhTien);

                        table.addCell(new Phrase(String.valueOf(stt++), fontNormal));
                        table.addCell(new Phrase(tenDV, fontNormal));
                        table.addCell(new Phrase("60 phút", fontNormal));
                        table.addCell(new Phrase("1", fontNormal));
                        table.addCell(new Phrase(String.format("%,.0f", donGia) + " VND", fontNormal));
                        table.addCell(new Phrase(String.format("%,.0f", thanhTien) + " VND", fontNormal));
                    }
                }
            }

            doc.add(table);
            doc.add(new Paragraph("---------------------------------------------", fontNormal));

            // HIỂN THỊ THÔNG TIN TÀI CHÍNH GIỐNG BÊN ĐẶT DỊCH VỤ
            doc.add(new Paragraph(String.format("Tổng tiền dịch vụ: %s VND",
                    String.format("%,.0f", tongCong.doubleValue())), fontNormal));

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
                        String.format("%,.0f", tongCong.doubleValue())), fontNormal));
            }

            int diemThuong = tongCong.divideToIntegralValue(BigDecimal.valueOf(100000)).intValue();
            if (diemThuong > 0) {
//                doc.add(new Paragraph("Điểm tích lũy được thưởng: +" + diemThuong + " điểm", fontBold));
            }

            // Hiển thị QR Code nếu còn tiền phải trả (GIỐNG BÊN ĐẶT DỊCH VỤ)
            if (tienPhaiTra.compareTo(BigDecimal.ZERO) > 0) {
                try {
                    String bankBin = "970431";
                    String accountNumber = "0973791643";
                    String accountName = "NGUYEN DIEM THAO NGUYEN";
                    String addInfo = "Thanh toán đặt lịch #" + datLich.getMaLich() + " - " + khachHang.getHoTen();

                    String qrUrl = "https://img.vietqr.io/image/"
                            + bankBin + "-" + accountNumber
                            + "-compact.png?amount=" + tienPhaiTra
                            + "&addInfo=" + URLEncoder.encode(addInfo, StandardCharsets.UTF_8)
                            + "&accountName=" + URLEncoder.encode(accountName, StandardCharsets.UTF_8);

                    BufferedImage qrBufferedImage = ImageIO.read(new URL(qrUrl));

                    String qrFileName = "VietQR_DatLich_" + System.currentTimeMillis() + ".png";
                    String qrPath = billDirectory + File.separator + qrFileName;
                    ImageIO.write(qrBufferedImage, "PNG", new File(qrPath));

                    com.itextpdf.text.Image qrImage = com.itextpdf.text.Image.getInstance(qrPath);
                    qrImage.scaleToFit(120, 120);
                    qrImage.setAlignment(Element.ALIGN_CENTER);

                    doc.add(new Paragraph("\nMã QR thanh toán:", fontBold));
                    doc.add(qrImage);

                    doc.add(new Paragraph("Ngân hàng: EximBank", fontNormal));
                    doc.add(new Paragraph("Chủ tài khoản: " + accountName, fontNormal));
                    doc.add(new Paragraph("Số tài khoản: " + accountNumber, fontNormal));

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
            String ghiChu = "Hóa đơn từ lịch hẹn #" + datLich.getMaLich() + " - " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
            if (coTaiKhoanTraTruoc) {
                ghiChu += " - Đã thanh toán " + String.format("%,.0f", tienDaThanhToan.doubleValue()) + " VND qua tài khoản trả trước";
            }
            doc.add(new Paragraph("\nGhi chú: " + ghiChu, fontNormal));

            doc.add(new Paragraph("\nSweet home - Thân khỏe - Tâm an", fontBold));
            doc.add(new Paragraph("Cám ơn khách thương đã tin và ủng hộ Sweet home. Hẹn gặp lại!", fontNormal));

            doc.close();

            JOptionPane.showMessageDialog(view, "Đã in hóa đơn thành công!");
            try {
                Desktop.getDesktop().open(new File(filePath));
            } catch (Exception e) {
                System.err.println("Không thể mở file PDF: " + e.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Lỗi khi in hóa đơn PDF: " + e.getMessage());
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

    private void handleXacNhan() {
        DatLich selectedAppointment = view.getSelectedAppointment();
        if (selectedAppointment == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn lịch hẹn để xác nhận", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (selectedAppointment.getMaGiuong() != null) {
                Giuong giuong = giuongService.getGiuongById(selectedAppointment.getMaGiuong());
                if (giuong != null && "Đang sử dụng".equals(giuong.getTrangThai())) {
                    JOptionPane.showMessageDialog(view,
                            "Không thể xác nhận lịch hẹn. Giường " + giuong.getSoHieu() + " đang được sử dụng.\nVui lòng chọn giường khác hoặc đợi giường trống.",
                            "Cảnh báo",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            int confirm = JOptionPane.showConfirmDialog(view,
                    "Xác nhận lịch hẹn này?\nKhách hàng: "
                    + khachHangService.getKhachHangById(selectedAppointment.getMaKhachHang()).getHoTen()
                    + "\nThời gian: " + selectedAppointment.getGioDat().format(DateTimeFormatter.ofPattern("HH:mm")),
                    "Xác nhận lịch hẹn", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = datLichService.updateTrangThai(selectedAppointment.getMaLich(), "Đã xác nhận");

                if (success && selectedAppointment.getMaGiuong() != null) {
                    giuongService.updateTrangThai(selectedAppointment.getMaGiuong(), "Đang sử dụng");
                    view.refreshGiuongComboBox();
                }

                if (success) {
                    DatLich updatedAppointment = datLichService.getDatLichById(selectedAppointment.getMaLich());
                    if (updatedAppointment != null) {
                        view.highlightSelectedAppointment(updatedAppointment);
                    }

                    view.updateTimeline();
                } else {
                    JOptionPane.showMessageDialog(view, "Xác nhận lịch hẹn thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Lỗi khi xác nhận lịch hẹn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleHuyLich() {
        DatLich selectedAppointment = view.getSelectedAppointment();
        if (selectedAppointment == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn lịch hẹn để hủy", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int confirm = JOptionPane.showConfirmDialog(view,
                    "Hủy lịch hẹn này?\nKhách hàng: "
                    + khachHangService.getKhachHangById(selectedAppointment.getMaKhachHang()).getHoTen()
                    + "\nThời gian: " + selectedAppointment.getGioDat().format(DateTimeFormatter.ofPattern("HH:mm"))
                    + "\n\nSau khi hủy, giường sẽ được chuyển về trạng thái 'Trống' (nếu có).",
                    "Xác nhận hủy lịch", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = datLichService.updateTrangThai(selectedAppointment.getMaLich(), "Đã hủy");

                if (success && selectedAppointment.getMaGiuong() != null) {
                    giuongService.updateTrangThai(selectedAppointment.getMaGiuong(), "Trống");

                    Giuong giuong = giuongService.getGiuongById(selectedAppointment.getMaGiuong());
                    if (giuong != null) {
                        System.out.println("Đã cập nhật trạng thái giường " + giuong.getSoHieu()
                                + " từ '" + giuong.getTrangThai() + "' -> 'Trống' (do hủy lịch)");
                    }

                    view.refreshGiuongComboBox();
                }

                if (success) {
                    DatLich updatedAppointment = datLichService.getDatLichById(selectedAppointment.getMaLich());
                    if (updatedAppointment != null) {
                        view.highlightSelectedAppointment(updatedAppointment);
                    }

                    view.updateTimeline();
                } else {
                    JOptionPane.showMessageDialog(view, "Hủy lịch hẹn thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Lỗi khi hủy lịch hẹn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleThemDichVu() {
        DichVu selectedDichVu = (DichVu) view.getCbDichVu().getSelectedItem();
        if (selectedDichVu != null && selectedDichVu.getMaDichVu() != null) {
            for (int i = 0; i < view.getListModelDichVu().size(); i++) {
                DichVu dv = view.getListModelDichVu().getElementAt(i);
                if (dv.getMaDichVu().equals(selectedDichVu.getMaDichVu())) {
                    JOptionPane.showMessageDialog(view, "Dịch vụ này đã được thêm", "Thông báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            view.getListModelDichVu().addElement(selectedDichVu);
        }
    }

    private void handleXoaDichVu() {
        int selectedIndex = view.getListDichVu().getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn dịch vụ để xóa", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            DichVu dichVuSeXoa = view.getListModelDichVu().getElementAt(selectedIndex);
            view.getListModelDichVu().remove(selectedIndex);
            view.xoaPhanCongNhanVien(dichVuSeXoa);
            view.capNhatHienThiPhanCong();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi khi xóa dịch vụ: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private DatLich validateAndGetFormData() {
        KhachHang selectedKhachHang = (KhachHang) view.getCbKhachHang().getSelectedItem();
        if (selectedKhachHang == null || selectedKhachHang.getMaKhachHang() == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn khách hàng", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        String ngayDatStr = view.getTxtNgayDat().getText().trim();
        LocalDate ngayDat;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            ngayDat = LocalDate.parse(ngayDatStr, formatter);

            if (ngayDat.isBefore(LocalDate.now())) {
                JOptionPane.showMessageDialog(view, "Ngày đặt không được trong quá khứ", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(view, "Ngày đặt không hợp lệ. Vui lòng nhập theo định dạng dd/MM/yyyy", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        String gioDatStr = view.getTxtGioDat().getText().trim();
        LocalTime gioDat;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            gioDat = LocalTime.parse(gioDatStr, formatter);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(view, "Giờ đặt không hợp lệ. Vui lòng nhập theo định dạng HH:mm", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        Giuong selectedGiuong = (Giuong) view.getCbGiuong().getSelectedItem();
        Integer maGiuong = (selectedGiuong != null && selectedGiuong.getMaGiuong() != null)
                ? selectedGiuong.getMaGiuong() : null;

        Integer soLuongNguoi = (Integer) view.getSpinnerSoLuongNguoi().getValue();
        if (soLuongNguoi == null || soLuongNguoi < 1) {
            soLuongNguoi = 1;
        }

        String ghiChu = view.getTxtGhiChu().getText().trim();

        DatLich datLich = new DatLich();

        if (isEditMode && currentEditId != -1) {
            datLich.setMaLich(currentEditId);
        }

        datLich.setMaKhachHang(selectedKhachHang.getMaKhachHang());
        datLich.setNgayDat(ngayDat);
        datLich.setGioDat(gioDat);
        datLich.setTrangThai("Chờ xác nhận");
        datLich.setMaGiuong(maGiuong);
        datLich.setGhiChu(ghiChu);
        datLich.setSoLuongNguoi(soLuongNguoi);

        List<DatLichChiTiet> danhSachDichVu = new ArrayList<>();
        Map<DichVu, NhanVien> phanCong = view.getPhanCongNhanVien();

        for (int i = 0; i < view.getListModelDichVu().size(); i++) {
            DichVu dichVu = view.getListModelDichVu().getElementAt(i);
            if (dichVu != null && dichVu.getMaDichVu() != null) {
                DatLichChiTiet chiTiet = new DatLichChiTiet();
                chiTiet.setMaDichVu(dichVu.getMaDichVu());
                chiTiet.setDichVu(dichVu);

                NhanVien nhanVienPhanCong = phanCong.get(dichVu);
                if (nhanVienPhanCong != null && nhanVienPhanCong.getMaNhanVien() != null) {
                    chiTiet.setMaNhanVien(nhanVienPhanCong.getMaNhanVien());
                    chiTiet.setNhanVien(nhanVienPhanCong);
                }

                danhSachDichVu.add(chiTiet);
            }
        }
        datLich.setDanhSachDichVu(danhSachDichVu);

        return datLich;
    }

    private void fillFormData(DatLich datLich) {
        for (int i = 0; i < view.getCbKhachHang().getItemCount(); i++) {
            KhachHang kh = view.getCbKhachHang().getItemAt(i);
            if (kh.getMaKhachHang().equals(datLich.getMaKhachHang())) {
                view.getCbKhachHang().setSelectedIndex(i);
                break;
            }
        }

        if (datLich.getMaGiuong() != null) {
            for (int i = 0; i < view.getCbGiuong().getItemCount(); i++) {
                Giuong g = view.getCbGiuong().getItemAt(i);
                if (g.getMaGiuong() != null && g.getMaGiuong().equals(datLich.getMaGiuong())) {
                    view.getCbGiuong().setSelectedIndex(i);
                    break;
                }
            }
        } else {
            view.getCbGiuong().setSelectedIndex(0);
        }

        view.getTxtNgayDat().setText(datLich.getNgayDat().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        view.getTxtGioDat().setText(datLich.getGioDat().format(DateTimeFormatter.ofPattern("HH:mm")));
        view.getTxtGhiChu().setText(datLich.getGhiChu() != null ? datLich.getGhiChu() : "");

        if (datLich.getSoLuongNguoi() != null) {
            view.getSpinnerSoLuongNguoi().setValue(datLich.getSoLuongNguoi());
        } else {
            view.getSpinnerSoLuongNguoi().setValue(1);
        }

        view.getListModelDichVu().clear();
        view.clearPhanCongNhanVien();

        if (datLich.hasDichVu()) {
            for (DatLichChiTiet chiTiet : datLich.getDanhSachDichVu()) {
                if (chiTiet.getDichVu() != null) {
                    view.getListModelDichVu().addElement(chiTiet.getDichVu());

                    if (chiTiet.getNhanVien() != null) {
                        view.themPhanCongNhanVien(chiTiet.getDichVu(), chiTiet.getNhanVien());
                    }
                }
            }
        }

        view.capNhatHienThiPhanCong();
    }

    private void clearForm() {
        view.getCbKhachHang().setSelectedIndex(0);
        view.getCbDichVu().setSelectedIndex(0);
        view.getCbGiuong().setSelectedIndex(0);
        view.getTxtGioDat().setText("");
        view.getTxtGhiChu().setText("");
        view.getListModelDichVu().clear();
        view.getSpinnerSoLuongNguoi().setValue(1);
        view.clearPhanCongNhanVien();

        isEditMode = false;
        currentEditId = -1;
        view.setMaGiuongCu(null);

        view.getTxtNgayDat().setText(view.getSelectedDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }
}
