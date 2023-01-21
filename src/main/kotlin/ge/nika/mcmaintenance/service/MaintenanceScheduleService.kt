package ge.nika.mcmaintenance.service

import ge.nika.mcmaintenance.core.*
import ge.nika.mcmaintenance.persistence.repository.AppRepository
import java.time.LocalDate

class MaintenanceScheduleService(
    private val repository: AppRepository
) {

    fun getUsersMaintenanceSchedule(userId: String): List<BikeScheduleDto> {
        return repository.getUsersMaintenanceSchedules(userId).map { it.toDto() }
    }

    fun saveUsersMaintenanceSchedule(userId: String, data: List<BikeScheduleDto>): List<BikeScheduleDto> {
        val updated = data.map { newBikeSchedule ->
            BikeSchedule(
                bikeName = newBikeSchedule.bikeName,
                bikeImage = newBikeSchedule.bikeImage,
                lastOdometerReading = newBikeSchedule.lastOdometerReading,
                odometerUnits = newBikeSchedule.odometerUnits,
                schedule = newBikeSchedule.schedule.map { scheduleItemDto ->
                    ScheduleItem(
                        name = scheduleItemDto.name,
                        intervalType = scheduleItemDto.intervalType,
                        interval = scheduleItemDto.interval,
                        entries = scheduleItemDto.entries.map {
                            ScheduleItemEntry(it.odometerReading, LocalDate.parse(it.entryDate))
                        }
                    )
                }
            )
        }
        repository.insertUsersMaintenanceData(userId, updated)
        return updated.map { it.toDto() }
    }

    fun convertDistances(userId: String, bikeSchedule: BikeScheduleDto, newDistanceUnit: DistanceUnit): BikeScheduleDto =
        repository.getUsersMaintenanceSchedules(userId)
            .first { it.bikeName == bikeSchedule.bikeName }
            .convertTo(newDistanceUnit)
            .toDto()
}