package com.companyname.productscatalogservice.controller

import com.companyname.productscatalogservice.model.dto.DiscountRuleResponse
import com.companyname.productscatalogservice.model.dto.entity.DiscountScope
import com.companyname.productscatalogservice.service.DiscountService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [DiscountRuleController::class])
@AutoConfigureMockMvc
class DiscountRuleControllerMockkTest @Autowired constructor(
    var mockMvc: MockMvc
) {
    @MockkBean
    lateinit var service: DiscountService

    private fun rule(
        id: Long = 1,
        scope: DiscountScope = DiscountScope.CATEGORY,
        categoryId: Long? = 10,
        skuSuffix: String? = "ABC123",
        percent: Long = 15
    ) = DiscountRuleResponse(id, scope, categoryId, skuSuffix, percent)

    // -------------------- CREATE --------------------
    @Test
    fun `POST create returns 201 and body`() {
        every { service.create(any()) } returns
                rule(id = 100, scope = DiscountScope.CATEGORY, categoryId = 7, skuSuffix = "SKUEND", percent = 20)

        val body = """
            {
              "scope": "CATEGORY",
              "categoryId": 7,
              "skuSuffix": "SKUEND",
              "percent": 20
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/v1/discount-rules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(100))
            .andExpect(jsonPath("$.scope").value("CATEGORY"))
            .andExpect(jsonPath("$.categoryId").value(7))
            .andExpect(jsonPath("$.skuSuffix").value("SKUEND"))
            .andExpect(jsonPath("$.percent").value(20))

        verify(exactly = 1) {
            service.create(match {
                it.scope == DiscountScope.CATEGORY &&
                        it.categoryId == 7L &&
                        it.skuSuffix == "SKUEND" &&
                        it.percent == 20L
            })
        }
    }

    @Test
    fun `POST create - invalid payload returns 400 and service not called`() {
        val invalid = """
            {
              "categoryId": 7,
              "skuSuffix": "THIS_IS_MORE_THAN_16",
              "percent": 101
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/v1/discount-rules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalid)
        )
            .andExpect(status().isBadRequest)

        verify(exactly = 0) { service.create(any()) }
    }

    // -------------------- GET BY ID --------------------
    @Test
    fun `GET by id returns 200 and body`() {
        every { service.get(42) } returns rule(id = 42, percent = 30)

        mockMvc.perform(get("/api/v1/discount-rules/{id}", 42))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(42))
            .andExpect(jsonPath("$.percent").value(30))

        verify { service.get(42) }
    }

    // -------------------- UPDATE --------------------
    @Test
    fun `PUT update full payload returns 200 and delegates`() {
        every { service.update(5, any()) } returns
                rule(id = 5, scope = DiscountScope.CATEGORY, categoryId = 9, skuSuffix = "XYZ", percent = 5)

        val body = """
            {
              "scope": "CATEGORY",
              "categoryId": 9,
              "skuSuffix": "XYZ",
              "percent": 5
            }
        """.trimIndent()

        mockMvc.perform(
            put("/api/v1/discount-rules/{id}", 5)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(5))
            .andExpect(jsonPath("$.percent").value(5))

        verify(exactly = 1) {
            service.update(5, match {
                it.scope == DiscountScope.CATEGORY &&
                        it.categoryId == 9L &&
                        it.skuSuffix == "XYZ" &&
                        it.percent == 5L
            })
        }
    }

    @Test
    fun `PUT update partial payload (only percent) returns 200`() {
        every { service.update(7, any()) } returns rule(id = 7, percent = 25)

        val body = """{ "percent": 25 }"""

        mockMvc.perform(
            put("/api/v1/discount-rules/{id}", 7)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(7))
            .andExpect(jsonPath("$.percent").value(25))

        verify {
            service.update(7, match {
                it.scope == null && it.categoryId == null && it.skuSuffix == null && it.percent == 25L
            })
        }
    }

    @Test
    fun `PUT update - invalid fields return 400 and do not call service`() {
        val invalid = """{ "percent": 101, "skuSuffix": "THIS_IS_MORE_THAN_16" }"""

        mockMvc.perform(
            put("/api/v1/discount-rules/{id}", 8)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalid)
        )
            .andExpect(status().isBadRequest)

        verify(exactly = 0) { service.update(any(), any()) }
    }

    // -------------------- DELETE --------------------
    @Test
    fun `DELETE returns 200 and body`() {
        every { service.delete(9) } returns rule(id = 9)

        mockMvc.perform(delete("/api/v1/discount-rules/{id}", 9))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(9))

        verify { service.delete(9) }
    }

    // -------------------- LIST --------------------
    @Test
    fun `GET list defaults page=0 size=20`() {
        val page = PageImpl(listOf(rule(id = 1, percent = 10), rule(id = 2, percent = 20)), PageRequest.of(0, 20), 2)
        every { service.list(0, 20) } returns page

        mockMvc.perform(get("/api/v1/discount-rules"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content[0].id").value(1))
            .andExpect(jsonPath("$.content[1].percent").value(20))

        verify { service.list(0, 20) }
    }

    @Test
    fun `GET list custom page and size`() {
        val page = PageImpl(listOf(rule(id = 77, percent = 33)), PageRequest.of(2, 5), 6)
        every { service.list(2, 5) } returns page

        mockMvc.perform(
            get("/api/v1/discount-rules")
                .param("page", "2")
                .param("size", "5")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content[0].id").value(77))
            .andExpect(jsonPath("$.size").value(5))

        verify { service.list(2, 5) }
    }
}
