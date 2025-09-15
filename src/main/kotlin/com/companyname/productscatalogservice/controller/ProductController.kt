package com.companyname.productscatalogservice.controller

import com.companyname.productscatalogservice.model.dto.CreateProductRequest
import com.companyname.productscatalogservice.model.dto.ProductResponse
import com.companyname.productscatalogservice.model.dto.UpdateProductRequest
import com.companyname.productscatalogservice.service.ProductService
import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/products")
class ProductController(
    private val service: ProductService
) {
    @Operation(summary = "Create product", description = "Creates a new product")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody @Valid req: CreateProductRequest): ProductResponse =
        service.create(req)

    @Operation(summary = "Get product", description = "Get product")
    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): ProductResponse =
        service.get(id)

    @Operation(summary = "Update product", description = "Update product")
    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody @Valid req: UpdateProductRequest): ProductResponse =
        service.updateOptimistic(id, req)

    @Operation(summary = "Delete product", description = "Delete product")
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ProductResponse =
        service.delete(id)

    @Operation(summary = "Get list of products", description = "Get list of products")
    @GetMapping
    fun list(
        @RequestParam(required = false) categoryName: String?,
        @RequestParam(required = false) categoryId: Long?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(name = "sort", required = false) sort: List<String>?,
        @RequestParam(defaultValue = "ASC") defaultDirection: Sort.Direction
    ): Page<ProductResponse> =
        service.list(categoryName, categoryId, page, size, sort, defaultDirection)
}