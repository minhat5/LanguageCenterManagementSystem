package vn.edu.ute.service;

import vn.edu.ute.model.Certificate;
import vn.edu.ute.model.Result;

import java.util.List;

public interface CertificationService {
    List<Result> getAllResults() throws Exception;
    void saveResult(Result result) throws Exception;
    void deleteResult(Long resultId) throws Exception;

    List<Certificate> getAllCertificates() throws Exception;
    void issueCertificate(Certificate certificate) throws Exception;
    void deleteCertificate(Long certId) throws Exception;

    List<Result> getEligibleForCertificate() throws Exception;
}
