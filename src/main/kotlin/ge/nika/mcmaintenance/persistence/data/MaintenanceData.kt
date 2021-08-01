package ge.nika.mcmaintenance.persistence.data

import org.joda.time.LocalDate

data class BikeSchedule(
    val bikeName: String,
    val lastOdometerReading: Long,
    val bikeImage: String,
    val schedule: List<ScheduleItem>
)

data class ScheduleItem(
    val name: String,
    val interval: Long,
    val entries: List<ScheduleItemEntry>
)

data class ScheduleItemEntry(
    val odometerReading: Long,
    val entryDate: LocalDate
)