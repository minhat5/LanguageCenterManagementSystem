package vn.edu.ute.service;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.Teacher;
import vn.edu.ute.repo.TeacherRepo;

import java.util.List;

public class TeacherService {
    private final TeacherRepo teacherRepo;
    private final TransactionManager tx;

    public TeacherService(TeacherRepo teacherRepo, TransactionManager tx) {
        this.teacherRepo = teacherRepo;
        this.tx = tx;
    }

    public List<Teacher> getAll() throws Exception {
        return tx.runInTransaction(teacherRepo::findAll);
    }
}
