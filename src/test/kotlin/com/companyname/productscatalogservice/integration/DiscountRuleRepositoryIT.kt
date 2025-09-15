package com.companyname.productscatalogservice.integration

import com.companyname.productscatalogservice.model.dto.entity.DiscountRuleEntity
import com.companyname.productscatalogservice.model.dto.entity.DiscountScope
import com.companyname.productscatalogservice.repository.CategoryRepository
import com.companyname.productscatalogservice.repository.DiscountRuleRepository
import com.companyname.productscatalogservice.repository.ProductRepository
import com.companyname.productscatalogservice.service.DiscountService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.jdbc.Sql
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
class DiscountRuleRepositoryIT(
    private val discountRuleRepository: DiscountRuleRepository,
    private val categoryRepository: CategoryRepository,
    private val discountService: DiscountService,
    private val productRepository: ProductRepository,
) : BaseDbIntegrationTest() {

    @Test
    @Transactional
    @Sql("file:src/test/resources/sql/test-category-data.sql")
    fun `longest matching suffix wins (005 beats 5)`() {
        // Ensure a longer (more specific) rule exists in addition to any seeded '5'
        val rule005 = DiscountRuleEntity(
            scope = DiscountScope.SKU_SUFFIX,
            categoryId = null,
            skuSuffix = "005",
            percent = 15L
        )
        discountRuleRepository.save(rule005)

        val result = discountRuleRepository.findSkuSuffixRule("SKU0005")

        assertThat(result).isNotNull
        assertThat(result!!.scope).isEqualTo(DiscountScope.SKU_SUFFIX)
        assertThat(result.skuSuffix).isEqualTo("005")
        assertThat(result.percent).isEqualTo(15L)
    }

    @Test
    @Transactional
    @Sql("file:src/test/resources/sql/test-category-data.sql")
    fun `returns null when no suffix matches`() {
        val result = discountRuleRepository.findSkuSuffixRule("ABC123")
        assertThat(result).isNull()
    }

    @Test
    @Transactional
    @Sql("file:src/test/resources/sql/test-category-data.sql")
    fun `returns category rule (Electronics)`() {
        val electronicsId = categoryRepository.findByName("Electronics")
        val result = discountRuleRepository.findFirstByCategoryId(electronicsId.id!!)

        assertThat(result).isNotNull
        assertThat(result!!.scope).isEqualTo(DiscountScope.CATEGORY)
        assertThat(result.categoryId).isEqualTo(electronicsId.id!!)
        assertThat(result.percent).isEqualTo(15L)
    }

    @Test
    @Transactional
    @Sql("file:src/test/resources/sql/test-category-data.sql")
    fun `check discount is cleared from product`() {
        val electronicsId = categoryRepository.findByName("Electronics")
        val result = discountRuleRepository.findFirstByCategoryId(electronicsId.id!!)
        val categoryId = result!!.categoryId!!

        assertThat(result).isNotNull
        assertThat(result.scope).isEqualTo(DiscountScope.CATEGORY)
        assertThat(result.categoryId).isEqualTo(electronicsId.id!!)
        assertThat(result.percent).isEqualTo(15L)

        discountService.delete(result!!.id!!)
        val page = productRepository.findByCategory_Id(categoryId, PageRequest.of(0, 100))
        assertThat(page.content).allMatch { it.discount == null && it.discountPrice == null }
    }
}