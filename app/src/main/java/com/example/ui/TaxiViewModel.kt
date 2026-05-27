package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.Driver
import com.example.data.TaxiDatabase
import com.example.data.TaxiRepository
import com.example.data.Trip
import com.example.data.calculateDistanceKm
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MoenchengladbachLocation(
    val name: String,
    val lat: Double,
    val lon: Double,
    val description: String
)

val GLADBACH_PLACES = listOf(
    MoenchengladbachLocation("Mönchengladbach Hbf (City Center)", 51.1912, 6.4352, "Central Railway Station"),
    MoenchengladbachLocation("Rheydt Hauptbahnhof", 51.1685, 6.4442, "Rheydt South Station"),
    MoenchengladbachLocation("Schloss Wickrath (Wickrath Palace)", 51.1306, 6.3574, "Historical Baroque Castle"),
    MoenchengladbachLocation("Odenkirchen Tiergarten (Zoo)", 51.1332, 6.4497, "Local Animal Park"),
    MoenchengladbachLocation("Borussia-Park (Stadium)", 51.1824, 6.3872, "Legendary Football Arena"),
    MoenchengladbachLocation("Hardt Forest (Nature Reserve)", 51.2056, 6.3475, "Tranquil Woodland Path"),
    MoenchengladbachLocation("Neuwerk Market Square", 51.2185, 6.4715, "Cathedral & Historic Suburb"),
    MoenchengladbachLocation("Bunter Garten (Park)", 51.2012, 6.4398, "Floral Botanical Gardens"),
    MoenchengladbachLocation("Mönchengladbach Airport (MGL)", 51.2315, 6.5050, "Business & Aviation Airfield")
)

class TaxiViewModel(application: Application) : AndroidViewModel(application) {

    private val database = Room.databaseBuilder(
        application,
        TaxiDatabase::class.java, "taxi_database"
    ).build()

    private val repository = TaxiRepository(database.taxiDao())

    val drivers: StateFlow<List<Driver>> = repository.allDrivers.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val trips: StateFlow<List<Trip>> = repository.allTrips.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // UI Configuration States
    private val _activeRole = MutableStateFlow("customer") // "customer", "driver", "manager"
    val activeRole: StateFlow<String> = _activeRole.asStateFlow()

    private val _selectedDriverId = MutableStateFlow<Int?>(null)
    val selectedDriverId: StateFlow<Int?> = _selectedDriverId.asStateFlow()

    private val _pickupPlace = MutableStateFlow(GLADBACH_PLACES[0])
    val pickupPlace: StateFlow<MoenchengladbachLocation> = _pickupPlace.asStateFlow()

    private val _destPlace = MutableStateFlow(GLADBACH_PLACES[4]) // Default to Borussia-Park!
    val destPlace: StateFlow<MoenchengladbachLocation> = _destPlace.asStateFlow()

    private val _customerNameInput = MutableStateFlow("Markus")
    val customerNameInput: StateFlow<String> = _customerNameInput.asStateFlow()

    private val _activeCustomerTripId = MutableStateFlow<Int?>(null)
    val activeCustomerTripId: StateFlow<Int?> = _activeCustomerTripId.asStateFlow()

    private val _xpCelebrationMsg = MutableStateFlow<String?>(null)
    val xpCelebrationMsg: StateFlow<String?> = _xpCelebrationMsg.asStateFlow()

    // Drivers input state for creation
    val newDriverName = MutableStateFlow("")
    val newDriverCar = MutableStateFlow("")
    val newDriverPlate = MutableStateFlow("")

    private var animationJob: kotlinx.coroutines.Job? = null

    init {
        // Prepopulate default actors if none exist
        viewModelScope.launch {
            val exist = database.taxiDao().getAllDrivers().first()
            if (exist.isEmpty()) {
                prepopulateDefaultDrivers()
            }
            // Sync selected driver default to Hans (index 0) if any exists
            val updated = database.taxiDao().getAllDrivers().first()
            if (updated.isNotEmpty()) {
                _selectedDriverId.value = updated[0].id
            }
        }
    }

    private suspend fun prepopulateDefaultDrivers() {
        val defaults = listOf(
            Driver(
                name = "Hans Müller",
                carModel = "Mercedes E-Class (Black)",
                carLicensePlate = "MG-HM-182",
                level = 1,
                xp = 60,
                daysRemaining = 6,
                latitude = 51.1685, // Rheydt
                longitude = 6.4442,
                status = "Available",
                totalEarnings = 145.50,
                dueCommission = 14.55
            ),
            Driver(
                name = "Sabine Lehmann",
                carModel = "Hyundai Ioniq 5 (Silver)",
                carLicensePlate = "MG-SL-2911",
                level = 2,
                xp = 80,
                daysRemaining = 5,
                latitude = 51.1824, // Holt
                longitude = 6.4172,
                status = "Available",
                totalEarnings = 320.00,
                dueCommission = 32.00
            ),
            Driver(
                name = "Klaus Fischer",
                carModel = "Audi A6 Avant",
                carLicensePlate = "MG-KF-4890",
                level = 3,
                xp = 35,
                daysRemaining = 4,
                latitude = 51.2185, // Neuwerk
                longitude = 6.4715,
                status = "Available",
                totalEarnings = 612.00,
                dueCommission = 61.20
            ),
            Driver(
                name = "Amira Al-Saeed",
                carModel = "Tesla Model Y (White)",
                carLicensePlate = "MG-AA-9520",
                level = 4,
                xp = 95,
                daysRemaining = 1, // 1 day before suspension - great for testing!
                latitude = 51.1306, // Wickrath
                longitude = 6.3574,
                status = "Available",
                totalEarnings = 1250.00,
                dueCommission = 125.00
            ),
            Driver(
                name = "Dieter Schmidt",
                carModel = "Volkswagen Passat GTE",
                carLicensePlate = "MG-DS-330",
                level = 1,
                xp = 5,
                daysRemaining = 7,
                latitude = 51.2056, // Hardt
                longitude = 6.3475,
                status = "Available",
                totalEarnings = 0.0,
                dueCommission = 0.0
            )
        )
        for (driver in defaults) {
            repository.insertDriver(driver)
        }
    }

    // Role switcher
    fun setRole(role: String) {
        _activeRole.value = role
    }

    // Selected active driver switcher
    fun selectActiveDriver(driverId: Int) {
        _selectedDriverId.value = driverId
    }

    // Location setup
    fun setPickup(location: MoenchengladbachLocation) {
        _pickupPlace.value = location
    }

    fun setDestination(location: MoenchengladbachLocation) {
        _destPlace.value = location
    }

    fun setCustomerName(name: String) {
        if (name.isNotBlank()) {
            _customerNameInput.value = name
        }
    }

    // Booking actions
    fun orderTaxi() {
        viewModelScope.launch {
            val pickup = _pickupPlace.value
            val dest = _destPlace.value
            
            // Calculate distance
            val distance = calculateDistanceKm(pickup.lat, pickup.lon, dest.lat, dest.lon)
            
            // German Base tariff = 4.30 EUR + 2.20 EUR / km
            val rawFare = 4.30 + (distance * 2.20)
            val roundedFare = Math.round(rawFare * 100.0) / 100.0
            val commissionVal = Math.round((roundedFare * 0.10) * 100.0) / 100.0

            val trip = Trip(
                customerName = _customerNameInput.value,
                pickupName = pickup.name,
                pickupLat = pickup.lat,
                pickupLon = pickup.lon,
                destName = dest.name,
                destLat = dest.lat,
                destLon = dest.lon,
                distanceKm = distance,
                fareEuro = roundedFare,
                commissionEuro = commissionVal,
                status = "Requested"
            )

            val id = repository.insertTrip(trip)
            _activeCustomerTripId.value = id
        }
    }

    // Driver Action Accept
    fun driverAcceptTrip(tripId: Int) {
        val currentDriverId = _selectedDriverId.value ?: return
        viewModelScope.launch {
            val ok = repository.acceptTrip(tripId, currentDriverId)
            if (ok) {
                // Instantly update local active customer tracking too!
                _activeCustomerTripId.value = tripId
                // Start arriving motion animation
                startArrivalSim(tripId)
            }
        }
    }

    // Driver Progress
    fun advanceActiveTrip(tripId: Int, status: String) {
        viewModelScope.launch {
            val previousDriver = _selectedDriverId.value?.let { repository.getDriverById(it) }
            val ok = repository.advanceTripStatus(tripId, status)
            if (ok) {
                if (status == "Accepted") { // Trip advances to "Arriving"
                    startArrivalSim(tripId)
                } else if (status == "Arriving") { // Trip advances to "InProgress"
                    startDrivingSim(tripId)
                } else if (status == "InProgress") { // Trip completes
                    // Check if driver leveled up!
                    _selectedDriverId.value?.let { dId ->
                        val currentDriver = repository.getDriverById(dId)
                        if (currentDriver != null && previousDriver != null && currentDriver.level > previousDriver.level) {
                            _xpCelebrationMsg.value = "Hooray! ${currentDriver.name} leveled up to Level ${currentDriver.level} (${currentDriver.levelName})! Search radius is now expanded up to ${currentDriver.maxSearchRadiusKm} km."
                        }
                    }
                }
            }
        }
    }

    fun dismissCelebration() {
        _xpCelebrationMsg.value = null
    }

    // Cancel dynamic reservation
    fun customerCancelTrip() {
        val tripId = _activeCustomerTripId.value ?: return
        viewModelScope.launch {
            repository.cancelTrip(tripId)
            _activeCustomerTripId.value = null
            animationJob?.cancel()
        }
    }

    fun driverCancelTrip(tripId: Int) {
        viewModelScope.launch {
            repository.cancelTrip(tripId)
            animationJob?.cancel()
        }
    }

    // Clear passenger ongoing trip
    fun clearCustomerBookingState() {
        _activeCustomerTripId.value = null
    }

    // Commission payout from driver center
    fun driverPayCommission(amount: Double) {
        val driverId = _selectedDriverId.value ?: return
        viewModelScope.launch {
            repository.payCommission(driverId, amount)
        }
    }

    // Triggered by Admin - Pay driver balance wire/dues recording
    fun adminClearDriverCommision(driverId: Int) {
        viewModelScope.launch {
            val d = repository.getDriverById(driverId) ?: return@launch
            repository.payCommission(driverId, d.dueCommission)
        }
    }

    // Triggered by Admin - Force suspend toggle
    fun adminToggleLockState(driverId: Int) {
        viewModelScope.launch {
            val d = repository.getDriverById(driverId) ?: return@launch
            val updated = d.copy(
                isSuspended = !d.isSuspended,
                status = if (!d.isSuspended) "Offline" else "Available"
            )
            repository.updateDriver(updated)
        }
    }

    // Time cycle stimulation - "Simulate Next Day"
    fun simulateNextDay() {
        viewModelScope.launch {
            val list = drivers.value
            repository.simulateDayPassageForDrivers(list)
        }
    }

    // Admin reset
    fun resetEntireSystem() {
        viewModelScope.launch {
            animationJob?.cancel()
            _activeCustomerTripId.value = null
            repository.hardReset()
            prepopulateDefaultDrivers()
            val updated = database.taxiDao().getAllDrivers().first()
            if (updated.isNotEmpty()) {
                _selectedDriverId.value = updated[0].id
            }
        }
    }

    // Driver self add registration
    fun registerNewDriver() {
        viewModelScope.launch {
            val name = newDriverName.value.trim()
            val car = newDriverCar.value.trim()
            val plate = newDriverPlate.value.trim()

            if (name.isNotBlank() && car.isNotBlank() && plate.isNotBlank()) {
                val driver = Driver(
                    name = name,
                    carModel = car,
                    carLicensePlate = plate,
                    level = 1,
                    xp = 0,
                    daysRemaining = 7,
                    latitude = 51.1912 + (Math.random() - 0.5) * 0.05, // random near center
                    longitude = 6.4352 + (Math.random() - 0.5) * 0.05,
                    status = "Available"
                )
                val id = repository.insertDriver(driver)
                _selectedDriverId.value = id
                
                // Clear state inputs
                newDriverName.value = ""
                newDriverCar.value = ""
                newDriverPlate.value = ""
            }
        }
    }

    // Arriving trip coordinates motion animation
    private fun startArrivalSim(tripId: Int) {
        animationJob?.cancel()
        animationJob = viewModelScope.launch {
            val trip = repository.getTripById(tripId) ?: return@launch
            val dId = trip.driverId ?: return@launch
            val originalDriver = repository.getDriverById(dId) ?: return@launch

            val steps = 12
            val delayMs = 900L
            val startLat = originalDriver.latitude
            val startLon = originalDriver.longitude
            val destLat = trip.pickupLat
            val destLon = trip.pickupLon

            for (i in 1..steps) {
                kotlinx.coroutines.delay(delayMs)
                val activeTripNow = repository.getTripById(tripId) ?: break
                if (activeTripNow.status != "Arriving") break

                val d = repository.getDriverById(dId) ?: break
                val t = i.toDouble() / steps
                val nextLat = startLat + t * (destLat - startLat)
                val nextLon = startLon + t * (destLon - startLon)

                repository.updateDriver(d.copy(latitude = nextLat, longitude = nextLon))
            }
        }
    }

    // Driving trip coordinates motion animation
    private fun startDrivingSim(tripId: Int) {
        animationJob?.cancel()
        animationJob = viewModelScope.launch {
            val trip = repository.getTripById(tripId) ?: return@launch
            val dId = trip.driverId ?: return@launch

            val steps = 16
            val delayMs = 1100L
            val startLat = trip.pickupLat
            val startLon = trip.pickupLon
            val destLat = trip.destLat
            val destLon = trip.destLon

            for (i in 1..steps) {
                kotlinx.coroutines.delay(delayMs)
                val activeTripNow = repository.getTripById(tripId) ?: break
                if (activeTripNow.status != "InProgress") break

                val d = repository.getDriverById(dId) ?: break
                val t = i.toDouble() / steps
                val nextLat = startLat + t * (destLat - startLat)
                val nextLon = startLon + t * (destLon - startLon)

                repository.updateDriver(d.copy(latitude = nextLat, longitude = nextLon))
            }

            // Auto advance or wait for user to hit "Collected and complete" (leaving it clickable for more real feeling!)
        }
    }
}
