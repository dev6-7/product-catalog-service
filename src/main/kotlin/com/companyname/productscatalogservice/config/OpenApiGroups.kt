package com.companyname.productscatalogservice.config

import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiGroups {

    @Bean
    fun productsApi(): GroupedOpenApi =
        GroupedOpenApi.builder().group("products").pathsToMatch("/api/v1/products/**").build()

    @Bean
    fun categoriesApi(): GroupedOpenApi =
        GroupedOpenApi.builder().group("categories").pathsToMatch("/api/v1/categories/**").build()

    @Bean
    fun discountsApi(): GroupedOpenApi =
        GroupedOpenApi.builder().group("discounts").pathsToMatch("/api/v1/discount-rules/**").build()
}