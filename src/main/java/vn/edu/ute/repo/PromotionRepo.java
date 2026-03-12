package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Promotion;

import java.util.List;

public interface PromotionRepo {
    void save(EntityManager em, Promotion promotion);
    void update(EntityManager em, Promotion promotion);
    void delete(EntityManager em, Long promotionId);
    Promotion findById(EntityManager em, Long promotionId);
    List<Promotion> findAll(EntityManager em);
}
