package vn.edu.ute.service.impl;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.Certificate;
import vn.edu.ute.model.Result;
import vn.edu.ute.repo.CertificateRepo;
import vn.edu.ute.repo.ResultRepo;
import vn.edu.ute.service.CertificationService;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class CertificationServiceImpl implements CertificationService {
    private final TransactionManager txManager;
    private final ResultRepo resultRepo;
    private final CertificateRepo certRepo;

    public CertificationServiceImpl(TransactionManager txManager, ResultRepo resultRepo, CertificateRepo certRepo) {
        this.txManager = txManager;
        this.resultRepo = resultRepo;
        this.certRepo = certRepo;
    }
;

    @Override
    public List<Result> getAllResults() throws Exception {
        return txManager.runInTransaction(resultRepo::findAll);
    }

    @Override
    public void saveResult(Result result) throws Exception {
        if (result.getScore() != null) {
            double score = result.getScore().doubleValue();
            if (score >= 8.5) result.setGrade("Xuất sắc");
            else if (score >= 7.0) result.setGrade("Giỏi");
            else if (score >= 5.0) result.setGrade("Khá");
            else result.setGrade("Không đạt");
        }

        txManager.runInTransaction(em -> {
            if (result.getResultId() == null) {
                // Kiểm tra xem HV này đã có điểm môn này chưa
                boolean exists = resultRepo.findAll(em).stream()
                        .anyMatch(x -> x.getStudent().getStudentId().equals(result.getStudent().getStudentId())
                                && x.getClas().getClassId().equals(result.getClas().getClassId()));
                if (exists) throw new Exception("Học viên này đã có điểm trong Lớp này rồi!");
                resultRepo.save(em, result);
            } else {
                resultRepo.update(em, result);
            }
            return null;
        });
    }

    @Override
    public void deleteResult(Long resultId) throws Exception {
        txManager.runInTransaction(em ->{
            resultRepo.delete(em, resultId);
            return null;
        });
    }

    @Override
    public List<Certificate> getAllCertificates() throws Exception {
        return txManager.runInTransaction(certRepo::findAll);
    }

    @Override
    public void issueCertificate(Certificate certificate) throws Exception {
        txManager.runInTransaction(em -> {
           boolean duplicateSerial = certRepo.findAll(em).stream()
                   .anyMatch(x -> x.getSerialNo().equalsIgnoreCase(certificate.getSerialNo()));
            if (duplicateSerial) throw new Exception("Số Serial Chứng chỉ đã tồn tại!");
            certRepo.save(em, certificate);
            return null;
        });
    }

    @Override
    public void deleteCertificate(Long certId) throws Exception {
        txManager.runInTransaction(em ->{
            certRepo.delete(em, certId);
            return null;
        });
    }

    @Override
    public List<Result> getEligibleForCertificate() throws Exception {
        return txManager.runInTransaction(em -> {
            List<Result> allResults = resultRepo.findAll(em);
            List<Certificate> allCerts = certRepo.findAll(em);

            return allResults.stream()
                    // Lấy những người có điểm >= 5.0
                    .filter(r -> r.getScore() != null && r.getScore().compareTo(new BigDecimal("5.0")) >= 0)
                    //Lọc ra những người chưa có chứng chỉ cho lớp đó
                    .filter(r -> allCerts.stream().noneMatch(c ->
                            c.getStudent().getStudentId().equals(r.getStudent().getStudentId()) &&
                                    c.getClas().getClassId().equals(r.getClas().getClassId())
                    ))
                    .collect(Collectors.toList());
        });
    }
}
