package ge.nika.mcmaintenance.service.crypto

import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class BCryptTest {

    @Test
    fun `can hash and match the given text`() {
        println(UUID.randomUUID())
        val password = "Enterance161@"
        val hashedPassword = BCrypt().getHash(password)

        println(hashedPassword)
        assertNotEquals(password, hashedPassword)
        assertTrue(BCrypt().matches(password, hashedPassword))
    }

    @Test
    fun `should get different hash every time but still match every hash`() {
        val password = "nika123"
        val hashedPassword1 = BCrypt().getHash(password)
        val hashedPassword2 = BCrypt().getHash(password)
        val hashedPassword3 = BCrypt().getHash(password)

        assertNotEquals(hashedPassword1, hashedPassword2)
        assertNotEquals(hashedPassword1, hashedPassword3)
        assertNotEquals(hashedPassword2, hashedPassword3)

        assertTrue(BCrypt().matches(password, hashedPassword1))
        assertTrue(BCrypt().matches(password, hashedPassword2))
        assertTrue(BCrypt().matches(password, hashedPassword3))
    }
}