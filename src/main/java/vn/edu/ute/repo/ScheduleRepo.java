package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Schedule;

import java.util.List;

public interface ScheduleRepo {
    //Tìm tất cả lịch học
    List<Schedule> findAll(EntityManager em);
    //Thêm lịch học mới
    void insert(EntityManager em, Schedule schedule);
    //Cập nhật thông tin lịch học
    void update(EntityManager em, Schedule schedule);
    //Xoá lịch học
    void delete(EntityManager em, Long id);
    //Tìm lịch học theo id
    Schedule findById(EntityManager em, Long id);
}
