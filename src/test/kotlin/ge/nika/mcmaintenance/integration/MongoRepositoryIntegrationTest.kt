package ge.nika.mcmaintenance.integration

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import ge.nika.mcmaintenance.persistence.data.Session
import ge.nika.mcmaintenance.persistence.repository.MongoRepository
import org.bson.Document
import org.joda.time.LocalDateTime.now
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID.randomUUID
import kotlin.test.assertEquals

class MongoRepositoryIntegrationTest {

    private val dbUrl = "mongodb+srv://bikeApp:bikeapp123@cluster0.h6uoc.mongodb.net/motorcycle_maintenance_schedules?retryWrites=true&w=majority"
    private val dbName = "motorcycle_maintenance_schedules"
    private val mongoClient: MongoClient = MongoClients.create(dbUrl)
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
}