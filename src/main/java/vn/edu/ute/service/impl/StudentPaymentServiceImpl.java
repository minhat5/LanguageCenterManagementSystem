package vn.edu.ute.service.impl;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.common.enumeration.DiscountType;
import vn.edu.ute.common.enumeration.InvoiceStatus;
import vn.edu.ute.common.enumeration.PaymentStatus;
import vn.edu.ute.model.Invoice;
import vn.edu.ute.model.Payment;
import vn.edu.ute.model.Promotion;
import vn.edu.ute.repo.InvoiceRepo;
import vn.edu.ute.repo.PaymentRepo;
import vn.edu.ute.repo.PromotionRepo;
import vn.edu.ute.service.StudentPaymentService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class StudentPaymentServiceImpl implements StudentPaymentService {
    private final TransactionManager txManager;
    private final InvoiceRepo invoiceRepo;
    private final PaymentRepo paymentRepo;
    private final PromotionRepo promotionRepo;

    public StudentPaymentServiceImpl(TransactionManager txManager, InvoiceRepo invoiceRepo, PaymentRepo paymentRepo, PromotionRepo promotionRepo) {
        this.txManager = txManager;
        this.invoiceRepo = invoiceRepo;
        this.paymentRepo = paymentRepo;
        this.promotionRepo = promotionRepo;
    }

    @Override
    public List<Invoice> getMyInvoices(Long studentId) throws Exception {
        return txManager.runInTransaction(em -> invoiceRepo.findByStudentId(em, studentId));
    }

    @Override
    public void applyPromotion(Long invoiceId, String promoCode) throws Exception {
        txManager.runInTransaction(em -> {
            Invoice invoice = invoiceRepo.findById(em, invoiceId);
            if (invoice == null) throw new Exception("Không tìm thấy hóa đơn!");
            if (invoice.getStatus() == InvoiceStatus.Paid) throw new Exception("Hóa đơn đã được thanh toán, không thể áp dụng mã!");
            if (invoice.getPromotion() != null) throw new Exception("Hóa đơn này đã được áp dụng mã khuyến mãi rồi!");

            Promotion promo = promotionRepo.findByPromoName(em, promoCode);
            if (promo == null || !"ACTIVE".equalsIgnoreCase(promo.getStatus().name())) {
                throw new Exception("Mã khuyến mãi không tồn tại hoặc không còn hoạt động!");
            }

            // Kiểm tra tời hạn
            LocalDate today = LocalDate.now();
            if ((promo.getStartDate() != null && today.isBefore(promo.getStartDate())) ||
                    (promo.getEndDate() != null && today.isAfter(promo.getEndDate()))) {
                throw new Exception("Mã khuyến mãi không nằm trong thời gian áp dụng!");
            }

            // Tính toán số tiền mới
            BigDecimal currentTotal = invoice.getTotalAmount();
            BigDecimal newTotal = currentTotal;

            if (promo.getDiscountType() == DiscountType.Amount) {
                newTotal = currentTotal.subtract(promo.getDiscountValue());
            } else if (promo.getDiscountType() == DiscountType.Percent) {
                BigDecimal discountAmt = currentTotal.multiply(promo.getDiscountValue()).divide(new BigDecimal("100"));
                newTotal = currentTotal.subtract(discountAmt);
            }

            if (newTotal.compareTo(BigDecimal.ZERO) < 0) newTotal = BigDecimal.ZERO;

            invoice.setPromotion(promo);
            invoice.setTotalAmount(newTotal);
            invoiceRepo.update(em, invoice);
            return null;
        });
    }

    @Override
    public void payInvoice(Long invoiceId, vn.edu.ute.common.enumeration.PaymentMethod method) throws Exception {
        txManager.runInTransaction(em -> {
            Invoice invoice = invoiceRepo.findById(em, invoiceId);
            if (invoice == null) throw new Exception("Không tìm thấy hóa đơn!");
            if (invoice.getStatus() == InvoiceStatus.Paid) throw new Exception("Hóa đơn này đã được thanh toán!");

            // Tạo Giao dịch thanh toán (Payment)
            Payment payment = new Payment();
            payment.setStudent(invoice.getStudent());
            payment.setInvoice(invoice);
            payment.setAmount(invoice.getTotalAmount());
            payment.setPaymentDate(LocalDateTime.now());
            payment.setPaymentMethod(method);
            payment.setStatus(PaymentStatus.Completed);
            payment.setReferenceCode("STU-" + System.currentTimeMillis());

            paymentRepo.save(em, payment);

            invoice.setStatus(InvoiceStatus.Paid);
            invoiceRepo.update(em, invoice);
            return null;
        });
    }
}