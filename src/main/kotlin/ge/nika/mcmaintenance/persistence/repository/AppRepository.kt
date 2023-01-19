package ge.nika.mcmaintenance.persistence.repository

import ge.nika.mcmaintenance.core.BikeSchedule
import ge.nika.mcmaintenance.persistence.data.Session
import ge.nika.mcmaintenance.persistence.data.User

interface AppRepository {
    fun getUserByUserName(userName: String): User?
    fun getSessionById(sessionId: String): Session?
    fun getUsersMaintenanceSchedules(userId: String): List<BikeSchedule>
    fun insertUsersMaintenanceData(userId: String, data: List<BikeSchedule>)
    fun saveSession(session: Session)
    fun saveUser(user: User)
}