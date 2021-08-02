package ge.nika.mcmaintenance.web

import ge.nika.mcmaintenance.service.LogInService
import ge.nika.mcmaintenance.util.toJson
import ge.nika.mcmaintenance.web.auth.RequireSessionAuth
import org.http4k.core.*
import org.http4k.core.HttpHandler
import org.http4k.filter.CorsPolicy
import org.http4k.filter.DebuggingFilters.PrintRequestAndResponse
import org.http4k.filter.ServerFilters.Cors
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes

fun applicationWebEndpoints(logInService: LogInService): HttpHandler =
    PrintRequestAndResponse()
        .then(RequireSessionAuth(logInService))
        .then(Cors(CorsPolicy.UnsafeGlobalPermissive))
        .then(routes(
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