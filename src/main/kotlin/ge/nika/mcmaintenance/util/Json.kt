package ge.nika.mcmaintenance.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue

fun <T> toJson(obj: T): String = ObjectMapper().registerModule(JodaModule()).writeValueAsString(obj)

inline fun <reified T> fromJson(jsonString: String): T =
    ObjectMapper()
        .registerModules(KotlinModule(), JodaModule())
        .readValue(jsonString)