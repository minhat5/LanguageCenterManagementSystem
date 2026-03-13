package vn.edu.ute.common.factory;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.repo.*;
import vn.edu.ute.repo.impl.*;
import vn.edu.ute.service.*;
import vn.edu.ute.service.impl.*;

public class ServiceFactory {
    private static ServiceFactory instance;

    private final TransactionManager txManager;

    // --- REPOSITORIES ---
    private final UserAccountRepo userAccountRepo;
    private final StaffRepo staffRepo;
    private final StudentRepo studentRepo;
    private final CourseRepo courseRepo;
    private final ClasRepo classRepo;
    private final TeacherRepo teacherRepo;
    private final BranchRepo branchRepo;
    private final RoomRepo roomRepo;
    private final ScheduleRepo scheduleRepo;
    private final AttendanceRepo attendanceRepo;
    private final EnrollmentRepo enrollmentRepo;
    private final NotificationRepo notificationRepo;
    // Bổ sung cho nhánh Report/Enrollment
    private final PlacementTestRepo placementTestRepo;
    private final PromotionRepo promotionRepo;
    private final CertificateRepo certificateRepo;
    private final ResultRepo resultRepo;
    private final PaymentRepo paymentRepo;
    private final InvoiceRepo invoiceRepo;

    // --- SERVICES ---
    private final AuthService authService;
    private final StaffService staffService;
    private final StudentService studentService;
    private final CourseService courseService;
    private final ClasService classService;
    private final TeacherService teacherService;
    private final BranchService branchService;
    private final RoomService roomService;
    private final ScheduleService scheduleService;
    private final AttendanceService attendanceService;
    private final NotificationService notificationService;
    // Services bổ sung
    private final EnrollmentService enrollmentService;
    private final PromotionService promotionService;
    private final CertificationService certificationService;
    private final ReportService reportService;
    private final StudentPaymentService studentPaymentService;

    private ServiceFactory() {
        this.txManager = new TransactionManager();

        // 1. Khởi tạo tất cả Repositories
        this.userAccountRepo = new UserAccountRepoImpl();
        this.staffRepo = new StaffRepoImpl();
        this.studentRepo = new StudentRepoImpl();
        this.courseRepo = new CourseRepoImpl();
        this.classRepo = new ClasRepoImpl();
        this.teacherRepo = new TeacherRepoImpl();
        this.branchRepo = new BranchRepoImpl();
        this.roomRepo = new RoomRepoImpl();
        this.scheduleRepo = new ScheduleRepoImpl();
        this.attendanceRepo = new AttendanceRepoImpl();
        this.enrollmentRepo = new EnrollmentRepoImpl();
        this.notificationRepo = new NotificationRepoImpl();
        this.placementTestRepo = new PlacementTestRepoImpl();
        this.promotionRepo = new PromotionRepoImpl();
        this.certificateRepo = new CertificateRepoImpl();
        this.resultRepo = new ResultRepoImpl();
        this.paymentRepo = new PaymentRepoImpl();
        this.invoiceRepo = new InvoiceRepoImpl();

        // 2. Khởi tạo Services cơ bản
        this.authService = new AuthServiceImpl(userAccountRepo, txManager);
        this.staffService = new StaffServiceImpl(staffRepo, userAccountRepo, txManager);
        this.studentService = new StudentServiceImpl(studentRepo, userAccountRepo, txManager);
        this.courseService = new CourseServiceImpl(courseRepo, txManager);
        this.classService = new ClasServiceImpl(classRepo, txManager);
        this.teacherService = new TeacherServiceImpl(teacherRepo, userAccountRepo, txManager);
        this.branchService = new BranchServiceImpl(branchRepo, txManager);
        this.roomService = new RoomServiceImpl(roomRepo, txManager);
        this.scheduleService = new ScheduleServiceImpl(scheduleRepo, attendanceRepo, enrollmentRepo, txManager);
        this.attendanceService = new AttendanceServiceImpl(attendanceRepo, txManager);
        this.notificationService = new NotificationServiceImpl(notificationRepo, txManager);

        // 3. Khởi tạo Services nâng cao
        this.enrollmentService = new EnrollmentServiceImpl(
                txManager,
                placementTestRepo,
                enrollmentRepo,
                classRepo,
                studentRepo
        );

        this.promotionService = new PromotionServiceImpl(txManager, promotionRepo);

        // CertificationService sử dụng CertificateRepo
        this.certificationService = new CertificationServiceImpl(txManager, resultRepo, certificateRepo);

        // ReportService tổng hợp dữ liệu từ các mảng khác nhau
        this.reportService = new ReportServiceImpl(txManager, paymentRepo, resultRepo);

        this.studentPaymentService = new StudentPaymentServiceImpl(txManager, invoiceRepo, paymentRepo, promotionRepo);
    }

    public static synchronized ServiceFactory getInstance() {
        if (instance == null) {
            instance = new ServiceFactory();
        }
        return instance;
    }

    // --- GETTERS ---
    public AuthService getAuthService() { return authService; }
    public StaffService getStaffService() { return staffService; }
    public StudentService getStudentService() { return studentService; }
    public CourseService getCourseService() { return courseService; }
    public ClasService getClassService() { return classService; }
    public TeacherService getTeacherService() { return teacherService; }
    public BranchService getBranchService() { return branchService; }
    public RoomService getRoomService() { return roomService; }
    public ScheduleService getScheduleService() { return scheduleService; }
    public AttendanceService getAttendanceService() { return attendanceService; }
    public NotificationService getNotificationService() { return notificationService; }

    // Bổ sung Getters cho MainFrame
    public EnrollmentService getEnrollmentService() { return enrollmentService; }
    public PromotionService getPromotionService() { return promotionService; }
    public CertificationService getCertificationService() { return certificationService; }
    public ReportService getReportService() { return reportService; }
    public StudentPaymentService getStudentPaymentService() { return studentPaymentService; }
}