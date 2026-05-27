# Gladbach Cab — Upgrade Plan

## Overview
Redesign the existing single-app prototype into a **production-quality taxi platform** for Mönchengladbach, Germany, inspired by the TaxiF/Uber design. Three separate apps (Customer, Driver, Admin) sharing a Firebase backend.

---

## Phase 1 — Architecture & Backend

| Task | Details |
|---|---|
| **Split into 3 apps** | Use Gradle product flavors (customer/driver/admin) from the same codebase |
| **Firebase setup** | Firestore for real-time data, Firebase Auth for login, FCM for push notifications |
| **Shared data model** | Migrate from local Room DB to Firestore collections: `users`, `drivers`, `trips`, `payments` |
| **Real-time sync** | Customer sees driver location in real-time; driver sees incoming requests instantly |
| **Maps SDK** | Add Google Maps Compose dependency, replace all Canvas drawing |

---

## Phase 2 — Customer App (TaxiF-style)

### Screens
| Screen | Description |
|---|---|
| **Home / Map** | Full-screen Google Maps, auto-detect current location, draggable pickup pin, destination search bar |
| **Destination picker** | Text search + quick-select chips for 9 Mönchengladbach landmarks (same as current) |
| **Vehicle type** | Horizontal carousel: Standard, Luxury, Minivan — each with photo, description, price/km |
| **Ride preview** | Route polyline on map, fare breakdown, ETA, "Book Now" CTA |
| **Live tracking** | Real-time driver marker moving on map, driver photo/name/rating card, ETA countdown, cancel button |
| **Post-ride** | Rating (1-5 stars), "Pay on Site" reminder, trip receipt, book again |

### Key flows
- Booking: Map → Pickup pin → Search/select destination → Choose vehicle → See price → Book
- Tracking: Driver accepts → See driver moving toward you on map → Ride in progress to destination → Complete

---

## Phase 3 — Driver App

### Screens
| Screen | Description |
|---|---|
| **Live Map** | Current GPS location streamed to Firestore, nearby request heatmap |
| **Availability toggle** | Online/Offline switch at top |
| **Job Board** | List of `Requested` trips near driver position — **sorted by distance, gated by level** |
| **Trip navigation** | Accept → Drive to pickup (driver marker moves) → Start trip → Navigate to destination → Complete |
| **Earnings dashboard** | XP progress bar, level badge, total earnings, due commission, "Pay Dues" button |
| **Level-up modal** | Celebration when XP threshold is hit |

### Level system (unchanged from current logic)
| Level | Name | Search Radius | XP Needed |
|---|---|---|---|
| 1 | Rookie | 2.5 km | 0 |
| 2 | Experienced | 6.0 km | 100 |
| 3 | Expert | 12.0 km | 200 |
| 4 | Master (Elite) | 30.0 km (whole city) | 300 |

---

## Phase 4 — Admin Dashboard

### Screens
| Screen | Description |
|---|---|
| **Overview** | Gross revenue, outstanding commission, paid commission — dark-themed cards |
| **Driver Registry** | List of drivers with name, level, dues, days remaining, suspend/activate toggle, "Record Payment" |
| **Trip Log** | All trips with status, fare, driver, customer — filterable by date/status |
| **Time Engine** | "Simulate Day" button for testing weekly suspension logic |
| **Reset** | Restore default drivers and trips |

### Auto-payout engine
- **WorkManager** runs weekly check on service days
- If `daysRemaining <= 0 && dueCommission > 0` → auto-suspend driver
- Push notification to driver: "Account suspended — pay 10% dues to reactivate"

---

## Phase 5 — UI/UX Overhaul

| Item | Current | Target |
|---|---|---|
| **Maps** | Canvas drawing (static) | Google Maps Compose (interactive tiles, real markers) |
| **Color scheme** | Purple/lavender | Taxi yellow/black or clean white + brand color |
| **Typography** | System default | Clean sans-serif (proper font resource) |
| **Cards** | Round with borders | Material 3 elevated cards with shadows |
| **Navigation** | Role pills in app | Standard bottom nav / drawer per app |
| **Driver markers** | Emoji + name | Custom car icon with rotation direction |
| **Route line** | Dashed Canvas line | Google Maps polyline with ETA |
| **Animations** | Manual coroutine | Proper Compose animations (marker movement, transitions) |

---

## Phase 6 — Enhancements

- **Push notifications** (FCM): Trip accepted, driver arriving, ride complete, suspension warning
- **Receipt history**: PDF or in-app receipt for each completed trip
- **Favorite places**: Save frequent addresses for quick booking
- **Rating system**: 1-5 stars + optional comment, visible to drivers
- **Multi-language**: German (primary) + English
- **Offline mode**: Cache last-known state, queue actions when offline

---

## Tech Stack

| Component | Choice |
|---|---|
| **UI** | Jetpack Compose + Material 3 |
| **Maps** | Google Maps Compose (`maps-compose`) |
| **Backend** | Firebase Firestore + Firebase Auth + FCM |
| **Location** | Fused Location Provider |
| **Background tasks** | WorkManager |
| **Build** | Gradle with product flavors |
| **Min SDK** | 26 (Android 8.0) |
| **Target SDK** | 36 |

---

## File Structure

```
app/
├── src/
│   ├── customer/          # Customer flavor
│   │   └── java/.../
│   ├── driver/            # Driver flavor
│   │   └── java/.../
│   ├── admin/             # Admin flavor
│   │   └── java/.../
│   ├── main/              # Shared code
│   │   ├── java/com/example/
│   │   │   ├── data/      # Firestore repositories
│   │   │   ├── ui/        # Shared theme, components
│   │   │   └── util/      # Helpers
│   │   └── res/
├── build.gradle.kts
```

---

## Order of Implementation

1. **Phase 1** — Firebase setup, product flavors, shared data layer
2. **Phase 2** — Customer app with Google Maps, booking flow, live tracking
3. **Phase 3** — Driver app with job board, navigation, earnings dashboard
4. **Phase 4** — Admin dashboard with driver management and time engine
5. **Phase 5** — UI polish, dark mode, animations
6. **Phase 6** — Notifications, receipts, ratings, i18n
