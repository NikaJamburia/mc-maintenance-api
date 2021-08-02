package ge.nika.mcmaintenance.service.request

import org.joda.time.LocalDateTime

data class LogInRequest(
    val userName: String,
    val password: String,
    val logInTime: LocalDateTime
)