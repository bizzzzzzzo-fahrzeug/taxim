package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class TaxiRepository(private val taxiDao: TaxiDao) {
    val allDrivers: Flow<List<Driver>> = taxiDao.getAllDrivers()
    val allTrips: Flow<List<Trip>> = taxiDao.getAllTrips()

    suspend fun getDriverById(id: Int): Driver? = taxiDao.getDriverById(id)
    suspend fun getTripById(id: Int): Trip? = taxiDao.getTripById(id)

    suspend fun insertDriver(driver: Driver): Int = taxiDao.insertDriver(driver).toInt()
    suspend fun updateDriver(driver: Driver) = taxiDao.updateDriver(driver)

    suspend fun insertTrip(trip: Trip): Int = taxiDao.insertTrip(trip).toInt()
    suspend fun updateTrip(trip: Trip) = taxiDao.updateTrip(trip)

    // Accept trip
    suspend fun acceptTrip(tripId: Int, driverId: Int): Boolean {
        val trip = getTripById(tripId) ?: return false
        val driver = getDriverById(driverId) ?: return false

        if (driver.isSuspended) return false // Cannot accept if suspended
        if (trip.status != "Requested") return false

        // Mark driver as Busy
        val updatedDriver = driver.copy(status = "Busy")
        updateDriver(updatedDriver)

        // Update trip status
        val updatedTrip = trip.copy(
            status = "Accepted",
            driverId = driverId,
            driverName = driver.name
        )
        updateTrip(updatedTrip)
        return true
    }

    // Update trip progress with custom triggers
    suspend fun advanceTripStatus(tripId: Int, currentStatus: String): Boolean {
        val trip = getTripById(tripId) ?: return false
        val driverId = trip.driverId ?: return false
        val driver = getDriverById(driverId) ?: return false

        val nextStatus = when (currentStatus) {
            "Accepted" -> "Arriving"
            "Arriving" -> "InProgress"
            "InProgress" -> "Completed"
            else -> return false
        }

        if (nextStatus == "Completed") {
            // Calculate driver rewards
            val updatedXp = driver.xp + 25 // 25 XP per trip
            var newLevel = driver.level
            var finalXp = updatedXp

            // Level up checks
            if (finalXp >= 100) {
                if (newLevel < 4) {
                    newLevel++
                    finalXp -= 100 // carry over excess XP
                } else {
                    finalXp = 100 // cap master
                }
            }

            val newTotalEarnings = driver.totalEarnings + trip.fareEuro
            val newDueCommission = driver.dueCommission + trip.commissionEuro

            val finishedDriver = driver.copy(
                status = "Available",
                totalTrips = driver.totalTrips + 1,
                xp = finalXp,
                level = newLevel,
                totalEarnings = newTotalEarnings,
                dueCommission = newDueCommission
            )
            updateDriver(finishedDriver)

            val finishedTrip = trip.copy(status = "Completed")
            updateTrip(finishedTrip)
        } else {
            // Just update trip status and keep driver status as is
            val intermediateTrip = trip.copy(status = nextStatus)
            updateTrip(intermediateTrip)
        }

        return true
    }

    // Pay due commission fee to management center
    suspend fun payCommission(driverId: Int, amountToPay: Double): Boolean {
        val driver = getDriverById(driverId) ?: return false
        if (amountToPay <= 0 || driver.dueCommission <= 0) return false

        val payment = amountToPay.coerceAtMost(driver.dueCommission)
        val remainingDues = (driver.dueCommission - payment).coerceAtLeast(0.0)

        // If outstanding dues are fully cleared, lift suspension (if suspended)
        val liftSuspension = remainingDues <= 0.0
        val isStillSuspended = if (liftSuspension) false else driver.isSuspended

        val updatedDriver = driver.copy(
            dueCommission = remainingDues,
            isSuspended = isStillSuspended
        )
        updateDriver(updatedDriver)
        return true
    }

    // Cancel dynamic jobs
    suspend fun cancelTrip(tripId: Int): Boolean {
        val trip = getTripById(tripId) ?: return false
        if (trip.status == "Completed" || trip.status == "Cancelled") return false

        trip.driverId?.let { driverId ->
            getDriverById(driverId)?.let { d ->
                updateDriver(d.copy(status = "Available"))
            }
        }

        updateTrip(trip.copy(status = "Cancelled"))
        return true
    }

    // Simulate passage of a service day.
    // Reducing remaining service days. If hits 0, auto-calculates dues and suspends if unpaid.
    suspend fun simulateDayPassageForDrivers(driversList: List<Driver>) {
        for (driver in driversList) {
            var days = driver.daysRemaining - 1
            var suspended = driver.isSuspended

            if (days <= 0) {
                // End of the 7-day week of service cycle
                if (driver.dueCommission > 0.0) {
                    // Driver has unpaid commission dues after 1 week - Account Suspended
                    suspended = true
                }
                days = 7 // Reset the timer, but they remain suspended until dues are paid!
            }

            val updatedDriver = driver.copy(
                daysRemaining = days,
                isSuspended = suspended,
                // If they are suspended, set status offline
                status = if (suspended) "Offline" else driver.status
            )
            updateDriver(updatedDriver)
        }
    }

    // Hard reset both drivers and trips
    suspend fun hardReset() {
        taxiDao.clearAllTrips()
        taxiDao.clearAllDrivers()
    }
}

// Haversine formula to compute exact distance in Mönchengladbach
fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6371.0 // Radius of the Earth in km
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return r * c
}
