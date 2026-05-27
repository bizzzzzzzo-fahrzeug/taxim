package com.example.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "drivers")
data class Driver(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val carModel: String,
    val carLicensePlate: String,
    val level: Int = 1, // 1: Rookie, 2: Experienced, 3: Expert, 4: Master (Expertise levels)
    val xp: Int = 0,    // Level XP (100 xp to level up)
    val totalTrips: Int = 0,
    val totalEarnings: Double = 0.0, // Cumulative revenue on site
    val dueCommission: Double = 0.0, // 10% owed to agency
    val daysRemaining: Int = 7,      // Service days left for 1-week payment clearance
    val isSuspended: Boolean = false,
    val latitude: Double, // Real lat/lon for Mönchengladbach
    val longitude: Double,
    val status: String = "Available" // "Available", "Offline", "Busy"
) {
    // Determine search radius based on level:
    // Level 1: Under 2 km
    // Level 2: Under 5 km
    // Level 3: Under 10 km
    // Level 4 (Master): Unlimited (covers entire Moenchengladbach: up to 25 km)
    val maxSearchRadiusKm: Double
        get() = when (level) {
            1 -> 2.5
            2 -> 6.0
            3 -> 12.0
            else -> 30.0
        }

    val xpNeededForNextLevel: Int
        get() = 100

    val levelProgress: Float
        get() = (xp.toFloat() / xpNeededForNextLevel.toFloat()).coerceIn(0f, 1f)

    val levelName: String
        get() = when (level) {
            1 -> "Rookie"
            2 -> "Experienced"
            3 -> "Expert"
            else -> "Master (Elite)"
        }
}

@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val customerName: String,
    val pickupName: String,
    val pickupLat: Double,
    val pickupLon: Double,
    val destName: String,
    val destLat: Double,
    val destLon: Double,
    val distanceKm: Double,
    val fareEuro: Double,
    val commissionEuro: Double, // 10% of fare
    val status: String = "Requested", // "Requested", "Accepted", "Arriving", "InProgress", "Completed", "Cancelled"
    val driverId: Int? = null,
    val driverName: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isPaidToAdmin: Boolean = false // If the 10% commission was cleared
)

@Dao
interface TaxiDao {
    @Query("SELECT * FROM drivers ORDER BY level DESC, name ASC")
    fun getAllDrivers(): Flow<List<Driver>>

    @Query("SELECT * FROM drivers WHERE id = :id")
    suspend fun getDriverById(id: Int): Driver?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDriver(driver: Driver): Long

    @Update
    suspend fun updateDriver(driver: Driver)

    @Query("SELECT * FROM trips ORDER BY timestamp DESC")
    fun getAllTrips(): Flow<List<Trip>>

    @Query("SELECT * FROM trips WHERE driverId = :driverId ORDER BY timestamp DESC")
    fun getTripsByDriver(driverId: Int): Flow<List<Trip>>

    @Query("SELECT * FROM trips WHERE id = :id")
    suspend fun getTripById(id: Int): Trip?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: Trip): Long

    @Update
    suspend fun updateTrip(trip: Trip)

    @Query("DELETE FROM trips")
    suspend fun clearAllTrips()

    @Query("DELETE FROM drivers")
    suspend fun clearAllDrivers()
}

@Database(entities = [Driver::class, Trip::class], version = 1, exportSchema = false)
abstract class TaxiDatabase : RoomDatabase() {
    abstract fun taxiDao(): TaxiDao
}
