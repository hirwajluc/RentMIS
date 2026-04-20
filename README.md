# RentMIS — Rental Management Information System

A production-grade, full-stack web application for managing rental properties, contracts, payments, and tenants. Built with Spring Boot and a modern HTML/JS frontend, RentMIS supports multiple user roles with tailored dashboards and integrates with local payment and identity verification systems.

---

## Features

### User Roles
| Role | Capabilities |
|------|-------------|
| **Admin** | Full system oversight, user management, tenant report review, analytics |
| **Landlord** | Property & unit management, contract creation, payment tracking, agent assignment |
| **Agent** | Manage assigned properties on behalf of landlords |
| **Tenant** | View active contracts, units, invoices, and payment history |

### Core Modules
- **Property Management** — Register properties with per-floor area budgets; track used/available area per floor in real time
- **Unit Management** — Create units with area enforcement (warn at 80%, block when floor area is exceeded)
- **Contract Management** — Create lease contracts with tenant flagging (verified reports or overdue payments shown with red flag, non-selectable)
- **Payment Processing** — Manual payments, GLSPay online checkout (single & multi-period), webhook handling, receipt upload
- **Invoice Generation** — PDF invoice generation via iText 7
- **Tenant Reporting** — Landlords report problematic tenants; admins review and verify
- **NIDA Integration** — Rwanda national ID verification to auto-fill registration fields
- **EBM Integration** — Rwanda Revenue Authority (Inkomane) fiscal device support
- **Blockchain Contract Integrity** — Cryptographic signing of contract records
- **Audit Logging** — Full audit trail on critical entity changes
- **Rate Limiting** — Bucket4j-based per-IP rate limiting on all endpoints
- **Multi-language** — Language preference per user

---

## Technology Stack

### Backend
| Technology | Version | Purpose |
|-----------|---------|---------|
| Java | 21 | Runtime |
| Spring Boot | 3.2.4 | Application framework |
| Spring Security | 6.x | Authentication & authorization |
| Spring Data JPA / Hibernate | 6.x | ORM & database access |
| MySQL | 8.x | Relational database |
| JWT (jjwt) | 0.12.x | Stateless auth tokens |
| MapStruct | 1.5.x | DTO mapping |
| Lombok | 1.18.x | Boilerplate reduction |
| iText 7 | 7.x | PDF invoice generation |
| Bucket4j | 8.x | Rate limiting |
| Flyway | 9.x | Database migrations |
| Apache HttpClient 5 | 5.x | External API calls |
| Springdoc OpenAPI | 2.x | Swagger UI (`/swagger-ui.html`) |

### Frontend
| Technology | Purpose |
|-----------|---------|
| HTML5 / CSS3 / Vanilla JS | UI (served as static resources by Spring Boot) |
| Bootstrap 5 | Responsive layout & components |
| TomSelect | Enhanced select dropdowns |
| Chart.js | Dashboard analytics charts |
| DataTables | Sortable/paginated tables |

### Integrations
| Integration | Purpose |
|------------|---------|
| **GLSPay** | Online payment checkout & webhook |
| **NIDA API** | Rwanda national ID lookup |
| **EBM / Inkomane** | Fiscal receipting (RRA) |

---

## Prerequisites

Before deploying, ensure the server has:

- **Java 21** (`java -version`)
- **Maven 3.9+** (`mvn -version`)
- **MySQL 8.x** (`mysql --version`)
- **Git** (`git --version`)
- Open port **5050** (or whichever port you configure)

---

## Deployment on a Linux Server (via SSH)

### 1. Connect to the server

```bash
ssh your-user@your-server-ip
```

### 2. Install Java 21

```bash
sudo apt update
sudo apt install -y openjdk-21-jdk
java -version
```

### 3. Install Maven

```bash
sudo apt install -y maven
mvn -version
```

### 4. Install MySQL 8

```bash
sudo apt install -y mysql-server
sudo systemctl start mysql
sudo systemctl enable mysql
```

### 5. Create the database and user

```bash
sudo mysql -u root
```

```sql
CREATE DATABASE rentmis CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'rentmis_user'@'localhost' IDENTIFIED BY 'YourStrongPassword!';
GRANT ALL PRIVILEGES ON rentmis.* TO 'rentmis_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

### 6. Clone the repository

```bash
mkdir -p /opt/apps
cd /opt/apps
git clone https://github.com/hirwajluc/RentMIS.git
cd RentMIS
```

### 7. Create the environment file

```bash
nano .env
```

Paste and fill in your values:

```env
# Application
APP_BASE_URL=http://YOUR_SERVER_IP:5050
APP_PORT=5050

# Database
DB_HOST=localhost
DB_PORT=3306
DB_NAME=rentmis
DB_USERNAME=rentmis_user
DB_PASSWORD=YourStrongPassword!

# JWT
JWT_SECRET=replace-with-a-long-random-256-bit-secret
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000

# GLSPay
GLSPAY_BASE_URL=http://your-glspay-host
GLSPAY_API_KEY=your-glspay-api-key

# NIDA
NID_API_URL=http://105.179.0.124:2050/turikumwe_goodlink/nid

# EBM (optional)
EBM_BASE_URL=https://backend-qa.inkomane.rw
EBM_CLIENT_ID=your-client-id
EBM_CLIENT_SECRET=your-client-secret
EBM_COMPANY_ID=your-company-id
EBM_BRANCH_ID=your-branch-id

# CORS
ALLOWED_ORIGINS=http://YOUR_SERVER_IP:5050

# File uploads
UPLOAD_DIR=/opt/apps/RentMIS/uploads/receipts

# Logging
LOG_LEVEL=INFO
```

> **Important:** The `.env` file is excluded from version control (`.gitignore`). Never commit it.

### 8. Build the application

```bash
mvn clean package -DskipTests
```

This produces `target/RentMIS-1.0.0.jar`.

### 9. Start the application

```bash
chmod +x start.sh
./start.sh
```

The script will:
- Load all variables from `.env`
- Start MySQL if not running
- Kill any previous instance
- Launch the JAR in the background and write the PID to `rentmis.pid`

### 10. Verify it is running

```bash
tail -f logs/rentmis.log
```

Then open in a browser:

```
http://YOUR_SERVER_IP:5050/login
```

---

## Managing the Application

### Stop

```bash
kill $(cat rentmis.pid)
```

### Restart

```bash
./start.sh
```

### View logs

```bash
tail -f logs/rentmis.log
```

### Update to latest version

```bash
git pull
mvn clean package -DskipTests
./start.sh
```

---

## Running as a systemd Service (recommended for production)

Create a service file:

```bash
sudo nano /etc/systemd/system/rentmis.service
```

```ini
[Unit]
Description=RentMIS - Rental Management Information System
After=network.target mysql.service

[Service]
Type=forking
User=root
WorkingDirectory=/opt/apps/RentMIS
ExecStart=/opt/apps/RentMIS/start.sh
PIDFile=/opt/apps/RentMIS/rentmis.pid
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```

Enable and start:

```bash
sudo systemctl daemon-reload
sudo systemctl enable rentmis
sudo systemctl start rentmis
sudo systemctl status rentmis
```

---

## API Documentation

Swagger UI is available at:

```
http://YOUR_SERVER_IP:5050/swagger-ui.html
```

Health check endpoint:

```
http://YOUR_SERVER_IP:5050/actuator/health
```

---

## Default Ports

| Service | Port |
|---------|------|
| RentMIS web app | `5050` |
| MySQL | `3306` |

---

## Project Structure

```
RentMIS/
├── src/
│   ├── main/
│   │   ├── java/com/rentmis/
│   │   │   ├── controller/      # REST API controllers
│   │   │   ├── service/         # Business logic
│   │   │   ├── repository/      # JPA repositories
│   │   │   ├── model/           # Entities & enums
│   │   │   ├── dto/             # Request/response DTOs
│   │   │   ├── mapper/          # MapStruct mappers
│   │   │   ├── config/          # Security, CORS, audit config
│   │   │   └── exception/       # Global error handling
│   │   └── resources/
│   │       ├── static/          # Frontend (HTML, CSS, JS)
│   │       ├── application.yml  # App configuration
│   │       └── db/migration/    # SQL schema (Flyway)
├── start.sh                     # Production startup script
├── pom.xml                      # Maven build file
└── .env                         # Environment variables (not committed)
```

---

## License

This project is proprietary software. All rights reserved.
