# Forza Telemetry & Vehicle Database API 🏎️💨

A robust, enterprise-ready RESTful Open API built with **Spring Boot 3** and **Kotlin** designed to centralize and serve comprehensive vehicle data, game-specific performance metrics, performance index (PI) classifications, and live-service Festival Playlist milestones across the *Forza Horizon* and *Forza Motorsport* ecosystems.

---

## 🛠️ Tech Stack & Architecture

* **Backend Engine:** Spring Boot 3.x (WebMVC) with **Kotlin**
* **Database Layer:** **PostgreSQL** (Hosted serverless on cloud **NeonDB**) via Spring Data JPA & Hibernate
* **Database Version Control:** **Flyway Migration**
* **Environment/Credential Management:** `me.paulschwarz:spring-dotenv`
* **Health & Metrics Monitoring:** Spring Boot Actuator
* **Testing:** JUnit 5 (Integration and Spring Boot Context testing)
* **Build System:** Gradle (Kotlin DSL - `build.gradle.kts`)

---

## 📐 Database Schema Design & Normalization

To handle complex architectural requirements where vehicle specifications remain constant across history but performance metrics (PI Class, Speed, Handling, Offroad stats), monetization, and event distributions change radically between titles (e.g., *Forza Horizon 6* vs. *Forza Motorsport*), the system relies on a fully normalized 3-tier layout.

### Architectural Blueprint:

1. **Core Lookup Tables:** `games`, `manufacturers`, `divisions`, `pi_car_class`
2. **Core Vehicle Profile:** `vehicles` (Stores static real-world specifications like historical factory weight, layout, engine configuration, and dimensions)
3. **Game-Specific Vehicle Data:** `game_vehicle_stats` (Maps static car data to varying per-game PI ratings and upgrade requirements)
4. **Live Service & Playlists:** `festival_playlists` & `playlist_rewards` (Binds dynamic seasonal campaigns seamlessly to specific game vehicle profiles)

### Key Engineering Choices:

* **The `divisions` Hybrid Mapping:** Solves the naming discrepancy between Playground Games (*Horizon's* "Car Types" e.g., *Extreme Track Toys*) and Turn 10 (*Motorsport's* "Divisions" e.g., *Modern Factory Racecars*) cleanly via a unified lookup relation.
* **Granular Normalization (`pi_car_class`):** Extracted car classes (D, C, B, A, S1, S2, X, P, R) into a standalone relation to enforce domain validation and safeguard indexing.
* **Junction mapping via `game_vehicle_stats_id`:** Ensures dynamic live-service playlist rewards bind precisely to a specific game edition of a vehicle rather than leaking globally across unrelated titles.

---

## 🔒 Security & Local Configuration

This project enforces secure credential separation guidelines to eliminate secret leakage. Production environment properties are parsed natively out of local environment contexts using a `.env` infrastructure file hidden completely from source controls.

### Local Development Setup

1. Clone the repository:
```bash
git clone https://github.com/yourusername/forza-api.git
cd forza-api

```


2. Create a `.env` file at the root of your project directory:
```env
DB_HOST=your-neon-database-url.neon.tech
DB_PORT=5432
DB_NAME=neondb
DB_USERNAME=your_db_user
DB_PASSWORD=your_secure_db_password

```


3. Ensure your local configuration files are safely ignored by git:
```text
# .gitignore
.env

```



---

## 🚦 Automated Migrations & Testing

The application manages structural version control automatically using **Flyway**. Upon booting up the Spring container context, schemas are compiled, checked, and updated safely inside the remote PostgreSQL node.

### Running Connection Integrity Tests

Before running domain service operations, the stack includes full **JUnit 5 Integration Tests** verifying environment bindings, remote SSL validation rules, and connection pooling states.

To execute the verification suit directly from the terminal layer:

```bash
./gradlew test

```

### Successful Test Verification Trace:

```text
[Test worker] com.zaxxer.hikari.HikariDataSource   : HikariPool-1 - Start completed.
[Test worker] org.flywaydb.core.FlywayExecutor     : Database: jdbc:postgresql://ep-proud-mode-... (PostgreSQL 18.4)
[Test worker] org.flywaydb.core.command.DbMigrate  : Successfully applied 1 migration (V1__init_schema)

--- Connection Verified ---
Connected to catalog: neondb
Database Driver: PostgreSQL JDBC Driver
Sanity Check SQL Execution Result: 1 (Success)

BUILD SUCCESSFUL in 24s

```
