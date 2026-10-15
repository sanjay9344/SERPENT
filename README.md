# SnakeGuard — Stage 1: Snake Information, Safety Guidance, and User Authentication

SnakeGuard is a full-stack educational and wildlife safety web platform designed to reduce snakebite fatalities, clear up common medical misconceptions, and promote peaceful coexistence with serpents.

Stage 1 focuses on:
- A responsive, medical-and-wildlife-safety branded **Home Page**.
- Representative **Indian Snake Species Identification** cards with interactive search and filtering.
- **Snakebite First Aid & Emergency Protocol** (WHO-aligned evidence-based guidelines).
- **User / Patient Registration** with robust server and client validation.
- **Secure Authentication & Session Management** using Spring Security and MySQL.
- **MySQL Database Connectivity** via Spring JDBC (`JdbcTemplate`).

---

## 1. Technology Stack

- **Frontend:** Vanilla HTML5, CSS3, Modern JavaScript (Fetch API).
- **Backend:** Java Spring Boot 3.3.x (with Java 17+ LTS compatibility).
- **Database Connectivity:** Spring JDBC (`JdbcTemplate` with parameterized queries).
- **Database:** MySQL 8.x.
- **Security & Authentication:** Spring Security (BCrypt password hashing, HTTP sessions, CSRF protection).
- **Build Tool:** Apache Maven.

> **Zero Third-Party Frontend Frameworks:** No React, Angular, Vue, Tailwind, or Bootstrap. Built entirely with Vanilla Web Standards.  
> **No ORM Complexity:** No Hibernate, JPA, or Spring Data JPA. Built directly with beginner-friendly, high-performance Spring JDBC.

---

## 2. Project Directory Structure

```text
pbl/
├── pom.xml                                  # Maven dependencies and build configuration
├── schema.sql                               # MySQL database and table initialization script
├── run.bat                                  # Windows batch launcher script
├── run.ps1                                  # Windows PowerShell launcher script
├── README.md                                # Comprehensive documentation and student guide
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── snakeguard/
│   │   │           ├── SnakeGuardApplication.java        # Spring Boot entry point
│   │   │           ├── config/
│   │   │           │   └── SecurityConfig.java           # Spring Security filter chain & CSRF configuration
│   │   │           ├── controller/
│   │   │           │   └── AuthController.java           # REST controller for registration, login, logout, /me
│   │   │           ├── dto/
│   │   │           │   ├── ApiResponse.java              # Standardized JSON response envelope
│   │   │           │   ├── LoginRequest.java             # Login request credentials DTO
│   │   │           │   ├── RegisterRequest.java          # Registration form DTO with validation rules
│   │   │           │   └── UserResponse.java             # Safe user data DTO (never exposes password hash)
│   │   │           ├── exception/
│   │   │           │   ├── DuplicateEmailException.java   # Custom conflict exception for duplicate accounts
│   │   │           │   ├── ResourceNotFoundException.java # Custom 404 exception
│   │   │           │   └── GlobalExceptionHandler.java    # Centralized REST exception handler (@RestControllerAdvice)
│   │   │           ├── model/
│   │   │           │   └── User.java                     # User entity mapped to MySQL `users` table
│   │   │           ├── repository/
│   │   │           │   ├── UserRepository.java           # Repository interface for persistence contracts
│   │   │           │   └── JdbcUserRepository.java       # Spring JDBC implementation using JdbcTemplate
│   │   │           └── service/
│   │   │               ├── UserService.java              # Business service interface
│   │   │               ├── UserServiceImpl.java          # Registration, password hashing, and user logic
│   │   │               └── CustomUserDetailsService.java # Spring Security UserDetailsService bridge
│   │   └── resources/
│   │       ├── application.properties               # Database credentials & server settings
│   │       ├── schema.sql                           # Database DDL script
│   │       └── static/                              # Frontend static assets served by Spring Boot
│   │           ├── index.html                       # Home page (Hero, Snakes, First Aid, Emergency modal)
│   │           ├── login.html                       # User login page
│   │           ├── signup.html                      # User / Patient sign-up page
│   │           ├── css/
│   │           │   ├── styles.css                   # Core design tokens, layout, typography, components
│   │           │   ├── auth.css                     # Auth cards, forms, inputs, and feedback alerts
│   │           │   └── responsive.css               # Media queries for tablet and mobile devices
│   │           ├── js/
│   │           │   ├── api.js                       # Vanilla JS Fetch wrapper with CSRF & session handling
│   │           │   ├── validation.js                # Reusable client-side form validation utilities
│   │           │   ├── main.js                      # Home page snake dataset, search, filters & session UI
│   │           │   └── auth.js                      # Login & signup form submission and validation handling
│   │           └── assets/
│   │               └── images/
│   │                   ├── logo.svg                 # SnakeGuard shield emblem
│   │                   ├── hero-snakes.svg          # Hero section graphic illustration
│   │                   ├── indian-cobra.svg         # Indian Cobra illustration with spectacle hood
│   │                   ├── common-krait.svg         # Common Krait illustration with paired white bands
│   │                   ├── russells-viper.svg       # Russell's Viper illustration with spot chains
│   │                   ├── saw-scaled-viper.svg     # Saw-Scaled Viper illustration with crown cross
│   │                   ├── indian-rat-snake.svg     # Non-venomous Indian Rat Snake illustration
│   │                   └── checkered-keelback.svg   # Non-venomous Checkered Keelback illustration
│   └── test/
│       ├── java/
│       │   └── com/
│       │       └── snakeguard/
│       │           ├── controller/
│       │           │   └── AuthControllerTest.java       # MockMvc tests for register, login, logout, me
│       │           ├── repository/
│       │           │   └── JdbcUserRepositoryTest.java   # Unit tests for JdbcTemplate parameterized queries
│       │           └── service/
│       │               └── UserServiceTest.java          # Unit tests for registration, hashing, duplicates
│       └── resources/
│           └── mockito-extensions/
│               └── org.mockito.plugins.MockMaker
```

---

## 3. Purpose of Each Main Java Class

1. **`SnakeGuardApplication.java`**: The Spring Boot entry point. Bootstraps the application, component scanning, embedded Tomcat server, and static resource handlers.
2. **`model/User.java`**: The core domain entity representing an account in the database. Contains `id`, `fullName`, `email`, `phone`, `country`, `passwordHash`, `role`, `accountStatus`, and timestamps. Its `toString()` method purposefully omits `passwordHash` to prevent accidental logging of sensitive credentials.
3. **`dto/RegisterRequest.java`**: Carries user sign-up input. Annotated with Jakarta Validation constraints (`@NotBlank`, `@Email`, `@Size`, `@Pattern`) for backend input sanitization.
4. **`dto/LoginRequest.java`**: Carries login credentials (`email` and `password`).
5. **`dto/UserResponse.java`**: Clean, sanitized representation of the user returned in API responses. Guarantees that passwords and password hashes are never exposed to the frontend.
6. **`dto/ApiResponse.java`**: Generic response envelope `{ "success": true, "message": "...", "data": ..., "error": null, "timestamp": ... }` ensuring consistent API responses.
7. **`repository/UserRepository.java`**: Defines data persistence contracts (`findById`, `findByEmail`, `existsByEmail`, `save`).
8. **`repository/JdbcUserRepository.java`**: Implements `UserRepository` using Spring's `JdbcTemplate`. Executes parameterized SQL queries (`?` placeholders) and maps database rows to `User` objects using a `RowMapper`. Uses `GeneratedKeyHolder` to retrieve generated auto-increment keys.
9. **`service/UserServiceImpl.java`**: Enforces business rules: verifies matching passwords, rejects duplicate email registrations, hashes passwords using BCrypt, and maps entities to safe DTOs.
10. **`service/CustomUserDetailsService.java`**: Implements Spring Security's `UserDetailsService`. Loads user credentials from MySQL and converts roles into Spring Security authorities (`ROLE_USER`).
11. **`config/SecurityConfig.java`**: Configures the HTTP security filter chain: enables CSRF protection using `CookieCsrfTokenRepository(withHttpOnlyFalse)`, permits public static resources and auth endpoints, secures authenticated endpoints, sets up session invalidation on logout, and creates the `BCryptPasswordEncoder` bean.
12. **`controller/AuthController.java`**: Exposes REST endpoints:
    - `POST /api/auth/register`: Signs up new users.
    - `POST /api/auth/login`: Authenticates credentials and sets server-managed HTTP session.
    - `GET /api/auth/me`: Retrieves currently authenticated user details.
    - `POST /api/auth/logout`: Invalidates session and clears cookies.
    - `GET /api/auth/csrf`: Provides CSRF token information.
13. **`exception/GlobalExceptionHandler.java`**: Annotated with `@RestControllerAdvice`. Catches validation errors (`MethodArgumentNotValidException`), duplicate email conflicts (`DuplicateEmailException`), and bad credentials (`BadCredentialsException`), returning consistent JSON error messages.

---

## 4. Prerequisites & Installation

### Required Software
1. **Java JDK:** Java 17 or later (Java 17, 21, or 26).
   - Check with: `java -version`
2. **Apache Maven:** Maven 3.8+ (A local installation is provided at `C:\Users\sanjay\.maven\apache-maven-3.9.9\bin\mvn.cmd`).
   - Check with: `mvn -v`
3. **MySQL Server:** MySQL 8.x (via MySQL Community Server, XAMPP, or MariaDB).

---

## 5. Database Setup (MySQL)

### Step 1: Start MySQL
Ensure your local MySQL service is running (e.g., via the MySQL Windows Service, XAMPP Control Panel, or command prompt).

### Step 2: Create the Database & Tables
You can run the provided `schema.sql` script directly in your MySQL client (MySQL Workbench, phpMyAdmin, or MySQL CLI):

```bash
mysql -u root -p < schema.sql
```

Or execute the SQL commands manually:

```sql
CREATE DATABASE IF NOT EXISTS snakeguard_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE snakeguard_db;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    country VARCHAR(50) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'ROLE_USER',
    account_status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

---

## 6. Database Configuration

Database credentials are fully configurable in `src/main/resources/application.properties` or via environment variables without editing Java code:

```properties
# Default local configuration
spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:mysql://localhost:3306/snakeguard_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&createDatabaseIfNotExist=true}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME:root}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

If your MySQL root user has a password (e.g. `secret123`), you can either:
1. Set the environment variables in PowerShell before launching:
   ```powershell
   $env:SPRING_DATASOURCE_USERNAME = "root"
   $env:SPRING_DATASOURCE_PASSWORD = "your_mysql_password"
   ```
2. Or edit `src/main/resources/application.properties` directly:
   ```properties
   spring.datasource.password=your_mysql_password
   ```

---

## 7. How to Run the Application

You can launch SnakeGuard using any of the following methods:

### Method A: Using the provided helper scripts
- **Command Prompt / Windows Explorer:** Double-click `run.bat`
- **PowerShell:**
  ```powershell
  .\run.ps1
  ```

### Method B: Using Maven directly
```powershell
& "C:\Users\sanjay\.maven\apache-maven-3.9.9\bin\mvn.cmd" spring-boot:run
```
*(or simply `mvn spring-boot:run` if Maven is added to your system `PATH`)*

### Method C: Running the packaged JAR
```powershell
java -jar target/snakeguard-1.0.0.jar
```

---

## 8. Opening and Testing the Web Application

Once started, open your web browser and navigate to:

👉 **`http://localhost:8080/`** (or `http://localhost:8080/index.html`)

### Testing Features on the Home Page:
1. **Interactive Navigation:**
   - Click **Snake Information** to smooth-scroll directly to the species grid.
   - Click **First-Aid Guidance** to jump to the emergency protocols.
   - Click **EMERGENCY — SEEK MEDICAL HELP** in the header or hero section to view the emergency action modal and emergency helpline numbers.
2. **Search and Filtering:**
   - Type `cobra` or `viper` into the search box to test instant client-side filtering.
   - Filter by **Venomous Species Only** or **Non-venomous Species Only**.
   - Click **Reset Filters** to restore the full dataset.
   - Search for a non-existent name like `python` to verify the helpful empty state.

### Testing User Registration:
1. Click **Sign Up** (or navigate to `http://localhost:8080/signup.html`).
2. Fill in the required fields:
   - **Full Name:** Priya Sharma
   - **Email:** `priya@example.com`
   - **Phone:** `+91 9876543210`
   - **Country:** India
   - **Password:** `SnakeSafety2026!`
   - **Confirm Password:** `SnakeSafety2026!`
3. Click **Show** on the password input to verify the visibility toggle.
4. Click **Complete Registration**:
   - The button disables and displays a loading spinner.
   - On completion, a green success banner appears and automatically redirects you to `login.html`.
5. Try submitting the exact same email again to verify that duplicate registration is rejected with an informative error message.

### Testing User Login and Session:
1. Navigate to `http://localhost:8080/login.html`.
2. Enter the registered credentials:
   - Email: `priya@example.com`
   - Password: `SnakeSafety2026!`
3. Click **Sign In**:
   - The backend authenticates credentials via Spring Security and establishes a server-managed session.
   - You are redirected back to the home page (`index.html`).
4. On the home page:
   - Notice the header now displays a personalized greeting with your avatar initial: **`Hello, Priya`**.
   - Notice the **Sign Out** button appears in place of Login/Sign Up.
5. Click **Sign Out**:
   - The session is invalidated via `POST /api/auth/logout`.
   - The header immediately returns to the guest state showing Login and Sign Up buttons.

---

## 9. Educational Concepts for Java Students

### A. How the Frontend Communicates with Spring Boot
- The HTML, CSS, and JS files live in `src/main/resources/static/`. Spring Boot automatically serves these files from the root URL (`http://localhost:8080/`).
- Frontend JavaScript uses the standard browser `fetch()` API (`js/api.js`).
- Because both frontend and backend are hosted on the same origin (port 8080), browser session cookies (`SNAKEGUARD_SESSION`) and CSRF cookies (`XSRF-TOKEN`) are automatically stored and transmitted securely.

### B. How Spring JDBC Accesses MySQL with Parameterized Queries
Instead of using complex ORM frameworks, SnakeGuard uses Spring's `JdbcTemplate`.

To prevent **SQL Injection** attacks, user data is never concatenated into SQL strings. Notice the question mark (`?`) placeholders:

```java
// SAFE: Parameterized SQL placeholder prevents SQL injection
String sql = "SELECT id, full_name, email, phone, country, password_hash, role, account_status, created_at, updated_at " +
             "FROM users WHERE LOWER(email) = LOWER(?)";

User user = jdbcTemplate.queryForObject(sql, userRowMapper, email.trim());
```

The database driver treats the parameter strictly as data, neutralizing any malicious SQL commands.

### C. How Spring Security Hashes Passwords and Manages Sessions
1. **Password Hashing:** Passwords are never stored in plaintext. In `UserServiceImpl.java`, passwords are encrypted using `BCryptPasswordEncoder`:
   ```java
   String hashedPassword = passwordEncoder.encode(request.getPassword());
   ```
   BCrypt automatically generates a cryptographic salt and performs one-way hashing with multiple computation rounds (10 rounds by default).
2. **Session Cookies:** Upon successful login, Spring Security creates an HTTP session and sets an `HttpOnly` cookie (`SNAKEGUARD_SESSION`). The browser cannot read this cookie via JavaScript `document.cookie`, protecting it against Cross-Site Scripting (XSS) session theft.
3. **CSRF Protection:** Spring Security generates a unique Cross-Site Request Forgery token in `CookieCsrfTokenRepository(withHttpOnlyFalse)`. Client JavaScript reads the `XSRF-TOKEN` cookie and attaches it as the `X-XSRF-TOKEN` header on POST requests, blocking unauthorized third-party requests.

---

## 10. Automated Tests Included

All automated tests can be executed at any time using:

```powershell
& "C:\Users\sanjay\.maven\apache-maven-3.9.9\bin\mvn.cmd" test
```

| Test Class | Tests Run | What It Verifies |
| :--- | :---: | :--- |
| **`AuthControllerTest`** | 7 | HTTP status codes (201 Created, 200 OK, 400 Bad Request, 401 Unauthorized, 409 Conflict), validation error mapping, login response payload, and logout response. |
| **`JdbcUserRepositoryTest`** | 4 | Parameterized query execution, email lookup, email existence check, and update operations via `JdbcOperations`. |
| **`UserServiceTest`** | 5 | BCrypt password hashing, preventing plaintext storage, mismatched password rejection, duplicate email prevention, and authenticated session retrieval. |
| **Total** | **16 / 16 Passed** | **100% Passing Suite** |

---

## 11. Stage 1 Scope & Known Limitations

Per the project specifications for **Stage 1**:
- **Scope Included:** Home page, snake sample dataset, first-aid safety guidance, user registration, user login, MySQL persistence, session management, and logout.
- **Stage 1 Limitations (Planned for Future Stages):**
  - No separate dashboards or portals for hospitals, snake catchers, or administrators.
  - No GPS tracking, Google Maps, or automated hospital routing.
  - No automated ambulance or emergency service dispatch.
  - Password reset via email is not configured in Stage 1.
  - Snake species dataset is an initial representative sample of 6 species for educational awareness.
