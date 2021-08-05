package ge.nika.mcmaintenance.repository

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.result.InsertOneResult
import com.mongodb.client.result.UpdateResult
import ge.nika.mcmaintenance.getResourceFile
import ge.nika.mcmaintenance.persistence.data.*
import ge.nika.mcmaintenance.persistence.repository.MongoRepository
import ge.nika.mcmaintenance.util.fromJson
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.bson.BsonArray
import org.bson.BsonString
import org.bson.Document
import org.bson.conversions.Bson
import org.joda.time.LocalDateTime
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalStateException
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MongoRepositoryTest {

    private val dbname = "db"

    private val nikasDocument = Document("_id", "123")
        .append("userName", "nika")
        .append("password", "pass123")
        .append("bikeSchedules", BsonArray.parse(this.getResourceFile("schedule.json").readText()))

    private val noScheduleUser = Document("_id", "no schedule")
        .append("userName", "aaaaa")
        .append("password", "pass123")

    private val sessionDocument = Document("_id", "123")
        .append("userId", "someUser")
        .append("expiresOn", "[2021,8,2,16,34,31,240]")

    private val usersCollection = mockk<MongoCollection<Document>> {
        every { find(Document("userName", "nika")).first() } returns nikasDocument
        every { find(Document("_id", "123")).first() } returns nikasDocument
        every { find(Document("_id", "1234")).first() } returns nikasDocument
        every { find(Document("_id", "noUser")).first() } returns null
        every { find(Document("userName", "beqa")).first() } returns null
        every { replaceOne(Document("_id", "123"), any()) } returns UpdateResult.acknowledged(1, 1, BsonString("aaa"))
        every { replaceOne(Document("_id", "1234"), any()) } returns UpdateResult.acknowledged(0, 1, BsonString("aaa"))
        every { insertOne(any()) } returns InsertOneResult.acknowledged(BsonString("aaa"))
        every { find(Document("_id", "no schedule")).first() } returns noScheduleUser
    }

    private val sessionsCollection = mockk<MongoCollection<Document>> {
        every { find(Document("_id", "123")).first() } returns sessionDocument
        every { find(Document("_id", "1234")).first() } returns null
        every { insertOne(any()) } returns InsertOneResult.acknowledged(BsonString("aaa"))
    }

    private val mongoClient = mockk<MongoClient> {
        every { getDatabase(dbname) } returns mockk {
            every { getCollection("users") } returns usersCollection
            every { getCollection("sessions") } returns sessionsCollection
        }
    }

    private val repository = MongoRepository(mongoClient, dbname)

    @Test
    fun `finds user by username`() {
        val user = repository.getUserByUserName("nika")
        assertNotNull(user)
        assertEquals(user.id, "123")
        assertEquals(user.password, "pass123")
        assertEquals(user.userName, "nika")
    }

    @Test
    fun `returns null when client not found`() {
        val user = repository.getUserByUserName("beqa")
        assertNull(user)
    }

    @Test
    fun `finds session by id`() {
        val session = repository.getSessionById("123")
        assertNotNull(session)
        assertEquals(session.id, "123")
        assertEquals(session.userId, "someUser")
        assertEquals(session.expiresOn, LocalDateTime.parse("2021-08-02T16:34:31.240"))

    }

    @Test
    fun `returns null when session not found`() {
        val session = repository.getSessionById("1234")
        assertNull(session)
    }

    @Test
    fun `correctly serializes and saves session`() {
        repository.saveSession(Session("s1", "u1", LocalDateTime.parse("2021-08-01T12:15:00")))
        verify(exactly = 1) {
            sessionsCollection.insertOne(
                Document("_id", "s1")
                    .append("userId", "u1")
                        // [2021,8,1,12,15,0,0]
                    .append("expiresOn", "[2021,8,1,12,15,0,0]")
            )
        }
    }

    @Test
    fun `gets users bike schedules`() {
        val schedule = repository.getUsersMaintenanceSchedules("123")

        assertTrue(schedule.isNotEmpty())
        assertEquals("cbr250", schedule[0].bikeName)

    }

    @Test
    fun `returns empty list when user does not exist`() {
        val schedule = repository.getUsersMaintenanceSchedules("noUser")
        assertTrue(schedule.isEmpty())
    }

    @Test
    fun `saves users schedule`() {
        val scheduleItem = fromJson<BikeSchedule>(this.getResourceFile("schedule-item.json").readText())
        repository.insertUsersMaintenanceData("123", listOf(scheduleItem))
        verify(exactly = 1) { usersCollection.replaceOne(Document("_id", "123"), any()) }
    }

    @Test
    fun `throws error if couldnt update`() {
        val scheduleItem = fromJson<BikeSchedule>(this.getResourceFile("schedule-item.json").readText())
        assertThrows<IllegalStateException> { repository.insertUsersMaintenanceData("1234", listOf(scheduleItem)) }
    }

    @Test
    fun `saves user`() {
        val user = User("userId", "userName", "pass")

        repository.saveUser(user)
        verify(exactly = 1) { usersCollection.insertOne(
            Document("_id", user.id)
                .append("userName", user.userName)
                .append("password", user.password)
        ) }
    }

    @Test
    fun `returns empty list if user has no schedule`() {
        assertTrue(repository.getUsersMaintenanceSchedules("no schedule").isEmpty())
    }

}