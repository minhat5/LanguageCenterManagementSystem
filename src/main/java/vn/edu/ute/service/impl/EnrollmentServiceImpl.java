package vn.edu.ute.service.impl;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.enumeration.EnrollmentStatus;
import vn.edu.ute.enumeration.Level;
import vn.edu.ute.enumeration.Result;
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
            Student student = studentRepo.findById(em, studentId);
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
        //Lamdas Stream lọc danh sách phù hợp với trình độ và trạng thái
        return allClasses.stream()
                // Lọc lớp đang mở
                .filter(c -> c.getStatus().name().equals("Active"))
                // Lọc lớp có Level khớp với bài test
                .filter(c -> c.getCourse() != null && c.getCourse().getLevel() == targetLevel)
                .collect(Collectors.toList());
        });
    }

    @Override
    public void enrollStudent(Long studentId, Long classId) throws Exception {
        txManager.runInTransaction(em -> {
            List<Enrollment> enrollments = enrollmentRepo.findAll(em);
            Clas targetClass = clasRepo.findById(em, classId);
            Student targetStudent = studentRepo.findById(em, studentId);

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
}
