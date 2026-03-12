package vn.edu.ute.service.impl;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.common.enumeration.DiscountType;
import vn.edu.ute.common.enumeration.InvoiceStatus;
import vn.edu.ute.common.enumeration.PaymentMethod;
import vn.edu.ute.common.enumeration.PaymentStatus;
import vn.edu.ute.model.*;
import vn.edu.ute.repo.*;
import vn.edu.ute.service.AccountingService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AccountingServiceImpl implements AccountingService {

    private final TransactionManager txManager;
    private final InvoiceRepo invoiceRepo;
    private final PaymentRepo paymentRepo;
    private final EnrollmentRepo enrollmentRepo;
    private final PromotionRepo promotionRepo;
    private final StudentRepo studentRepo;

    public AccountingServiceImpl(TransactionManager txManager, InvoiceRepo invoiceRepo, PaymentRepo paymentRepo,
                                 EnrollmentRepo enrollmentRepo, PromotionRepo promotionRepo, StudentRepo studentRepo) {
        this.txManager = txManager;
        this.invoiceRepo = invoiceRepo;
        this.paymentRepo = paymentRepo;
        this.enrollmentRepo = enrollmentRepo;
        this.promotionRepo = promotionRepo;
        this.studentRepo = studentRepo;
    }

    @Override
    public List<Invoice> getAllInvoices() throws Exception {
        return txManager.runInTransaction(invoiceRepo::findAll);
    }

    @Override
    public List<Payment> getAllPayments() throws Exception {
        return txManager.runInTransaction(paymentRepo::findAll);
    }

    @Override
    public void generateInvoice(Long studentId, Long enrollmentId, Long promotionId, String note) throws Exception {
        txManager.runInTransaction(em -> {
            Student student = studentRepo.findById(em, studentId).orElse(null);;
            Enrollment enrollment = em.find(Enrollment.class, enrollmentId);
            if (student == null || enrollment == null) throw new Exception("Không tìm thấy Học viên hoặc Lớp ghi danh!");

            // 1. Lấy học phí gốc từ Khóa học (Course)
            BigDecimal baseFee = enrollment.getClas().getCourse().getFee();
            BigDecimal finalAmount = baseFee;
            Promotion promotion = null;

            // 2. Tính toán Khuyến mãi (nếu có)
            if (promotionId != null) {
                promotion = promotionRepo.findById(em, promotionId);
                if (promotion != null && "ACTIVE".equalsIgnoreCase(promotion.getStatus().name())) {
                    if (promotion.getDiscountType() == DiscountType.Amount) {
                        finalAmount = baseFee.subtract(promotion.getDiscountValue());
                    } else if (promotion.getDiscountType() == DiscountType.Percent) {
                        BigDecimal discountAmount = baseFee.multiply(promotion.getDiscountValue()).divide(new BigDecimal("100"));
                        finalAmount = baseFee.subtract(discountAmount);
                    }
                }
            }
            if (finalAmount.compareTo(BigDecimal.ZERO) < 0) finalAmount = BigDecimal.ZERO;

            // 3. Tạo hóa đơn
            Invoice invoice = new Invoice();
            invoice.setStudent(student);
            invoice.setPromotion(promotion);
            invoice.setTotalAmount(finalAmount);
            invoice.setIssueDate(LocalDate.now());
            invoice.setStatus(InvoiceStatus.Issued); // Chờ thanh toán
            invoice.setNote(note);

            invoiceRepo.save(em, invoice);
            return null;
        });
    }

    @Override
    public void processPayment(Long invoiceId, BigDecimal amount, PaymentMethod method, String refCode) throws Exception {
        txManager.runInTransaction(em -> {
            Invoice invoice = invoiceRepo.findById(em, invoiceId);
            if (invoice == null) throw new Exception("Không tìm thấy Hóa đơn!");
            if (invoice.getStatus() == InvoiceStatus.Paid) throw new Exception("Hóa đơn này đã được thanh toán đầy đủ!");
            if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new Exception("Số tiền thanh toán phải lớn hơn 0!");

            // 1. Tạo bản ghi Payment
            Payment payment = new Payment();
            payment.setStudent(invoice.getStudent());
            payment.setInvoice(invoice);
            payment.setAmount(amount);
            payment.setPaymentDate(LocalDateTime.now());
            payment.setPaymentMethod(method);
            payment.setStatus(PaymentStatus.Completed);
            payment.setReferenceCode(refCode);

            paymentRepo.save(em, payment);

            // 2. LAMBDAS: Tính tổng số tiền đã thanh toán cho Hóa đơn này
            List<Payment> allPayments = paymentRepo.findAll(em);
            BigDecimal totalPaid = allPayments.stream()
                    .filter(p -> p.getInvoice() != null && p.getInvoice().getInvoiceId().equals(invoiceId))
                    .filter(p -> p.getStatus() == PaymentStatus.Completed)
                    .map(Payment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Cộng thêm số tiền vừa đóng ở Transaction hiện tại
            totalPaid = totalPaid.add(amount);

            // 3. Cập nhật trạng thái Hóa đơn nếu đã đóng đủ
            if (totalPaid.compareTo(invoice.getTotalAmount()) >= 0) {
                invoice.setStatus(InvoiceStatus.Paid);
                invoiceRepo.update(em, invoice);
            }

            return null;
        });
    }
}