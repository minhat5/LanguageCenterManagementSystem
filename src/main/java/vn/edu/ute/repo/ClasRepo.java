package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Clas;

import java.util.List;

public interface ClasRepo {
    //Tìm tất cả lớp học
    List<Clas> findAll(EntityManager em);
    //Thêm lớp học mới
    void insert(EntityManager em, Clas clas);
    //Cập nhật thông tin lớp học
    void update(EntityManager em, Clas clas);
    //Xoá lớp học
    void delete(EntityManager em, Long id);
    //Tìm lớp học theo id
    Clas findById(EntityManager em, Long id);
}
