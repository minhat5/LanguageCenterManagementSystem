package vn.edu.ute.service.impl;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.common.enumeration.EnrollmentStatus;
import vn.edu.ute.common.enumeration.Level;
import vn.edu.ute.common.enumeration.Result;
import vn.edu.ute.model.Clas;
import vn.edu.ute.model.Enrollment;
import vn.edu.ute.model.PlacementTest;
import vn.edu.ute.model.Student;
import vn.edu.ute.repo.ClasRepo;
import vn.edu.ute.repo.EnrollmentRepo;
import vn.edu.ute.repo.PlacementTestRepo;
import vn.edu.ute.repo.StudentRepo;
import vn.edu.ute.service.EnrollmentService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class EnrollmentServiceImpl implements EnrollmentService {
    private final TransactionManager txManager;
    private final PlacementTestRepo testRepo;
    private final EnrollmentRepo enrollmentRepo;
    private final ClasRepo clasRepo;
    private final StudentRepo studentRepo;

    public EnrollmentServiceImpl(TransactionManager txManager, PlacementTestRepo testRepo, EnrollmentRepo enrollmentRepo, ClasRepo clasRepo, StudentRepo studentRepo) {
        this.txManager =  txManager;
        this.testRepo = testRepo;
        this.enrollmentRepo = enrollmentRepo;
        this.clasRepo = clasRepo;
        this.studentRepo = studentRepo;
    }

    @Override
    public void submitPlacementTest(Long studentId, BigDecimal score, String note) throws Exception {
        txManager.runInTransaction(em -> {
            Student student = studentRepo.findById(em, studentId).orElse(null);
            if (student == null) throw new Exception("Không tìm thấy Học viên!");

            PlacementTest test = new PlacementTest();
            test.setStudent(student);
            test.setScore(score);
            test.setNote(note);
            // Logic xếp trình độ (Dưới 5.0 -> A1, 5.0 - 7.5 -> B1, > 7.5 -> C1)
            Level suggestedLevel = score.compareTo(new BigDecimal("5.0")) < 0 ? Level.Beginner :
                    score.compareTo(new BigDecimal("7.5")) <= 0 ? Level.Intermediate : Level.Advanced;
            test.setSuggestedLevel(suggestedLevel);
            test.setTestDate(LocalDate.now());
            testRepo.save(em, test);
            return null;
        });
    }

    @Override
    public List<Clas> getSuggestedClasses(Long studentId) throws Exception {
        return txManager.runInTransaction(em -> {
        List<PlacementTest> tests = testRepo.findByStudentId(em, studentId);
        //Lamdas Stream lấy bài test có điểm cao nhất
        PlacementTest bestTest = tests.stream()
                .max(Comparator.comparing(PlacementTest::getScore))
                .orElseThrow(() -> new Exception("Học viên chưa có bài thi!"));
        Level targetLevel = bestTest.getSuggestedLevel();
        List<Clas> allClasses = clasRepo.findAll(em);
        List<Enrollment> enrollments = enrollmentRepo.findAll(em);
        //Lamdas Stream lọc danh sách phù hợp với trình độ và trạng thái
        return allClasses.stream()
                // Lọc lớp đang mở
                .filter(c -> c.getStatus() == vn.edu.ute.common.enumeration.ClassStatus.Open || c.getStatus() == vn.edu.ute.common.enumeration.ClassStatus.Planned)
                // Lọc lớp có Level khớp với bài test
                .filter(c -> c.getCourse() != null && c.getCourse().getLevel() == targetLevel)
                // lớp chưa đầy
                .filter(c -> enrollments.stream()
                        .filter(e -> e.getClas().getClassId().equals(c.getClassId()))
                        .count() < c.getMaxStudent())
                .collect(Collectors.toList());
        });
    }

    @Override
    public void enrollStudent(Long studentId, Long classId) throws Exception {
        txManager.runInTransaction(em -> {
            List<Enrollment> enrollments = enrollmentRepo.findAll(em);
            Clas targetClass = clasRepo.findById(em, classId);
            Student targetStudent = studentRepo.findById(em, studentId).orElse(null);

            if (targetStudent == null) throw new Exception("Không tìm thấy học viên!");
            if (targetClass == null) throw new Exception("Không tìm thấy lớp học!");

            if (targetStudent.getStatus() == vn.edu.ute.common.enumeration.Status.Inactive) {
                throw new Exception("Học viên đang tạm ngưng hoạt động, không thể ghi danh!");
            }
            if (targetClass.getStatus() == vn.edu.ute.common.enumeration.ClassStatus.Cancelled || 
                targetClass.getStatus() == vn.edu.ute.common.enumeration.ClassStatus.Completed) {
                throw new Exception("Lớp học này đã kết thúc hoặc bị hủy, không thể nhận thêm sinh viên!");
            }

            //Lambda kiểm tra học viên đã đăng ký chưa
            boolean isEnrolled = enrollments.stream()
                    .anyMatch(e -> e.getStudent().getStudentId().equals(studentId)
                            && e.getClas().getClassId().equals(classId)
                            && e.getStatus() == EnrollmentStatus.Enrolled);
            if (isEnrolled)
                throw new Exception("Học viên đã ghi danh vào lớp này!");

            //Lambda kiểm tra sỉ số lớp hiện tại
            Long currentStudents = enrollments.stream()
                    .filter(e -> e.getClas().getClassId().equals(classId)
                            && e.getStatus() == EnrollmentStatus.Enrolled)
                    .count();
            if (currentStudents >= targetClass.getMaxStudent())
                throw new Exception("Lớp học đã đạt sỉ số tối đa!");

            Enrollment newEnrollment = new Enrollment();
            newEnrollment.setStudent(targetStudent);
            newEnrollment.setClas(targetClass);
            newEnrollment.setEnrollmentDate(LocalDate.now());
            newEnrollment.setStatus(EnrollmentStatus.Enrolled);
            newEnrollment.setResult(Result.NA);
            enrollmentRepo.save(em, newEnrollment);
            return null;
        });
    }
    @Override
    public List<PlacementTest> getAllPlacementTests() throws Exception {
        return txManager.runInTransaction(em -> {
            return testRepo.findAll(em);
        });
    }

    @Override
    public void updatePlacementTest(Long testId, BigDecimal newScore, String newNote) throws Exception {
        txManager.runInTransaction(em -> {
            PlacementTest test = testRepo.findById(em, testId);
            if (test == null) throw new Exception("Không tìm thấy bài thi!");

            test.setScore(newScore);
            test.setNote(newNote);

            // Tính lại Level dựa trên điểm mới
            Level suggestedLevel = newScore.compareTo(new BigDecimal("5.0")) < 0 ? Level.Beginner :
                    newScore.compareTo(new BigDecimal("7.5")) <= 0 ? Level.Intermediate : Level.Advanced;
            test.setSuggestedLevel(suggestedLevel);

            return null;
        });
    }

    @Override
    public void deletePlacementTest(Long testId) throws Exception {
        txManager.runInTransaction(em -> {
            testRepo.delete(em, testId);
            return null;
        });
    }
    @Override
    public List<Enrollment> getAllEnrollments() throws Exception {
        return txManager.runInTransaction(em -> {
            return enrollmentRepo.findAll(em);
        });
    }

    @Override
    public void updateEnrollmentStatus(Long enrollmentId, EnrollmentStatus newStatus) throws Exception {
        txManager.runInTransaction(em -> {
            // Dùng EntityManager tìm trực tiếp theo ID
            Enrollment enrollment = em.find(Enrollment.class, enrollmentId);
            if (enrollment == null) throw new Exception("Không tìm thấy thông tin ghi danh!");

            enrollment.setStatus(newStatus);
            // JPA tự động lưu lại thay đổi khi Transaction commit
            return null;
        });
    }
    @Override
    public List<Clas> getAllClasses() throws Exception {
        return txManager.runInTransaction(em -> clasRepo.findAll(em));
    }

    @Override
    public void updateEnrollment(Long enrollmentId, Long newClassId, EnrollmentStatus newStatus, Result newResult) throws Exception {
        txManager.runInTransaction(em -> {
            // Tìm Ghi danh hiện tại
            Enrollment enrollment = em.find(Enrollment.class, enrollmentId);
            if (enrollment == null) throw new Exception("Không tìm thấy thông tin ghi danh!");

            // Kiểm tra xem Lớp học có bị thay đổi không
            if (!enrollment.getClas().getClassId().equals(newClassId)) {
                Clas newClass = clasRepo.findById(em, newClassId);
                if (newClass == null) throw new Exception("Không tìm thấy lớp học mới!");

                if (newClass.getStatus() == vn.edu.ute.common.enumeration.ClassStatus.Cancelled || 
                    newClass.getStatus() == vn.edu.ute.common.enumeration.ClassStatus.Completed) {
                    throw new Exception("Lớp học mới này đã kết thúc hoặc bị hủy, không thể chuyển lớp!");
                }

                // Cập nhật lớp học mới
                enrollment.setClas(newClass);
            }

            // Cập nhật trạng thái và kết quả
            enrollment.setStatus(newStatus);
            enrollment.setResult(newResult);

            return null;
        });
    }

    @Override
    public List<Enrollment> getEnrollmentsByClassId(Long classId) throws Exception {
        return txManager.runInTransaction(em -> enrollmentRepo.findByClassId(em, classId));
    }
}
