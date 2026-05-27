package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.Driver
import com.example.data.Trip
import com.example.data.calculateDistanceKm
import com.example.ui.GLADBACH_PLACES
import com.example.ui.MoenchengladbachLocation
import com.example.ui.TaxiViewModel
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SleekBg
import com.example.ui.theme.SleekTextDark
import com.example.ui.theme.SleekBrandPurple
import com.example.ui.theme.SleekPurpleLightBg
import com.example.ui.theme.SleekPurpleDarkText
import com.example.ui.theme.SleekBlueLightBg
import com.example.ui.theme.SleekBlueDarkText
import com.example.ui.theme.SleekMapBg
import com.example.ui.theme.SleekGrayBorder
import com.example.ui.theme.SleekCardBorder
import com.example.ui.theme.SleekAlertBg
import com.example.ui.theme.SleekAlertText
import com.example.ui.theme.SleekAlertBorder
import com.example.ui.theme.SleekNavBarBg
import com.example.ui.theme.SleekActivePill
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                TaxiAppMainScreen()
            }
        }
    }
}

@Composable
fun TaxiAppMainScreen(viewModel: TaxiViewModel = viewModel()) {
    val drivers by viewModel.drivers.collectAsState()
    val trips by viewModel.trips.collectAsState()
    val activeRole by viewModel.activeRole.collectAsState()
    val selectedDriverId by viewModel.selectedDriverId.collectAsState()
    val pickupPlace by viewModel.pickupPlace.collectAsState()
    val destPlace by viewModel.destPlace.collectAsState()
    val customerNameInput by viewModel.customerNameInput.collectAsState()
    val activeCustomerTripId by viewModel.activeCustomerTripId.collectAsState()
    val xpCelebrationMsg by viewModel.xpCelebrationMsg.collectAsState()

    val activeDriver = drivers.find { d -> d.id == selectedDriverId }
    val activeCustomerTrip = trips.find { t -> t.id == activeCustomerTripId }

    // Map the bottom padding of navigation bars automatically
    val navigationPadding = WindowInsets.navigationBars.asPaddingValues()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = SleekBg, // Soft Lavender-tinted White background
        contentWindowInsets = WindowInsets.navigationBars
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .background(SleekBg),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header: Application Logo and Dynamic Tag
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SleekBg)
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Dispatcher MG",
                        color = SleekTextDark,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2E7D32)) // Pulsing center indicator green pulse style
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "MÖNCHENGLADBACH CENTER",
                            color = SleekTextDark.copy(alpha = 0.6f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Header Profile bubble representation (AD) from the design layout
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(SleekBlueLightBg)
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "AD",
                        color = SleekBlueDarkText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            // Central Mönchengladbach Map View
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(SleekMapBg)
                    .border(1.dp, SleekGrayBorder)
            ) {
                MoenchengladbachMetroMap(
                    drivers = drivers,
                    activeTrip = activeCustomerTrip ?: trips.find { t -> t.status in listOf("Accepted", "Arriving", "InProgress") },
                    selectedDriver = activeDriver,
                    pickupPlace = pickupPlace,
                    destPlace = destPlace
                )
                
                // Overlay Badge - styled just like design HTML's bottom left overlay
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                        .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(24.dp))
                        .border(1.dp, SleekGrayBorder, RoundedCornerShape(24.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "12 ACTIVE DRIVERS",
                        color = SleekTextDark,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Role Navigation Pills Drawer (Sleek Bottom-like Material Navigation Bar Style)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SleekNavBarBg)
                    .border(BorderStroke(1.dp, SleekGrayBorder))
                    .padding(vertical = 10.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val roles = listOf(
                    Triple("customer", "👤 Rider View", "Customer Booking"),
                    Triple("driver", "🚖 Driver Hub", "My Levels & Jobs"),
                    Triple("manager", "⚙️ Dispatcher", "LEDGER MANAGER")
                )
                roles.forEach { (roleKey, label, desc) ->
                    val isSelected = activeRole == roleKey
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(if (isSelected) SleekActivePill else Color.Transparent)
                            .clickable { viewModel.setRole(roleKey) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = label,
                                color = if (isSelected) SleekBrandPurple else SleekTextDark.copy(alpha = 0.6f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = desc,
                                color = if (isSelected) SleekPurpleDarkText else SleekTextDark.copy(alpha = 0.4f),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Body Area based on selected Role Drawer
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(SleekBg)
            ) {
                when (activeRole) {
                    "customer" -> CustomerScreen(
                        viewModel = viewModel,
                        drivers = drivers,
                        activeTrip = activeCustomerTrip,
                        pickupPlace = pickupPlace,
                        destPlace = destPlace,
                        customerName = customerNameInput
                    )
                    "driver" -> DriverScreen(
                        viewModel = viewModel,
                        drivers = drivers,
                        trips = trips,
                        activeDriver = activeDriver
                    )
                    "manager" -> AdminScreen(
                        viewModel = viewModel,
                        drivers = drivers,
                        trips = trips
                    )
                }
            }
        }
    }

    // Celebration level up modal
    if (xpCelebrationMsg != null) {
        Dialog(onDismissRequest = { viewModel.dismissCelebration() }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1E22)),
                border = BorderStroke(2.dp, Color(0xFFFFCC00)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🏆 LEVEL UP!", fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color(0xFFFFCC00))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = xpCelebrationMsg ?: "",
                        color = Color.White,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { viewModel.dismissCelebration() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFCC00)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Awesome!", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Map rendering component with custom GIS translating formula
@Composable
fun MoenchengladbachMetroMap(
    drivers: List<Driver>,
    activeTrip: Trip?,
    selectedDriver: Driver?,
    pickupPlace: MoenchengladbachLocation,
    destPlace: MoenchengladbachLocation
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val width = constraints.maxWidth.toFloat()
        val height = constraints.maxHeight.toFloat()

        // Bounds of Mönchengladbach region
        val minLat = 51.12
        val maxLat = 51.24
        val minLon = 6.33
        val maxLon = 6.51

        fun getX(lon: Double): Float {
            val t = (lon - minLon) / (maxLon - minLon)
            return (t * width * 0.84f + width * 0.08f).toFloat()
        }

        fun getY(lat: Double): Float {
            val t = (lat - minLat) / (maxLat - minLat)
            // Invert since top-left is (0,0) as usual in 2D axes
            return ((1.0 - t) * height * 0.80f + height * 0.10f).toFloat()
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            // 1. Draw regional roads
            val centerHbf = Offset(getX(6.4352), getY(51.1912))
            val rheydtHbf = Offset(getX(6.4442), getY(51.1685))
            val wickrathPalace = Offset(getX(6.3574), getY(51.1306))
            val odenkirchenZoo = Offset(getX(6.4497), getY(51.1332))
            val borussiaStadium = Offset(getX(6.3872), getY(51.1824))
            val hardtForest = Offset(getX(6.3475), getY(51.2056))
            val neuwerkMarket = Offset(getX(6.4715), getY(51.2185))
            val bunterGarten = Offset(getX(6.4398), getY(51.2012))
            val airportMGL = Offset(getX(6.5050), getY(51.2315))

            // Background Grid Lines matching clean M3 layout grids
            val gridStep = size.width / 10f
            for (i in 1..9) {
                drawLine(
                    color = Color(0xFFDCDDE4),
                    start = Offset(i * gridStep, 0f),
                    end = Offset(i * gridStep, size.height),
                    strokeWidth = 1f
                )
                drawLine(
                    color = Color(0xFFDCDDE4),
                    start = Offset(0f, i * (size.height / 6f)),
                    end = Offset(size.width, i * (size.height / 6f)),
                    strokeWidth = 1f
                )
            }

            // Draw major connecting roads (White arterial streets with backing borders for outstanding depth)
            val roadBacking = Color(0xFFC8CAD6)
            val roadSurface = Color(0xFFFFFFFF)

            val connections = listOf(
                hardtForest to borussiaStadium,
                borussiaStadium to centerHbf,
                centerHbf to neuwerkMarket,
                centerHbf to bunterGarten,
                neuwerkMarket to airportMGL,
                centerHbf to rheydtHbf,
                rheydtHbf to odenkirchenZoo,
                rheydtHbf to wickrathPalace,
                wickrathPalace to borussiaStadium
            )

            // Draw thick dark gray backgrounds
            connections.forEach { (start, end) ->
                drawLine(roadBacking, start, end, strokeWidth = 9f)
            }
            // Draw clean crisp white surfaces inside
            connections.forEach { (start, end) ->
                drawLine(roadSurface, start, end, strokeWidth = 5f)
            }

            // 2. Draw active booking path (connecting pick-up and destination) if requested
            if (activeTrip != null && activeTrip.status in listOf("Accepted", "Arriving", "InProgress")) {
                val startPt = Offset(getX(activeTrip.pickupLon), getY(activeTrip.pickupLat))
                val endPt = Offset(getX(activeTrip.destLon), getY(activeTrip.destLat))
                
                // Pulsing Route path
                drawLine(
                    color = if (activeTrip.status == "InProgress") SleekBrandPurple else Color(0xFF006C4C),
                    start = startPt,
                    end = endPt,
                    strokeWidth = 5f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                )
            }

            // 3. Draw static landmarks for visual placement anchor
            val hubs = listOf(
                "Central" to centerHbf, "Rheydt" to rheydtHbf, "Wickrath" to wickrathPalace,
                "Odenkirchen" to odenkirchenZoo, "Borussia-Park" to borussiaStadium,
                "Hardt" to hardtForest, "Neuwerk" to neuwerkMarket, "Airport" to airportMGL
            )

            hubs.forEach { (lbl, offset) ->
                drawCircle(
                    color = Color.White,
                    radius = 12f,
                    center = offset
                )
                drawCircle(
                    color = SleekBrandPurple,
                    radius = 5f,
                    center = offset,
                    style = Stroke(2.5f)
                )
            }
        }

        // Draw labels and cars using overlays to avoid heavy custom native font painting
        val hubs = listOf(
            Triple("Central Hbf", 51.1912, 6.4352),
            Triple("Rheydt", 51.1685, 6.4442),
            Triple("Wickrath", 51.1306, 6.3574),
            Triple("Odenkirchen", 51.1332, 6.4497),
            Triple("Borussia Stadium", 51.1824, 6.3872),
            Triple("Hardt Forest", 51.2056, 6.3475),
            Triple("Neuwerk Market", 51.2185, 6.4715),
            Triple("Airport MGL", 51.2315, 6.5050)
        )

        // Draw hub name annotations with clean white cards
        hubs.forEach { (name, lat, lon) ->
            val px = getX(lon)
            val py = getY(lat)
            Box(
                modifier = Modifier
                    .offset(px.dp - 30.dp, py.dp - 18.dp)
                    .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(4.dp))
                    .border(1.dp, SleekGrayBorder.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(
                    text = name,
                    color = SleekTextDark,
                    fontSize = 8.sp,
                    maxLines = 1,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 4. Draw pickup & destination pins on actual client state
        if (activeTrip != null && activeTrip.status in listOf("Requested", "Accepted", "Arriving", "InProgress")) {
            val pickX = getX(activeTrip.pickupLon)
            val pickY = getY(activeTrip.pickupLat)
            val destX = getX(activeTrip.destLon)
            val destY = getY(activeTrip.destLat)

            // Green pick up pin
            Box(
                modifier = Modifier
                    .offset(pickX.dp - 10.dp, pickY.dp - 12.dp)
                    .size(20.dp)
                    .background(Color(0xFF006C4C), CircleShape)
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("A", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
            }

            // Red destination pin
            Box(
                modifier = Modifier
                    .offset(destX.dp - 10.dp, destY.dp - 12.dp)
                    .size(20.dp)
                    .background(Color(0xFFB3261E), CircleShape)
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("B", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
            }
        }

        // 5. Draw active mobile drivers live locations
        drivers.forEach { driver ->
            val dX = getX(driver.longitude)
            val dY = getY(driver.latitude)
            val isSelectedOnMap = selectedDriver?.id == driver.id

            Box(
                modifier = Modifier
                    .offset(dX.dp - 14.dp, dY.dp - 14.dp)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        if (driver.isSuspended) SleekAlertBg
                        else if (driver.status == "Busy") SleekPurpleLightBg
                        else if (isSelectedOnMap) SleekPurpleLightBg
                        else Color.White
                    )
                    .border(
                        if (isSelectedOnMap) 2.dp else 1.dp,
                        if (driver.isSuspended) SleekAlertText
                        else if (isSelectedOnMap || driver.status == "Available") SleekBrandPurple
                        else SleekGrayBorder,
                        CircleShape
                    )
                    .clickable { /* Choose map driver */ },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (driver.isSuspended) "🚫" else if (driver.status == "Busy") "🔒" else "🚖",
                        fontSize = 12.sp
                    )
                }
            }

            // Driver Name tag below the active vehicle
            Box(
                modifier = Modifier
                    .offset(dX.dp - 35.dp, dY.dp + 16.dp)
                    .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(4.dp))
                    .border(1.dp, if (isSelectedOnMap) SleekBrandPurple else SleekGrayBorder, RoundedCornerShape(4.dp))
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
                Text(
                    text = "${driver.name.substringBefore(" ")} Lv.${driver.level}",
                    color = if (driver.isSuspended) SleekAlertText else SleekTextDark,
                    fontSize = 8.sp,
                    maxLines = 1
                )
            }
        }
    }
}

// Helper coordinate endpoints

// Screen 1: CUSTOMER VIEW SEGMENT
@Composable
fun CustomerScreen(
    viewModel: TaxiViewModel,
    drivers: List<Driver>,
    activeTrip: Trip?,
    pickupPlace: MoenchengladbachLocation,
    destPlace: MoenchengladbachLocation,
    customerName: String
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("customer_screen_layout")
    ) {
        // Customer Identity Config Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, SleekCardBorder),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Contact Info",
                        tint = SleekBrandPurple,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Rider Username", color = SleekTextDark.copy(alpha = 0.6f), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        Text(customerName, color = SleekTextDark, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    // Rename mini action
                    Button(
                        onClick = {
                            val names = listOf("Markus", "Anja", "Frank", "Tanja", "Christian", "Stefanie")
                            viewModel.setCustomerName(names.random())
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SleekPurpleLightBg),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("Change", color = SleekPurpleDarkText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Live Tracker View if active trip is outstanding
        if (activeTrip != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.5.dp, SleekBrandPurple),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ACTIVE TRIP STATUS",
                                color = SleekBrandPurple,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                letterSpacing = 0.5.sp
                            )
                            Badge(
                                containerColor = when (activeTrip.status) {
                                    "Requested" -> Color(0xFFFFA000)
                                    "Accepted", "Arriving" -> SleekBrandPurple
                                    "InProgress" -> Color(0xFF006C4C)
                                    else -> Color(0xFF006C4C)
                                }
                            ) {
                                Text(
                                    text = activeTrip.status.uppercase(Locale.getDefault()),
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = when (activeTrip.status) {
                                "Requested" -> "Searching for closest competent drivers in Gladbach..."
                                "Accepted" -> "Driver ${activeTrip.driverName} has ACCEPTED your trip! Preparing routing."
                                "Arriving" -> "Driver ${activeTrip.driverName} is on route to pickup location! Watch real-time radar mapping."
                                "InProgress" -> "In transit • En route to: ${activeTrip.destName}. Cash pay will clear on site."
                                else -> "Completed! Thank you for riding Gladbach Cab."
                            },
                            color = SleekTextDark,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = SleekCardBorder.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Trip Details
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Pickup Location", color = SleekTextDark.copy(alpha = 0.5f), fontSize = 10.sp, fontWeight = FontWeight.Medium)
                                Text(activeTrip.pickupName, color = SleekTextDark, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Destination Location", color = SleekTextDark.copy(alpha = 0.5f), fontSize = 10.sp, fontWeight = FontWeight.Medium)
                                Text(activeTrip.destName, color = SleekTextDark, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Calculated Distance", color = SleekTextDark.copy(alpha = 0.5f), fontSize = 10.sp, fontWeight = FontWeight.Medium)
                                Text(String.format(Locale.getDefault(), "%.2f km", activeTrip.distanceKm), color = SleekTextDark, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("German Site Fare", color = SleekTextDark.copy(alpha = 0.5f), fontSize = 10.sp, fontWeight = FontWeight.Medium)
                                Text(String.format(Locale.getDefault(), "%.2f €", activeTrip.fareEuro), color = SleekBrandPurple, fontSize = 18.sp, fontWeight = FontWeight.Black)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (activeTrip.status in listOf("Requested", "Accepted")) {
                            Button(
                                onClick = { viewModel.customerCancelTrip() },
                                colors = ButtonDefaults.buttonColors(containerColor = SleekAlertBg),
                                border = BorderStroke(1.dp, SleekAlertBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("cancel_ride_button")
                            ) {
                                Text("Cancel Booking Request", color = SleekAlertText, fontWeight = FontWeight.Bold)
                            }
                        } else if (activeTrip.status == "Completed") {
                            Button(
                                onClick = { viewModel.clearCustomerBookingState() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006C4C)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("payout_ok_button")
                            ) {
                                Text("Clear Payout & Book Another Ride", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            // Ride is in progress, can't cancel
                            Text(
                                "⚠️ You are currently in transit. Ride cannot be cancelled.",
                                color = Color(0xFFFFA000),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        } else {
            // Pick Address & Create order Form
            item {
                Text(
                    text = "Request a Trip",
                    color = SleekTextDark,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Select Pickup Address Chip list
                Text("Select PICKUP Point (Station A):", color = SleekBrandPurple, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 6.dp))
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    items(GLADBACH_PLACES) { loc ->
                        val isSelected = pickupPlace.name == loc.name
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) SleekPurpleLightBg else Color.White
                            ),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, if (isSelected) SleekBrandPurple else SleekCardBorder),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clickable { viewModel.setPickup(loc) }
                        ) {
                            Text(
                                text = "📍 " + loc.name.substringBefore(" ("),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                color = if (isSelected) SleekPurpleDarkText else SleekTextDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Select Destination Address Chip list
                Text("Select DESTINATION Point (Station B):", color = SleekBrandPurple, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 6.dp))
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    items(GLADBACH_PLACES) { loc ->
                        val isSelected = destPlace.name == loc.name
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) SleekBlueLightBg else Color.White
                            ),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, if (isSelected) SleekBrandPurple else SleekCardBorder),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clickable { viewModel.setDestination(loc) }
                        ) {
                            Text(
                                text = "🏁 " + loc.name.substringBefore(" ("),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                color = if (isSelected) SleekBlueDarkText else SleekTextDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Selected Route Details Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, SleekCardBorder),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Place, contentDescription = null, tint = Color(0xFF006C4C), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "From: ${pickupPlace.name}", color = SleekTextDark, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Place, contentDescription = null, tint = Color(0xFFB3261E), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "To: ${destPlace.name}", color = SleekTextDark, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = SleekCardBorder.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Fare estimate structure
                        val distance = calculateDistanceKm(pickupPlace.lat, pickupPlace.lon, destPlace.lat, destPlace.lon)
                        val estFare = 4.30 + (distance * 2.20)
                        val driverOwes = estFare * 0.10

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Route Distance", color = SleekTextDark.copy(alpha = 0.5f), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                Text(String.format(Locale.getDefault(), "%.2f km", distance), color = SleekTextDark, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Est. On-site Price", color = SleekTextDark.copy(alpha = 0.5f), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                Text(String.format(Locale.getDefault(), "%.2f €", estFare), color = SleekBrandPurple, fontSize = 18.sp, fontWeight = FontWeight.Black)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SleekNavBarBg, RoundedCornerShape(12.dp))
                                .border(1.dp, SleekCardBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("💶 Payment Scheme:", color = SleekTextDark.copy(alpha = 0.6f), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            Text("PAY ON SITE", color = Color(0xFF006C4C), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        
                        Text(
                            text = "Note: In Mönchengladbach, Germany, taxis charge on arrival. The administration puts a 10% commission fee of ${String.format(Locale.getDefault(), "%.2f €", driverOwes)} to the driver's ledger on completed trips.",
                            color = SleekTextDark.copy(alpha = 0.5f),
                            fontSize = 9.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                if (pickupPlace.name == destPlace.name) {
                    Text(
                        text = "⚠️ Pickup and Destination cannot be identical.",
                        color = Color(0xFFB3261E),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                } else {
                    Button(
                        onClick = { viewModel.orderTaxi() },
                        colors = ButtonDefaults.buttonColors(containerColor = SleekBrandPurple),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("book_taxi_button")
                    ) {
                        Text(
                            "Order Gladbach Cab Now",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }

        // Active Nearby Drivers indicators
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, SleekCardBorder),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "🟢 ACTIVE LOCAL TAXI CARRIERS",
                        color = Color(0xFF006C4C),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    drivers.forEach { driver ->
                        val distanceToPickup = calculateDistanceKm(driver.latitude, driver.longitude, pickupPlace.lat, pickupPlace.lon)
                        val inCarrierRange = distanceToPickup <= driver.maxSearchRadiusKm

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Text(if (driver.isSuspended) "🚫" else "🚖", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "${driver.name} (Lvl ${driver.level} • ${driver.levelName})",
                                        color = if (driver.isSuspended) SleekAlertText else SleekTextDark,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${driver.carModel} [${driver.carLicensePlate}]",
                                        color = SleekTextDark.copy(alpha = 0.6f),
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = if (driver.isSuspended) "SUSPENDED" else driver.status.uppercase(Locale.getDefault()),
                                    color = if (driver.isSuspended) SleekAlertText
                                    else if (driver.status == "Busy") Color(0xFFFFA000)
                                    else Color(0xFF006C4C),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = String.format(Locale.getDefault(), "%.1f km away", distanceToPickup),
                                    color = if (inCarrierRange) Color(0xFF006C4C) else SleekAlertText,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        HorizontalDivider(color = SleekCardBorder.copy(alpha = 0.3f), thickness = 1.dp)
                    }
                }
            }
        }
    }
}

// Screen 2: DRIVER PORTAL SEGMENT
@Composable
fun DriverScreen(
    viewModel: TaxiViewModel,
    drivers: List<Driver>,
    trips: List<Trip>,
    activeDriver: Driver?
) {
    val newName by viewModel.newDriverName.collectAsState()
    val newCar by viewModel.newDriverCar.collectAsState()
    val newPlate by viewModel.newDriverPlate.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("driver_screen_layout")
    ) {
        // Driver selector
        item {
            Row(
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Select Active Driver Profile",
                    color = SleekTextDark,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = {
                        // Quick switch driver to let examiner toggle easily
                        val currentIndex = drivers.indexOfFirst { d -> d.id == activeDriver?.id }
                        if (currentIndex != -1 && drivers.isNotEmpty()) {
                            val nextIndex = (currentIndex + 1) % drivers.size
                            viewModel.selectActiveDriver(drivers[nextIndex].id)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SleekPurpleLightBg),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text("Cycle Profile 🔁", color = SleekPurpleDarkText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Driver Choice Chips
            LazyRow(
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                items(drivers) { d ->
                    val isSelected = d.id == activeDriver?.id
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) SleekPurpleLightBg else Color.White
                        ),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clickable { viewModel.selectActiveDriver(d.id) },
                        border = if (d.isSuspended) BorderStroke(1.dp, SleekAlertText) 
                                 else if (isSelected) BorderStroke(1.dp, SleekBrandPurple)
                                 else BorderStroke(1.dp, SleekCardBorder),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = (if (d.isSuspended) "🚫 " else "") + d.name.substringBefore(" "),
                                color = if (isSelected) SleekPurpleDarkText else SleekTextDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Lvl ${d.level} • ${d.levelName}",
                                color = if (isSelected) SleekPurpleDarkText.copy(alpha = 0.8f) else SleekTextDark.copy(alpha = 0.5f),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        if (activeDriver == null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, SleekCardBorder),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Please select or register a driver below to enter the portal.",
                        color = SleekTextDark,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else {
            // Suspended View Warning Block
            if (activeDriver.isSuspended) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SleekAlertBg),
                        border = BorderStroke(1.dp, SleekAlertBorder),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = SleekAlertText)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "ACCOUNT SUSPENDED",
                                    color = SleekAlertText,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Dear ${activeDriver.name}, your account is suspended because you failed to clear your 10% agency dues of ${String.format(Locale.getDefault(), "%.2f €", activeDriver.dueCommission)} at the end of your 1-week service interval. Please pay outstanding dues below to resume service.",
                                color = SleekAlertText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Driver Dashboard License Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.5.dp, if (activeDriver.isSuspended) SleekAlertText else SleekBrandPurple),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("DRIVER LICENSE", color = SleekBrandPurple, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                                Text(activeDriver.name, color = SleekTextDark, fontSize = 18.sp, fontWeight = FontWeight.Black)
                            }
                            Badge(containerColor = if (activeDriver.isSuspended) SleekAlertText else Color(0xFF006C4C)) {
                                Text(
                                    text = if (activeDriver.isSuspended) "SUSPENDED" else activeDriver.status.uppercase(Locale.getDefault()),
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Leveling Meter
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "EXP: Level ${activeDriver.level} (${activeDriver.levelName})",
                                color = SleekTextDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${activeDriver.xp} / ${activeDriver.xpNeededForNextLevel} XP",
                                color = SleekTextDark.copy(alpha = 0.6f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = activeDriver.levelProgress,
                            color = SleekBrandPurple,
                            trackColor = SleekNavBarBg,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Radiuses explanation
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = SleekBrandPurple, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Dispatch Radius Limit: ${activeDriver.maxSearchRadiusKm} km range • " + when (activeDriver.level) {
                                    1 -> "Local (Rookie Level)"
                                    2 -> "District (Level 2)"
                                    3 -> "City Wide (Expert Level)"
                                    else -> "All Metro Mönchengladbach (Master)"
                                },
                                color = SleekTextDark.copy(alpha = 0.5f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = SleekCardBorder.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(14.dp))

                        // Financial values
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("On-site Cash Earned", color = SleekTextDark.copy(alpha = 0.5f), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                Text(String.format(Locale.getDefault(), "%.2f €", activeDriver.totalEarnings), color = SleekTextDark, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Owed Commission (10%)", color = SleekTextDark.copy(alpha = 0.5f), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                Text(
                                    text = String.format(Locale.getDefault(), "%.2f €", activeDriver.dueCommission),
                                    color = if (activeDriver.dueCommission > 0.0) SleekAlertText else SleekTextDark,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                                Text("Days Left to Pay", color = SleekTextDark.copy(alpha = 0.5f), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                Text(
                                    text = "${activeDriver.daysRemaining} Days",
                                    color = if (activeDriver.daysRemaining <= 1) SleekAlertText else SleekBrandPurple,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Commission Clearing Payment section
                        if (activeDriver.dueCommission > 0.0) {
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = SleekCardBorder.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Pay Outstanding 10% Dues",
                                    color = SleekTextDark,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Button(
                                    onClick = { viewModel.driverPayCommission(activeDriver.dueCommission) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006C4C)),
                                    modifier = Modifier.testTag("pay_commission_button")
                                ) {
                                    Text("Pay ${String.format(Locale.getDefault(), "%.2f €", activeDriver.dueCommission)} Due", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Driver Ongoing trip controls (if any)
            val driverActiveTrip = trips.find { t -> t.driverId == activeDriver.id && t.status != "Completed" && t.status != "Cancelled" }
            if (driverActiveTrip != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.5.dp, SleekBrandPurple),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "🚀 ONGOING RIDE BOOKING",
                                color = SleekBrandPurple,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Client: ${driverActiveTrip.customerName}",
                                color = SleekTextDark,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Pickup: ${driverActiveTrip.pickupName}",
                                color = SleekTextDark.copy(alpha = 0.6f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Dest: ${driverActiveTrip.destName}",
                                color = SleekTextDark.copy(alpha = 0.6f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("MGM Contract Fare:", color = SleekTextDark.copy(alpha = 0.5f), fontSize = 11.sp)
                                Text(String.format(Locale.getDefault(), "%.2f €", driverActiveTrip.fareEuro), color = SleekBrandPurple, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(16.dp))

                            // Action triggers
                            when (driverActiveTrip.status) {
                                "Accepted" -> {
                                    Button(
                                        onClick = { viewModel.advanceActiveTrip(driverActiveTrip.id, "Accepted") },
                                        colors = ButtonDefaults.buttonColors(containerColor = SleekBrandPurple),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Drive to Pick up Location (A) 🌐", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                                "Arriving" -> {
                                    Button(
                                        onClick = { viewModel.advanceActiveTrip(driverActiveTrip.id, "Arriving") },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006C4C)),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                        // Starts simulated trip driving on main thread
                                    ) {
                                        Text("Start Trip (Passenger is On Site) 🚖", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                                "InProgress" -> {
                                    Button(
                                        onClick = { viewModel.advanceActiveTrip(driverActiveTrip.id, "InProgress") },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006C4C)),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Complete Taxi Ride • Collect Cash € on Arrival", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { viewModel.driverCancelTrip(driverActiveTrip.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                border = BorderStroke(1.dp, SleekAlertText),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Decline/Cancel Job", color = SleekAlertText, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            } else {
                // List of Available bookings with distance filters based on expert limits
                item {
                    Text(
                        text = "AVAILABLE DISPATCH JOBS",
                        color = SleekTextDark,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                val requestedTrips = trips.filter { t -> t.status == "Requested" }

                if (activeDriver.isSuspended) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SleekAlertBg),
                            border = BorderStroke(1.dp, SleekAlertBorder),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "🔒 DIAL dispatch is locked because your account is suspended. Please pay outstanding dues first.",
                                color = SleekAlertText,
                                modifier = Modifier.padding(16.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else if (requestedTrips.isEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, SleekCardBorder),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "💤 No available passenger booking orders in the Mönchengladbach network. Go to Customer role and request a trip!",
                                color = SleekTextDark.copy(alpha = 0.6f),
                                modifier = Modifier.padding(16.dp),
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                } else {
                    items(requestedTrips) { t ->
                        // Calculate dispatch distance from driver current position to pickup location
                        val distToPickup = calculateDistanceKm(activeDriver.latitude, activeDriver.longitude, t.pickupLat, t.pickupLon)
                        val isWithinRange = distToPickup <= activeDriver.maxSearchRadiusKm

                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, if (isWithinRange) Color(0xFF006C4C) else SleekCardBorder),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Client: ${t.customerName}", color = SleekTextDark, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = String.format(Locale.getDefault(), "%.2f €", t.fareEuro),
                                        color = SleekBrandPurple,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Pickup: ${t.pickupName}", color = SleekTextDark.copy(alpha = 0.6f), fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text("Dest: ${t.destName}", color = SleekTextDark.copy(alpha = 0.6f), fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)

                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(color = SleekCardBorder.copy(alpha = 0.5f))
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Client Distance", color = SleekTextDark.copy(alpha = 0.5f), fontSize = 9.sp)
                                        Text(
                                            text = String.format(Locale.getDefault(), "%.2f km", distToPickup),
                                            color = if (isWithinRange) SleekTextDark else SleekAlertText,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    if (isWithinRange) {
                                        Button(
                                            onClick = { viewModel.driverAcceptTrip(t.id) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006C4C)),
                                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                            modifier = Modifier.height(34.dp)
                                        ) {
                                            Text("Accept Drive ✅", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    } else {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Lock, contentDescription = null, tint = SleekAlertText, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Far (" + String.format(Locale.getDefault(), "%.0f%% limit exceeded", (distToPickup / activeDriver.maxSearchRadiusKm - 1.0) * 100.0) + ")",
                                                color = SleekAlertText,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                if (!isWithinRange) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "🔒 Requires Level ${if (distToPickup <= 6.0) 2 else if (distToPickup <= 12.0) 3 else 4} ${if (distToPickup <= 6.0) "(Experienced)" else if (distToPickup <= 12.0) "(Expert)" else "(Master)"} status. Current Level range limit is ${activeDriver.maxSearchRadiusKm} km.",
                                        color = SleekAlertText,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Driver Signup / Register custom driver
            item {
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = SleekCardBorder.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "REGISTER NEW TAXI DRIVER",
                    color = SleekTextDark,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, SleekCardBorder),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        OutlinedTextField(
                            value = newName,
                            onValueChange = { viewModel.newDriverName.value = it },
                            label = { Text("Driver Name") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SleekBrandPurple,
                                unfocusedBorderColor = SleekCardBorder,
                                focusedLabelColor = SleekBrandPurple,
                                unfocusedLabelColor = SleekTextDark.copy(alpha = 0.6f),
                                focusedTextColor = SleekTextDark,
                                unfocusedTextColor = SleekTextDark
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newCar,
                            onValueChange = { viewModel.newDriverCar.value = it },
                            label = { Text("Vehicle Spec (Mercedes E-Class)") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SleekBrandPurple,
                                unfocusedBorderColor = SleekCardBorder,
                                focusedLabelColor = SleekBrandPurple,
                                unfocusedLabelColor = SleekTextDark.copy(alpha = 0.6f),
                                focusedTextColor = SleekTextDark,
                                unfocusedTextColor = SleekTextDark
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newPlate,
                            onValueChange = { viewModel.newDriverPlate.value = it },
                            label = { Text("Germany License Plate (MG-XX-111)") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SleekBrandPurple,
                                unfocusedBorderColor = SleekCardBorder,
                                focusedLabelColor = SleekBrandPurple,
                                unfocusedLabelColor = SleekTextDark.copy(alpha = 0.6f),
                                focusedTextColor = SleekTextDark,
                                unfocusedTextColor = SleekTextDark
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.registerNewDriver() },
                            colors = ButtonDefaults.buttonColors(containerColor = SleekBrandPurple),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = newName.isNotBlank() && newCar.isNotBlank() && newPlate.isNotBlank()
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Register as Carrier Driver", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// Screen 3: ADMIN/MANAGER SEGMENT
@Composable
fun AdminScreen(
    viewModel: TaxiViewModel,
    drivers: List<Driver>,
    trips: List<Trip>
) {
    val completedTrips = trips.filter { t -> t.status == "Completed" }
    
    // Total gross on-site cash revenue
    val totalRevenue = completedTrips.sumOf { t -> t.fareEuro }
    // Commission outstanding to clear
    val outstandingCommission = drivers.sumOf { d -> d.dueCommission }
    // Total commission collected (already cleared/paid)
    // For calculation simplicity: we can sum total completed commissions minus what remains due
    val totalCommissionBilled = completedTrips.sumOf { t -> t.commissionEuro }
    val commissionPaid = (totalCommissionBilled - outstandingCommission).coerceAtLeast(0.0)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("admin_screen_layout")
    ) {
        // Business Metrics Row
        item {
            Text(
                "MÖNCHENGLADBACH CAB BALANCE SHEET",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1E22)),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Gross Revenue", color = Color(0xFF8899A6), fontSize = 10.sp)
                        Text(String.format(Locale.getDefault(), "%.2f €", totalRevenue), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black)
                        Text("Completed: ${completedTrips.size} trips", color = Color(0xFF637381), fontSize = 8.sp)
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1E22)),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Ledger Dues", color = Color(0xFFFFA000), fontSize = 10.sp)
                        Text(String.format(Locale.getDefault(), "%.2f €", outstandingCommission), color = Color(0xFFFFCC00), fontSize = 14.sp, fontWeight = FontWeight.Black)
                        Text("10% Gross Tax Outstanding", color = Color(0xFF637381), fontSize = 8.sp)
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1E22)),
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Commissions Clear", color = Color(0xFF2E7D32), fontSize = 10.sp)
                        Text(String.format(Locale.getDefault(), "%.2f €", commissionPaid), color = Color(0xFF2E7D32), fontSize = 14.sp, fontWeight = FontWeight.Black)
                        Text("Paid to Center", color = Color(0xFF637381), fontSize = 8.sp)
                    }
                }
            }
        }

        // TIME SIMULATION ENGINE CARD - Critical User Journey testing!
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2F20)),
                border = BorderStroke(1.dp, Color(0xFF2E7D32)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "⏳ SERVICE CYCLE TIMEMOTION DOCK",
                        color = Color(0xFF81C784),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Advance the service calendar here. Once driver service exceeds 1 week (7 days) without clearing their 10% cash dues, our dispatch routing engine automatically suspends their license.",
                        color = Color(0xFFE8F5E9),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { viewModel.simulateNextDay() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("simulate_time_button")
                    ) {
                        Text("Simulate 1 Service Day Passage 🔁", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Active Carrier Registry Manager
        item {
            Text(
                "ACTIVE TAXI CARRIERS REGISTRY (${drivers.size} ACTIVE)",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        items(drivers) { driver ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1E22)),
                border = BorderStroke(1.dp, if (driver.isSuspended) Color(0xFFD32F2F) else Color(0xFF2D3545)),
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = driver.name + " (" + driver.levelName + " Lvl " + driver.level + ")",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Plate: ${driver.carLicensePlate} • XP: ${driver.xp}/100",
                                color = Color(0xFF8899A6),
                                fontSize = 10.sp
                            )
                        }

                        // Suspended Label Indicator
                        Badge(containerColor = if (driver.isSuspended) Color(0xFFD32F2F) else Color(0xFF2E7D32)) {
                            Text(
                                text = if (driver.isSuspended) "SUSPENDED" else driver.status.uppercase(Locale.getDefault()),
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = Color(0xFF222830))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Driver Balance", color = Color(0xFF8899A6), fontSize = 10.sp)
                            Text(String.format(Locale.getDefault(), "%.2f €", driver.totalEarnings), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Outstanding Dues", color = Color(0xFF8899A6), fontSize = 10.sp)
                            Text(
                                text = String.format(Locale.getDefault(), "%.2f €", driver.dueCommission),
                                color = if (driver.dueCommission > 0.0) Color(0xFFD32F2F) else Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column {
                            Text("Dues Deadline", color = Color(0xFF8899A6), fontSize = 10.sp)
                            Text(
                                text = "${driver.daysRemaining} Days Left",
                                color = if (driver.daysRemaining <= 1) Color(0xFFD32F2F) else Color(0xFFFFCC00),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Administrative action buttons
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        if (driver.dueCommission > 0.0) {
                            Button(
                                onClick = { viewModel.adminClearDriverCommision(driver.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                modifier = Modifier
                                    .height(28.dp)
                                    .padding(end = 6.dp)
                            ) {
                                Text("Record Pay Receipt", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Button(
                            onClick = { viewModel.adminToggleLockState(driver.id) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (driver.isSuspended) Color(0xFFFFA000) else Color(0xFFD32F2F)
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text(
                                text = if (driver.isSuspended) "Re-activate" else "Force Lock",
                                color = if (driver.isSuspended) Color.Black else Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Active Jobs Log Registry
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "TAXICAB DISPATCH JOB LOGS (COMPLETED: ${completedTrips.size})",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (trips.isEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1E22)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No recorded trips in network DB.",
                        color = Color(0xFF8899A6),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        items(trips) { trip ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1E22)),
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(bottom = 6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Rider: ${trip.customerName} • Driver: ${trip.driverName ?: "Pending"}",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "From ${trip.pickupName} to ${trip.destName}",
                            color = Color(0xFF8899A6),
                            fontSize = 10.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Column(horizontalAlignment = Alignment.End, modifier = Modifier.padding(start = 8.dp)) {
                        Text(
                            text = String.format(Locale.getDefault(), "%.2f €", trip.fareEuro),
                            color = Color(0xFFFFCC00),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                        Badge(
                            containerColor = when (trip.status) {
                                "Completed" -> Color(0xFF2E7D32)
                                "Cancelled" -> Color(0xFFD32F2F)
                                "Requested" -> Color(0xFFFFA000)
                                else -> Color(0xFF1976D2)
                            }
                        ) {
                            Text(trip.status.uppercase(Locale.getDefault()), color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Hard Reset Button for simulation recovery
        item {
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { viewModel.resetEntireSystem() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_reset_button")
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("RESTORE DEFAULT ACTORS & TRIPS", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// Custom Badge replacement for older Material Jetpack Compose releases compatibility
@Composable
fun Badge(
    containerColor: Color,
    content: @Composable () -> Unit
) {
    Surface(
        color = containerColor,
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier.padding(2.dp)
    ) {
        Box(modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)) {
            content()
        }
    }
}
