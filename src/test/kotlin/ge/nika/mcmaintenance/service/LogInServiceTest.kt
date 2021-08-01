package ge.nika.mcmaintenance.service

import ge.nika.mcmaintenance.persistence.data.User
import ge.nika.mcmaintenance.persistence.repository.AppRepository
import ge.nika.mcmaintenance.service.crypto.Encryption
import ge.nika.mcmaintenance.service.request.LogInRequest
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.joda.time.LocalDateTime.now
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalStateException
import kotlin.test.assertEquals

class LogInServiceTest {

    private val repository = mockk<AppRepository> {
        every { getUserByUserName("nika") } returns User("123", "nika", "enctypted pass")
        every { getUserByUserName("beqa") } returns null
        every { saveSession(any()) } returns Unit
    }

    private val encryption = mockk<Encryption> {
        every { matches("valid pass", any()) } returns true
        every { matches("invalid pass", any()) } returns false
    }

    @Test
    fun `correctly creates and saves users session when everything is correct`() {
        val service = LogInService(repository, encryption, 15)
        val session = service.logIn(LogInRequest("nika", "valid pass"))

        assertEquals("123", session.userId)
        assertEquals(now().minuteOfHour + 15, session.expiresOn.minuteOfHour)
        verify(exactly = 1) { repository.saveSession(session) }
    }

    @Test
    fun `throws error when user is not found`() {
        val service = LogInService(repository, encryption, 15)
        val exception = assertThrows<IllegalStateException> { service.logIn(LogInRequest("beqa", "valid pass")) }
        assertEquals("Wrong username or password", exception.message)
        verify(exactly = 0) { repository.saveSession(any()) }
    }

    @Test
    fun `throws error when password is incorrect`() {
        val service = LogInService(repository, encryption, 15)
        val exception = assertThrows<IllegalStateException> { service.logIn(LogInRequest("nika", "invalid pass")) }
        assertEquals("Wrong username or password", exception.message)
        verify(exactly = 0) { repository.saveSession(any()) }
    }
}