package vn.edu.ute.repo.impl;

import jakarta.persistence.EntityManager;
import vn.edu.ute.repo.StaffRepository;

import java.util.Optional;

public class StaffRepositoryImpl implements StaffRepository {

    @Override
    public vn.edu.ute.model.Staff save(EntityManager em, vn.edu.ute.model.Staff staff) {
        if (staff.getStaffId() == null) {
            em.persist(staff);
            return staff;
        } else {
            return em.merge(staff);
        }
    }

    @Override
    public Optional<vn.edu.ute.model.Staff> findById(EntityManager em, Long staffId) {
        return Optional.ofNullable(em.find(vn.edu.ute.model.Staff.class, staffId));
    }

    @Override
    public java.util.List<vn.edu.ute.model.Staff> findAll(EntityManager em) {
        return em.createQuery("SELECT s FROM Staff s", vn.edu.ute.model.Staff.class).getResultList();
    }

    @Override
    public void deleteById(EntityManager em, Long staffId) {
        vn.edu.ute.model.Staff s = em.find(vn.edu.ute.model.Staff.class, staffId);
        if (s != null) {
            em.remove(s);
        }
    }
}
