package vn.edu.ute.service;

import vn.edu.ute.common.enumeration.PaymentMethod;

import java.math.BigDecimal;
import java.util.Map;

public interface ReportService {
    BigDecimal getTotalRevenue() throws Exception;
    Map<String, BigDecimal> getRevenueByMonth(int year) throws Exception;
    Map<PaymentMethod, BigDecimal> getRevenueByPaymentMethod() throws Exception;

    Map<String, Long> getAcademicPerformanceStats() throws Exception;
    double getPassRate() throws Exception;
}