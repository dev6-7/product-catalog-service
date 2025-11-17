package com.companyname.productscatalogservice.repository

import com.companyname.productscatalogservice.model.dto.entity.CategoryEntity
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query

interface CategoryRepository : JpaRepository<CategoryEntity, Long> {
    fun existsByName(slug: String): Boolean

    fun findByName(name: String): CategoryEntity

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM CategoryEntity p WHERE p.id = :id")
    fun findForUpdateById(id: Long): CategoryEntity?
}