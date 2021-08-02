package ge.nika.mcmaintenance.web.filter

import org.http4k.core.*
import java.lang.IllegalStateException

class HandleDomainErrors: Filter {

    override fun invoke(next: HttpHandler): HttpHandler = { request: Request ->
        try {
            next(request)
        } catch (e: IllegalStateException) {
            Response(Status.BAD_REQUEST).body(e.message ?: "Error occurred")
        }
    }
}