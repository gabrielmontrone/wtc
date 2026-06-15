# WTC API — Backend

REST API for **WTC**, a customer relationship / messaging SaaS platform. Built with
**Spring Boot 3** and **Java 17**, backed by **MongoDB Atlas**, with JWT authentication,
S3-compatible file storage, and Firebase push notifications.

> Companion Android app: **[wtc-front](https://github.com/gabrielmontrone/wtc-front)**

---

## 🔗 Live demo

| | |
|---|---|
| **API base URL** | `https://<your-service>.onrender.com` |
| **Interactive API docs (Swagger UI)** | `https://<your-service>.onrender.com/swagger-ui.html` |
| **Health check** | `https://<your-service>.onrender.com/health` |

> Hosted on Render's free tier — the first request after idle may take ~30s to wake up.
> Open the Swagger UI to explore and call every endpoint directly in the browser.
> _(Replace `<your-service>` with your real Render URL after deploying — see [Deployment](#-deployment).)_

---

## ✨ Features

- **Authentication & authorization** — JWT-based login/registration with role-based access (`CLIENTE`, `OPERADOR`) via Spring Security.
- **Customers** — CRUD with filtering (VIP, loyalty, active) and pagination.
- **Segments** — rule-based customer segmentation (VIP, score, loyalty thresholds).
- **Campaigns** — operator-managed messaging campaigns.
- **Messaging & conversations** — send messages to a customer, a segment, or a group; bidirectional chat and conversation history.
- **Attachments** — file uploads via pre-signed URLs to S3-compatible storage (MinIO locally).
- **Notifications** — push notifications through Firebase Cloud Messaging.
- **Observations & audit log** — operator notes on customers and an audit trail of actions.
- **OpenAPI/Swagger** — fully documented, browsable API.

## 🧱 Tech stack

| Area | Technology |
|---|---|
| Language / runtime | Java 17 |
| Framework | Spring Boot 3.5 (Web, Security, Validation, Actuator) |
| Database | MongoDB (Spring Data MongoDB) |
| Auth | JWT (jjwt), Spring Security, BCrypt |
| Storage | AWS S3 SDK v2 / MinIO |
| Notifications | Firebase Admin SDK |
| API docs | springdoc-openapi (Swagger UI) |
| Build | Maven (wrapper included) |
| Container | Docker (multi-stage build) |

## 🏗️ Architecture

The codebase is organized by **feature module** (`auth`, `customer`, `segment`, `campaign`,
`message`, `conversation`, `attachment`, `notification`, `observation`, `audit`), each with its
own controller, service, MongoDB document, repository, and DTOs.

```
src/main/java/com/wtc/
├── auth/           # login, register, JWT, security filter
├── customer/       # customer CRUD + filtering
├── segment/        # rule-based segmentation
├── campaign/       # messaging campaigns
├── message/        # send + list messages, chat
├── conversation/   # conversation history
├── attachment/     # S3 pre-signed uploads
├── notification/   # Firebase Cloud Messaging
├── observation/    # operator notes on customers
├── audit/          # audit logging
└── config/         # security, OpenAPI, logging
```

📄 More detail in [`docs/`](docs/): API reference, MongoDB data model, and architecture notes.

## 🚀 Running locally

**Prerequisites:** Java 17+, a MongoDB connection string (e.g. a free [MongoDB Atlas](https://www.mongodb.com/atlas) cluster). Docker is optional (for MinIO).

1. Copy the env template and fill in your values:
   ```bash
   cp .env.example .env
   # edit .env: set MONGODB_URI and JWT_SECRET
   ```
2. Export the variables (or use your IDE's env-file support):
   ```bash
   # PowerShell
   Get-Content .env | ForEach-Object { if ($_ -match '^\s*([^#=]+)=(.*)$') { [Environment]::SetEnvironmentVariable($matches[1].Trim(), $matches[2].Trim()) } }
   ```
3. Run:
   ```bash
   ./mvnw spring-boot:run
   ```
4. Open <http://localhost:8080/swagger-ui.html>.

> ⚠️ **Configuration is read entirely from environment variables** — no credentials are
> stored in the repository. See [`.env.example`](.env.example) for every supported variable.

## 🐳 Deployment

This service ships with a multi-stage [`Dockerfile`](Dockerfile) and a Render
[`render.yaml`](render.yaml) blueprint. To deploy on [Render](https://render.com) (free):

1. Push this repo to GitHub.
2. In Render: **New +** → **Blueprint** → select this repo. It reads `render.yaml`.
3. When prompted, paste your **MongoDB connection string** into `MONGODB_URI`.
   `JWT_SECRET` is generated automatically.
4. In **MongoDB Atlas → Network Access**, allow `0.0.0.0/0` so Render can connect.
5. Deploy. Your live Swagger UI will be at `https://<service>.onrender.com/swagger-ui.html`.

## 🔐 Security notes

- No secrets are committed; all sensitive config comes from environment variables.
- Passwords are hashed with BCrypt; endpoints are protected by JWT + role checks.

---

_This project was developed as part of coursework at FIAP._
