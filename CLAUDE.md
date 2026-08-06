# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**NanoServer** is a Spring MVC web application that serves as a backend and web services platform for IoT "NanO" gadgets. It provides user authentication, device management, energy monitoring, and multi-language support with role-based access control.

## Architecture

### High-Level Stack
- **Framework**: Spring Framework 4.1.6 (MVC + Web + AOP)
- **Build**: Maven 3.x
- **Database**: MySQL with stored procedures
- **View Layer**: JSP with JSTL
- **Frontend**: Vanilla JavaScript with jQuery
- **Logging**: Log4j2
- **Security**: Custom encryption with AES (SecureUtils)

### Layered Architecture

The application follows a classic 3-tier architecture:

1. **Controller Layer** (`src/main/java/nano/server/controllers/`)
   - Spring `@Controller` classes handle HTTP requests
   - Base class: `BaseController` provides common functionality (authentication, menu loading, localization, role-based access)
   - Pattern: Controllers validate user access via `getLoggedUserFromCookie()`, load menu items, localize models, then delegate to services
   - Examples: `LoginController`, `UsersController`, `DashboardController`, `DevicesController`, `EnergyController`

2. **Web Services Layer** (`src/main/java/nano/server/webServices/`)
   - REST API endpoints (e.g., `/api/services/login/login`, `/api/services/users/registration`)
   - Extend `BaseController` for shared security/authentication utilities
   - Use `@ResponseBody` to return JSON DTOs
   - Security validated via `WSSecurityUtils.isHeaderValid()` with API key/secret from `ServerProperties`
   - Endpoints accept encrypted payloads, decrypt with `SecureUtils.decrypt()`
   - Examples: `LoginWebService`, `UsersWebService`, `DevicesWebService`

3. **Service Layer** (`src/main/java/nano/server/db/services/`)
   - Interface definitions in `def/` directory (e.g., `IUserService`, `IDeviceService`)
   - Implementations in `impl/` directory (e.g., `UserService`, `DeviceService`)
   - Marked with `@Service` for Spring dependency injection
   - Business logic and database interaction coordination

4. **DAO Layer** (`src/main/java/nano/server/db/daos/`)
   - Data Access Objects handle direct database queries
   - `BaseDao` provides common query execution logic
   - Extend `BaseDao` and implement specific query methods
   - Use stored procedures via `DBScript` wrapper class

5. **Database Access** (`src/main/java/nano/server/db/access/`)
   - `DBAccess`: Low-level MySQL connection and stored procedure execution
   - `DBScript`: Wraps stored procedure calls with parameter binding
   - `DBParam`: Represents individual parameters (handles quoting, null values, lists)
   - Uses MySQL JDBC driver with Java 8 (1.8 source/target)

6. **Entities** (`src/main/java/nano/server/db/entities/`)
   - Plain Java objects representing database rows
   - Key entities: `User`, `Device`, `Role`, `Section`, `EnergyLog`, `EnergyConsumed`, `SecurityLog`
   - Used throughout service/DAO layers

### Database Design

- **Stored Procedures**: All queries execute via MySQL stored procedures (in `scripts/sps/`)
- **Tables**: Created/managed via SQL scripts in `scripts/tbls/` (users, devices, roles, sections, energy_log, energy_consumed, security_log)
- **Database**: `nano` schema (configured in `conf/app.properties`)
- **Authentication**: Passwords encrypted with AES in database; access via `SecureUtils.getDbKey()`

### Key Utilities

- **`SecureUtils`**: AES encryption/decryption, token generation, password hashing, random key selection based on date/time
- **`LocalizerUtils`**: Multi-language support (Spanish/English by default) via ResourceBundle pattern; dictionary files in `src/main/resources/localization/`
- **`WSSecurityUtils`**: API security validation (header-based key/secret checking)
- **`EmailUtils`**: Email sending (SMTP configuration in properties file)
- **`FileUtils`**: File and image handling
- **`MapperUtils`**: DTO/entity mapping (Jackson-based JSON conversion)

### Security Model

- **User Authentication**: Cookie-based tokens stored in `nano.server.token` cookie
- **Role-Based Access**: Each `Section` (menu item) has a required role level; checked in controllers via `userHasAccess()`
- **API Security**: Web services validate `securityKey` and `securitySecret` headers
- **Encrypted Communication**: Request/response bodies encrypted with AES; decrypted in controllers via `SecureUtils.decrypt()`

### Multi-Language & Sections

- **Localization**: Managed via `LocalizerUtils` and ResourceBundle properties files
- **Sections**: Database-driven menu items with role-based access; each section is a controllable feature area
- **Adding Sections**: Follow `/readmes/NewSectionReadme.txt` (update scripts, localization, controller, JSP)
- **Adding Languages**: Follow `/readmes/NewLanguageReadme.txt` (add to `LocalizerUtils.AVAILABLE_LOCALES`, dictionary file, flag image)

### Front-End

- **JSP Views**: Located in `src/main/webapp/jsp/` organized by controller (dashboard, login, users, devices, etc.)
- **View Resolver**: Spring configured in `NanoServer-servlet.xml` to resolve JSP files from `/jsp/` prefix
- **Static Resources**: CSS, JavaScript, images in `src/main/webapp/resources/`
- **JavaScript Modules**: `secure.js` (encryption), `main.js` (shared utilities), module-specific files (login.js, users.js, devices.js, energy.js, etc.)

## Build & Deployment

### Build Commands

```bash
# Clean and compile
mvn clean compile

# Build WAR package
mvn clean package

# Skip tests (if any exist)
mvn clean package -DskipTests
```

### Configuration

- **Application Properties**: `conf/app.properties` (database, email, security keys, admin host)
- **Spring Config**: `src/main/webapp/WEB-INF/NanoServer-servlet.xml` (component scanning, JSP view resolver, multipart resolver)
- **Web Config**: `src/main/webapp/WEB-INF/web.xml` (DispatcherServlet, error page mappings)
- **Logging Config**: `src/main/resources/log4j2.xml` (rolling files with size-based triggers)

### Deployment

- **Type**: WAR (Web Application Archive)
- **Final Name**: "NanO Server" (from pom.xml) — Maven produces `target/NanO Server.war` (literal
  space in the filename); quote it (`"target/NanO Server.war"`) in any shell command that touches it.
- **Target Container**: Any Servlet 2.5+ compatible container (Tomcat, etc.)
- **Multipart Upload**: Max 10MB (configured in Spring's CommonsMultipartResolver)

#### Live deploy to `nano-server`

Production/live instance is a Tomcat 8.5.90 container (`nano_server`, via `sudo docker`) on the
host reachable over SSH as `nano-server` (alias in `~/.ssh/config`, private VPN address
`10.8.4.6`). The public domain `nano.fuerz4.com` reaches this **same** box through a Cloudflare
Tunnel (`cloudflared` runs there as a service) — there is no separate staging/production split;
deploying to `nano-server` **is** deploying to what the public domain (and therefore every mobile
app pointed at it, including Fuerz4 Assistant) actually serves. There's no CI/CD — deploys are a
manual, three-step process:

1. **Build** locally: `mvn -o clean package -DskipTests` → produces `target/NanO Server.war`.
2. **Upload**, renamed to match the deploy script's expectation:
   `scp "target/NanO Server.war" nano-server:~/NanoServer.war`
3. **Run the existing update script** on the remote host (passwordless `sudo` is configured there
   for the `updateNanoServer.sh` steps):
   `ssh nano-server "cd ~ && bash updateNanoServer.sh"`

   `~/updateNanoServer.sh` (root-owned, not executable — invoke with `bash`, not `./`) does, in
   order: `sudo docker stop nano_server` → backs up the currently-deployed WAR to
   `~/backups/nano_server/NanoServer_<date>.war` → removes the exploded
   `/etc/apps/webapps/nano_server/ROOT` directory → moves the freshly-uploaded
   `~/NanoServer.war` into `/etc/apps/webapps/nano_server/ROOT.war` → `sudo docker start
   nano_server` (Tomcat re-explodes and redeploys the WAR on startup). This backup step is the
   rollback path if a deploy goes bad: restore the most recent file under
   `~/backups/nano_server/` to `/etc/apps/webapps/nano_server/ROOT.war` and re-run the stop/start.
4. **Verify** the container came back up and the new code is actually being served — don't just
   trust the script's "Done!" output:
   - `ssh nano-server "sudo docker ps --filter name=nano_server"` (should show `Up` with a fresh
     "CREATED"/uptime) and `sudo docker logs --tail 40 nano_server` (look for Spring's
     `FrameworkServlet 'NanoServer': initialization completed`, not a stack trace).
   - Hit a known endpoint through the **public** URL (not just `localhost` on the box) to confirm
     the Cloudflare Tunnel is routing to the freshly-started container, e.g.
     `curl -X POST https://nano.fuerz4.com/api/services/devices/latestValue -d '{}'` — a `401`
     (WSSE-unauthorized) confirms the route is live and mapped; a `404` means the deploy didn't
     actually pick up the new controller, a `500`/connection error means the container didn't
     come back up cleanly.

There is no automated test suite gating this — `mvn -o compile`/`package` succeeding only proves
the code compiles, not that it behaves correctly against live data. Treat every deploy as
needing a manual smoke test against a real device/app afterward.

## Database Scripts

- **Tables** (`scripts/tbls/`): SQL CREATE TABLE statements; prefix with `drop table if exists` for idempotent execution
- **Stored Procedures** (`scripts/sps/`): Callable procedures executed by `DBAccess`
- **Events** (`scripts/evn/`): MySQL scheduled events (e.g., `evCleanUpSecurityLogs.sql`)

Example flow: Java code creates a `DBScript` with stored procedure name and params → `DBAccess` executes `CALL spProcName(...)` → ResultSet returned for mapping into entities.

## Key Dependencies

- **Spring Framework** (4.1.6): Core DI, MVC, Web, AOP
- **Servlet API** (3.1.0): Web container contracts
- **MySQL Connector** (5.1.49): JDBC driver
- **Jackson** (2.11.0): JSON serialization/deserialization
- **Log4j2** (2.13.3): Structured logging
- **Apache Commons**: codec, fileupload, httpclient (utility libraries)
- **Apache Tiles** (3.0.5): JSP template composition (optional, present but minimal use observed)
- **JSTL/JSP Standard Library** (1.2): JSP tag libraries

## Common Development Tasks

### Adding a New Feature Section

1. Create table migration in `scripts/tbls/new_section.sql`
2. Create stored procedures for CRUD in `scripts/sps/spAll{Section}*.sql`, `spCount{Section}.sql`, etc.
3. Create entity class in `src/main/java/nano/server/db/entities/{Section}.java`
4. Create DAO in `src/main/java/nano/server/db/daos/{Section}Dao.java` extending `BaseDao`
5. Create service interface `I{Section}Service` and implementation `{Section}Service` with Spring `@Service` annotation
6. Create controller `{Section}Controller extends BaseController` with JSP view in `src/main/webapp/jsp/{section}/`
7. Add menu item localization keys in `src/main/resources/localization/dictionary_*.properties`
8. Update `Section` table with new section record

### Adding an API Endpoint

1. Create or extend a WebService class (e.g., `DevicesWebService`) with `@RequestMapping` method
2. Accept encrypted `String encryptedData` parameter
3. Decrypt with `SecureUtils.decrypt(encryptedData, DtoClass.class)`
4. Validate security headers with `WSSecurityUtils.isHeaderValid(...)`
5. Return `ResultDto` or `ResponseDto` for JSON serialization
6. Corresponding DTO in `src/main/java/nano/server/dtos/`

### Handling Authentication in Controllers

- Call `getLoggedUserFromCookie(request)` to extract authenticated user
- Check access with `userHasAccess(user)` against current section
- Load menu with `loadMenu(model, request, user)`
- On logout, set `user.setToken(null)` and save via `userService.setUser()`

### Localization

- Keys are stored in `localization/dictionary_[locale].properties` files
- Retrieve at runtime: `LocalizerUtils.getLocalizedText(request, "key_name")`
- Add new language: append locale code to `LocalizerUtils.AVAILABLE_LOCALES`, create `dictionary_[locale].properties`

## File Structure Summary

```
NanoServer/
├── conf/                              # Configuration files
│   └── app.properties                 # Database, email, security settings
├── src/main/
│   ├── java/nano/server/
│   │   ├── controllers/               # Spring @Controller classes
│   │   ├── webServices/               # REST API endpoints
│   │   ├── db/
│   │   │   ├── entities/              # JPA-like POJOs
│   │   │   ├── daos/                  # Data Access Objects
│   │   │   ├── services/
│   │   │   │   ├── def/               # Service interfaces
│   │   │   │   └── impl/              # Service implementations
│   │   │   └── access/                # DBAccess, DBScript, DBParam
│   │   ├── dtos/                      # Data Transfer Objects
│   │   ├── enums/                     # ServerProperties, ResponseType, EntityType, etc.
│   │   └── utils/                     # Utility classes (SecureUtils, LocalizerUtils, etc.)
│   ├── resources/
│   │   ├── localization/              # dictionary_*.properties (i18n)
│   │   ├── templates/                 # Email HTML templates
│   │   └── log4j2.xml                 # Logging configuration
│   └── webapp/
│       ├── WEB-INF/
│       │   ├── NanoServer-servlet.xml # Spring MVC config
│       │   └── web.xml                # Servlet config
│       ├── jsp/                       # JSP templates (by controller)
│       └── resources/
│           ├── css/                   # Stylesheets
│           ├── js/                    # Client-side JavaScript
│           ├── img/                   # Images & flags
│           └── icons/                 # Icon assets
├── scripts/
│   ├── tbls/                          # Table creation scripts
│   ├── sps/                           # Stored procedure definitions
│   └── evn/                           # MySQL event scripts
├── readmes/
│   ├── NewSectionReadme.txt           # Steps to add new section
│   └── NewLanguageReadme.txt          # Steps to add new language
├── pom.xml                            # Maven configuration
└── .project                           # Eclipse project metadata
```

## Code Style

- Do not add comments to code.
- When adding new code, follow the existing layered architecture (Controllers → WebServices → Services → DAOs → DBAccess → Entities).
- Code must compile free of both warnings and errors.
- Localization `.properties` files must be saved with UTF-8 encoding. `LocalizerUtils` uses a custom `ResourceBundle.Control` to read them as UTF-8 — do not remove it.

## Development Notes

- **Spring Version**: 4.1.6.RELEASE
- **Java Version**: Java 8 (1.8)
- **Package Naming**: `nano.server.*` consistently used
- **Error Handling**: Custom `DBException` for database errors; caught and logged in controllers
- **Encryption Key**: Dynamic key selection in `SecureUtils.pickKeyIndex()` based on current date/hour/minute
- **Session Management**: Token stored in cookie and database; tokens validated on every request
