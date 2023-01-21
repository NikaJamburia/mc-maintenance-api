package ge.nika.mcmaintenance.web

import ge.nika.mcmaintenance.core.BikeSchedule
import ge.nika.mcmaintenance.core.BikeScheduleDto
import ge.nika.mcmaintenance.service.LogInService
import ge.nika.mcmaintenance.service.MaintenanceScheduleService
import ge.nika.mcmaintenance.service.UsersDataService
import ge.nika.mcmaintenance.service.request.ConvertDistanceRequest
import ge.nika.mcmaintenance.service.request.UserCredentials
import ge.nika.mcmaintenance.util.fromJson
import ge.nika.mcmaintenance.web.filter.HandleDomainErrors
import ge.nika.mcmaintenance.web.filter.RequireSessionAuth
import org.http4k.core.*
import org.http4k.core.HttpHandler
import org.http4k.filter.AllowAll
import org.http4k.filter.CorsPolicy
import org.http4k.filter.DebuggingFilters.PrintRequestAndResponse
import org.http4k.filter.OriginPolicy
import org.http4k.filter.ServerFilters.Cors
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import java.time.LocalDateTime.now

fun applicationWebEndpoints(logInService: LogInService, usersDataService: MaintenanceScheduleService): HttpHandler =
    PrintRequestAndResponse()
        .then(Cors(CorsPolicy(OriginPolicy.AllowAll(), listOf("content-type", "session-id"), Method.values().toList(), true)))
        .then(HandleDomainErrors())
        .then(routes(
            logIn(logInService),
            register(logInService),
            RequireSessionAuth(logInService).then(getMaintenanceSchedule(usersDataService)),
            RequireSessionAuth(logInService).then(saveMaintenanceSchedule(usersDataService)),
            RequireSessionAuth(logInService).then(convertDistance(usersDataService)),
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

fun getMaintenanceSchedule(usersDataService: MaintenanceScheduleService): RoutingHttpHandler =
    "/maintenance-schedule" bind Method.GET to { request: Request ->
        val userId = request.header("user-id") ?: error("User not authorised")
        val schedule = usersDataService.getUsersMaintenanceSchedule(userId)
        jsonResponse(Status.OK, schedule)
    }

fun saveMaintenanceSchedule(usersDataService: MaintenanceScheduleService): RoutingHttpHandler =
    "/maintenance-schedule" bind Method.POST to { request: Request ->
        val userId = request.header("user-id") ?: error("User not authorised")
        val schedule: List<BikeScheduleDto> = fromJson(request.bodyString())

        val updated = usersDataService.saveUsersMaintenanceSchedule(userId, schedule)
        jsonResponse(Status.OK, updated)
    }

fun convertDistance(usersDataService: MaintenanceScheduleService): RoutingHttpHandler =
    "/convert-distance" bind Method.POST to { request: Request ->
        val userId = request.header("user-id") ?: error("User not authorised")
        val requestBody: ConvertDistanceRequest = fromJson(request.bodyString())

        val converted = usersDataService.convertDistances(userId, requestBody.schedule, requestBody.newUnit)
        jsonResponse(Status.OK, converted)
    }

