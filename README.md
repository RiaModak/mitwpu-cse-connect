# MITWPU CSE Connect

A full-stack university CSE department portal for managing student records, clubs, achievements, and announcements.

## Tech Stack

- **Backend**: Java 17, Spring Boot 3.2, Spring Security, JWT, MySQL, JPA/Hibernate
- **Frontend**: React 18, Vite, Tailwind CSS, React Router, Recharts, Framer Motion

## Prerequisites

- Java 17+
- Maven 3.8+
- Node.js 18+
- MySQL 8.0+

## Setup

### 1. Database

```sql
CREATE DATABASE cse_connect;
```

### 2. Backend

```bash
cd backend
# Update database credentials in src/main/resources/application.properties if needed
mvn spring-boot:run
```

The backend starts on `http://localhost:8080`.

### 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend starts on `http://localhost:5173`.

## Default Login Credentials

| Role    | Email                          | Password      |
|---------|--------------------------------|---------------|
| Admin   | admin@mitwpu.edu.in            | Admin@1234    |
| Teacher | sunita.sharma@mitwpu.edu.in    | Teacher@1234  |
| Student | ria.modak@mitwpu.edu.in        | Student@1234  |

## Features

- JWT-based authentication with role-based access control (ADMIN, TEACHER, STUDENT)
- Student management with CRUD, bulk CSV import, academic records
- Teacher management with panel assignment
- Club management with membership tracking and notices
- Achievement submission and verification workflow
- Announcements with audience targeting
- Audit logging for all mutations
- Soft-delete pattern for data preservation
- Glassmorphism UI with responsive design
- Charts and analytics dashboards
- File upload support for achievement proofs

## API Endpoints

### Auth
- `POST /api/auth/login` - Login
- `POST /api/auth/refresh` - Refresh token
- `POST /api/auth/logout` - Logout

### Students
- `GET /api/students` - List students (paginated, filterable)
- `GET /api/students/{prn}` - Get student details
- `POST /api/students` - Create student
- `PUT /api/students/{prn}` - Update student
- `DELETE /api/students/{prn}` - Soft-delete student
- `POST /api/students/bulk-import` - Bulk import from CSV
- `POST /api/students/{prn}/academic-records` - Add academic record
- `PUT /api/students/me` - Update own profile (student only)

### Teachers
- `GET /api/teachers` - List teachers
- `POST /api/teachers` - Create teacher
- `PUT /api/teachers/{id}` - Update teacher
- `DELETE /api/teachers/{id}` - Delete teacher
- `POST /api/teachers/assign-panel` - Assign panel

### Clubs
- `GET /api/clubs` - List clubs
- `GET /api/clubs/{id}` - Club details
- `POST /api/clubs` - Create club
- `POST /api/clubs/{id}/members` - Add member
- `POST /api/clubs/{id}/notices` - Post notice

### Achievements
- `GET /api/achievements` - List achievements (paginated)
- `POST /api/achievements` - Submit achievement
- `PUT /api/achievements/{id}/verify` - Verify/reject achievement

### Announcements
- `GET /api/announcements` - Get visible announcements
- `POST /api/announcements` - Create announcement

### Admin
- `GET /api/admin/audit-logs` - Get audit logs
- `GET /api/dashboard/admin` - Admin dashboard stats
- `GET /api/dashboard/teacher` - Teacher dashboard stats
- `GET /api/dashboard/student` - Student dashboard stats
