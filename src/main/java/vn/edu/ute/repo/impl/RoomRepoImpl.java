package vn.edu.ute.repo.impl;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Room;
import vn.edu.ute.repo.RoomRepo;

import java.util.List;
import java.util.Optional;

public class RoomRepoImpl implements RoomRepo {

    @Override
    public List<Room> findAll(EntityManager em) {
        String jpql = "SELECT r FROM Room r" +
                " LEFT JOIN FETCH r.branch";
        return em.createQuery(jpql, Room.class).getResultList();
    }
    @Override
    public Room save(EntityManager em, Room room) {
        if (room.getRoomId() == null) {
            em.persist(room);
            return room;
        } else {
            return em.merge(room);
        }
    }

    @Override
    public Optional<Room> findById(EntityManager em, Long id) {
        return Optional.ofNullable(em.find(Room.class, id));
    }

    @Override
    public List<Room> findByBranchId(EntityManager em, Long branchId) {
        return em.createQuery("SELECT r FROM Room r WHERE r.branch.branchId = :branchId", Room.class)
                .setParameter("branchId", branchId)
                .getResultList();
    }

    @Override
    public void deleteById(EntityManager em, Long id) {
        Room room = em.find(Room.class, id);
        if (room != null) {
            em.remove(room);
        }
    }
}
