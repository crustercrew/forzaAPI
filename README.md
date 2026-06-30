[![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-7F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Spring Boot 3](https://img.shields.io/badge/Spring%20Boot%203.x-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Neon%20Cloud-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)](https://neon.tech/)
[![Redis](https://img.shields.io/badge/Redis-Upstash%20TLS-DC382D?style=for-the-badge&logo=redis&logoColor=white)](https://upstash.com/)
[![Security](https://img.shields.io/badge/Spring%20Security-JWT%20Stateless-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white)]()

An enterprise-grade, high-performance RESTful public engine designed to centralize, process, and serve absolute vehicle telemetry data, cross-game performance matrices, naming indices, and live-service dynamic seasonal milestones across the entire **Forza Horizon** and **Forza Motorsport** franchise semiverse.

Built from the ground up to address the community gap left by the decommissioning of historical community trackers, this backend architecture is engineered for low-latency lookups, strict relational data integrity, and extreme traffic scalability.

---

## 🚀 Key Architectural Superpowers

### 1. High-Performance Multi-Criteria Filter Engine
Traditional basic CRUD lookups break down under complex automotive comparisons. This architecture integrates a dynamic querying layer utilizing **Spring Data JPA Specifications** (`JpaSpecificationExecutor`). Clients can issue relational multi-tier requests querying across varying manufacturers, generation ranges, and drivetrain indices simultaneously without facing catastrophic SQL injection vectors.

### 2. Globally Distributed Two-Tier Caching Grid
To prevent database exhaustion against intense client-side traffic spikes (e.g., during live-game season changes), the read-heavy master lookup endpoints are fronted by a **Cloud Redis distributed memory layer (Upstash Cloud TLS)**.
* **Cache Eviction Mapping**: Mutating operations (`CREATE`, `UPDATE`, `DELETE`) are strictly bound to transactional `@CacheEvict(allEntries = true)` rules to immediately invalidate stale collection queries, achieving complete data consistency between the PostgreSQL relational node and memory caches.
* **Granular Single-Key Lookups**: Isolated single-entity endpoints capitalize on direct Redis Key-Value serialization for instantaneous execution times.

### 3. Stateless Security Gate & Decoupled Secret Runtime
Secured utilizing a rigid implementation of **Spring Security** paired with stateless **JSON Web Token (JWT)** verification:
* **Zero-Credential Code Footprint**: The application strictly separates infrastructure logic from secrets. All sensitive keys—including the custom HMAC-SHA256 JWT signature passphrase, Neon DB cloud strings, and Redis TLS access keys—are hydrated at startup via a strictly ignored localized `.env` injection framework.
* **Role-Based Access Control (RBAC)**: Public read routes are completely unthrottled for community open-access, while data modification vectors are armored under internal administrative validation constraints.

### 4. Enterprise Data Normalization & Structural Lifecycle
* **Flyway Schema Version Control**: Automatic database schema baseline generation, sequencing mutations seamlessly upon container bootstrap.
* **Hibernate Session Optimization**: Anti-pattern **OSIV (Open-Session-In-View)** is explicitly set to `false` across production properties to shield the pool boundary from connection starvation. Strict `@Transactional(readOnly = true)` annotations govern reading service frames to maximize internal query speed.

---

## 🗺️ Relational Data Schema Overview

The relational entity structure handles the structural domain logic where a single physical vehicle model exhibits distinct telemetry statistics, class categories, and unlock pricing properties depending on the targeted game release installment.

```text
  ┌─────────────────┐          ┌───────────────────┐          ┌─────────────────┐
  │      GAMES      │          │   MANUFACTURERS   │          │    DIVISIONS    │
  └────────┬────────┘          └─────────┬─────────┘          └────────┬────────┘
           │                             │                             │
           │ 1                           │ 1                           │ 1
           │                             │                             │
           │       ┌───────────────┐     │                             │
           └──────►│FESTIVAL_PLAY- │     │                             │
           │       │ LISTS         │     │                             │
           │       └───────┬───────┘     │                             │
           │               │ 1           │                             │
           │               │             │                             │
           │               │ M           │                             │
           │       ┌───────▼───────┐     │ M                           │
           │       │PLAYLIST_REWRDS│     │                             │
           │       └───────┬───────┘     │                             │
           │               │ M           │                             │
           │               │             │                             │
           │               │ 1           │                             │
           │       ┌───────▼───────┐     │ 1                           │
           │       │ GAME_VEHICLE_ │◄────┼─────────────────────────────┘
           │       │     STATS     │     │
           │       └───────▲───────┘     │
           │               │             │
           │               │ 1           │
           │               │             │
           │       ┌───────┴───────┐     │ 1
           └──────►│   VEHICLES    │◄────┘
                   └───────┬───────┘
                           │ 1
                           │
                           │ M
                   ┌───────▼───────┐
                   │VEHICLE_IMAGES │
                   └───────────────┘
                 
```

## 📖 Live API Contract Specifications
Every public gate within the production engine is natively documented with explicit structural parameters to guarantee an exceptional Developer Experience (DX) for integration engineers.

## 🚗 Vehicle Core Roster
`GET /api/v1/vehicles` — Dynamic catalog browser. Allows composite multi-filtering sorting operations (e.g., ?manufacturerId=12&drivetrain=AWD&startYear=2000&endYear=2012). Highly cached via Redis collection signatures.

`GET /api/v1/vehicles/{id}` — Standalone metadata lookup by unique database primary key.

[exact match] `GET /api/v1/vehicles/search/{name}` — Partial model wildcard autocomplete lookup.

## 📊 Performance Index & Telemetry
`GET /api/v1/vehicle-stats` — Advanced analytical gateway mapping parent models to serial game execution data (?vehicleId=5&gameId=7&performanceClass=S). Returns detailed telemetry indices (speed, handling, acceleration, launch, braking, offroad), cost tiers, and rarity status flags.

`GET /api/v1/vehicle-stats/{id}` — Standalone discrete entry query gate.

## 📅 Live-Service Seasonal Tracks
`GET /api/v1/festival-playlist` — History log catalog query filtered by targeted game series numbers and active seasonal shifts (e.g., Summer, Autumn, series_milestone). Automatically nests complete reward vehicles arrays.

`GET /api/v1/festival-playlist/{id}` — Fetch static data profile metrics for an absolute seasonal milestone event record.

## 🗂️ Lookup Tables (Cached Master Data Component Dropdowns)
`GET /api/v1/manufacturers` — Roster of worldwide car brand entries.

`GET /api/v1/manufacturers/by-country?country=Japan` — Aggregated manufacturer array nested by country profiles.

`GET /api/v1/divisions` — Browse or lookup exact vehicle division brackets.

`GET /api/v1/games` — Fetch game title editions map and launch years index registry.

## ⚡ Quickstart Deployment Pipeline
#### 1. Environment Set-Up
Clone the repository source to your system context:

```Bash
git clone [https://github.com/crustercrew/forzaAPI.git](https://github.com/crustercrew/forzaAPI.git)
cd forzaAPI
```

To run the application context safely without exposing core secrets, establish a customized .env profile variable at the root directory of your cloned codebase:

```Ini, TOML
# Core Database Credentials (e.g., Neon serverless PostgreSQL instance)
DB_HOST=your-neon-database-url.neon.tech
DB_PORT=5432
DB_NAME=neondb
DB_USERNAME=your_db_username
DB_PASSWORD=your_secure_db_password

# Distributed Caching In-Memory Tier (e.g., Upstash Redis Server)
REDIS_HOST=your-redis-instance.upstash.io
REDIS_PORT=6379
REDIS_PASSWORD=your_secure_redis_password

# System Cryptography Encryption Secrets
JWT_SECRET_KEY=your_minimum_256_bit_cryptographic_signing_key_secret_string
```

#### 2. Execution Pipeline
The application handles structural version controls out-of-the-box via Flyway migrations. Run the following command to boot up the entire compiled container context:

``` Bash
./gradlew bootRun
🧪 Connection & Testing Framework
Before deployment execution, run the integration testing suite validating remote SSL handshakes, repository queries, pooling behaviors, and security context validations:
```
## 🧪 Connection & Testing Framework
Before deployment execution, run the integration testing suite validating remote SSL handshakes, repository queries, pooling behaviors, and security context validations:
``` Bash
./gradlew test
```
## 📖 Live Interactive Portal Access
Once the engine registers a successful bootstrap state, explore, interact with, and execute mock transactions on live JSON payloads through your local web browser interface:

Interactive OpenAPI Sandbox Testing UI: http://localhost:8080/swagger-ui/index.html

Built with absolute production precision for the global open-source automotive developer community.