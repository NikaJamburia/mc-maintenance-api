package ge.nika.mcmaintenance.service

import ge.nika.mcmaintenance.persistence.data.Session
import ge.nika.mcmaintenance.persistence.data.User
import ge.nika.mcmaintenance.persistence.repository.AppRepository
import ge.nika.mcmaintenance.service.crypto.Encryption
import ge.nika.mcmaintenance.service.request.UserCredentials
import org.joda.time.LocalDateTime
import java.util.*

class LogInService(
    private val repository: AppRepository,
    private val encryption: Encryption,
    private val sessionActiveForMinutes: Int
) {

    fun logIn(request: UserCredentials, logInTime: LocalDateTime): Session {
        val user = repository.getUserByUserName(request.userName) ?: error("Wrong username or password")
        check(passwordMatches(request, user)) { "Wrong username or password" }

        val session = createSession(user, logInTime, sessionActiveForMinutes)
        repository.saveSession(session)
        return session
    }

    fun getSession(sessionId: String): Session? = repository.getSessionById(sessionId)

    fun register(userCredentials: UserCredentials): User {
        val user = repository.getUserByUserName(userCredentials.userName)

        user
            ?. let { error("Username already exists!") }
            ?: let {
                val hashedPass = encryption.getHash(userCredentials.password)
                val user = User(UUID.randomUUID().toString(), userCredentials.userName, hashedPass)
                repository.saveUser(user)
                return user
            }
    }

    private fun passwordMatches(request: UserCredentials, user: User) = encryption.matches(request.password, user.password)

    private fun createSession(user: User, onTime: LocalDateTime, minutesTillExpires: Int) =
        Session(UUID.randomUUID().toString(), user.id, onTime.plusMinutes(minutesTillExpires))
}