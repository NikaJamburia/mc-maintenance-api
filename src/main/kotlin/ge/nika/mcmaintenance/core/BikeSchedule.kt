package ge.nika.mcmaintenance.core

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.LocalDate

data class BikeSchedule(
    val bikeName: String,
    val lastOdometerReading: Long,
    val odometerUnits: DistanceUnit,
    val bikeImage: String,
    val schedule: List<ScheduleItem>
) {
    init {
        check(lastOdometerReading >= 0) {
            "Last odometer reading must be positive number!"
        }
    }
}

@JsonIgnoreProperties(value = ["nextServiceMileage", "nextServiceDate", "entriesSorted"], allowGetters = true)
data class ScheduleItem(
    val name: String,
    val intervalType: IntervalType,
    val interval: Long,
    val entries: List<ScheduleItemEntry>
) {

    init {
        check(interval >= 0) {
            "Interval must be positive number!"
        }
    }

    val nextServiceMileage: Long? = lastEntryByMileage()
            ?.takeIf { intervalType == IntervalType.DISTANCE }
            ?.let { it.odometerReading + interval }

    val nextServiceDate: LocalDate? = lastEntryByDate()
            ?.takeIf { intervalType == IntervalType.MONTHS }
            ?.entryDate?.plusMonths(interval)

    val entriesSorted: List<ScheduleItemEntry> = if (intervalType == IntervalType.DISTANCE) {
            entries.sortedBy { it.odometerReading }
        } else {
            entries.sortedBy { it.entryDate }
        }

    private fun lastEntryByDate(): ScheduleItemEntry? = entries.maxByOrNull { it.entryDate }
    private fun lastEntryByMileage(): ScheduleItemEntry? = entries.maxByOrNull { it.odometerReading }
}

data class ScheduleItemEntry(
    val odometerReading: Long,
    val entryDate: LocalDate
) {
    init {
        check(odometerReading >= 0) {
            "odometerReading must be positive number!"
        }
    }
}