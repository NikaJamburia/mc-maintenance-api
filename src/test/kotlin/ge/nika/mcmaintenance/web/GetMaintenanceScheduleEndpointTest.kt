package ge.nika.mcmaintenance.web

import ge.nika.mcmaintenance.core.toDto
import ge.nika.mcmaintenance.fixtures.fakeBikeSchedule
import ge.nika.mcmaintenance.fixtures.getResourceFile
import ge.nika.mcmaintenance.service.MaintenanceScheduleService
import ge.nika.mcmaintenance.util.fromJson
import io.mockk.every
import io.mockk.mockk
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalStateException
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetMaintenanceScheduleEndpointTest {

    @Test
    fun `returns json of the schedule`() {

        val schedule = listOf(fakeBikeSchedule().toDto())

        val service: MaintenanceScheduleService = mockk {
            every { getUsersMaintenanceSchedule("1") } returns schedule
        }

        val response = getMaintenanceSchedule(service)(
            Request(Method.GET, "/maintenance-schedule").header("user-id", "1")
        )

        assertEquals(Status.OK, response.status)
        assertEquals("application/json", response.header("content-type"))
        assertTrue(response.bodyString().startsWith("["))
        assertTrue(response.bodyString().endsWith("]"))
        assertEquals(schedule, fromJson(response.bodyString()))
    }

    @Test
    fun `throws exception when users id not provided`() {

        val exception = assertThrows<IllegalStateException> {
            getMaintenanceSchedule(mockk())(Request(Method.GET, "/maintenance-schedule"))
        }

        assertEquals("User not authorised", exception.message)

    }
}