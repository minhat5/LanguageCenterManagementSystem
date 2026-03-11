package vn.edu.ute.repo.impl;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Room;
import vn.edu.ute.repo.RoomRepo;

import java.util.List;

public class RoomRepoImpl implements RoomRepo {

    @Override
    public List<Room> findAll(EntityManager em) {
        String jpql = "SELECT r FROM Room r" +
                " LEFT JOIN FETCH r.branch";
        return em.createQuery(jpql, Room.class).getResultList();
    }
}
