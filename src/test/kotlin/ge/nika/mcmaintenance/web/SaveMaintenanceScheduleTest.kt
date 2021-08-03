package ge.nika.mcmaintenance.web

import ge.nika.mcmaintenance.getResourceFile
import ge.nika.mcmaintenance.service.UsersDataService
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
import kotlin.test.assertTrue

class SaveMaintenanceScheduleTest {

    private val service = mockk<UsersDataService> {
        every { saveUsersMaintenanceSchedule(any(), any()) } returns Unit
    }

    @Test
    fun `returns ok response and saves the schedule`() {
        val response = saveMaintenanceSchedule(service)(
            Request(Method.POST, "/maintenance-schedule")
                .header("user-id", "1")
                .body(this.getResourceFile("schedule.json").readText())
        )

        assertEquals(Status.OK, response.status)
        assertEquals("application/json", response.header("content-type"))
        assertEquals("""{"message": "schedule saved"}""", response.bodyString())
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