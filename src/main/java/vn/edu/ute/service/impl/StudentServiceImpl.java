package vn.edu.ute.service.impl;

import vn.edu.ute.common.enumeration.Role;
import vn.edu.ute.common.util.PasswordUtil;
import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.Student;
import vn.edu.ute.model.UserAccount;
import vn.edu.ute.repo.StudentRepo;
import vn.edu.ute.repo.UserAccountRepo;
import vn.edu.ute.service.StudentService;

public class StudentServiceImpl implements StudentService {

    private final StudentRepo studentRepo;
    private final UserAccountRepo userAccountRepo;
    private final TransactionManager txManager;

    public StudentServiceImpl(StudentRepo studentRepo, UserAccountRepo userAccountRepo, TransactionManager txManager) {
        this.studentRepo = studentRepo;
        this.userAccountRepo = userAccountRepo;
        this.txManager = txManager;
    }

    @Override
    public Student registerStudentAccount(Student studentInfo, String username, String password) throws Exception {
        return txManager.runInTransaction(em -> {
            // 1. Kiểm tra tồn tại (Email, Phone, Username)
            if (studentRepo.existsByEmail(em, studentInfo.getEmail())) {
                throw new Exception("Email sinh viên đã tồn tại: " + studentInfo.getEmail());
            }
            if (studentRepo.existsByPhone(em, studentInfo.getPhone())) {
                throw new Exception("Số điện thoại sinh viên đã tồn tại: " + studentInfo.getPhone());
            }
            if (userAccountRepo.existsByUsername(em, username)) {
                throw new Exception("Tên đăng nhập đã tồn tại: " + username);
            }

            // 2. Lưu hồ sơ Student
            Student savedStudent = studentRepo.save(em, studentInfo);

            // 3. Khởi tạo tài khoản UserAccount map với Student
            UserAccount account = new UserAccount();
            account.setUsername(username);
            account.setPasswordHash(PasswordUtil.hashPassword(password));
            account.setRole(Role.Student);
            account.setIsActive(true);
            account.setStudent(savedStudent);

            userAccountRepo.save(em, account);

            // Link back mapping in memory just in case
            savedStudent.setUserAccount(account);

            return savedStudent;
        });
    }

    @Override
    public java.util.List<Student> getAllStudents() throws Exception {
        return txManager.runInTransaction(studentRepo::findAll);
    }

    @Override
    public Student updateStudent(Student student) throws Exception {
        return txManager.runInTransaction(em -> studentRepo.save(em, student));
    }

    @Override
    public void deleteStudent(Long id) throws Exception {
        txManager.runInTransaction(em -> {
            studentRepo.deleteById(em, id);
            return null;
        });
    }

    @Override
    public java.util.List<Student> filterStudents(String keyword, vn.edu.ute.common.enumeration.Gender gender, vn.edu.ute.common.enumeration.Status status) throws Exception {
        return getAllStudents().stream()
                .filter(s -> {
                    boolean matchKw = (keyword == null || keyword.trim().isEmpty()) ||
                            (s.getFullName() != null && s.getFullName().toLowerCase().contains(keyword.toLowerCase())) ||
                            (s.getPhone() != null && s.getPhone().contains(keyword));
                    boolean matchGen = (gender == null) || (s.getGender() == gender);
                    boolean matchSt = (status == null) || (s.getStatus() == status);
                    return matchKw && matchGen && matchSt;
                })
                .collect(java.util.stream.Collectors.toList());
    }
}