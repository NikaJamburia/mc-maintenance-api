package ge.nika.mcmaintenance.service

import ge.nika.mcmaintenance.persistence.repository.AppRepository

class UsersDataService(
    private val repository: AppRepository
) {

    fun getUsersDataAsJson(userId: String): String {
//        return repository.getUsersMaintenanceSchedules(userId) ?: error("Data not found!")
        return "aaa"
    }

    fun insertUsersDataJson(userId: String, data: String) {
//        repository.insertUsersMaintenanceData(userId, data)
    }
}