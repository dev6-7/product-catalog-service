package com.companyname.productscatalogservice.model.dto

import com.companyname.productscatalogservice.model.dto.entity.DiscountScope
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class CreateDiscountRuleRequest(
    @field:NotNull val scope: DiscountScope,
    val categoryId: Long? = null,
    @field:Size(max = 16) val skuSuffix: String? = null,
    @field:Min(0) @field:Max(100) val percent: Long
)

data class UpdateDiscountRuleRequest(
    val scope: DiscountScope? = null,     // optional change of scope
    val categoryId: Long? = null,
    @field:Size(max = 16) val skuSuffix: String? = null,
    @field:Min(0) @field:Max(100) val percent: Long? = null
)

data class DiscountRuleResponse(
    val id: Long,
    val scope: DiscountScope,
    val categoryId: Long?,
    val skuSuffix: String?,
    val percent: Long
)