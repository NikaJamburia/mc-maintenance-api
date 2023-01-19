package ge.nika.mcmaintenance.fixtures

import ge.nika.mcmaintenance.core.*
import java.time.LocalDate

fun fakeBikeSchedule(
    bikeName: String = "Honda",
    bikeImage: String = "unknown.png",
    lastOdometerReading: Long = 1000,
    odometerUnits: DistanceUnit = DistanceUnit.KM,
    scheduleItems: List<ScheduleItem> = emptyList()
): BikeSchedule = BikeSchedule(
    bikeName = bikeName,
    bikeImage = bikeImage,
    lastOdometerReading = lastOdometerReading,
    odometerUnits = odometerUnits,
    schedule = scheduleItems
)

fun fakeScheduleItem(
    name: String = "Oil change 1",
    interval: Long = 1000,
    intervalType: IntervalType = IntervalType.DISTANCE,
    entries: List<ScheduleItemEntry> = emptyList(),
): ScheduleItem = ScheduleItem(
    name = name,
    interval = interval,
    intervalType = intervalType,
    entries = entries,
)

fun fakeScheduleItemEntry(
    odometerReading: Long = 1000,
    entryDate: LocalDate = LocalDate.now()
): ScheduleItemEntry = ScheduleItemEntry(
    odometerReading = odometerReading,
    entryDate = entryDate
)