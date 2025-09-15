package com.companyname.productscatalogservice.controller

import com.companyname.productscatalogservice.model.dto.CategoryResponse
import com.companyname.productscatalogservice.model.dto.CreateCategoryRequest
import com.companyname.productscatalogservice.model.dto.UpdateCategoryRequest
import com.companyname.productscatalogservice.service.CategoryService
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(CategoryController::class)
class CategoryControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper
) {

    @MockkBean
    private lateinit var service: CategoryService

    private fun Any.json() = objectMapper.writeValueAsString(this)

    @Test
    fun `create returns 201 with body`() {
        val req = CreateCategoryRequest(name = "Electronics")
        val resp = CategoryResponse(id = 1L, name = "Electronics", version = 0)

        every { service.create(req) } returns resp

        mockMvc.perform(
            post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(req.json())
        )
            .andExpect(status().isCreated)
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", equalTo(1)))
            .andExpect(jsonPath("$.name", equalTo("Electronics")))

        verify(exactly = 1) { service.create(req) }
    }

    @Test
    fun `get returns category by id`() {
        val resp = CategoryResponse(id = 42L, name = "Home & Kitchen", version = 2)
        every { service.get(42L) } returns resp

        mockMvc.perform(get("/api/v1/categories/{id}", 42))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id", equalTo(42)))
            .andExpect(jsonPath("$.name", equalTo("Home & Kitchen")))
            .andExpect(jsonPath("$.version", equalTo(2)))

        verify { service.get(42L) }
    }

    @Test
    fun `update returns updated category`() {
        val req = UpdateCategoryRequest(name = "Home & Kitchen (New)", version = 2)
        val resp = CategoryResponse(id = 42L, name = "Home & Kitchen (New)", version = 3)

        every { service.update(42L, req) } returns resp

        mockMvc.perform(
            put("/api/v1/categories/{id}", 42)
                .contentType(MediaType.APPLICATION_JSON)
                .content(req.json())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id", equalTo(42)))
            .andExpect(jsonPath("$.name", equalTo("Home & Kitchen (New)")))
            .andExpect(jsonPath("$.version", equalTo(3)))

        verify { service.update(42L, req) }
    }

    @Test
    fun `delete returns deleted category`() {
        val resp = CategoryResponse(id = 7L, name = "Toys & Games", version = 5)
        every { service.delete(7L) } returns resp

        mockMvc.perform(delete("/api/v1/categories/{id}", 7))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id", equalTo(7)))

        verify { service.delete(7L) }
    }

    @Test
    fun `list returns all categories`() {
        val list = listOf(
            CategoryResponse(1, "Electronics", 0),
            CategoryResponse(2, "Home & Kitchen", 0)
        )
        every { service.list() } returns list

        mockMvc.perform(get("/api/v1/categories"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", hasSize<Int>(2)))
            .andExpect(jsonPath("$[0].name", equalTo("Electronics")))
            .andExpect(jsonPath("$[1].name", equalTo("Home & Kitchen")))

        verify { service.list() }
    }

    @Test
    fun `create returns 400 on validation error`() {
        val bad = mapOf("name" to "   ")

        mockMvc.perform(
            post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bad))
        ).andExpect(status().isBadRequest)

        verify(exactly = 0) { service.create(any()) }
    }
}
