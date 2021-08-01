package ge.nika.mcmaintenance.repository

import com.mongodb.MongoClient
import com.mongodb.client.MongoCollection
import ge.nika.mcmaintenance.persistence.data.Session
import ge.nika.mcmaintenance.persistence.repository.MongoRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.bson.Document
import org.joda.time.LocalDateTime
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class MongoRepositoryTest {

    private val dbname = "db"

    private val nikasDocument = Document("_id", "123")
        .append("userName", "nika")
        .append("password", "pass123")

    private val sessionDocument = Document("_id", "123")
        .append("userId", "someUser")
        .append("expiresOn", "2021-08-01T12:00:15.000")

    private val usersCollection = mockk<MongoCollection<Document>> {
        every { find(Document("userName", "nika")).first() } returns nikasDocument
        every { find(Document("userName", "beqa")).first() } returns null
    }

    private val sessionsCollection = mockk<MongoCollection<Document>> {
        every { find(Document("_id", "123")).first() } returns sessionDocument
        every { find(Document("_id", "1234")).first() } returns null
        every { insertOne(any()) } returns Unit
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
        assertEquals(session.expiresOn, LocalDateTime.parse("2021-08-01T12:00:15.000"))

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
                    .append("expiresOn", "2021-08-01T12:15:00.000")
            )
        }
    }

}