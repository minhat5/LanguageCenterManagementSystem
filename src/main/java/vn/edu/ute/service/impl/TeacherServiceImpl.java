package vn.edu.ute.service.impl;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.Teacher;
import vn.edu.ute.repo.TeacherRepo;
import vn.edu.ute.service.TeacherService;

import java.util.List;

public class TeacherServiceImpl implements TeacherService {
    private final TeacherRepo teacherRepo;
    private final TransactionManager tx;

    public TeacherServiceImpl(TeacherRepo teacherRepo, TransactionManager tx) {
        this.teacherRepo = teacherRepo;
        this.tx = tx;
    }

    @Override
    public List<Teacher> getAll() throws Exception {
        return tx.runInTransaction(teacherRepo::findAll);
    }
}
