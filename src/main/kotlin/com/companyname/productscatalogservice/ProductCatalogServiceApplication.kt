package com.companyname.productscatalogservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ProductCatalogService

fun main(args: Array<String>) {
    runApplication<ProductCatalogService>(*args)
}
