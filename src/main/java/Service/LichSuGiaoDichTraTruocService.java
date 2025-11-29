package Service;

import Repository.LichSuGiaoDichTraTruocRepository;
import Model.LichSuGiaoDichTraTruoc;

import java.util.List;

public class LichSuGiaoDichTraTruocService {

    private final LichSuGiaoDichTraTruocRepository repository;

    public LichSuGiaoDichTraTruocService() {
        this.repository = new LichSuGiaoDichTraTruocRepository();
    }

    public List<LichSuGiaoDichTraTruoc> getAllLichSu() {
        try {
            return repository.getAll();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy lịch sử giao dịch: " + e.getMessage(), e);
        }
    }

    public List<LichSuGiaoDichTraTruoc> getLichSuByMaKhachHang(int maKhachHang) {
        try {
            return repository.getByMaKhachHang(maKhachHang);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy lịch sử theo khách hàng: " + e.getMessage(), e);
        }
    }
}