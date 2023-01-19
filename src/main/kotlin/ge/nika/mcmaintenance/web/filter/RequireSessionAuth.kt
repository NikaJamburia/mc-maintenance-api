package ge.nika.mcmaintenance.web.filter

import ge.nika.mcmaintenance.persistence.data.Session
import ge.nika.mcmaintenance.service.LogInService
import ge.nika.mcmaintenance.web.forbidden
import org.http4k.core.*
import java.time.LocalDateTime

class RequireSessionAuth(
    private val logInService: LogInService
): Filter {
    override fun invoke(next: HttpHandler): HttpHandler =
        { request: Request ->
            request.header("session-id")
                ?. let {
                    logInService.getSession(it)
                        ?. let { session ->
                            if(isValidSession(session, LocalDateTime.now())) {
                                next(request.header("user-id", session.userId))
                            } else {
                                forbidden("Session Expired")
                            }
                        } ?: forbidden("No Session found")
                } ?: forbidden("Access forbidden")
        }

    private fun isValidSession(session: Session, checkTime: LocalDateTime): Boolean = !session.expiresOn.isBefore(checkTime)

}