package ge.nika.mcmaintenance.web.auth

import ge.nika.mcmaintenance.service.LogInService
import org.http4k.core.*

class RequireSessionAuth(
    private val logInService: LogInService
): Filter {
    override fun invoke(next: HttpHandler): HttpHandler =
        { request: Request ->
            request.header("session-id")
                ?. let {
                    if(logInService.isValidSession(it)) {
                        next(request)
                    } else {
                        forbidden()
                    }
                } ?: forbidden()
        }


    private fun forbidden() = Response(Status.FORBIDDEN).body("Access forbidden")
}