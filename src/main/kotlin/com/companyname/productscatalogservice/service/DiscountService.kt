package com.companyname.productscatalogservice.service

import com.companyname.productscatalogservice.model.dto.CreateDiscountRuleRequest
import com.companyname.productscatalogservice.model.dto.DiscountRuleResponse
import com.companyname.productscatalogservice.model.dto.UpdateDiscountRuleRequest
import com.companyname.productscatalogservice.model.dto.entity.DiscountRuleEntity
import com.companyname.productscatalogservice.model.dto.entity.DiscountScope
import com.companyname.productscatalogservice.model.dto.entity.ProductEntity
import com.companyname.productscatalogservice.exception.NotFound
import com.companyname.productscatalogservice.repository.DiscountRuleRepository
import com.companyname.productscatalogservice.repository.ProductRepository
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class DiscountService(
    private val discountRepo: DiscountRuleRepository,
    private val productRepository: ProductRepository
) {

    fun applyDiscount(categoryId: Long?, productSku: String, productPrice: BigDecimal): DiscountResult {
        val catRule = categoryId?.let(discountRepo::findFirstByCategoryId)
        val skuRule = discountRepo.findSkuSuffixRule(productSku)
        val maxDiscount = listOfNotNull(catRule?.percent, skuRule?.percent).maxOrNull() ?: 0

        val final = productPrice *
                (BigDecimal.ONE - BigDecimal(maxDiscount).divide(BigDecimal("100")))
        return DiscountResult(final, maxDiscount)
    }

    /**
     * Recompute all products in a category.
     */
    @Transactional
    fun recomputeForCategory(categoryId: Long, batch: Int = 500) {
        var page = 0
        while (true) {
            val chunk = productRepository.findByCategory_Id(
                categoryId,
                PageRequest.of(page, batch)
            ).content
            if (chunk.isEmpty()) break
            chunk.forEach {
                val discountResult = applyDiscount(categoryId, it.sku, it.price)
                it.discountPrice = discountResult.discountPrice
                it.discount = discountResult.discount
            }
            productRepository.saveAll(chunk)
            page++
        }
    }

    /**
     * Recompute all products matching a SKU suffix.
     */
    @Transactional
    fun recomputeForSkuSuffix(suffix: String, batch: Int = 500) {
        var page = 0
        while (true) {
            val chunk = productRepository
                .findBySkuEndingWith(suffix, PageRequest.of(page, batch))
                .content
            if (chunk.isEmpty()) break
            chunk.forEach {
                val discountResult = applyDiscount(it.category?.id, it.sku, it.price)
                it.discountPrice = discountResult.discountPrice
                it.discount = discountResult.discount
            }
            productRepository.saveAll(chunk)
            page++
        }
    }

    @Transactional
    fun recomputeForProduct(p: ProductEntity) {
        applyDiscount(p.category?.id, p.sku, p.price)
        productRepository.save(p)
    }

    @Transactional
    fun create(req: CreateDiscountRuleRequest): DiscountRuleResponse {
        validate(req.scope, req.categoryId, req.skuSuffix)
        val saved = discountRepo.save(
            DiscountRuleEntity(
                scope = req.scope,
                categoryId = req.categoryId,
                skuSuffix = req.skuSuffix,
                percent = req.percent
            )
        )
        when (saved.scope) {
            DiscountScope.CATEGORY -> saved.categoryId?.let(::recomputeForCategory)
            DiscountScope.SKU_SUFFIX -> saved.skuSuffix?.let(::recomputeForSkuSuffix)
        }
        return saved.toDto()
    }

    @Transactional
    fun get(id: Long): DiscountRuleResponse =
        discountRepo.findById(id).orElseThrow { NotFound("Discount rule $id not found") }.toDto()

    @Transactional
    fun update(id: Long, req: UpdateDiscountRuleRequest): DiscountRuleResponse {
        val discountEntity = discountRepo.findById(id)
            .orElseThrow { NotFound("Discount rule $id not found") }

        val newScope = req.scope ?: discountEntity.scope
        val newCatId = req.categoryId ?: discountEntity.categoryId
        val newSuffix = req.skuSuffix ?: discountEntity.skuSuffix
        val newPercent = req.percent ?: discountEntity.percent

        validate(newScope, newCatId, newSuffix)

        discountEntity.scope = newScope
        discountEntity.categoryId = newCatId
        discountEntity.skuSuffix = newSuffix
        discountEntity.percent = newPercent

        when (discountEntity.scope) {
            DiscountScope.CATEGORY -> discountEntity.categoryId?.let(::recomputeForCategory)
            DiscountScope.SKU_SUFFIX -> discountEntity.skuSuffix?.let(::recomputeForSkuSuffix)
        }
        return discountRepo.save(discountEntity).toDto()
    }

    @Transactional
    fun delete(id: Long): DiscountRuleResponse {
        val discountEntity = discountRepo.findForUpdateById(id)
            ?: throw NotFound("Discount $id not found")
        discountRepo.delete(discountEntity)

        when (discountEntity.scope) {
            DiscountScope.CATEGORY -> productRepository.clearDiscountsByCategoryId(discountEntity.categoryId!!)
            DiscountScope.SKU_SUFFIX -> productRepository.clearDiscountsBySkuSuffix(discountEntity.skuSuffix!!)
        }

        return discountEntity.toDto()
    }

    @Transactional
    fun list(
        page: Int,
        size: Int,
    ): Page<DiscountRuleResponse> {
        val pageable = PageRequest.of(page, size)
        val data = discountRepo.findAll(pageable)
        return data.map { it.toDto() }
    }

    private fun validate(scope: DiscountScope, categoryId: Long?, skuSuffix: String?) {
        when (scope) {
            DiscountScope.CATEGORY -> {
                require(categoryId != null) { "categoryId is required for CATEGORY scope" }
                require(skuSuffix.isNullOrBlank()) { "skuSuffix must be null/blank for CATEGORY scope" }
            }

            DiscountScope.SKU_SUFFIX -> {
                require(!skuSuffix.isNullOrBlank()) { "skuSuffix is required for SKU_SUFFIX scope" }
                require(categoryId == null) { "categoryId must be null for SKU_SUFFIX scope" }
            }
        }
    }

    private fun DiscountRuleEntity.toDto() = DiscountRuleResponse(
        id = requireNotNull(id),
        scope = scope,
        categoryId = categoryId,
        skuSuffix = skuSuffix,
        percent = percent
    )
}

data class DiscountResult(
    val discountPrice: BigDecimal,
    val discount: Long
)