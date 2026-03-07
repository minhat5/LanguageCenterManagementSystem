package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Room;

import java.util.List;

public interface RoomRepo {
    // Tìm tất cả phòng học
    List<Room> findAll(EntityManager em);
}
