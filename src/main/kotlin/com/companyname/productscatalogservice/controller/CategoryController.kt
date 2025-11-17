package com.companyname.productscatalogservice.controller

import com.companyname.productscatalogservice.model.dto.CategoryResponse
import com.companyname.productscatalogservice.model.dto.CreateCategoryRequest
import com.companyname.productscatalogservice.model.dto.UpdateCategoryRequest
import com.companyname.productscatalogservice.service.CategoryService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/categories")
class CategoryController(
    private val service: CategoryService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody @Valid req: CreateCategoryRequest): CategoryResponse =
        service.create(req)

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): CategoryResponse = service.get(id)

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody @Valid req: UpdateCategoryRequest
    ): CategoryResponse =
        service.update(id, req)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): CategoryResponse = service.delete(id)

    @GetMapping
    fun list(): List<CategoryResponse> = service.list()
}
