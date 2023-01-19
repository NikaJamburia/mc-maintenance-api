package ge.nika.mcmaintenance.core

import ge.nika.mcmaintenance.core.DistanceUnit.*
import kotlin.math.roundToLong

fun BikeSchedule.convertTo(unit: DistanceUnit): BikeSchedule {
    val conversionFun = conversionFunctions[this.odometerUnits to unit]

    checkNotNull(conversionFun) {
        "Conversion strategy not defined for converting ${this.odometerUnits} to $unit"
    }

    return this.copy(
        odometerUnits = unit,
        lastOdometerReading = conversionFun(this.lastOdometerReading),
        schedule = this.schedule.map { scheduleItem ->
            scheduleItem.copy(
                interval = if (scheduleItem.intervalType == IntervalType.MONTHS) {
                    scheduleItem.interval
                } else {
                    conversionFun(scheduleItem.interval)
                },
                entries = scheduleItem.entries.map { entry ->
                    entry.copy(odometerReading = conversionFun(entry.odometerReading))
                }
            )
        }
    )
}

private val conversionFunctions: Map<Pair<DistanceUnit, DistanceUnit>, DistanceConversion> = mapOf(
    (KM to KM) uses { distance -> distance },
    (MILES to MILES) uses { distance -> distance },
    (KM to MILES) uses { distance -> (distance / 1.609).roundToLong() },
    (MILES to KM) uses { distance -> (distance * 1.609).roundToLong() },
)

private typealias DistanceConversion = (Long) -> Long

private infix fun Pair<DistanceUnit, DistanceUnit>.uses(conversionFun: DistanceConversion) =
    Pair(this, conversionFun)