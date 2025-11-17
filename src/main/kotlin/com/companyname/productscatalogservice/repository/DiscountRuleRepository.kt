package com.companyname.productscatalogservice.repository

import com.companyname.productscatalogservice.model.dto.entity.DiscountRuleEntity
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface DiscountRuleRepository : JpaRepository<DiscountRuleEntity, Long> {

    /**
     * Finds the most specific active SKU-suffix discount rule for a given SKU.
     *
     * Matching logic:
     * - Uses SQL `:sku LIKE '%' || sku_suffix` → suffix match (case-sensitive).
     * - Orders by `length(sku_suffix)` DESC so longer (more specific) suffix wins.
     * - Returns only one rule (LIMIT 1).
     */
    @Query(
        value = """
            SELECT * 
            FROM discount_rules
            WHERE scope = 'SKU_SUFFIX'
              AND :sku LIKE '%' || sku_suffix
            ORDER BY length(sku_suffix) DESC
            LIMIT 1
        """,
        nativeQuery = true
    )
    fun findSkuSuffixRule(@Param("sku") sku: String): DiscountRuleEntity?

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM DiscountRuleEntity p WHERE p.id = :id")
    fun findForUpdateById(id: Long): DiscountRuleEntity?

    fun findFirstByCategoryId(categoryId: Long): DiscountRuleEntity?
}
