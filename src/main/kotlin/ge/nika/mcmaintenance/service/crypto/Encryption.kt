package ge.nika.mcmaintenance.service.crypto

interface Encryption {
    fun getHash(plainText: String): String
    fun matches(plainText: String, hashedText: String): Boolean
}