package com.companyname.productscatalogservice.model.dto

import jakarta.validation.constraints.NotBlank

data class CreateCategoryRequest(
    @field:NotBlank val name: String,
)

data class UpdateCategoryRequest(
    val name: String? = null,
    val version: Long
)

data class CategoryResponse(
    val id: Long,
    val name: String,
    val version: Long,
)
