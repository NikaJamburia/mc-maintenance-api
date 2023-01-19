package ge.nika.mcmaintenance.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue

val objectMapper: ObjectMapper =
    ObjectMapper()
        .registerModule(KotlinModule())
        .registerModule(JavaTimeModule())
fun <T> toJson(obj: T): String = objectMapper.writeValueAsString(obj)
inline fun <reified T> fromJson(jsonString: String): T = objectMapper.readValue(jsonString)