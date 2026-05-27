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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
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
import com.example.ui.theme.OffWhite
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.TaxiYellow
import com.example.ui.theme.YellowBg
import com.example.ui.theme.TaxiYellowDark
import com.example.ui.theme.GrayBg
import com.example.ui.theme.MapBg
import com.example.ui.theme.BorderMedium
import com.example.ui.theme.BorderLight
import com.example.ui.theme.AlertBg
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.AlertBorder
import com.example.ui.theme.NavBg
import com.example.ui.theme.YellowBgDark
import com.example.ui.theme.TaxiAmber
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SlateGray
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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = OffWhite,
        bottomBar = {
            NavigationBar(
                containerColor = NavBg,
                tonalElevation = 0.dp
            ) {
                val navItems = listOf(
                    Triple("customer", "Rider", Icons.Default.Person),
                    Triple("driver", "Driver", Icons.Default.Place),
                    Triple("manager", "Admin", Icons.Default.Build),
                )
                navItems.forEach { (roleKey, label, icon) ->
                    NavigationBarItem(
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label, fontWeight = FontWeight.Bold) },
                        selected = activeRole == roleKey,
                        onClick = { viewModel.setRole(roleKey) },
                        colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                            selectedIconColor = CharcoalDark,
                            selectedTextColor = CharcoalDark,
                            indicatorColor = TaxiYellow,
                            unselectedIconColor = SlateGray,
                            unselectedTextColor = SlateGray,
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(OffWhite),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(OffWhite)
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Gladbach Cab",
                        color = CharcoalDark,
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
                                .background(SuccessGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "MÖNCHENGLADBACH CENTER",
                            color = CharcoalDark.copy(alpha = 0.6f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(GrayBg)
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "AD",
                        color = CharcoalDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(MapBg)
                    .border(1.dp, BorderMedium)
            ) {
                MoenchengladbachMetroMap(
                    drivers = drivers,
                    activeTrip = activeCustomerTrip ?: trips.find { t -> t.status in listOf("Accepted", "Arriving", "InProgress") },
                    selectedDriver = activeDriver,
                    pickupPlace = pickupPlace,
                    destPlace = destPlace
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                        .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(24.dp))
                        .border(1.dp, BorderMedium, RoundedCornerShape(24.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "12 ACTIVE DRIVERS",
                        color = CharcoalDark,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(OffWhite)
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
                colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                border = BorderStroke(2.dp, TaxiYellow),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🏆 LEVEL UP!", fontSize = 24.sp, fontWeight = FontWeight.Black, color = TaxiYellow)
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
                        colors = ButtonDefaults.buttonColors(containerColor = TaxiYellow),
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
                
                val routeColor = if (activeTrip.status == "InProgress") TaxiYellow else TaxiYellowDark
                drawLine(
                    color = routeColor.copy(alpha = 0.3f),
                    start = startPt,
                    end = endPt,
                    strokeWidth = 7f,
                )
                drawLine(
                    color = routeColor,
                    start = startPt,
                    end = endPt,
                    strokeWidth = 4f,
                )
                val dirMid = Offset((startPt.x + endPt.x) / 2f, (startPt.y + endPt.y) / 2f)
                val angle = kotlin.math.atan2((endPt.y - startPt.y).toDouble(), (endPt.x - startPt.x).toDouble()).toFloat()
                drawCircle(routeColor, 5f, dirMid)
                drawCircle(PureWhite, 2f, dirMid)
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
                    color = TaxiYellow,
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
                    .border(1.dp, BorderMedium.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(
                    text = name,
                    color = CharcoalDark,
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
                    .background(TaxiYellow, CircleShape)
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
                    .background(ErrorRed, CircleShape)
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
                        if (driver.isSuspended) AlertBg
                        else if (driver.status == "Busy") YellowBg
                        else if (isSelectedOnMap) YellowBg
                        else Color.White
                    )
                    .border(
                        if (isSelectedOnMap) 2.dp else 1.dp,
                        if (driver.isSuspended) ErrorRed
                        else if (isSelectedOnMap || driver.status == "Available") TaxiYellow
                        else BorderMedium,
                        CircleShape
                    )
                    .clickable { /* Choose map driver */ },
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(18.dp)) {
                    val cx = size.width / 2
                    val cy = size.height / 2
                    if (driver.isSuspended) {
                        drawLine(ErrorRed, Offset(cx - 5f, cy - 5f), Offset(cx + 5f, cy + 5f), strokeWidth = 3f)
                        drawLine(ErrorRed, Offset(cx + 5f, cy - 5f), Offset(cx - 5f, cy + 5f), strokeWidth = 3f)
                    } else if (driver.status == "Busy") {
                        drawCircle(TaxiYellow, 7f, Offset(cx, cy))
                        drawCircle(CharcoalDark, 3f, Offset(cx, cy - 2f))
                        drawRect(CharcoalDark, Offset(cx - 4f, cy + 1f), androidx.compose.ui.geometry.Size(8f, 3f))
                    } else {
                        val bodyColor = if (isSelectedOnMap) TaxiYellow else CharcoalDark
                        val path = androidx.compose.ui.graphics.Path().apply {
                            moveTo(cx - 7f, cy - 3f)
                            lineTo(cx - 5f, cy - 7f)
                            lineTo(cx + 5f, cy - 7f)
                            lineTo(cx + 7f, cy - 3f)
                            lineTo(cx + 8f, cy - 1f)
                            lineTo(cx + 8f, cy + 3f)
                            lineTo(cx + 5f, cy + 3f)
                            lineTo(cx + 4f, cy + 6f)
                            lineTo(cx - 4f, cy + 6f)
                            lineTo(cx - 5f, cy + 3f)
                            lineTo(cx - 8f, cy + 3f)
                            lineTo(cx - 8f, cy - 1f)
                            close()
                        }
                        drawPath(path, bodyColor)
                        drawCircle(PureWhite, 3f, Offset(cx - 4f, cy + 3f))
                        drawCircle(PureWhite, 3f, Offset(cx + 4f, cy + 3f))
                    }
                }
            }

            // Driver Name tag below the active vehicle
            Box(
                modifier = Modifier
                    .offset(dX.dp - 35.dp, dY.dp + 16.dp)
                    .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(4.dp))
                    .border(1.dp, if (isSelectedOnMap) TaxiYellow else BorderMedium, RoundedCornerShape(4.dp))
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
                Text(
                    text = "${driver.name.substringBefore(" ")} Lv.${driver.level}",
                    color = if (driver.isSuspended) ErrorRed else CharcoalDark,
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
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
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
                        tint = TaxiYellow,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Rider Username", color = CharcoalDark.copy(alpha = 0.6f), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        Text(customerName, color = CharcoalDark, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    // Rename mini action
                    Button(
                        onClick = {
                            val names = listOf("Markus", "Anja", "Frank", "Tanja", "Christian", "Stefanie")
                            viewModel.setCustomerName(names.random())
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = YellowBg),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("Change", color = CharcoalDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Live Tracker View if active trip is outstanding
        if (activeTrip != null) {
            item {
Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.5.dp, TaxiYellow),
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
                                color = TaxiYellow,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                letterSpacing = 0.5.sp
                            )
                            Badge(
                                containerColor = when (activeTrip.status) {
                                    "Requested" -> TaxiAmber
                                    "Accepted", "Arriving" -> TaxiYellow
                                    "InProgress" -> TaxiYellow
                                    else -> TaxiYellow
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
                            color = CharcoalDark,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = BorderLight.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Trip Details
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Pickup Location", color = CharcoalDark.copy(alpha = 0.5f), fontSize = 10.sp, fontWeight = FontWeight.Medium)
                                Text(activeTrip.pickupName, color = CharcoalDark, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Destination Location", color = CharcoalDark.copy(alpha = 0.5f), fontSize = 10.sp, fontWeight = FontWeight.Medium)
                                Text(activeTrip.destName, color = CharcoalDark, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Calculated Distance", color = CharcoalDark.copy(alpha = 0.5f), fontSize = 10.sp, fontWeight = FontWeight.Medium)
                                Text(String.format(Locale.getDefault(), "%.2f km", activeTrip.distanceKm), color = CharcoalDark, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("German Site Fare", color = CharcoalDark.copy(alpha = 0.5f), fontSize = 10.sp, fontWeight = FontWeight.Medium)
                                Text(String.format(Locale.getDefault(), "%.2f €", activeTrip.fareEuro), color = TaxiYellow, fontSize = 18.sp, fontWeight = FontWeight.Black)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (activeTrip.status in listOf("Requested", "Accepted")) {
                            Button(
                                onClick = { viewModel.customerCancelTrip() },
                                colors = ButtonDefaults.buttonColors(containerColor = AlertBg),
                                border = BorderStroke(1.dp, AlertBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("cancel_ride_button")
                            ) {
                                Text("Cancel Booking Request", color = ErrorRed, fontWeight = FontWeight.Bold)
                            }
                        } else if (activeTrip.status == "Completed") {
                            Button(
                                onClick = { viewModel.clearCustomerBookingState() },
                                colors = ButtonDefaults.buttonColors(containerColor = TaxiYellow),
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
                                color = TaxiAmber,
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
                    color = CharcoalDark,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Select Pickup Address Chip list
                Text("Select PICKUP Point (Station A):", color = TaxiYellow, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 6.dp))
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    items(GLADBACH_PLACES) { loc ->
                        val isSelected = pickupPlace.name == loc.name
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) YellowBg else Color.White
                            ),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, if (isSelected) TaxiYellow else BorderLight),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clickable { viewModel.setPickup(loc) }
                        ) {
                            Text(
                                text = "📍 " + loc.name.substringBefore(" ("),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                color = if (isSelected) CharcoalDark else CharcoalDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Select Destination Address Chip list
                Text("Select DESTINATION Point (Station B):", color = TaxiYellow, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 6.dp))
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    items(GLADBACH_PLACES) { loc ->
                        val isSelected = destPlace.name == loc.name
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) GrayBg else Color.White
                            ),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, if (isSelected) TaxiYellow else BorderLight),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clickable { viewModel.setDestination(loc) }
                        ) {
                            Text(
                                text = "🏁 " + loc.name.substringBefore(" ("),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                color = if (isSelected) CharcoalDark else CharcoalDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Selected Route Details Card
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BorderLight),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Place, contentDescription = null, tint = TaxiYellow, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "From: ${pickupPlace.name}", color = CharcoalDark, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Place, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "To: ${destPlace.name}", color = CharcoalDark, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = BorderLight.copy(alpha = 0.5f))
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
                                Text("Route Distance", color = CharcoalDark.copy(alpha = 0.5f), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                Text(String.format(Locale.getDefault(), "%.2f km", distance), color = CharcoalDark, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Est. On-site Price", color = CharcoalDark.copy(alpha = 0.5f), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                Text(String.format(Locale.getDefault(), "%.2f €", estFare), color = TaxiYellow, fontSize = 18.sp, fontWeight = FontWeight.Black)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(NavBg, RoundedCornerShape(12.dp))
                                .border(1.dp, BorderLight.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("💶 Payment Scheme:", color = CharcoalDark.copy(alpha = 0.6f), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            Text("PAY ON SITE", color = TaxiYellow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        
                        Text(
                            text = "Note: In Mönchengladbach, Germany, taxis charge on arrival. The administration puts a 10% commission fee of ${String.format(Locale.getDefault(), "%.2f €", driverOwes)} to the driver's ledger on completed trips.",
                            color = CharcoalDark.copy(alpha = 0.5f),
                            fontSize = 9.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                if (pickupPlace.name == destPlace.name) {
                    Text(
                        text = "⚠️ Pickup and Destination cannot be identical.",
                        color = ErrorRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                } else {
                    Button(
                        onClick = { viewModel.orderTaxi() },
                        colors = ButtonDefaults.buttonColors(containerColor = TaxiYellow),
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
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BorderLight),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "🟢 ACTIVE LOCAL TAXI CARRIERS",
                        color = TaxiYellow,
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
                                        color = if (driver.isSuspended) ErrorRed else CharcoalDark,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${driver.carModel} [${driver.carLicensePlate}]",
                                        color = CharcoalDark.copy(alpha = 0.6f),
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = if (driver.isSuspended) "SUSPENDED" else driver.status.uppercase(Locale.getDefault()),
                                    color = if (driver.isSuspended) ErrorRed
                                    else if (driver.status == "Busy") TaxiAmber
                                    else TaxiYellow,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = String.format(Locale.getDefault(), "%.1f km away", distanceToPickup),
                                    color = if (inCarrierRange) TaxiYellow else ErrorRed,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        HorizontalDivider(color = BorderLight.copy(alpha = 0.3f), thickness = 1.dp)
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
                    color = CharcoalDark,
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
                    colors = ButtonDefaults.buttonColors(containerColor = YellowBg),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text("Cycle Profile 🔁", color = CharcoalDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
                            containerColor = if (isSelected) YellowBg else Color.White
                        ),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clickable { viewModel.selectActiveDriver(d.id) },
                        border = if (d.isSuspended) BorderStroke(1.dp, ErrorRed) 
                                 else if (isSelected) BorderStroke(1.dp, TaxiYellow)
                                 else BorderStroke(1.dp, BorderLight),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = (if (d.isSuspended) "🚫 " else "") + d.name.substringBefore(" "),
                                color = if (isSelected) CharcoalDark else CharcoalDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Lvl ${d.level} • ${d.levelName}",
                                color = if (isSelected) CharcoalDark.copy(alpha = 0.8f) else CharcoalDark.copy(alpha = 0.5f),
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
                    border = BorderStroke(1.dp, BorderLight),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Please select or register a driver below to enter the portal.",
                        color = CharcoalDark,
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
                        colors = CardDefaults.cardColors(containerColor = AlertBg),
                        border = BorderStroke(1.dp, AlertBorder),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = ErrorRed)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "ACCOUNT SUSPENDED",
                                    color = ErrorRed,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Dear ${activeDriver.name}, your account is suspended because you failed to clear your 10% agency dues of ${String.format(Locale.getDefault(), "%.2f €", activeDriver.dueCommission)} at the end of your 1-week service interval. Please pay outstanding dues below to resume service.",
                                color = ErrorRed,
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
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.5.dp, if (activeDriver.isSuspended) ErrorRed else TaxiYellow),
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
                                Text("DRIVER LICENSE", color = TaxiYellow, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                                Text(activeDriver.name, color = CharcoalDark, fontSize = 18.sp, fontWeight = FontWeight.Black)
                            }
                            Badge(containerColor = if (activeDriver.isSuspended) ErrorRed else TaxiYellow) {
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
                                color = CharcoalDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${activeDriver.xp} / ${activeDriver.xpNeededForNextLevel} XP",
                                color = CharcoalDark.copy(alpha = 0.6f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = activeDriver.levelProgress,
                            color = TaxiYellow,
                            trackColor = NavBg,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Radiuses explanation
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = TaxiYellow, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Dispatch Radius Limit: ${activeDriver.maxSearchRadiusKm} km range • " + when (activeDriver.level) {
                                    1 -> "Local (Rookie Level)"
                                    2 -> "District (Level 2)"
                                    3 -> "City Wide (Expert Level)"
                                    else -> "All Metro Mönchengladbach (Master)"
                                },
                                color = CharcoalDark.copy(alpha = 0.5f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = BorderLight.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(14.dp))

                        // Financial values
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("On-site Cash Earned", color = CharcoalDark.copy(alpha = 0.5f), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                Text(String.format(Locale.getDefault(), "%.2f €", activeDriver.totalEarnings), color = CharcoalDark, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Owed Commission (10%)", color = CharcoalDark.copy(alpha = 0.5f), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                Text(
                                    text = String.format(Locale.getDefault(), "%.2f €", activeDriver.dueCommission),
                                    color = if (activeDriver.dueCommission > 0.0) ErrorRed else CharcoalDark,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                                Text("Days Left to Pay", color = CharcoalDark.copy(alpha = 0.5f), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                Text(
                                    text = "${activeDriver.daysRemaining} Days",
                                    color = if (activeDriver.daysRemaining <= 1) ErrorRed else TaxiYellow,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Commission Clearing Payment section
                        if (activeDriver.dueCommission > 0.0) {
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = BorderLight.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Pay Outstanding 10% Dues",
                                    color = CharcoalDark,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Button(
                                    onClick = { viewModel.driverPayCommission(activeDriver.dueCommission) },
                                    colors = ButtonDefaults.buttonColors(containerColor = TaxiYellow),
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
                        border = BorderStroke(1.5.dp, TaxiYellow),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "🚀 ONGOING RIDE BOOKING",
                                color = TaxiYellow,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Client: ${driverActiveTrip.customerName}",
                                color = CharcoalDark,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Pickup: ${driverActiveTrip.pickupName}",
                                color = CharcoalDark.copy(alpha = 0.6f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Dest: ${driverActiveTrip.destName}",
                                color = CharcoalDark.copy(alpha = 0.6f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("MGM Contract Fare:", color = CharcoalDark.copy(alpha = 0.5f), fontSize = 11.sp)
                                Text(String.format(Locale.getDefault(), "%.2f €", driverActiveTrip.fareEuro), color = TaxiYellow, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(16.dp))

                            // Action triggers
                            when (driverActiveTrip.status) {
                                "Accepted" -> {
                                    Button(
                                        onClick = { viewModel.advanceActiveTrip(driverActiveTrip.id, "Accepted") },
                                        colors = ButtonDefaults.buttonColors(containerColor = TaxiYellow),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Drive to Pick up Location (A) 🌐", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                                "Arriving" -> {
                                    Button(
                                        onClick = { viewModel.advanceActiveTrip(driverActiveTrip.id, "Arriving") },
                                        colors = ButtonDefaults.buttonColors(containerColor = TaxiYellow),
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
                                        colors = ButtonDefaults.buttonColors(containerColor = TaxiYellow),
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
                                border = BorderStroke(1.dp, ErrorRed),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Decline/Cancel Job", color = ErrorRed, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            } else {
                // List of Available bookings with distance filters based on expert limits
                item {
                    Text(
                        text = "AVAILABLE DISPATCH JOBS",
                        color = CharcoalDark,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                val requestedTrips = trips.filter { t -> t.status == "Requested" }

                if (activeDriver.isSuspended) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = AlertBg),
                            border = BorderStroke(1.dp, AlertBorder),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "🔒 DIAL dispatch is locked because your account is suspended. Please pay outstanding dues first.",
                                color = ErrorRed,
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
                            border = BorderStroke(1.dp, BorderLight),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "💤 No available passenger booking orders in the Mönchengladbach network. Go to Customer role and request a trip!",
                                color = CharcoalDark.copy(alpha = 0.6f),
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
                            border = BorderStroke(1.dp, if (isWithinRange) TaxiYellow else BorderLight),
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
                                    Text("Client: ${t.customerName}", color = CharcoalDark, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = String.format(Locale.getDefault(), "%.2f €", t.fareEuro),
                                        color = TaxiYellow,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Pickup: ${t.pickupName}", color = CharcoalDark.copy(alpha = 0.6f), fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text("Dest: ${t.destName}", color = CharcoalDark.copy(alpha = 0.6f), fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)

                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(color = BorderLight.copy(alpha = 0.5f))
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Client Distance", color = CharcoalDark.copy(alpha = 0.5f), fontSize = 9.sp)
                                        Text(
                                            text = String.format(Locale.getDefault(), "%.2f km", distToPickup),
                                            color = if (isWithinRange) CharcoalDark else ErrorRed,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    if (isWithinRange) {
                                        Button(
                                            onClick = { viewModel.driverAcceptTrip(t.id) },
                                            colors = ButtonDefaults.buttonColors(containerColor = TaxiYellow),
                                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                            modifier = Modifier.height(34.dp)
                                        ) {
                                            Text("Accept Drive ✅", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    } else {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Lock, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Far (" + String.format(Locale.getDefault(), "%.0f%% limit exceeded", (distToPickup / activeDriver.maxSearchRadiusKm - 1.0) * 100.0) + ")",
                                                color = ErrorRed,
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
                                        color = ErrorRed,
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
                HorizontalDivider(color = BorderLight.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "REGISTER NEW TAXI DRIVER",
                    color = CharcoalDark,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BorderLight),
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
                                focusedBorderColor = TaxiYellow,
                                unfocusedBorderColor = BorderLight,
                                focusedLabelColor = TaxiYellow,
                                unfocusedLabelColor = CharcoalDark.copy(alpha = 0.6f),
                                focusedTextColor = CharcoalDark,
                                unfocusedTextColor = CharcoalDark
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newCar,
                            onValueChange = { viewModel.newDriverCar.value = it },
                            label = { Text("Vehicle Spec (Mercedes E-Class)") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = TaxiYellow,
                                unfocusedBorderColor = BorderLight,
                                focusedLabelColor = TaxiYellow,
                                unfocusedLabelColor = CharcoalDark.copy(alpha = 0.6f),
                                focusedTextColor = CharcoalDark,
                                unfocusedTextColor = CharcoalDark
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newPlate,
                            onValueChange = { viewModel.newDriverPlate.value = it },
                            label = { Text("Germany License Plate (MG-XX-111)") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = TaxiYellow,
                                unfocusedBorderColor = BorderLight,
                                focusedLabelColor = TaxiYellow,
                                unfocusedLabelColor = CharcoalDark.copy(alpha = 0.6f),
                                focusedTextColor = CharcoalDark,
                                unfocusedTextColor = CharcoalDark
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.registerNewDriver() },
                            colors = ButtonDefaults.buttonColors(containerColor = TaxiYellow),
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
                    colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Gross Revenue", color = SlateGray, fontSize = 10.sp)
                        Text(String.format(Locale.getDefault(), "%.2f €", totalRevenue), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black)
                        Text("Completed: ${completedTrips.size} trips", color = SlateGray, fontSize = 8.sp)
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Ledger Dues", color = TaxiAmber, fontSize = 10.sp)
                        Text(String.format(Locale.getDefault(), "%.2f €", outstandingCommission), color = TaxiYellow, fontSize = 14.sp, fontWeight = FontWeight.Black)
                        Text("10% Gross Tax Outstanding", color = SlateGray, fontSize = 8.sp)
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Commissions Clear", color = SuccessGreen, fontSize = 10.sp)
                        Text(String.format(Locale.getDefault(), "%.2f €", commissionPaid), color = SuccessGreen, fontSize = 14.sp, fontWeight = FontWeight.Black)
                        Text("Paid to Center", color = SlateGray, fontSize = 8.sp)
                    }
                }
            }
        }

        // TIME SIMULATION ENGINE CARD - Critical User Journey testing!
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2F20)),
                border = BorderStroke(1.dp, SuccessGreen),
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
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
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
                colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                border = BorderStroke(1.dp, if (driver.isSuspended) ErrorRed else CharcoalSurface),
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
                                color = SlateGray,
                                fontSize = 10.sp
                            )
                        }

                        // Suspended Label Indicator
                        Badge(containerColor = if (driver.isSuspended) ErrorRed else SuccessGreen) {
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
                    HorizontalDivider(color = CharcoalSurface)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Driver Balance", color = SlateGray, fontSize = 10.sp)
                            Text(String.format(Locale.getDefault(), "%.2f €", driver.totalEarnings), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Outstanding Dues", color = SlateGray, fontSize = 10.sp)
                            Text(
                                text = String.format(Locale.getDefault(), "%.2f €", driver.dueCommission),
                                color = if (driver.dueCommission > 0.0) ErrorRed else Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column {
                            Text("Dues Deadline", color = SlateGray, fontSize = 10.sp)
                            Text(
                                text = "${driver.daysRemaining} Days Left",
                                color = if (driver.daysRemaining <= 1) ErrorRed else TaxiYellow,
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
                                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
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
                                containerColor = if (driver.isSuspended) TaxiAmber else ErrorRed
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
                    colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No recorded trips in network DB.",
                        color = SlateGray,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        items(trips) { trip ->
            Card(
                colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
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
                            color = SlateGray,
                            fontSize = 10.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Column(horizontalAlignment = Alignment.End, modifier = Modifier.padding(start = 8.dp)) {
                        Text(
                            text = String.format(Locale.getDefault(), "%.2f €", trip.fareEuro),
                            color = TaxiYellow,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                        Badge(
                            containerColor = when (trip.status) {
                                "Completed" -> SuccessGreen
                                "Cancelled" -> ErrorRed
                                "Requested" -> TaxiAmber
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
                colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
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
