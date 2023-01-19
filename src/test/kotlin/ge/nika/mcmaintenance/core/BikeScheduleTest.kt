package ge.nika.mcmaintenance.core

import ge.nika.mcmaintenance.fixtures.fakeBikeSchedule
import ge.nika.mcmaintenance.fixtures.fakeScheduleItem
import ge.nika.mcmaintenance.fixtures.fakeScheduleItemEntry
import io.kotest.matchers.shouldBe
import java.time.LocalDate
import org.junit.jupiter.api.Test

class BikeScheduleTest {

    @Test
    fun `next service mileage is correctly calculated for a schedule item`() {
        val item = fakeScheduleItem(
            interval = 2000,
            entries = listOf(
                fakeScheduleItemEntry(odometerReading = 1000)
            )
        )

        item.nextServiceMileage shouldBe 3000
    }

    @Test
    fun `next service mileage is null if no entries present`() {
        val item = fakeScheduleItem(interval = 2000)

        item.nextServiceMileage shouldBe null
    }

    @Test
    fun `next service mileage is correctly calculated if item has several entries incorrectly sorted`() {
        val item = fakeScheduleItem(
            interval = 2000,
            entries = listOf(
                fakeScheduleItemEntry(odometerReading = 1000),
                fakeScheduleItemEntry(odometerReading = 500),
                fakeScheduleItemEntry(odometerReading = 3000),
                fakeScheduleItemEntry(odometerReading = 2000),
                fakeScheduleItemEntry(odometerReading = 1500),
            )
        )

        // 3000 + 2000
        item.nextServiceMileage shouldBe 5000
    }

    @Test
    fun `next service date is null if items interval type is distance`() {
        val item = fakeScheduleItem(intervalType = IntervalType.DISTANCE)

        item.nextServiceDate shouldBe null
    }

    @Test
    fun `next service date is correctly calculated for an item with one entry`() {
        val item = fakeScheduleItem(
            intervalType = IntervalType.MONTHS,
            interval = 6,
            entries = listOf(
                fakeScheduleItemEntry(entryDate = LocalDate.parse("2022-01-15")),
            )
        )

        item.nextServiceDate shouldBe LocalDate.parse("2022-07-15")
    }

    @Test
    fun `next service date is null if no entries present`() {
        val item = fakeScheduleItem(intervalType = IntervalType.MONTHS)

        item.nextServiceDate shouldBe null
    }

    @Test
    fun `next service mileage is null if items interval type is months`() {
        val item = fakeScheduleItem()

        item.nextServiceMileage shouldBe null
    }

    @Test
    fun `next service date is correctly calculated if item has several entries incorrectly sorted`() {
        val item = fakeScheduleItem(
            interval = 1,
            intervalType = IntervalType.MONTHS,
            entries = listOf(
                fakeScheduleItemEntry(entryDate = LocalDate.parse("2022-01-15")),
                fakeScheduleItemEntry(entryDate = LocalDate.parse("2023-01-15")),
                fakeScheduleItemEntry(entryDate = LocalDate.parse("2021-01-15")),
                fakeScheduleItemEntry(entryDate = LocalDate.parse("2023-01-14")),
                fakeScheduleItemEntry(entryDate = LocalDate.parse("2022-01-13")),
            )
        )

        // 2023-01-15 + 1 month
        item.nextServiceDate shouldBe LocalDate.parse("2023-02-15")
    }

    @Test
    fun `next service mileage is correctly calculated after conversion`() {
        val schedule = fakeBikeSchedule(
            scheduleItems = listOf(
                fakeScheduleItem(
                    interval = 1000,
                    entries = listOf(
                        fakeScheduleItemEntry(500)
                    )
                )
            )
        )
        schedule.schedule[0].nextServiceMileage shouldBe 1500

        val converted = schedule.convertTo(DistanceUnit.MILES)

        // 311 + 622
        converted.schedule[0].nextServiceMileage shouldBe 933
    }

    @Test
    fun `entries are sorted by date on items with interval type of months`() {
        val item = fakeScheduleItem(
            intervalType = IntervalType.MONTHS,
            entries = listOf(
                fakeScheduleItemEntry(entryDate = LocalDate.parse("2022-01-15")),
                fakeScheduleItemEntry(entryDate = LocalDate.parse("2023-01-15")),
                fakeScheduleItemEntry(entryDate = LocalDate.parse("2021-01-15")),
                fakeScheduleItemEntry(entryDate = LocalDate.parse("2023-01-14")),
                fakeScheduleItemEntry(entryDate = LocalDate.parse("2022-01-13")),
            )
        )

        item.entriesSorted shouldBe listOf(
            fakeScheduleItemEntry(entryDate = LocalDate.parse("2021-01-15")),
            fakeScheduleItemEntry(entryDate = LocalDate.parse("2022-01-13")),
            fakeScheduleItemEntry(entryDate = LocalDate.parse("2022-01-15")),
            fakeScheduleItemEntry(entryDate = LocalDate.parse("2023-01-14")),
            fakeScheduleItemEntry(entryDate = LocalDate.parse("2023-01-15")),
        )
    }

    @Test
    fun `entries are sorted by odometer on items with interval type of distance`() {
        val item = fakeScheduleItem(
            intervalType = IntervalType.DISTANCE,
            entries = listOf(
                fakeScheduleItemEntry(odometerReading = 1000),
                fakeScheduleItemEntry(odometerReading = 500),
                fakeScheduleItemEntry(odometerReading = 3000),
                fakeScheduleItemEntry(odometerReading = 2000),
                fakeScheduleItemEntry(odometerReading = 1500),
            )
        )

        item.entriesSorted shouldBe listOf(
            fakeScheduleItemEntry(odometerReading = 500),
            fakeScheduleItemEntry(odometerReading = 1000),
            fakeScheduleItemEntry(odometerReading = 1500),
            fakeScheduleItemEntry(odometerReading = 2000),
            fakeScheduleItemEntry(odometerReading = 3000),
        )
    }
}