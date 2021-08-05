package ge.nika.mcmaintenance.web

import ge.nika.mcmaintenance.persistence.data.Session
import ge.nika.mcmaintenance.service.LogInService
import ge.nika.mcmaintenance.service.request.UserCredentials
import ge.nika.mcmaintenance.util.fromJson
import ge.nika.mcmaintenance.util.toJson
import io.mockk.every
import io.mockk.mockk
import org.http4k.core.Method
import org.http4k.core.Request
import org.joda.time.LocalDateTime.now
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalStateException
import kotlin.test.assertEquals

class LogInEndpointTest {

    @Test
    fun `returns session gotten from log in service`() {
        val session = Session("s1", "u1", now())
        val service = mockk<LogInService> { every { logIn(any(), any()) } returns session }
        val endpoint = logIn(service)

        val response = endpoint(Request(Method.POST, "/login")
                .body(toJson(UserCredentials("nika", "nika"))))

        assertEquals("application/json", response.header("content-type"))
        assertEquals(session, fromJson(response.bodyString()))
    }

    @Test
    fun `throws exception in case of error`() {
        val service = mockk<LogInService> { every { logIn(any(), any()) } throws IllegalStateException("user not found") }
        val endpoint = logIn(service)

        val exception = assertThrows<IllegalStateException> {
            endpoint(Request(Method.POST, "/login")
                .body(toJson(UserCredentials("nika", "nika"))))
        }

        assertEquals("user not found", exception.message)
    }
}