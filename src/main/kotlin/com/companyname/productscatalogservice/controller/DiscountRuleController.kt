package com.companyname.productscatalogservice.controller

import com.companyname.productscatalogservice.model.dto.CreateDiscountRuleRequest
import com.companyname.productscatalogservice.model.dto.DiscountRuleResponse
import com.companyname.productscatalogservice.model.dto.UpdateDiscountRuleRequest
import com.companyname.productscatalogservice.service.DiscountService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/discount-rules")
class DiscountRuleController(
    private val service: DiscountService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody @Valid req: CreateDiscountRuleRequest): DiscountRuleResponse =
        service.create(req)

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): DiscountRuleResponse =
        service.get(id)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody @Valid req: UpdateDiscountRuleRequest): DiscountRuleResponse =
        service.update(id, req)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): DiscountRuleResponse =
        service.delete(id)

    @GetMapping
    fun list(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
    ): Page<DiscountRuleResponse> =
        service.list(page, size)
}
