package com.companyname.productscatalogservice.service

import com.companyname.productscatalogservice.model.dto.CategoryResponse
import com.companyname.productscatalogservice.model.dto.CreateCategoryRequest
import com.companyname.productscatalogservice.model.dto.UpdateCategoryRequest
import com.companyname.productscatalogservice.model.dto.entity.CategoryEntity
import com.companyname.productscatalogservice.exception.Conflict
import com.companyname.productscatalogservice.exception.NotFound
import com.companyname.productscatalogservice.repository.CategoryRepository
import com.companyname.productscatalogservice.repository.ProductRepository
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository,
    private val discountService: DiscountService,
    private val productRepository: ProductRepository,
) {
    @Transactional
    fun create(req: CreateCategoryRequest): CategoryResponse {
        require(!categoryRepository.existsByName(req.name)) { "name '${req.name}' already exists" }
        val categoryEntity = categoryRepository.save(CategoryEntity(name = req.name))
        discountService.recomputeForCategory(categoryEntity.id!!)
        return categoryEntity.toResponse()
    }

    @Transactional(readOnly = true)
    fun get(id: Long): CategoryResponse =
        categoryRepository.findById(id)
            .orElseThrow { NotFound("Category $id not found") }
            .toResponse()

    @Transactional
    fun update(id: Long, req: UpdateCategoryRequest): CategoryResponse {
        val categoryEntity = categoryRepository.findById(id)
            .orElseThrow { NotFound("Category $id not found") }

        // fast check version from frontend form
        val current = categoryEntity.version ?: 0
        require(req.version == current) {
            "Stale version: expected=$current, got=${req.version}"
        }

        req.name?.let {
            if (it != categoryEntity.name && categoryRepository.existsByName(it))
                throw IllegalArgumentException("name '$it' already exists")
            categoryEntity.name = it
        }
        req.name?.let { categoryEntity.name = it }

        val saved = try {
            categoryRepository.saveAndFlush(categoryEntity)
        } catch (ex: ObjectOptimisticLockingFailureException) {
            //someone already applied changes to this entity
            throw Conflict("Product $id was modified by someone else")
        }

        return saved.toResponse()
    }

    @Transactional
    fun delete(id: Long): CategoryResponse {
        val categoryEntity = categoryRepository.findForUpdateById(id)
            ?: throw NotFound("Category $id not found")
        categoryRepository.delete(categoryEntity)
        productRepository.clearDiscountsByCategoryId(categoryEntity.id!!)
        return categoryEntity.toResponse()
    }

    @Transactional(readOnly = true)
    fun list(
    ): List<CategoryResponse> {
        val data = categoryRepository.findAll()
        return data.map { it.toResponse() }
    }

    private fun CategoryEntity.toResponse() =
        CategoryResponse(
            id = requireNotNull(id),
            name = name,
            version = this.version!!
        )
}