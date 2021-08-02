package ge.nika.mcmaintenance.web.filter

import ge.nika.mcmaintenance.service.LogInService
import ge.nika.mcmaintenance.web.forbidden
import org.http4k.core.*
import org.joda.time.LocalDateTime

class RequireSessionAuth(
    private val logInService: LogInService
): Filter {
    override fun invoke(next: HttpHandler): HttpHandler =
        { request: Request ->
            request.header("session-id")
                ?. let {
                    logInService.getSessionIfValid(it, LocalDateTime.now())
                        ?. let { session ->
                            next(request.header("user-id", session.userId))
                        } ?: forbidden()
                } ?: forbidden()
        }


}