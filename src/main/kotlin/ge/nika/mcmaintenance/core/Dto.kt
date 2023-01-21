package ge.nika.mcmaintenance.core

data class BikeScheduleDto(
    val bikeName: String,
    val lastOdometerReading: Long,
    val odometerUnits: DistanceUnit,
    val bikeImage: String,
    val schedule: List<ScheduleItemDto>
)

data class ScheduleItemDto(
    val name: String,
    val intervalType: IntervalType,
    val interval: Long,
    val entries: List<ScheduleItemEntryDto>,
    val nextServiceMileage: Long?,
    val nextServiceDate: String?,
)

data class ScheduleItemEntryDto(
    val odometerReading: Long,
    val entryDate: String
)

fun BikeSchedule.toDto(): BikeScheduleDto = BikeScheduleDto(
    bikeName,
    lastOdometerReading,
    odometerUnits,
    bikeImage,
    schedule.map { scheduleItem ->
        ScheduleItemDto(
            name = scheduleItem.name,
            intervalType = scheduleItem.intervalType,
            interval = scheduleItem.interval,
            nextServiceDate = scheduleItem.nextServiceDate.toString(),
            nextServiceMileage = scheduleItem.nextServiceMileage,
            entries = scheduleItem.entriesSorted.map { entry ->
                ScheduleItemEntryDto(entry.odometerReading, entry.entryDate.toString())
            }
        )
    }
)