package ge.nika.mcmaintenance

import java.net.URL

fun Any.getResourceFile(fileName: String): URL =
    this::class.java.classLoader.getResource(fileName)!!