package vn.edu.ute.service;

import vn.edu.ute.common.enumeration.PaymentMethod;
import vn.edu.ute.model.Invoice;
import vn.edu.ute.model.Payment;

import java.math.BigDecimal;
import java.util.List;

public interface AccountingService {
    List<Invoice> getAllInvoices() throws Exception;
    List<Payment> getAllPayments() throws Exception;

    void generateInvoice(Long studentId, Long enrollmentId, Long promotionId, String note) throws Exception;

    void processPayment(Long invoiceId, BigDecimal amount, PaymentMethod method, String refCode) throws Exception;
}
