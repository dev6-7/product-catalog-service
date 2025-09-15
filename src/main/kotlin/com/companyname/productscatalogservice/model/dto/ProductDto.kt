package com.companyname.productscatalogservice.model.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import java.math.BigDecimal

data class CreateProductRequest(
    @field:NotBlank val sku: String,
    @field:Positive val price: BigDecimal,
    @field:NotBlank val description: String,
    val categoryId: Long?,
)

data class UpdateProductRequest(
    @field:Positive val price: BigDecimal,
    @field:NotBlank val description: String,
    val categoryId: Long?,
    val version: Long
)

data class ProductResponse(
    val id: Long,
    val sku: String,
    val description: String,
    val categoryId: Long?,
    val categoryName: String?,
    val price: BigDecimal,
    val discount: Long,
    val discountPrice: BigDecimal,
    val version: Long,
)
