package com.companyname.productscatalogservice.util

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.DynamicPropertyRegistry
import java.io.File
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS z")

fun getJsonMapper(enableSnakeCase: Boolean = true, timeStampDeserializerEnabled: Boolean = false): JsonMapper {
    val jsonMapper = jsonMapper {
        addModule(kotlinModule())
    }.also {
        it
            .registerModule(
                JavaTimeModule().apply {
                    if (!timeStampDeserializerEnabled) {
                        this.addDeserializer(
                            ZonedDateTime::class.java,
                            object : JsonDeserializer<ZonedDateTime>() {
                                override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): ZonedDateTime {
                                    return ZonedDateTime.parse(p?.text, dateTimeFormatter)
                                }
                            })
                    }
                }
            )
            .registerModule(ParameterNamesModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }
    if (enableSnakeCase) jsonMapper.propertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE
    return jsonMapper
}

inline fun <reified T> parseFromFile(path: String, mapper: JsonMapper = getJsonMapper()): T = mapper.readValue(
    File(path).useLines { it.joinToString("").trim() }
)

inline fun <reified T> parseString(jsonResponse: String, mapper: JsonMapper = getJsonMapper()): T =
    mapper.readValue(jsonResponse)

fun toJSON(obj: Any?, mapper: JsonMapper = getJsonMapper(enableSnakeCase = false)): String =
    mapper.writeValueAsString(obj)

fun TestEntityManager.flushAndClear() {
    flush()
    clear()
}

operator fun DynamicPropertyRegistry.set(name: String, value: Any) =
    this.add(name) { value }
