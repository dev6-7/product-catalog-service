package com.companyname.productscatalogservice.model.dto.entity

import jakarta.persistence.*

enum class DiscountScope { CATEGORY, SKU_SUFFIX }

@Entity
@Table(name = "discount_rules")
class DiscountRuleEntity(

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    var scope: DiscountScope,

    @Column(name = "category_id")
    var categoryId: Long? = null,

    @Column(name = "sku_suffix", length = 16)
    var skuSuffix: String? = null,

    @Column(nullable = false)
    var percent: Long

) : BaseEntity<Long>() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override var id: Long? = null

    @Version
    @Column(nullable = false)
    var version: Long? = null
}
