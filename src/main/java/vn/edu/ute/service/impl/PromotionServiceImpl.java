package vn.edu.ute.service.impl;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.Promotion;
import vn.edu.ute.repo.PromotionRepo;
import vn.edu.ute.service.PromotionService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class PromotionServiceImpl implements PromotionService {
    private final TransactionManager txManager;
    private final PromotionRepo promotionRepo;

    // DI
    public PromotionServiceImpl(TransactionManager txManager, PromotionRepo promotionRepo) {
        this.txManager = txManager;
        this.promotionRepo = promotionRepo;
    }
    // Kiểm tra dữ liệu dùng chung
    private void validatePromotion(Promotion p) throws Exception {
        if (p.getPromoName() == null || p.getPromoName().trim().isEmpty())
            throw new Exception("Tên khuyến mãi không được để trống!");
        if (p.getDiscountValue() == null || p.getDiscountValue().doubleValue() <= 0)
            throw new Exception("Giá trị giảm phải lớn hơn 0!");
        if (p.getStartDate() != null && p.getEndDate() != null && p.getStartDate().isAfter(p.getEndDate()))
            throw new Exception("Ngày bắt đầu không được lớn hơn ngày kết thúc!");
    }

    @Override
    public void addPromotion(Promotion promotion) throws Exception {
        validatePromotion(promotion);
        txManager.runInTransaction(em -> {
            promotionRepo.save(em, promotion);
            return null;
        });
    }

    @Override
    public void updatePromotion(Promotion promotion) throws Exception {
        validatePromotion(promotion);
        txManager.runInTransaction(em -> {
            Promotion existing = promotionRepo.findById(em, promotion.getPromotionId());
            if (existing == null) throw new Exception("Không tìm thấy chương trình khuyến mãi!");
            promotionRepo.update(em, promotion);
            return null;
        });
    }

    @Override
    public void deletePromotion(Long promotionId) throws Exception {
        txManager.runInTransaction(em -> {
            promotionRepo.delete(em, promotionId);
            return null;
        });
    }

    @Override
    public List<Promotion> getAllPromotions() throws Exception {
        return txManager.runInTransaction(em -> promotionRepo.findAll(em));
    }

    @Override
    public List<Promotion> getValidActivePromotions() throws Exception {
        return txManager.runInTransaction(em -> {
            List<Promotion> allPromotions = promotionRepo.findAll(em);
            LocalDate today = LocalDate.now();

            // LAMBDA STREAM ĐỂ LỌC KHUYẾN MÃI HỢP LỆ
            return allPromotions.stream()
                    // 1. Phải đang Active
                    .filter(p -> "ACTIVE".equalsIgnoreCase(p.getStatus().name()))
                    // 2. Nếu có Ngày bắt đầu thì hôm nay phải >= Ngày bắt đầu
                    .filter(p -> p.getStartDate() == null || !today.isBefore(p.getStartDate()))
                    // 3. Nếu có Ngày kết thúc thì hôm nay phải <= Ngày kết thúc
                    .filter(p -> p.getEndDate() == null || !today.isAfter(p.getEndDate()))
                    .collect(Collectors.toList());
        });
    }
}
