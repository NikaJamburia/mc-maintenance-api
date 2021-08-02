package ge.nika.mcmaintenance.service

import ge.nika.mcmaintenance.persistence.data.Session
import ge.nika.mcmaintenance.persistence.data.User
import ge.nika.mcmaintenance.persistence.repository.AppRepository
import ge.nika.mcmaintenance.service.crypto.Encryption
import ge.nika.mcmaintenance.service.request.LogInRequest
import org.joda.time.LocalDateTime
import org.joda.time.LocalDateTime.now
import java.util.*

class LogInService(
    private val repository: AppRepository,
    private val encryption: Encryption,
    private val sessionActiveForMinutes: Int
) {

    fun logIn(request: LogInRequest): Session {
        val user = repository.getUserByUserName(request.userName) ?: error("Wrong username or password")
        check(passwordMatches(request, user)) { "Wrong username or password" }

        val session = createSession(user, request.logInTime, sessionActiveForMinutes)
        repository.saveSession(session)
        return session
    }

    fun getSessionIfValid(sessionId: String, checkTime: LocalDateTime): Session? =
        repository.getSessionById(sessionId)
            ?. let {
                if (isValidSession(it, checkTime)) {
                    it
                } else {
                    null
                }
            }

    private fun isValidSession(session: Session, checkTime: LocalDateTime): Boolean = !session.expiresOn.isBefore(checkTime)

    private fun passwordMatches(request: LogInRequest, user: User) = encryption.matches(request.password, user.password)

    private fun createSession(user: User, onTime: LocalDateTime, minutesTillExpires: Int) =
        Session(UUID.randomUUID().toString(), user.id, onTime.plusMinutes(minutesTillExpires))
}