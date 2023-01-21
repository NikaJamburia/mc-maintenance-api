package ge.nika.mcmaintenance.service.request

import ge.nika.mcmaintenance.core.BikeScheduleDto
import ge.nika.mcmaintenance.core.DistanceUnit

data class ConvertDistanceRequest (
    val schedule: BikeScheduleDto,
    val newUnit: DistanceUnit
)