package ge.nika.mcmaintenance.integration

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import ge.nika.mcmaintenance.getResourceFile
import ge.nika.mcmaintenance.persistence.data.*
import ge.nika.mcmaintenance.persistence.repository.MongoRepository
import org.bson.Document
import org.joda.time.LocalDateTime.now
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*
import java.util.UUID.randomUUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MongoRepositoryIntegrationTest {

    private val props = loadTestProperties()
    private val dbName = props.getProperty("db.name")
    private val mongoClient: MongoClient = MongoClients.create(props.getProperty("db.connection-string"))
    private val repository = MongoRepository(mongoClient, dbName)

    @BeforeEach
    fun clearDatabase() {
        println("Clearing database")
        mongoClient.getDatabase(dbName).getCollection("sessions").deleteMany(Document())
        mongoClient.getDatabase(dbName).getCollection("users").deleteMany(Document())
    }

    @Test
    fun `session is saved and can be retrieved by id`() {
        val session = Session(randomUUID().toString(), randomUUID().toString(), now().plusMinutes(15))
        repository.saveSession(session)
        val sessionFromDb = repository.getSessionById(session.id)!!

        assertEquals(session.id, sessionFromDb.id)
        assertEquals(session.userId, sessionFromDb.userId)
        assertEquals(session.expiresOn, sessionFromDb.expiresOn)
    }

    @Test
    fun `correctly retrieves user by username`() {
        insertUser("nika", "111")

        val user = repository.getUserByUserName("nika")
        assertNotNull(user)
        assertEquals("nika", user.userName)
        assertEquals("111", user.id)
        assertEquals("123", user.password)
    }

    @Test
    fun `inserts and retrieves users maintenance data`() {
        insertUser("vigaca", "123")

        val schedule = listOf(
            BikeSchedule("cb1100", miles(1000), "smth.png", listOf(
                ScheduleItem("oil change", miles(3000), listOf())
            ))
        )
        repository.insertUsersMaintenanceData("123", schedule)
        val scheduleFromDb = repository.getUsersMaintenanceSchedules("123")

        assertTrue(scheduleFromDb.isNotEmpty())
        assertEquals(scheduleFromDb[0], schedule[0])
    }

    @Test
    fun `throws error if inserting schedule for non existant user`() {
        assertThrows<IllegalStateException> { repository.insertUsersMaintenanceData("123", listOf()) }
    }

    @Test
    fun `can save user`() {
        val newUser = User("123", "nikanika", "passsss")
        repository.saveUser(newUser)

        val userFromDb = repository.getUserByUserName("nikanika")
        assertEquals(newUser, userFromDb)
        assertTrue(repository.getUsersMaintenanceSchedules("123").isEmpty())
    }


    private fun insertUser(userName: String, id: String) {
        mongoClient.getDatabase(dbName).getCollection("users").insertOne(
            Document("_id", id)
                .append("userName", userName)
                .append("password", "123")
        )
    }

    private fun loadTestProperties(): Properties {
        val props = Properties()
        props.load(this.getResourceFile("db-test.properties").openStream())
        return props
    }
}