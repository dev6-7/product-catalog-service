package com.companyname.productscatalogservice.controller

import com.companyname.productscatalogservice.model.dto.CreateProductRequest
import com.companyname.productscatalogservice.model.dto.ProductResponse
import com.companyname.productscatalogservice.model.dto.UpdateProductRequest
import com.companyname.productscatalogservice.service.ProductService
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Sort
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(ProductController::class)
class ProductControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper
) {

    @MockkBean
    private lateinit var service: ProductService

    private fun Any.json() = objectMapper.writeValueAsString(this)

    @Test
    fun `create returns 201 with body`() {
        val req = CreateProductRequest(
            sku = "SKU0001",
            price = "19.99".toBigDecimal(),
            description = "Wireless Mouse",
            categoryId = 1L
        )

        val resp = ProductResponse(
            id = 10L,
            sku = "SKU0001",
            price = "19.99".toBigDecimal(),
            description = "Wireless Mouse",
            categoryId = 1L,
            categoryName = "Electronics",
            discountPrice = "16.99".toBigDecimal(),
            discount = 15,
            version = 0
        )

        every { service.create(req) } returns resp

        mockMvc.perform(
            post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(req.json())
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id", equalTo(10)))
            .andExpect(jsonPath("$.sku", equalTo("SKU0001")))
            .andExpect(jsonPath("$.categoryName", equalTo("Electronics")))

        verify(exactly = 1) { service.create(req) }
    }

    @Test
    fun `get returns product by id`() {
        val resp = ProductResponse(
            id = 42L,
            sku = "SKU0042",
            price = "99.00".toBigDecimal(),
            description = "Desc",
            categoryId = 2L,
            categoryName = "Home & Kitchen",
            discountPrice = "74.25".toBigDecimal(),
            discount = 25,
            version = 3
        )
        every { service.get(42L) } returns resp

        mockMvc.perform(get("/api/v1/products/{id}", 42))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id", equalTo(42)))
            .andExpect(jsonPath("$.sku", equalTo("SKU0042")))
            .andExpect(jsonPath("$.version", equalTo(3)))

        verify { service.get(42L) }
    }

    @Test
    fun `update optimistic returns updated product`() {
        val req = UpdateProductRequest(
            price = "129.99".toBigDecimal(),
            description = "New desc",
            categoryId = 1L,
            version = 3
        )
        val resp = ProductResponse(
            id = 42L,
            sku = "SKU0042",
            price = "129.99".toBigDecimal(),
            description = "New desc",
            categoryId = 1L,
            categoryName = "Electronics",
            discountPrice = "110.49".toBigDecimal(),
            discount = 15,
            version = 4
        )
        every { service.updateOptimistic(42L, req) } returns resp

        mockMvc.perform(
            put("/api/v1/products/{id}", 42)
                .contentType(MediaType.APPLICATION_JSON)
                .content(req.json())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id", equalTo(42)))
            .andExpect(jsonPath("$.price", equalTo(129.99)))
            .andExpect(jsonPath("$.version", equalTo(4)))

        verify { service.updateOptimistic(42L, req) }
    }

    @Test
    fun `delete returns deleted product`() {
        val resp = ProductResponse(
            id = 7L,
            sku = "SKU0007",
            price = "75.00".toBigDecimal(),
            description = "Wallet",
            categoryId = 4L,
            categoryName = "Accessories",
            discountPrice = "75.00".toBigDecimal(),
            discount = 0,
            version = 1
        )
        every { service.delete(7L) } returns resp

        mockMvc.perform(delete("/api/v1/products/{id}", 7))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id", equalTo(7)))
            .andExpect(jsonPath("$.sku", equalTo("SKU0007")))

        verify { service.delete(7L) }
    }

    @Test
    fun `list returns page of products and passes query params to service`() {
        val categoryName = "Electronics"
        val categoryId = 1L
        val pageIdx = 2
        val size = 50
        val sortParams = listOf("sku:asc", "-price")
        val defaultDir = Sort.Direction.DESC

        val pageContent = listOf(
            ProductResponse(
                id = 1L,
                sku = "SKU0001",
                price = "19.99".toBigDecimal(),
                description = "Mouse",
                categoryId = 1L,
                categoryName = "Electronics",
                discountPrice = "16.99".toBigDecimal(),
                discount = 15,
                version = 0
            ),
            ProductResponse(
                id = 2L,
                sku = "SKU0002",
                price = "499.00".toBigDecimal(),
                description = "TV",
                categoryId = 1L,
                categoryName = "Electronics",
                discountPrice = "424.15".toBigDecimal(),
                discount = 15,
                version = 0
            )
        )
        val page = PageImpl(pageContent)

        every {
            service.list(categoryName, categoryId, pageIdx, size, sortParams, defaultDir)
        } returns page

        mockMvc.perform(
            get("/api/v1/products")
                .param("categoryName", categoryName)
                .param("categoryId", categoryId.toString())
                .param("page", pageIdx.toString())
                .param("size", size.toString())
                .param("sort", "sku:asc")
                .param("sort", "-price")
                .param("defaultDirection", "DESC")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content", hasSize<Int>(2)))
            .andExpect(jsonPath("$.content[0].sku", equalTo("SKU0001")))
            .andExpect(jsonPath("$.content[1].sku", equalTo("SKU0002")))

        verify(exactly = 1) {
            service.list(categoryName, categoryId, pageIdx, size, sortParams, defaultDir)
        }
    }

    @Test
    fun `create returns 400 on validation error`() {
        // assuming DTO validation annotations
        val bad = mapOf(
            "sku" to "   ",
            "price" to null,
            "description" to "x",
            "categoryId" to 1,
            "categoryName" to "Electronics"
        )

        mockMvc.perform(
            post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bad.json())
        ).andExpect(status().isBadRequest)

        verify(exactly = 0) { service.create(any()) }
    }
}
