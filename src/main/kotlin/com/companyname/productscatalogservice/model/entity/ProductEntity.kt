package com.companyname.productscatalogservice.model.dto.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.math.BigDecimal
import java.time.OffsetDateTime

@Entity
@Table(
    name = "products",
    indexes = [
        Index(name = "uk_products_sku", columnList = "sku", unique = true),
        Index(name = "idx_products_category", columnList = "category_id"),
        Index(name = "idx_products_sku_sort", columnList = "sku"),
        Index(name = "idx_products_price_sort", columnList = "price"),
        Index(name = "idx_products_desc_sort", columnList = "description"),
        Index(name = "idx_products_catname_sort", columnList = "category_name")
    ]
)
class ProductEntity(

    /**
     * SKU = Stock Keeping Unit — internal identifier of the product in the catalog/warehouse.
     * This is a business key, it identifies the product by merch/operations
     * (unlike the automatically generated database identifier)
     */
    @Column(nullable = false, unique = true, length = 16)
    var sku: String,

    /**
     * Product price.
     */
    @Column(nullable = false, precision = 12, scale = 2)
    var price: BigDecimal,

    /**
     * Result price. (field for faster view)
     */
    @Column(nullable = false, precision = 12, scale = 2)
    var discountPrice: BigDecimal? = null,

    /**
     * Discount. (field for faster view)
     */
    @Column(nullable = false)
    var discount: Long? = null,

    /**
     * Product description. QUESTION: can be null or empty string?
     */
    @Column(nullable = false, length = 255)
    var description: String,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = true)
    var category: CategoryEntity?,

    /**
     * For fast sorting, when we will have like millions of products
     */
    @Column(name = "category_name", nullable = true, length = 64)
    var categoryName: String?

) : BaseEntity<Long>() {

    // equals() compares by id. Two transient entities would both have id=default_value → they’d look equal (bug).
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override var id: Long? = null

    @Version
    @Column(nullable = false)
    var version: Long? = null

    @CreationTimestamp
    lateinit var createdAt: OffsetDateTime

    @UpdateTimestamp
    lateinit var updatedAt: OffsetDateTime
}