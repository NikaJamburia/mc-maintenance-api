package ge.nika.mcmaintenance.persistence.repository

import com.mongodb.MongoClient
import ge.nika.mcmaintenance.persistence.data.BikeSchedule
import ge.nika.mcmaintenance.persistence.data.Session
import ge.nika.mcmaintenance.persistence.data.User
import ge.nika.mcmaintenance.util.toJson
import org.bson.Document
import org.joda.time.LocalDateTime

class MongoRepository(
    private val mongoClient: MongoClient,
    private val dbName: String
) : AppRepository {

    override fun getUserByUserName(userName: String): User? {
        val userDocument: Document? =  usersCollection()
            .find(Document("userName", userName))
            .first()

        return userDocument ?. let {
            User(it.getString("_id"), it.getString("userName"), it.getString("password"))
        }
    }

    override fun getSessionById(sessionId: String): Session? {
        val sessionDocument: Document? = sessionsCollection()
            .find(Document("_id", sessionId))
            .first()

        return sessionDocument ?. let {
            Session(it.getString("_id"), it.getString("userId"), LocalDateTime.parse(it.getString("expiresOn")))
        }
    }

    override fun getUsersMaintenanceSchedules(userId: String): List<BikeSchedule> {
        val userDocument: Document? =  usersCollection()
            .find(Document("_id", userId))
            .first()

        return userDocument
            ?. getList("bikeSchedules", BikeSchedule::class.java)
            ?: listOf()
    }


    override fun insertUsersMaintenanceData(userId: String, data: List<BikeSchedule>) {
        val usersDocument = usersCollection().find(Document("_id", userId)).first() ?: error("User not found")
        usersDocument["bikeSchedules"] = Document.parse(toJson(data))

    }

    override fun saveSession(session: Session) {
        val sessionDocument = Document("_id", session.id)
            .append("userId", session.userId)
            .append("expiresOn", session.expiresOn.toString())
        sessionsCollection().insertOne(sessionDocument)
    }

    private fun sessionsCollection() = mongoClient.getDatabase(dbName).getCollection("sessions")
    private fun usersCollection() = mongoClient.getDatabase(dbName).getCollection("users")

}