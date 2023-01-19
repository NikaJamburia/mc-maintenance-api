package ge.nika.mcmaintenance.persistence.data

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import ge.nika.mcmaintenance.core.BikeSchedule
import ge.nika.mcmaintenance.core.DistanceUnit
import ge.nika.mcmaintenance.core.IntervalType
import ge.nika.mcmaintenance.core.ScheduleItemEntry
import java.time.LocalDate

@JsonIgnoreProperties(ignoreUnknown = true)
data class UsersScheduleData(
    val bikeSchedules: List<BikeSchedule>?
)