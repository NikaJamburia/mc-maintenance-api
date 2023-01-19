package ge.nika.mcmaintenance.persistence.data

import java.time.LocalDateTime

data class Session (
    val id: String,
    val userId: String,
    val expiresOn: LocalDateTime
)