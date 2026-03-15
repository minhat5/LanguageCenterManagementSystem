package vn.edu.ute.service.impl;

import vn.edu.ute.common.enumeration.PaymentMethod;
import vn.edu.ute.common.enumeration.PaymentStatus;
import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.Payment;
import vn.edu.ute.model.Result;
import vn.edu.ute.repo.PaymentRepo;
import vn.edu.ute.repo.ResultRepo;
import vn.edu.ute.service.ReportService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportServiceImpl implements ReportService {

    private final TransactionManager txManager;
    private final PaymentRepo paymentRepo;
    private final ResultRepo resultRepo;

    public ReportServiceImpl(TransactionManager txManager, PaymentRepo paymentRepo, ResultRepo resultRepo) {
        this.txManager = txManager;
        this.paymentRepo = paymentRepo;
        this.resultRepo = resultRepo;
    }

    @Override
    public BigDecimal getTotalRevenue() throws Exception {
        return txManager.runInTransaction(em -> {
            List<Payment> payments = paymentRepo.findAll(em);

            return payments.stream()
                    //Chỉ lấy những giao dịch đã hoàn tất
                    .filter(p -> p.getStatus() == PaymentStatus.Completed)
                    //Chuyển từ Object Payment sang kiểu BigDecimal (Amount)
                    .map(Payment::getAmount)
                    //Cộng dồn tất cả các giá trị lại, giá trị khởi tạo là ZERO
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        });
    }

    @Override
    public Map<String, BigDecimal> getRevenueByMonth(int year) throws Exception {
        return txManager.runInTransaction(em -> {
            List<Payment> payments = paymentRepo.findAll(em);

            return payments.stream()
                    //Lọc giao dịch thành công và trong năm được chỉ định
                    .filter(p -> p.getStatus() == PaymentStatus.Completed && p.getPaymentDate().getYear() == year)
                    //Nhóm theo tháng và tính tổng tiền mỗi tháng
                    .collect(Collectors.groupingBy(
                            p -> "Tháng " + p.getPaymentDate().getMonthValue(), // Key: String "Tháng X"
                            Collectors.reducing(BigDecimal.ZERO, Payment::getAmount, BigDecimal::add) // Value: Tổng tiền
                    ));
        });
    }

    @Override
    public Map<PaymentMethod, BigDecimal> getRevenueByPaymentMethod() throws Exception {
        return txManager.runInTransaction(em -> {
            List<Payment> payments = paymentRepo.findAll(em);

            return payments.stream()
                    .filter(p -> p.getStatus() == PaymentStatus.Completed)
                    //Nhóm theo Phương thức thanh toán (Bank, Cash, Momo...)
                    .collect(Collectors.groupingBy(
                            Payment::getPaymentMethod,
                            Collectors.reducing(BigDecimal.ZERO, Payment::getAmount, BigDecimal::add)
                    ));
        });
    }

    @Override
    public Map<String, Long> getAcademicPerformanceStats() throws Exception {
        return txManager.runInTransaction(em -> {
            List<Result> results = resultRepo.findAll(em);

            return results.stream()
                    //filter Loại bỏ những kết quả chưa được xếp loại
                    .filter(r -> r.getGrade() != null && !r.getGrade().isEmpty())
                    // Nhóm theo xếp loại (Giỏi, Khá, TB...) và Đếm số lượng
                    .collect(Collectors.groupingBy(
                            Result::getGrade,
                            Collectors.counting()
                    ));
        });
    }

    @Override
    public double getPassRate() throws Exception {
        return txManager.runInTransaction(em -> {
            List<Result> results = resultRepo.findAll(em);
            if (results.isEmpty()) return 0.0;

            // Đếm tổng số học viên đạt (Điểm >= 5.0)
            long passCount = results.stream()
                    .filter(r -> r.getScore() != null && r.getScore().compareTo(new BigDecimal("5.0")) >= 0)
                    .count();

            // Tính tỷ lệ %
            return (double) passCount / results.size() * 100;
        });
    }
}