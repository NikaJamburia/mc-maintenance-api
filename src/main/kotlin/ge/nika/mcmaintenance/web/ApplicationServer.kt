package ge.nika.mcmaintenance.web

import ge.nika.mcmaintenance.service.LogInService
import ge.nika.mcmaintenance.service.request.LogInRequest
import ge.nika.mcmaintenance.util.fromJson
import ge.nika.mcmaintenance.web.filter.HandleDomainErrors
import ge.nika.mcmaintenance.web.filter.RequireSessionAuth
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
        .then(HandleDomainErrors())
        .then(RequireSessionAuth(logInService))
        .then(Cors(CorsPolicy.UnsafeGlobalPermissive))
        .then(routes(
            logIn(logInService)
        ))


fun logIn(logInService: LogInService): RoutingHttpHandler =
    "/login" bind Method.POST to { request: Request ->
        val logInRequest: LogInRequest = fromJson(request.bodyString())

        val session = logInService.logIn(logInRequest)
        jsonResponse(Status.OK, session)
    }

