package ge.nika.mcmaintenance.web

import ge.nika.mcmaintenance.util.toJson
import org.http4k.core.Response
import org.http4k.core.Status

fun jsonResponse(status: Status, data: Any): Response =
    Response(status)
        .header("content-type", "application/json")
        .body(toJson(data))

fun forbidden() = jsonResponse(Status.FORBIDDEN, SingleMessageResponse("Access forbidden"))

data class SingleMessageResponse(val message: String)