package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Room;

import java.util.List;
import java.util.Optional;

public interface RoomRepo {
    // Tìm tất cả phòng học
    List<Room> findAll(EntityManager em);
    Room save(EntityManager em, Room room);
    Optional<Room> findById(EntityManager em, Long id);
    List<Room> findByBranchId(EntityManager em, Long branchId);
    void deleteById(EntityManager em, Long id);
}
