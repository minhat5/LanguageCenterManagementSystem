package vn.edu.ute.repo.impl;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Teacher;
import vn.edu.ute.repo.TeacherRepo;

import java.util.List;

public class TeacherRepoImpl implements TeacherRepo {

    @Override
    public List<Teacher> findAll(EntityManager em) {
        return em.createQuery("SELECT t FROM Teacher t", Teacher.class).getResultList();
    }
}
