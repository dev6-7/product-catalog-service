package com.companyname.productscatalogservice.integration

import com.companyname.productscatalogservice.util.set
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Timeout
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.TestConstructor
import org.springframework.test.jdbc.JdbcTestUtils
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

private const val POSTGRESQL_IMAGE = "postgres:16-alpine"

@DirtiesContext
@ActiveProfiles("it")
@Testcontainers(disabledWithoutDocker = true)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
abstract class BaseDbIntegrationTest(
    private val jdbcTemplate: JdbcTemplate? = null,
    private val tablesToCleanUp: List<String> = emptyList()
) {
    companion object {

        @Container
        private val postgres = PostgreSQLContainer<Nothing>(POSTGRESQL_IMAGE)
            .apply {
                withDatabaseName("testdb")
                withUsername("test")
                withPassword("test")
            }

        @JvmStatic
        @DynamicPropertySource
        private fun properties(registry: DynamicPropertyRegistry) {
            registry["spring.datasource.url"] = postgres.jdbcUrl
            registry["spring.datasource.password"] = postgres.password
            registry["spring.datasource.username"] = postgres.username
        }
    }

    @Timeout(5)
    @AfterEach
    fun cleanup() {
        jdbcTemplate?.let {
            check(tablesToCleanUp.isNotEmpty())
            JdbcTestUtils.deleteFromTables(
                jdbcTemplate, *tablesToCleanUp.toTypedArray()
            )
        }
    }

}