package vn.edu.ute.service;

import vn.edu.ute.common.enumeration.PaymentMethod;
import vn.edu.ute.model.Invoice;
import java.util.List;

public interface StudentPaymentService {
    List<Invoice> getMyInvoices(Long studentId) throws Exception;
    void applyPromotion(Long invoiceId, String promoCode) throws Exception;
    void payInvoice(Long invoiceId, PaymentMethod method) throws Exception;
}