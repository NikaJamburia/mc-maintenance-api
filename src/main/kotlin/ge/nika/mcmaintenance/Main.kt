package ge.nika.mcmaintenance

import ge.nika.mcmaintenance.web.applicationWebEndpoints
import org.http4k.server.Netty
import org.http4k.server.asServer

fun main() {
    applicationWebEndpoints().asServer(Netty(8080)).start()
}