package Controller;

import Model.KhachHang;
import Service.KhachHangService;
import View.TimKhachHangView;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class TimKhachHangController {

    private TimKhachHangView view;
    private KhachHangService khachHangService;
    private Object parentController;

    public TimKhachHangController(TimKhachHangView view, Object parentController) {
        this.view = view;
        this.parentController = parentController;
        this.khachHangService = new KhachHangService();
        initController();
    }

    private void initController() {
        // Sự kiện tìm kiếm
        view.getBtnTimKiem().addActionListener(e -> handleTimKiem());

        // Sự kiện chọn khách hàng
        view.getBtnChon().addActionListener(e -> handleChonKhachHang());

        // Sự kiện hủy
        view.getBtnHuy().addActionListener(e -> handleHuy());

        // Sự kiện double click trên bảng
        view.getTblKhachHang().addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    handleChonKhachHang();
                }
            }
        });
    }

    private void handleTimKiem() {
        if (!view.coDuLieuTimKiem()) {
            int confirm = JOptionPane.showConfirmDialog(
                    view,
                    "Bạn chưa nhập điều kiện tìm kiếm. Tìm kiếm tất cả khách hàng?",
                    "Xác nhận tìm kiếm",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
        }

        try {
            // Xóa dữ liệu cũ
            view.xoaTatCaDuLieu();

            // Tìm kiếm khách hàng
            List<KhachHang> dsKhachHang = khachHangService.searchKhachHang(
                    view.getTen(),
                    view.getSoDienThoai()
            );

            if (dsKhachHang.isEmpty()) {
                JOptionPane.showMessageDialog(view,
                        "Không tìm thấy khách hàng nào phù hợp!",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Thêm dữ liệu vào bảng
            for (KhachHang kh : dsKhachHang) {
                view.themKhachHangVaoBang(new Object[]{
                    kh.getMaKhachHang(),
                    kh.getHoTen(),
                    kh.getSoDienThoai()
                });
            }

            JOptionPane.showMessageDialog(view,
                    "Tìm thấy " + dsKhachHang.size() + " khách hàng!",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view,
                    "Lỗi khi tìm kiếm khách hàng: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleChonKhachHang() {
        int selectedRow = view.getTblKhachHang().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(view,
                    "Vui lòng chọn một khách hàng từ bảng!",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                view,
                "Bạn có chắc chọn khách hàng này?",
                "Xác nhận chọn",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            view.chonKhachHangHienTai();
            capNhatKhachHangDuocChon(
                    view.getMaKhachHangDuocChon(),
                    view.getTenKhachHangDuocChon(),
                    view.getSoDienThoaiDuocChon()
            );
            view.dispose();
        }
    }

    public void capNhatKhachHangDuocChon(Integer maKhachHang, String tenKhachHang, String soDienThoai) {
        try {
            if (parentController instanceof DatDichVuController) {
                DatDichVuController controller = (DatDichVuController) parentController;
                controller.capNhatKhachHangDuocChon(maKhachHang, tenKhachHang, soDienThoai);
            } else if (parentController instanceof TienTraTruocController) {
                TienTraTruocController controller = (TienTraTruocController) parentController;
                controller.capNhatKhachHangDuocChon(maKhachHang, tenKhachHang, soDienThoai);
            } else if (parentController instanceof LichSuGiaoDichTraTruocController) {
                LichSuGiaoDichTraTruocController controller = (LichSuGiaoDichTraTruocController) parentController;
                controller.capNhatKhachHangDuocChon(maKhachHang, tenKhachHang, soDienThoai);
            } else if (parentController instanceof QuanLyDatLichController) {
                QuanLyDatLichController controller = (QuanLyDatLichController) parentController;
                controller.capNhatKhachHangDuocChon(maKhachHang, tenKhachHang, soDienThoai);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view,
                    "Lỗi khi cập nhật khách hàng: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleHuy() {
        int confirm = JOptionPane.showConfirmDialog(
                view,
                "Bạn có chắc muốn hủy tìm kiếm?",
                "Xác nhận hủy",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            view.dispose();
        }
    }
}
