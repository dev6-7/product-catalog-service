package com.companyname.productscatalogservice.service

import com.companyname.productscatalogservice.model.dto.CreateProductRequest
import com.companyname.productscatalogservice.model.dto.ProductResponse
import com.companyname.productscatalogservice.model.dto.UpdateProductRequest
import com.companyname.productscatalogservice.model.dto.entity.ProductEntity
import com.companyname.productscatalogservice.exception.Conflict
import com.companyname.productscatalogservice.exception.NotFound
import com.companyname.productscatalogservice.repository.CategoryRepository
import com.companyname.productscatalogservice.repository.ProductRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val categoryRepo: CategoryRepository,
    private val discountService: DiscountService
) {
    @Transactional
    fun create(req: CreateProductRequest): ProductResponse {
        val cat = req.categoryId
            ?.let {
                categoryRepo.findById(it)
                    .orElse(null)
            }

        val productEntity = ProductEntity(
            sku = req.sku,
            price = req.price.setScale(2, RoundingMode.HALF_UP),
            description = req.description,
            category = cat,
            categoryName = cat?.name,
        )
        updateDiscountPrice(productEntity)

        return productRepository.save(productEntity).toResponse()
    }

    @Transactional(readOnly = true)
    fun get(id: Long): ProductResponse =
        productRepository.findById(id).orElseThrow { NotFound("Product $id not found") }.toResponse()

    @Transactional
    fun update(id: Long, req: UpdateProductRequest): ProductResponse {
        val cat = req.categoryId?.let { categoryRepo.findById(it).orElse(null) }

        val productEntity = productRepository.findById(id)
            .orElseThrow { NotFound("Product $id not found") }
        productEntity.price = req.price.setScale(2, RoundingMode.HALF_UP)
        productEntity.description = req.description
        productEntity.category = cat ?: productEntity.category
        productEntity.categoryName = cat?.name ?: productEntity.categoryName
        if (productEntity.price != req.price) {
            updateDiscountPrice(productEntity)
        }
        val updatedEntity = productRepository.save(productEntity)
        return updatedEntity.toResponse()
    }

    @Transactional
    fun updateOptimistic(id: Long, req: UpdateProductRequest): ProductResponse {
        val cat = req.categoryId?.let { categoryRepo.findById(it).orElse(null) }
        val productEntity = productRepository.findById(id)
            .orElseThrow { NotFound("Product $id not found") }

        // fast check version from frontend form
        val current = productEntity.version ?: 0
        require(req.version == current) {
            "Stale version: expected=$current, got=${req.version}"
        }

        req.price.let { productEntity.price = it }
        req.description.let { productEntity.description = it }
        if (req.categoryId != null) {
            productEntity.category = cat ?: productEntity.category
            productEntity.categoryName = cat?.name
        }

        val saved = try {
            productRepository.saveAndFlush(productEntity)
        } catch (ex: ObjectOptimisticLockingFailureException) {
            //someone already applied changes to this entity
            throw Conflict("Product $id was modified by someone else")
        }

        return saved.toResponse()
    }

    @Transactional
    fun delete(id: Long): ProductResponse {
        val productEntity = productRepository.findById(id).orElseThrow { NotFound("Product $id not found") }
        productRepository.delete(productEntity)
        return productEntity.toResponse()
    }

    @Transactional(readOnly = true)
    fun list(
        categoryName: String?,
        categoryId: Long?,
        page: Int,
        size: Int,
        sortParams: List<String>?,
        defaultDirection: Sort.Direction
    ): Page<ProductResponse> {
        val pageable = PageRequest.of(
            page,
            size,
            buildSort(sortParams, defaultDirection)
        )
        val data = when {
            categoryId != null -> productRepository.findByCategory_Id(categoryId, pageable)
            !categoryName.isNullOrBlank() -> productRepository.findByCategoryNameIgnoreCase(categoryName, pageable)
            else -> productRepository.findAll(pageable)
        }

        return data.map { it.toResponse() }
    }

    private fun buildSort(sortParams: List<String>?, defaultDir: Sort.Direction): Sort {
        fun mapField(api: String) = when (api.lowercase()) {
            "sku" -> "sku"
            "price", "price_eur", "priceeur" -> "price"
            "description" -> "description"
            "category", "categoryname", "category_name" -> "categoryName"
            else -> null
        }

        fun parseToken(token: String): Pair<String, Sort.Direction?> {
            if (token.startsWith("-")) return token.drop(1) to Sort.Direction.DESC
            if (token.startsWith("+")) return token.drop(1) to Sort.Direction.ASC
            val parts = token.split(':', limit = 2)
            return if (parts.size == 2)
                parts[0] to when (parts[1].lowercase()) {
                    "asc" -> Sort.Direction.ASC;
                    "desc" -> Sort.Direction.DESC;
                    else -> null
                }
            else token to null
        }

        val tokens = (sortParams ?: emptyList())
            .flatMap { it.split(',') }
            .map { it.trim() }
            .filter { it.isNotEmpty() }
        val orders = mutableListOf<Sort.Order>()
        val seen = mutableSetOf<String>()
        for (raw in tokens) {
            val (field, explicit) = parseToken(raw)
            val prop = mapField(field) ?: continue
            if (!seen.add(prop)) continue
            orders += Sort.Order.by(prop).with(explicit ?: defaultDir)
        }
        orders += Sort.Order.by("id").with(Sort.Direction.ASC)
        return Sort.by(orders)
    }

    private fun ProductEntity.toResponse(): ProductResponse {
        val price = price.setScale(2, RoundingMode.HALF_UP)
        return ProductResponse(
            id = requireNotNull(id),
            sku = sku,
            description = description,
            categoryId = category?.id,
            categoryName = categoryName,
            price = price,
            discount = discount ?: 0,
            discountPrice = discountPrice ?: BigDecimal.ZERO,
            version = this.version!!
        )
    }

    private fun updateDiscountPrice(productEntity: ProductEntity) {
        val discountResult = discountService.applyDiscount(
            productEntity.category?.id,
            productEntity.sku,
            productEntity.price
        )
        productEntity.discountPrice = discountResult.discountPrice
        productEntity.discount = discountResult.discount
    }
}