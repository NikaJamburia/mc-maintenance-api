package ge.nika.mcmaintenance.service

import ge.nika.mcmaintenance.core.BikeSchedule
import ge.nika.mcmaintenance.core.DistanceUnit
import ge.nika.mcmaintenance.core.convertTo
import ge.nika.mcmaintenance.persistence.repository.AppRepository

class UsersDataService(
    private val repository: AppRepository
) {

    fun getUsersMaintenanceSchedule(userId: String): List<BikeSchedule> {
        return repository.getUsersMaintenanceSchedules(userId)
    }

    fun saveUsersMaintenanceSchedule(userId: String, data: List<BikeSchedule>): List<BikeSchedule> {
        repository.insertUsersMaintenanceData(userId, data)
        return data
    }

    fun convertDistances(bikeSchedule: BikeSchedule, newDistanceUnit: DistanceUnit): BikeSchedule {
        return bikeSchedule.convertTo(newDistanceUnit)
    }
}