package ge.nika.mcmaintenance.web.filter

import ge.nika.mcmaintenance.util.fromJson
import ge.nika.mcmaintenance.web.SingleMessageResponse
import org.http4k.core.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class HandleDomainErrorsTest {

    @Test
    fun `returns bad request response when illegal state exception occurs in next handlers`() {
        val errorHandlingHttp = HandleDomainErrors().then { error("Some domain error") }
        val response = errorHandlingHttp(Request(Method.GET, ""))

        assertEquals(Status.BAD_REQUEST, response.status)
        assertEquals(SingleMessageResponse("Some domain error"), fromJson(response.bodyString()))
    }

    @Test
    fun `returns response from next handlers if no error occurs`() {
        val errorHandlingHttp = HandleDomainErrors().then { Response(Status.OK).body("ok response") }
        val response = errorHandlingHttp(Request(Method.GET, ""))

        assertEquals(Status.OK, response.status)
        assertEquals("ok response", response.bodyString())
    }
}