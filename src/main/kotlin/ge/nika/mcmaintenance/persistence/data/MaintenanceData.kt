package ge.nika.mcmaintenance.persistence.data

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.joda.time.LocalDate

@JsonIgnoreProperties(ignoreUnknown = true)
data class UsersScheduleData(
    val bikeSchedules: List<BikeSchedule>
)

data class BikeSchedule(
    val bikeName: String,
    val lastOdometerReading: OdometerReading,
    val bikeImage: String,
    val schedule: List<ScheduleItem>
)

data class ScheduleItem(
    val name: String,
    val interval: OdometerReading,
    val entries: List<ScheduleItemEntry>
)

data class ScheduleItemEntry(
    val odometerReading: OdometerReading,
    val entryDate: LocalDate
)

data class OdometerReading(
    val value: Long,
    val unit: LengthUnit
)

enum class LengthUnit {
    KM, MILES
}

fun miles(number: Long) = OdometerReading(number, LengthUnit.MILES)
fun km(number: Long) = OdometerReading(number, LengthUnit.KM)