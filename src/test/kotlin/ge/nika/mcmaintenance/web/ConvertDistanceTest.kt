package ge.nika.mcmaintenance.web

import ge.nika.mcmaintenance.core.BikeSchedule
import ge.nika.mcmaintenance.core.DistanceUnit.MILES
import ge.nika.mcmaintenance.core.convertTo
import ge.nika.mcmaintenance.core.toDto
import ge.nika.mcmaintenance.fixtures.fakeBikeSchedule
import ge.nika.mcmaintenance.service.MaintenanceScheduleService
import ge.nika.mcmaintenance.service.request.ConvertDistanceRequest
import ge.nika.mcmaintenance.util.fromJson
import ge.nika.mcmaintenance.util.toJson
import io.kotest.assertions.asClue
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.junit.jupiter.api.Test

class ConvertDistanceTest {
    @Test
    fun `returns json of the schedule`() {

        val schedule = fakeBikeSchedule()
        val scheduleDto = schedule.toDto()

        val service: MaintenanceScheduleService = mockk {
            every { convertDistances("1", scheduleDto, MILES) } returns schedule.convertTo(MILES).toDto()
        }

        val response = convertDistance(service)(
            Request(Method.POST, "/convert-distance")
                .header("user-id", "1")
                .body(toJson(ConvertDistanceRequest(
                    schedule = scheduleDto,
                    newUnit = MILES
                )))
        )

        response.status shouldBe Status.OK
        response.header("content-type") shouldBe "application/json"
        fromJson<BikeSchedule>(response.bodyString()).asClue { body ->
            body.lastOdometerReading shouldBe 622
            body.odometerUnits shouldBe MILES
        }

    }
}