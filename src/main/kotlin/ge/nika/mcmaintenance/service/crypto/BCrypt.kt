package ge.nika.mcmaintenance.service.crypto

import org.springframework.security.crypto.bcrypt.BCrypt


class BCrypt : Encryption {
    override fun getHash(plainText: String): String = BCrypt.hashpw(plainText, BCrypt.gensalt())
    override fun matches(plainText: String, hashedText: String): Boolean = BCrypt.checkpw(plainText, hashedText)
}