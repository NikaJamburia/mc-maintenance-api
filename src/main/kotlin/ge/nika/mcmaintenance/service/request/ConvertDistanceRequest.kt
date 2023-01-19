package ge.nika.mcmaintenance.service.request

import ge.nika.mcmaintenance.core.BikeSchedule
import ge.nika.mcmaintenance.core.DistanceUnit

data class ConvertDistanceRequest (
    val schedule: BikeSchedule,
    val newUnit: DistanceUnit
)