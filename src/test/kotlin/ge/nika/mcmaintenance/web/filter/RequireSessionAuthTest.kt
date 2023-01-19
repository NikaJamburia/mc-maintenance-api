package ge.nika.mcmaintenance.web.filter

import ge.nika.mcmaintenance.persistence.data.Session
import ge.nika.mcmaintenance.service.LogInService
import ge.nika.mcmaintenance.util.fromJson
import ge.nika.mcmaintenance.web.SingleMessageResponse
import io.mockk.every
import io.mockk.mockk
import org.http4k.core.*
import org.http4k.routing.bind
import org.http4k.routing.routes
import java.time.LocalDateTime
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RequireSessionAuthTest {

    @Test
    fun `returns response for next handlers if valid session exists and adds users id header to request`() {

        // service returns a valid session
        val service = mockk<LogInService> {
            every { getSession(any()) } returns Session("aaa", "nika123", LocalDateTime.now().plusMinutes(30))
        }

        val http = RequireSessionAuth(service)
            .then { request: Request -> Response(Status.OK).body("Logged in users id: ${request.header("user-id")}") }

        // request is sent with session-id header
        val response = http(Request(Method.GET, "").header("session-id", "123"))

        // we get response for inlaid handler
        assertEquals(Status.OK, response.status)
        assertEquals("Logged in users id: nika123", response.bodyString())

    }

    @Test
    fun `returns forbidden status when session-id not provided`() {
        val http = RequireSessionAuth(mockk()).then { Response(Status.OK) }
        val response = http(Request(Method.GET, ""))

        assertEquals(Status.FORBIDDEN, response.status)
        assertEquals(SingleMessageResponse("Access forbidden"), fromJson(response.bodyString()))
    }

    @Test
    fun `returns forbidden status when session-id is provided but session is expired`() {
        val service = mockk<LogInService> { every { getSession(any()) } returns Session("s1", "u1", LocalDateTime.now().minusSeconds(1)) }
        val http = RequireSessionAuth(service).then { Response(Status.OK) }
        val response = http(Request(Method.GET, "").header("session-id", "aaa"))

        assertEquals(Status.FORBIDDEN, response.status)
        assertEquals(SingleMessageResponse("Session Expired"), fromJson(response.bodyString()))
    }

    @Test
    fun `can decorate a routed handler`() {

        val service = mockk<LogInService> {
            every { getSession("valid") } returns Session("aaa", "nika123", LocalDateTime.now().plusMinutes(30))
            every { getSession("invalid") } returns null
        }

        val routedHttp = routes(
            "/a" bind Method.GET to { Response(Status.OK) },
            RequireSessionAuth(service).then("/b" bind Method.GET to {  req -> Response(Status.OK).body(req.header("user-id") ?: "") })
        )

        assertEquals(Status.OK, routedHttp(Request(Method.GET, "/a")).status)
        assertEquals(Status.OK, routedHttp(Request(Method.GET, "/b").header("session-id", "valid")).status)
        assertEquals("nika123", routedHttp(Request(Method.GET, "/b").header("session-id", "valid")).bodyString())
        assertEquals(Status.FORBIDDEN, routedHttp(Request(Method.GET, "/b").header("session-id", "invalid")).status)
        assertEquals(SingleMessageResponse("No Session found"), fromJson(routedHttp(Request(Method.GET, "/b").header("session-id", "invalid")).bodyString()))
    }
}