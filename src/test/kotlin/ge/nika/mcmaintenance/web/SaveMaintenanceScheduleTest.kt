package ge.nika.mcmaintenance.web

import ge.nika.mcmaintenance.core.BikeSchedule
import ge.nika.mcmaintenance.core.BikeScheduleDto
import ge.nika.mcmaintenance.core.toDto
import ge.nika.mcmaintenance.fixtures.fakeBikeSchedule
import ge.nika.mcmaintenance.fixtures.getResourceFile
import ge.nika.mcmaintenance.service.MaintenanceScheduleService
import ge.nika.mcmaintenance.util.asJson
import ge.nika.mcmaintenance.util.fromJson
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalStateException
import kotlin.test.assertEquals

class SaveMaintenanceScheduleTest {

    private val service = mockk<MaintenanceScheduleService> {
        every { saveUsersMaintenanceSchedule(any(), any()) } answers { it.invocation.args[1] as List<BikeScheduleDto> }
    }

    @Test
    fun `returns ok response and saves the schedule`() {
        val schedule = listOf(fakeBikeSchedule().toDto())
        val response = saveMaintenanceSchedule(service)(
            Request(Method.POST, "/maintenance-schedule")
                .header("user-id", "1")
                .body(schedule.asJson())
        )

        assertEquals(Status.OK, response.status)
        assertEquals("application/json", response.header("content-type"))
        assertEquals(schedule, fromJson(response.bodyString()))
        verify(exactly = 1) { service.saveUsersMaintenanceSchedule("1", any()) }
    }

    @Test
    fun `throws exception when users id not provided`() {

        val exception = assertThrows<IllegalStateException> {
            saveMaintenanceSchedule(mockk())(
                Request(Method.POST, "/maintenance-schedule")
                    .body(this.getResourceFile("schedule.json").readText())
            )
        }
        assertEquals("User not authorised", exception.message)

    }
}