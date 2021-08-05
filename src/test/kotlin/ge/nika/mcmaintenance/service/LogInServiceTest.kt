package ge.nika.mcmaintenance.service

import ge.nika.mcmaintenance.persistence.data.Session
import ge.nika.mcmaintenance.persistence.data.User
import ge.nika.mcmaintenance.persistence.repository.AppRepository
import ge.nika.mcmaintenance.service.crypto.BCrypt
import ge.nika.mcmaintenance.service.crypto.Encryption
import ge.nika.mcmaintenance.service.request.UserCredentials
import ge.nika.mcmaintenance.util.dateTime
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalStateException
import java.util.*
import kotlin.test.*

class LogInServiceTest {

    private val repository = mockk<AppRepository> {
        every { getUserByUserName("nika") } returns User("123", "nika", "enctypted pass")
        every { getUserByUserName("beqa") } returns null
        every { getUserByUserName("new user") } returns null
        every { saveSession(any()) } returns Unit
        every { saveUser(any()) } returns Unit
    }

    private val encryption = mockk<Encryption> {
        every { matches("valid pass", any()) } returns true
        every { matches("invalid pass", any()) } returns false
        every { getHash(any()) } answers { firstArg() as String + " hashed" }
    }

    @Test
    fun `correctly creates and saves users session when everything is correct`() {
        val service = LogInService(repository, encryption, 15)
        val session = service.logIn(UserCredentials("nika", "valid pass"), dateTime("2021-08-02T12:00:00"))

        assertEquals("123", session.userId)
        assertEquals(dateTime("2021-08-02T12:15:00"), session.expiresOn)
        verify(exactly = 1) { repository.saveSession(session) }
    }

    @Test
    fun `throws error when user is not found`() {
        val service = LogInService(repository, encryption, 15)
        val exception = assertThrows<IllegalStateException> { service.logIn(UserCredentials("beqa", "valid pass"), dateTime("2021-08-02T11:46:12.000")) }
        assertEquals("Wrong username or password", exception.message)
        verify(exactly = 0) { repository.saveSession(any()) }
    }

    @Test
    fun `throws error when password is incorrect`() {
        val service = LogInService(repository, encryption, 15)
        val exception = assertThrows<IllegalStateException> { service.logIn(UserCredentials("nika", "invalid pass"), dateTime("2021-08-02T11:46:12.000")) }
        assertEquals("Wrong username or password", exception.message)
        verify(exactly = 0) { repository.saveSession(any()) }
    }

    @Test
    fun `gives session if provided valid id`() {
        val sessionId = "valid session"
        val checkTime = dateTime("2021-08-02T11:46:12.000")
        val repository = mockk<AppRepository> {
            every { getSessionById(sessionId) } returns Session(sessionId, "nika", dateTime("2021-08-02T11:46:12.000"))
        }

        val service = LogInService(repository, encryption, 15)
        val session = service.getSessionIfValid(sessionId, checkTime)

        assertNotNull(session)
        assertEquals(sessionId, session.id)

    }

    @Test
    fun `returns null if session does not exist`() {
        val repository = mockk<AppRepository> {
            every { getSessionById("no session") } returns null
        }

        val service = LogInService(repository, encryption, 15)
        assertNull(service.getSessionIfValid("no session", dateTime("2021-08-02T11:46:12.000")))
    }

    @Test
    fun `returns null if session is expired`() {
        val checkTime = dateTime("2021-08-02T11:46:12.000")
        val sessionId = "expired session"
        val repository = mockk<AppRepository> {
            every { getSessionById(sessionId) } returns Session(sessionId, "nika", checkTime.minusMillis(1))
        }

        val service = LogInService(repository, encryption, 15)
        assertNull(service.getSessionIfValid(sessionId, checkTime))
    }

    @Test
    fun `returns non expired session`() {
        val checkTime = dateTime("2021-08-02T11:46:12.000")
        val sessionId = "session"
        val repository = mockk<AppRepository> {
            every { getSessionById(sessionId) } returns Session(sessionId, "nika", checkTime.plusMillis(1))
        }

        val service = LogInService(repository, encryption, 15)
        assertNotNull(service.getSessionIfValid(sessionId, checkTime))
    }

    @Test
    fun `hashes users password and saves him to repository`() {
        val service = LogInService(repository, encryption, 15)

        val registeredUser = service.register(UserCredentials("new user", "new pass"))

        assertDoesNotThrow { UUID.fromString(registeredUser.id) }
        assertEquals("new user", registeredUser.userName)
        assertEquals("new pass hashed", registeredUser.password)
        verify(exactly = 1) { repository.getUserByUserName("new user") }
    }

    @Test
    fun `throws exception if username already exists`() {
        val service = LogInService(repository, encryption, 15)

        val exception = assertThrows<IllegalStateException> { service.register(UserCredentials("nika", "new pass")) }
        assertEquals("Username already exists!", exception.message)

    }

    @Test
    fun `can correctly hash password with bcrypt`() {
        val service = LogInService(repository, BCrypt(), 15)

        val password = "new pass"
        val registeredUser = service.register(UserCredentials("new user", password))

        assertNotEquals(password, registeredUser.password)
        assertTrue(BCrypt().matches(password, registeredUser.password))
    }
}