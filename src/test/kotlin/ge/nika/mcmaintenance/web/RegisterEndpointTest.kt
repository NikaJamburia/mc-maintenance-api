package ge.nika.mcmaintenance.web

import ge.nika.mcmaintenance.persistence.data.User
import ge.nika.mcmaintenance.service.LogInService
import ge.nika.mcmaintenance.service.request.UserCredentials
import ge.nika.mcmaintenance.util.fromJson
import ge.nika.mcmaintenance.util.toJson
import io.mockk.every
import io.mockk.mockk
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class RegisterEndpointTest {

    @Test
    fun `registers user`() {
        val loginService = mockk<LogInService> {
            every { register(any()) } answers { User("123", (firstArg() as UserCredentials).userName, "hashed pass") }
        }
        val response = register(loginService)(Request(Method.POST, "/register").body(toJson(UserCredentials("nika", "aaa"))))

        assertEquals(Status.OK, response.status)
        assertEquals("nika", fromJson<User>(response.bodyString()).userName)
    }

    @Test
    fun `throws exception if it happens in service`() {
        val loginService = mockk<LogInService> {
            every { register(any()) } throws IllegalStateException("User exists")
        }

        assertThrows<IllegalStateException> {
            register(loginService)(Request(Method.POST, "/register").body(toJson(UserCredentials("nika", "aaa"))))
        }
    }
}