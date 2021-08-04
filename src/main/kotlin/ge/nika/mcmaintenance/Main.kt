package ge.nika.mcmaintenance

import com.mongodb.client.MongoClients
import ge.nika.mcmaintenance.persistence.repository.MongoRepository
import ge.nika.mcmaintenance.service.LogInService
import ge.nika.mcmaintenance.service.UsersDataService
import ge.nika.mcmaintenance.service.crypto.BCrypt
import ge.nika.mcmaintenance.web.applicationWebEndpoints
import org.http4k.server.Netty
import org.http4k.server.asServer
import java.util.*

fun main() {

    val properties = loadProperties("application.properties")
    val applicationPort = properties.getProperty("app.port").toInt()

    val mongoClient = MongoClients.create(properties.getProperty("db.connection-string"))
    val repository = MongoRepository(mongoClient, properties.getProperty("db.name"))
    val logInService = LogInService(repository, BCrypt(), properties.getProperty("app.session-valid-for-minutes").toInt())
    val usersDataService = UsersDataService(repository)

    println("Starting server on port $applicationPort")
    applicationWebEndpoints(logInService, usersDataService)
        .asServer(Netty(applicationPort))
        .start()
}

fun loadProperties(fileName: String): Properties {
    val props = Properties()
    props.load(object {}::class.java.classLoader.getResource(fileName)!!.openStream())
    return props
}
