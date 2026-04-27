package com.example.aispring.repository;

import com.example.aispring.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Product entity
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String nameQuery, String descriptionQuery);
    
    List<Product> findByCategory(String category);
    
    List<Product> findByPriceLessThanEqual(Double maxPrice);
    
    @Query("SELECT p FROM Product p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Product> searchProducts(String query);
}
