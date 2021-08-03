package ge.nika.mcmaintenance.service

import ge.nika.mcmaintenance.persistence.data.BikeSchedule
import ge.nika.mcmaintenance.persistence.repository.AppRepository

class UsersDataService(
    private val repository: AppRepository
) {

    fun getUsersMaintenanceSchedule(userId: String): List<BikeSchedule> {
        return repository.getUsersMaintenanceSchedules(userId)
    }

    fun saveUsersMaintenanceSchedule(userId: String, data: List<BikeSchedule>) {
        repository.insertUsersMaintenanceData(userId, data)
    }
}