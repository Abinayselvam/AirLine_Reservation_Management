# Airline Ticket Reservation System

A console-based (with an optional REST API) Airline Ticket Reservation System built in
Java, using raw JDBC against MySQL — no Spring, no ORM. Built incrementally, one use
case at a time, across 18 use-case groups covering search, booking, payments,
cancellations, admin flight management, notifications, reporting, and check-in.

## Tech Stack

| Layer            | Technology                                   |
|-------------------|-----------------------------------------------|
| Language          | Java 21                                       |
| Database          | MySQL 8.x                                     |
| Data Access       | Raw JDBC (`PreparedStatement`, no ORM)        |
| Password Hashing  | jBCrypt                                       |
| REST API (optional) | JDK built-in `com.sun.net.httpserver` (no external web framework) |

## Project Structure

```
main/java/
├── Main.java                 # Console app entry point
├── ApiMain.java               # Optional REST API entry point
└── examples/
    ├── model/                 # Plain data classes (Flight, Booking, User, ...)
    ├── enums/                 # Role, BookingStatus, Permission, ...
    ├── repository/            # JDBC data access (impl classes)
    │   └── irepository/       # Repository interfaces
    ├── service/                # Business logic (impl classes)
    │   └── iservice/           # Service interfaces
    ├── operations/             # Console menus - the UI layer
    ├── manager/                 # Singletons: FlightManager, BookingManager,
    │                              PaymentManager, NotificationManager
    ├── payment/                 # IPayment + UpiPayment/CardPayment/EmiPayment
    ├── notification/            # Notification (abstract) + Email/SMS/WhatsApp
    ├── integration/              # Payment/SMS/Email gateway adapters (mocked)
    ├── exception/                 # Custom exception hierarchy
    ├── api/                       # REST API handlers (JDK HttpServer)
    └── util/                       # Validators, generators, cache, session, etc.
```

**Layering rule of thumb:** `operations` (menus) call `service` (business logic),
`service` calls `repository` (JDBC), `repository` talks to the database. Each
repository and service is coded against its interface (`IXxxRepository` /
`IXxxService`), with the concrete class injected manually where it's used.

## Prerequisites

- JDK 21+
- MySQL 8.x running locally (or update the connection details — see below)
- A dependency manager (Maven/Gradle) **or** manually downloaded jars if compiling
  by hand. This project needs:
    - `mysql-connector-j` (MySQL JDBC driver)
    - `jbcrypt` (password hashing)

  If you're using Maven, add:
  ```xml
  <dependencies>
      <dependency>
          <groupId>com.mysql</groupId>
          <artifactId>mysql-connector-j</artifactId>
          <version>8.4.0</version>
      </dependency>
      <dependency>
          <groupId>org.mindrot</groupId>
          <artifactId>jbcrypt</artifactId>
          <version>0.4</version>
      </dependency>
  </dependencies>
  <properties>
      <maven.compiler.source>21</maven.compiler.source>
      <maven.compiler.target>21</maven.compiler.target>
  </properties>
  ```
  *(Use whatever versions your original `pom.xml` already pinned if they differ —
  a `pom.xml` wasn't part of this handoff, so these are just current, commonly
  used versions.)*

## Database Setup

1. Create the database:
   ```sql
   CREATE DATABASE airline_service;
   ```
2. Make sure your `users` and `user_profiles` tables (from UC 1.1 / UC 1.2) already
   exist — they predate this schema file and aren't included in it.
3. Run `schema.sql` (included alongside this README) to create everything else:
   ```
   mysql -u root -p airline_service < schema.sql
   ```
   This creates `flights`, `seats`, `airports`, `bookings`, `booking_passengers`,
   `payment_transactions`, `notification_logs`, and the supporting indexes, in
   foreign-key-safe order. Run it once — `CREATE INDEX` isn't idempotent in
   MySQL, so re-running the script after indexes already exist will error on
   those lines (harmless, everything else uses `IF NOT EXISTS`).

4. Update credentials in `examples/util/DBConnection.java` if they differ from
   the defaults:
   ```java
   URL      = "jdbc:mysql://localhost:3306/airline_service"
   USER     = "root"
   PASSWORD = "root"
   ```

## Running the Console App

```
javac -cp ".:path/to/mysql-connector-j.jar:path/to/jbcrypt.jar" -d out $(find main/java -name "*.java")
java  -cp "out:path/to/mysql-connector-j.jar:path/to/jbcrypt.jar" Main
```
(On Windows, replace `:` with `;` in the classpath.)

Or, if you're using an IDE (IntelliJ/Eclipse) with Maven/Gradle set up, just run
`Main.java` directly.

You'll land on the registration/login menu, and from there into role-specific
dashboards (Passenger / Admin / Airline Staff).

## Running the REST API (optional)

`ApiMain.java` starts a small HTTP server alongside — not instead of — the
console app. It's a separate entry point:

```
java -cp "out:..." ApiMain 8080
```

Available endpoints (all require header `X-API-Key: demo-key-123`):
```
GET /api/flights/search?source=DEL&destination=BLR&date=2026-09-01
GET /api/bookings/{pnr}
```

These use demo API keys and mocked gateway responses — see **Known
Limitations** below before treating this as production-ready.

## Feature Overview (by Use Case)

| UC | Feature |
|----|---------|
| 1  | Registration, login, role-based access (Passenger/Admin/Airline Staff), permission system |
| 2  | Flight search & filtering, sorting, Streams-based grouping/analytics |
| 3  | Seat map display and selection (window/aisle/middle, premium, exit row) |
| 4  | Booking creation, state machine (`INITIATED` → ... → `CONFIRMED`), PNR/e-ticket |
| 5  | Payment processing - UPI / Card / EMI, promo codes, refunds |
| 6  | Booking modification - flight change, passenger detail edits, seat change |
| 7  | Full and partial cancellation with time-based refund policy |
| 8  | Admin/Staff flight management - create, update schedule/fare/status, occupancy reports |
| 9  | Airport management - CRUD, search by code/city/name/country |
| 10 | Priority booking queue (`PriorityQueue` with starvation-prevention aging) |
| 11 | Singleton managers - `FlightManager`, `BookingManager`, `PaymentManager`, `NotificationManager` |
| 12 | Notifications - abstract `Notification` class, Email/SMS/WhatsApp channels |
| 13 | Reporting & analytics - revenue, trends, occupancy, demographics, LTV |
| 14 | Online check-in - window validation, document checks, boarding pass |
| 15 | Search optimization - in-memory caching, pagination, airport autocomplete |
| 16 | Business rules - lead time, passenger limits, infant/adult rule, real fare breakdown (GST, surcharges) |
| 17 | Custom exception hierarchy, input validation, session expiry |
| 18 | Payment/SMS/email gateway adapter interfaces (mocked), optional REST API |

## Known Limitations

This is a learning/portfolio project, and a few things are intentionally
simplified rather than guessed at:

- **Payment gateways are mocked.** `RazorpayClient`, `PayUClient`,
  `TwilioLikeSmsClient`, etc. simulate success/failure — none make real
  network calls. Swapping in a real provider means implementing
  `IPaymentGatewayClient` / `ISmsGatewayClient` / `IEmailGatewayClient` against
  actual credentials.
- **No real GDS integration** (Amadeus/Sabre) - `FlightRepository` /
  `AirportRepository` are the system's own source of truth.
- **REST API auth is a hardcoded demo key list**, not real OAuth2/JWT.
- **No PDF/Excel export** for boarding passes or booking history - console
  output and in-memory objects only.
- **Baggage weight and meal-upgrade pricing** aren't modeled - `Flight` and
  `BookingPassenger` don't carry those fields yet.
- **Exception-hierarchy retrofit is partial.** The pattern (throw
  `AirlineSystemException` subclasses from repositories/services instead of
  swallowing `SQLException`, validate input via `InputValidator`) is fully
  built and demonstrated in `FlightRepository`/`BookingService`, but hasn't
  been mechanically applied to every remaining repository/service yet.

## Default Login

There's no seeded admin account - register through the console app's sign-up
flow. To create an Admin or Airline Staff account rather than a Passenger,
you'll need to either extend the registration menu to accept a role choice, or
insert the `users` row directly with the appropriate `role` value.
