package ge.nika.mcmaintenance.web.filter

import ge.nika.mcmaintenance.web.SingleMessageResponse
import ge.nika.mcmaintenance.web.jsonResponse
import org.http4k.core.*
import java.lang.IllegalStateException

class HandleDomainErrors: Filter {

    override fun invoke(next: HttpHandler): HttpHandler = { request: Request ->
        try {
            next(request)
        } catch (e: IllegalStateException) {
            jsonResponse(Status.BAD_REQUEST, SingleMessageResponse(e.message ?: "Error occurred"))
        }
    }
}