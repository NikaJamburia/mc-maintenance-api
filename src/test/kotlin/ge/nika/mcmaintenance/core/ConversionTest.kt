package ge.nika.mcmaintenance.core

import ge.nika.mcmaintenance.fixtures.fakeBikeSchedule
import ge.nika.mcmaintenance.fixtures.fakeScheduleItem
import ge.nika.mcmaintenance.fixtures.fakeScheduleItemEntry
import io.kotest.assertions.asClue
import io.kotest.matchers.shouldBe
import java.time.LocalDate.now
import org.junit.jupiter.api.Test

class ConversionTest {

    @Test
    fun `schedule in kms is correctly converted to miles`() {
        // Given
        val schedule = fakeBikeSchedule(
            scheduleItems = listOf(
                fakeScheduleItem(
                    name = "Oil change",
                    interval = 3000,
                    intervalType = IntervalType.DISTANCE,
                    entries = listOf(
                        fakeScheduleItemEntry(odometerReading = 1000),
                        fakeScheduleItemEntry(odometerReading = 3000),
                    )
                ),
                fakeScheduleItem(
                    name = "Brake Fluid change",
                    interval = 24,
                    intervalType = IntervalType.MONTHS,
                    entries = listOf(
                        fakeScheduleItemEntry(entryDate = now().minusYears(2), odometerReading = 1000),
                        fakeScheduleItemEntry(entryDate = now(), odometerReading = 3000),
                    )
                )
            )
        )

        // When
        val result = schedule.convertTo(DistanceUnit.MILES)

        // Then
        result.odometerUnits shouldBe DistanceUnit.MILES
        result.lastOdometerReading shouldBe 622

        result.schedule[0].asClue { oilChange ->
            oilChange.interval shouldBe 1865
            oilChange.entries.asClue { oilChangeEntries ->
                oilChangeEntries[0].odometerReading shouldBe 622
                oilChangeEntries[1].odometerReading shouldBe 1865
            }
        }

        // items With interval type of month does not change interval
        result.schedule[1].asClue { brakeFluid ->
            brakeFluid.interval shouldBe 24
            brakeFluid.entries.asClue { brakeFluidChanges ->
                brakeFluidChanges[0].odometerReading shouldBe 622
                brakeFluidChanges[0].entryDate shouldBe now().minusYears(2)
                brakeFluidChanges[1].odometerReading shouldBe 1865
                brakeFluidChanges[1].entryDate shouldBe now()
            }
        }
    }

    @Test
    fun `schedule in miles is correctly converted to kms`() {
        // Given
        val schedule = fakeBikeSchedule(
            odometerUnits = DistanceUnit.MILES,
            lastOdometerReading = 1500,
            scheduleItems = listOf(
                fakeScheduleItem(
                    name = "Oil change",
                    interval = 1000,
                    intervalType = IntervalType.DISTANCE,
                    entries = listOf(
                        fakeScheduleItemEntry(odometerReading = 1000),
                        fakeScheduleItemEntry(odometerReading = 3000),
                    )
                )
            )
        )

        // When
        val result = schedule.convertTo(DistanceUnit.KM)

        // Then
        result.odometerUnits shouldBe DistanceUnit.KM
        result.lastOdometerReading shouldBe 2414

        result.schedule[0].asClue { oilChange ->
            oilChange.interval shouldBe 1609
            oilChange.entries.asClue { oilChangeEntries ->
                oilChangeEntries[0].odometerReading shouldBe 1609
                oilChangeEntries[1].odometerReading shouldBe 4827
            }
        }
    }

    @Test
    fun `converting km schedule to kms does not change anything`() {
        // Given
        val schedule = fakeBikeSchedule(
            scheduleItems = listOf(
                fakeScheduleItem(
                    name = "Oil change",
                    interval = 3000,
                    intervalType = IntervalType.DISTANCE,
                    entries = listOf(
                        fakeScheduleItemEntry(odometerReading = 1000),
                        fakeScheduleItemEntry(odometerReading = 3000),
                    )
                )
            )
        )

        // When
        val result = schedule.convertTo(DistanceUnit.KM)

        // Then
        result.odometerUnits shouldBe DistanceUnit.KM
        result.lastOdometerReading shouldBe 1000

        result.schedule[0].asClue { oilChange ->
            oilChange.interval shouldBe 3000
            oilChange.entries.asClue { oilChangeEntries ->
                oilChangeEntries[0].odometerReading shouldBe 1000
                oilChangeEntries[1].odometerReading shouldBe 3000
            }
        }
    }

    @Test
    fun `converting miles schedule to miles does not change anything`() {
        // Given
        val schedule = fakeBikeSchedule(
            odometerUnits = DistanceUnit.MILES,
            lastOdometerReading = 1500,
            scheduleItems = listOf(
                fakeScheduleItem(
                    name = "Oil change",
                    interval = 1000,
                    intervalType = IntervalType.DISTANCE,
                    entries = listOf(
                        fakeScheduleItemEntry(odometerReading = 1000),
                        fakeScheduleItemEntry(odometerReading = 3000),
                    )
                )
            )
        )

        // When
        val result = schedule.convertTo(DistanceUnit.MILES)

        // Then
        result.odometerUnits shouldBe DistanceUnit.MILES
        result.lastOdometerReading shouldBe 1500

        result.schedule[0].asClue { oilChange ->
            oilChange.interval shouldBe 1000
            oilChange.entries.asClue { oilChangeEntries ->
                oilChangeEntries[0].odometerReading shouldBe 1000
                oilChangeEntries[1].odometerReading shouldBe 3000
            }
        }
    }
}