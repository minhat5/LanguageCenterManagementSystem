package vn.edu.ute.service;

import vn.edu.ute.model.Promotion;

import java.util.List;

public interface PromotionService {
    void addPromotion(Promotion promotion) throws Exception;
    void updatePromotion(Promotion promotion) throws Exception;
    void deletePromotion(Long promotionId) throws Exception;
    List<Promotion> getAllPromotions() throws Exception;
    List<Promotion> getValidActivePromotions() throws Exception;
}
