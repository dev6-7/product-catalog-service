package com.companyname.productscatalogservice.integration

import com.companyname.productscatalogservice.repository.CategoryRepository
import com.companyname.productscatalogservice.repository.ProductRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.jdbc.Sql

@SpringBootTest
class ProductRepositoryIT(
    private val repo: ProductRepository,
    private val categoryRepository: CategoryRepository,
    jdbcTemplate: JdbcTemplate
) : BaseDbIntegrationTest(
    jdbcTemplate, tablesToCleanUp = listOf("products", "discount_rules", "categories")
) {

    @Test
    @Sql("file:src/test/resources/sql/test-category-data.sql")
    fun `findByCategory_Id returns all Electronics with pagination`() {
        val category = categoryRepository.findByName("Electronics")
        val page0 = repo.findByCategory_Id(category.id!!, PageRequest.of(0, 5))
        val page1 = repo.findByCategory_Id(category.id!!, PageRequest.of(1, 5))

        assertThat(page0.totalElements).isEqualTo(10)
        assertThat(page0.content).hasSize(5)
        assertThat(page1.content).hasSize(5)

        assertThat(page0.content.map { it.category?.name }.toSet()).containsOnly("Electronics")
        assertThat(page1.content.map { it.category?.name }.toSet()).containsOnly("Electronics")
    }

    @Test
    @Sql("file:src/test/resources/sql/test-category-data.sql")
    fun `findByCategoryNameIgnoreCase matches case-insensitively (Home & Kitchen → 5 items)`() {
        val page = repo.findByCategoryNameIgnoreCase(
            "hOmE & kItChEn",
            PageRequest.of(
                0,
                10,
                Sort.by(Sort.Direction.ASC, "sku")
            )
        )
        assertThat(page.totalElements).isEqualTo(5)
        assertThat(page.content).extracting<String> { it.categoryName ?: "" }
            .containsOnly("Home & Kitchen")
    }

    @Test
    @Sql("file:src/test/resources/sql/test-category-data.sql")
    fun `findBySkuEndingWith finds SKUs ending with '5' (005,015,025) → 3 items`() {
        val page = repo.findBySkuEndingWith(
            "5",
            PageRequest.of(0, 10, Sort.by("sku"))
        )
        val skus = page.content.map { it.sku }
        assertThat(page.totalElements).isEqualTo(3)
        assertThat(skus).containsExactlyInAnyOrder("SKU0005", "SKU0015", "SKU0025")
    }

    @Test
    @Sql("file:src/test/resources/sql/test-category-data.sql")
    fun `Pagination boundaries and stable id tiebreaker`() {
        val category = categoryRepository.findByName("Electronics")
        val size = 5
        val page0 = repo.findByCategory_Id(category.id!!, PageRequest.of(0, size))
        val page1 = repo.findByCategory_Id(category.id!!, PageRequest.of(1, size))
        val page2 = repo.findByCategory_Id(category.id!!, PageRequest.of(2, size))
        assertThat(page0.totalElements).isEqualTo(10)
        assertThat(page0.content.size).isEqualTo(5)
        assertThat(page1.content.size).isEqualTo(5)
        assertThat(page2.content).isEmpty()
    }

    @Test
    @Sql("file:src/test/resources/sql/test-category-data.sql")
    fun `Sorting by description desc within category`() {
        val category = categoryRepository.findByName("Electronics")
        val page = repo.findByCategory_Id(
            category.id!!,
            PageRequest.of(
                0,
                10,
                Sort.by(Sort.Direction.DESC, "description")
            )
        )
        val descriptions = page.content.map { it.description }
        assertThat(descriptions).isSortedAccordingTo(Comparator.reverseOrder())
        assertThat(page.content).allMatch { it.category?.name == "Electronics" }
    }
}
