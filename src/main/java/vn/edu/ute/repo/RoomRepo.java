package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Room;

import java.util.List;

public interface RoomRepo {
    List<Room> findAll(EntityManager em);
    Room findById(EntityManager em, int id);
}
