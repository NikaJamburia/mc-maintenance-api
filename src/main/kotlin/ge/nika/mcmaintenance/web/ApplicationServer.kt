package ge.nika.mcmaintenance.web

import ge.nika.mcmaintenance.util.toJson
import org.http4k.core.*
import org.http4k.core.HttpHandler
import org.http4k.filter.DebuggingFilters
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes

fun applicationWebEndpoints(): HttpHandler =  DebuggingFilters.PrintRequestAndResponse().then(routes(
    logIn()
))


fun logIn(): RoutingHttpHandler =
    "/login" bind Method.POST to { request: Request ->
        Response(Status.OK)
            .header("content-type", "application/json")
            .body("""{ "msg": "ok" }""")
    }


fun jsonResponse(status: Status, data: Any): Response =
    Response(status)
        .header("content-type", "application/json")
        .body(toJson(data))