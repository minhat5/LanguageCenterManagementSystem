package vn.edu.ute.repo.impl;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Promotion;
import vn.edu.ute.repo.PromotionRepo;

import java.util.List;

public class PromotionRepoImpl implements PromotionRepo {

    @Override
    public void save(EntityManager em, Promotion promotion) {
        em.persist(promotion);
    }

    @Override
    public void update(EntityManager em, Promotion promotion) {
        em.merge(promotion);
    }

    @Override
    public void delete(EntityManager em, Long promotionId) {
        Promotion p = em.find(Promotion.class, promotionId);
        if (p != null){
            em.remove(p);
        }
    }

    @Override
    public Promotion findById(EntityManager em, Long promotionId) {
        return em.find(Promotion.class, promotionId);
    }

    @Override
    public List<Promotion> findAll(EntityManager em) {
        return em.createQuery("SELECT p FROM Promotion p ORDER BY p.promotionId DESC", Promotion.class).getResultList();
    }

    @Override
    public Promotion findByPromoName(EntityManager em, String promoName) {
        try {
            String jpql = "SELECT p FROM Promotion p WHERE p.promoName = :name";
            return em.createQuery(jpql, Promotion.class).setParameter("name", promoName).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
