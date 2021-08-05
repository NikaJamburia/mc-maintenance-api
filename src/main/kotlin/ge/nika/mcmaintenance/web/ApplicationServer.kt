package ge.nika.mcmaintenance.web

import ge.nika.mcmaintenance.persistence.data.BikeSchedule
import ge.nika.mcmaintenance.service.LogInService
import ge.nika.mcmaintenance.service.UsersDataService
import ge.nika.mcmaintenance.service.request.UserCredentials
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
import org.joda.time.LocalDateTime.now

fun applicationWebEndpoints(logInService: LogInService, usersDataService: UsersDataService): HttpHandler =
    PrintRequestAndResponse()
        .then(HandleDomainErrors())
        .then(Cors(CorsPolicy.UnsafeGlobalPermissive))
        .then(routes(
            logIn(logInService),
            register(logInService),
            RequireSessionAuth(logInService).then(getMaintenanceSchedule(usersDataService)),
            RequireSessionAuth(logInService).then(saveMaintenanceSchedule(usersDataService))
        ))


fun logIn(logInService: LogInService): RoutingHttpHandler =
    "/login" bind Method.POST to { request: Request ->
        val userCredentials: UserCredentials = fromJson(request.bodyString())

        val session = logInService.logIn(userCredentials, now())
        jsonResponse(Status.OK, session)
    }

fun register(logInService: LogInService): RoutingHttpHandler =
    "/register" bind Method.POST to { request: Request ->
        val userCredentials: UserCredentials = fromJson(request.bodyString())
        val user = logInService.register(userCredentials)
        jsonResponse(Status.OK, user)
    }

fun getMaintenanceSchedule(usersDataService: UsersDataService): RoutingHttpHandler =
    "/maintenance-schedule" bind Method.GET to { request: Request ->
        val userId = request.header("user-id") ?: error("User not authorised")
        val schedule = usersDataService.getUsersMaintenanceSchedule(userId)
        jsonResponse(Status.OK, schedule)
    }

fun saveMaintenanceSchedule(usersDataService: UsersDataService): RoutingHttpHandler =
    "/maintenance-schedule" bind Method.POST to { request: Request ->
        val userId = request.header("user-id") ?: error("User not authorised")
        val schedule: List<BikeSchedule> = fromJson(request.bodyString())

        usersDataService.saveUsersMaintenanceSchedule(userId, schedule)
        jsonResponse(Status.OK, SingleMessageResponse("schedule saved"))
    }

