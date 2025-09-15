package com.companyname.productscatalogservice.model.dto.entity

import jakarta.persistence.MappedSuperclass
import org.hibernate.Hibernate

@MappedSuperclass
abstract class BaseEntity<ID>  {

    abstract val id: ID?

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as BaseEntity<*>

        if (id == null)
            return false

        return id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()
}