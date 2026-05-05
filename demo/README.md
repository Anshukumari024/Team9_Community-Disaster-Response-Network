# Community Disaster Response Network (CDRN)

A full-stack disaster management platform enabling real-time incident reporting, volunteer coordination, and authority dashboard with live notifications. Built with Spring Boot backend and React frontend.

## Project Structure

```
demo/
├── backend/                    # Spring Boot REST API + WebSocket server
│   ├── pom.xml                # Maven dependencies
│   ├── src/main/java/com/example/cdrn/
│   │   ├── CdrnBackendApplication.java
│   │   ├── model/             # Incident, User entities
│   │   ├── repository/        # Data access layer
│   │   ├── service/           # Business logic
│   │   ├── controller/        # REST endpoints
│   │   ├── websocket/         # STOMP config & notifications
│   │   ├── security/          # Spring Security setup
│   │   └── DataSeeder.java    # Sample data initialization
│   └── src/main/resources/
│       └── application.properties  # DB & server config
├── frontend/                  # React single-page app
│   ├── package.json           # NPM dependencies
│   ├── public/index.html
│   └── src/
│       ├── App.js             # Main navigation
│       ├── index.js           # React entry point
│       ├── styles.css         # Global styles
│       └── components/
│           ├── IncidentForm.js    # Citizen incident reporting
│           └── Dashboard.js       # Authority dashboard
└── README.md
```

## Features

### Citizens
- Report incidents (floods, fires, injuries, blocked roads) with location (GPS lat/long)
- Send SOS alerts with contact information
- Real-time incident status updates

### Volunteers
- Registration and verification
- Task assignments (from authority dashboard)
- Task status updates

### Authorities / Disaster Management Teams
- Authority dashboard to view all reported incidents
- Incident status management (IN_PROGRESS, RESOLVED)
- Real-time WebSocket broadcast of incident updates
- Map view with incident heatmap (coordinates shown)

### Technical Stack
- **Backend:** Spring Boot 4.0.6, Spring Data JPA, Spring WebSocket (STOMP), H2 in-memory database
- **Frontend:** React 18.2.0, Axios for HTTP, SockJS + @stomp/stompjs for real-time updates
- **Security:** Spring Security with basic auth (demo users)
- **Communication:** REST APIs + WebSocket STOMP for live notifications

## Quick Start

### Prerequisites
- JDK 17+
- Node.js 16+
- Maven (included: `mvnw.cmd` on Windows)

### 1. Start Backend

From the project root (`c:\Users\HP\OneDrive\Desktop\SpringBootEndTerm\demo`):

```powershell
Set-Location "c:\Users\HP\OneDrive\Desktop\SpringBootEndTerm\demo"
cmd /c "set SPRING_PROFILES_ACTIVE=dev&& cd /d C:\Users\HP\OneDrive\Desktop\SpringBootEndTerm\demo\backend&& ..\mvnw.cmd spring-boot:run"
```

Backend runs on **http://localhost:8081**

### 2. Start Frontend (Development)

In a new terminal, from the project root:

```powershell
cd frontend
npm install
npm start
```

Frontend dev server runs on **http://localhost:3000**  
(automatically proxies API calls to backend on 8081)

### 3. Test the App

**Citizen View (Report Incident):**
1. Navigate to http://localhost:3000
2. Fill out incident form (type, description, coordinates, etc.)
3. Click "Send SOS"

**Authority View (Dashboard):**
1. Click "Authority Dashboard" tab
2. See live incident list (WebSocket connected)
3. Click "Assign" to mark as IN_PROGRESS
4. Click "Resolve" to mark as RESOLVED
5. Changes broadcast to all connected clients in real-time

### 4. (Optional) Build Frontend for Production

```powershell
cd frontend
npm run build
```

Creates optimized bundle in `frontend/build/`. Deploy this folder to a static web server or CDN.

## API Reference

### Incidents

**Report Incident (Public)**
```
POST /api/incidents
Content-Type: application/json

{
  "type": "Flood",
  "description": "Water level rising rapidly",
  "latitude": 12.9716,
  "longitude": 77.5946,
  "severity": "HIGH",
  "reporterName": "Alice",
  "contact": "+911234567890"
}

Response: 201 Created
{
  "id": 1,
  "type": "Flood",
  "status": "REPORTED",
  "createdAt": "2026-05-05T13:00:00Z",
  ...
}
```

**List All Incidents**
```
GET /api/incidents

Response: 200 OK
[
  {
    "id": 1,
    "type": "Flood",
    "description": "Water level rising rapidly",
    "latitude": 12.9716,
    "longitude": 77.5946,
    "severity": "HIGH",
    "status": "REPORTED",
    "reporterName": "Alice",
    "contact": "+911234567890",
    "createdAt": "2026-05-05T13:00:00Z"
  },
  ...
]
```

**Update Incident Status (Authority Only - requires basic auth)**
```
PUT /api/incidents/{id}/status?status=IN_PROGRESS

Auth: Basic admin:adminpass

Response: 200 OK
{
  "id": 1,
  "status": "IN_PROGRESS",
  ...
}
```

### Users

**Register User**
```
POST /api/users/register
Content-Type: application/json

{
  "username": "john_volunteer",
  "displayName": "John Doe",
  "role": "VOLUNTEER"
}

Response: 201 Created
{
  "id": 2,
  "username": "john_volunteer",
  "displayName": "John Doe",
  "role": "VOLUNTEER",
  "verified": false
}
```

**List All Users**
```
GET /api/users

Response: 200 OK
[...]
```

### WebSocket (Real-Time)

**Connect & Subscribe to Incidents**
```javascript
// SockJS + STOMP
const client = new Client({
  webSocketFactory: () => new SockJS('/ws'),
  onConnect: () => {
    client.subscribe('/topic/incidents', (message) => {
      const incident = JSON.parse(message.body);
      console.log('New incident:', incident);
    });
  }
});
client.activate();
```

Every time an incident is created or status is updated, all subscribed clients receive a broadcast on `/topic/incidents`.

## Database

- **Default:** H2 in-memory (perfect for development & demos)
- **Location:** Data stored in RAM, cleared on restart
- **H2 Console:** http://localhost:8081/h2-console (JDBC URL: `jdbc:h2:mem:cdrn`)

### For Production

Edit `backend/src/main/resources/application.properties`:

**PostgreSQL:**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/cdrn
spring.datasource.username=postgres
spring.datasource.password=yourpassword
spring.datasource.driverClassName=org.postgresql.Driver
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

**MySQL:**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/cdrn
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
```

Add the corresponding JDBC driver to `backend/pom.xml`:
```xml
<dependency>
  <groupId>org.postgresql</groupId>
  <artifactId>postgresql</artifactId>
  <version>42.6.0</version>
</dependency>
```

## Development & Deployment

### Run Tests

Backend:
```powershell
.\mvnw.cmd -f backend/pom.xml test
```

Frontend:
```powershell
cd frontend
npm test
```

### Build Backend JAR

```powershell
.\mvnw.cmd -f backend/pom.xml -DskipTests package
```

JAR output: `backend/target/cdrn-backend-0.0.1-SNAPSHOT.jar`

Run JAR:
```bash
java -jar backend/target/cdrn-backend-0.0.1-SNAPSHOT.jar
```

### Docker (Optional)

Create `Dockerfile` in backend/:
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/cdrn-backend-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```bash
cd backend
docker build -t cdrn-backend:latest .
docker run -p 8081:8081 cdrn-backend:latest
```

## Security Notes

- **Current Setup:** Public development API with no login prompt for fast demo usage
- **Production:** Add JWT tokens, OAuth2, database-backed user store, HTTPS, and CSRF protection
- **CORS:** Enabled for localhost (see `WebSocketConfig.java`)
- **CSRF:** Disabled for API endpoints (enable for production with proper token handling)

## Known Limitations

1. H2 in-memory DB: data lost on restart
2. Single-server deployment (no clustering/microservices)
3. No SMS/WhatsApp notifications (architecture ready)
4. No offline-first caching (could use IndexedDB)
5. Map visualization shows coordinates as text (ready for Mapbox/Google Maps integration)

## Future Enhancements

- [ ] Integrate Google Maps / Mapbox for incident heatmap
- [ ] SMS/WhatsApp notifications via Twilio
- [ ] ML-based incident severity classification
- [ ] Offline mode with sync when online
- [ ] Volunteer location tracking & route optimization
- [ ] Mobile app (React Native)
- [ ] Disaster prediction using historical data
- [ ] Multi-language support (i18n)

## License

This project is part of a hackathon demonstration. All code is provided as-is for educational purposes.

---

**Questions or Issues?** Check backend logs in terminal or browser console (F12) for frontend errors.
