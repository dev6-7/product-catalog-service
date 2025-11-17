package com.companyname.productscatalogservice.model.dto.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import org.hibernate.annotations.UpdateTimestamp
import java.time.OffsetDateTime

@Entity
@Table(
    name = "categories",
)
class CategoryEntity(

    @Column(nullable = false, unique = true, length = 64)
    var name: String

) : BaseEntity<Long>() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override var id: Long? = null

    @Fetch(FetchMode.SUBSELECT)
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    var products: MutableList<ProductEntity> = mutableListOf()

    @Version
    @Column(nullable = false)
    var version: Long? = null

    @CreationTimestamp
    lateinit var createdAt: OffsetDateTime

    @UpdateTimestamp
    lateinit var updatedAt: OffsetDateTime
}
