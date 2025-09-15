package com.companyname.productscatalogservice.repository

import com.companyname.productscatalogservice.model.dto.entity.ProductEntity
import jakarta.persistence.LockModeType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ProductRepository : JpaRepository<ProductEntity, Long> {

    fun findByCategoryNameIgnoreCase(
        name: String, pageable: Pageable,
    ): Page<ProductEntity>

    fun findByCategory_Id(
        categoryId: Long, pageable: Pageable
    ): Page<ProductEntity>

    @Query("SELECT p FROM ProductEntity p WHERE p.sku LIKE %:suffix")
    fun findBySkuEndingWith(
        @Param("suffix") suffix: String, pageable: Pageable
    ): Page<ProductEntity>

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM ProductEntity p WHERE p.id = :id")
    fun findForUpdateById(id: Long): ProductEntity?

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
        value = """
            UPDATE products
            SET discount = NULL,
                discount_price = NULL,
                updated_at = NOW()
            WHERE category_id = :categoryId
        """, nativeQuery = true)
    fun clearDiscountsByCategoryId(@Param("categoryId") categoryId: Long): Int

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
        value = """
            UPDATE products
            SET discount = NULL,
                discount_price = NULL,
                updated_at = NOW()
            WHERE sku LIKE '%' || :suffix
        """, nativeQuery = true)
    fun clearDiscountsBySkuSuffix(@Param("suffix") suffix: String): Int
}
