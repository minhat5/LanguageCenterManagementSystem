package vn.edu.ute.service.impl;

import vn.edu.ute.common.enumeration.Role;
import vn.edu.ute.common.enumeration.Status;
import vn.edu.ute.common.util.PasswordUtil;
import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.Teacher;
import vn.edu.ute.model.UserAccount;
import vn.edu.ute.repo.TeacherRepo;
import vn.edu.ute.repo.UserAccountRepo;
import vn.edu.ute.service.TeacherService;

import java.util.List;
import java.util.stream.Collectors;

public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepo teacherRepo;
    private final UserAccountRepo userAccountRepo;
    private final TransactionManager txManager;

    public TeacherServiceImpl(TeacherRepo teacherRepo, UserAccountRepo userAccountRepo, TransactionManager txManager) {
        this.teacherRepo = teacherRepo;
        this.userAccountRepo = userAccountRepo;
        this.txManager = txManager;
    }

    @Override
    public List<Teacher> getAll() throws Exception {
        return getAllTeachers();
    }

    @Override
    public Teacher createTeacherAccount(Teacher teacherInfo, String username, String initialPassword) throws Exception {
        return txManager.runInTransaction(em -> {
            if (teacherRepo.existsByEmail(em, teacherInfo.getEmail())) {
                throw new Exception("Email giáo viên đã tồn tại: " + teacherInfo.getEmail());
            }
            if (teacherRepo.existsByPhone(em, teacherInfo.getPhone())) {
                throw new Exception("Số điện thoại giáo viên đã tồn tại: " + teacherInfo.getPhone());
            }
            if (userAccountRepo.existsByUsername(em, username)) {
                throw new Exception("Tên đăng nhập đã tồn tại: " + username);
            }

            Teacher savedTeacher = teacherRepo.save(em, teacherInfo);

            UserAccount account = new UserAccount();
            account.setUsername(username);
            account.setPasswordHash(PasswordUtil.hashPassword(initialPassword));
            account.setRole(Role.Teacher);
            account.setIsActive(true);
            account.setTeacher(savedTeacher);

            userAccountRepo.save(em, account);

            savedTeacher.setUserAccount(account);
            return savedTeacher;
        });
    }

    @Override
    public List<Teacher> getAllTeachers() throws Exception {
        return txManager.runInTransaction(em -> teacherRepo.findAll(em));
    }

    @Override
    public Teacher updateTeacher(Teacher teacher) throws Exception {
        return txManager.runInTransaction(em -> {
            Teacher updated = teacherRepo.save(em, teacher);
            em.createQuery("UPDATE UserAccount u SET u.isActive = :isActive WHERE u.teacher = :teacher")
              .setParameter("isActive", teacher.getStatus() == Status.Active)
              .setParameter("teacher", updated)
              .executeUpdate();
            return updated;
        });
    }

    @Override
    public void deleteTeacher(Long id) throws Exception {
        txManager.runInTransaction(em -> {
            teacherRepo.deleteById(em, id);
            return null;
        });
    }

    @Override
    public List<Teacher> filterTeachers(String keyword, Status status, String specialty) throws Exception {
        // Chuyển đổi toàn bộ danh sách thành dạng Stream để thao tác xử lý luồng
        return getAllTeachers().stream()
                // Áp dụng bộ lọc (filter) để giữ lại các bản ghi thỏa mãn điều kiện
                .filter(t -> {
                    boolean matchKw = (keyword == null || keyword.trim().isEmpty()) ||
                            (t.getFullName() != null && t.getFullName().toLowerCase().contains(keyword.toLowerCase()))
                            ||
                            (t.getPhone() != null && t.getPhone().contains(keyword));
                    boolean matchSt = (status == null) || (t.getStatus() == status);
                    boolean matchSpec = (specialty == null || specialty.trim().isEmpty() || specialty.equals("Tất cả"))
                            ||
                            (t.getSpecialty() != null
                                    && t.getSpecialty().toLowerCase().contains(specialty.toLowerCase()));
                    return matchKw && matchSt && matchSpec;
                })
                // Thu thập (collect) các bản ghi sau khi lọc trả về dưới dạng List
                .collect(Collectors.toList());
    }
}