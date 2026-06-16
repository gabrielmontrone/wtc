# WTC API — Backend

REST API for **WTC**, a customer relationship / messaging SaaS platform. Built with
**Spring Boot 3** and **Java 17**, backed by **MongoDB Atlas**, with JWT authentication,
S3-compatible file storage, and Firebase push notifications.

> Companion Android app: **[wtc-front](https://github.com/gabrielmontrone/wtc-front)**

---

## 🔗 Live demo

| | |
|---|---|
| **API base URL** | `https://wtc-ioxk.onrender.com` |
| **Interactive API docs (Swagger UI)** | `https://wtc-ioxk.onrender.com/swagger-ui.html` |
| **Health check** | `https://wtc-ioxk.onrender.com/health` |

> ⏱️ **Cold start:** the demo is hosted on Render's **free tier**, which sleeps after ~15 min
> of inactivity. The first request after idle can take **~30–60s** to wake the instance
> (subsequent requests are fast). If a call seems to hang, hit the health check once to warm it
> up, then retry. **For active development we recommend [running the API locally](#-running-locally)** —
> it is instant and works fully offline.

Open the Swagger UI to explore and call every endpoint directly in the browser, or use the
[`Using the API`](#-using-the-api) examples below.

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

## 📡 Using the API

Every example below works against either environment — just set the base URL:

```bash
# Hosted demo (free tier; first call may cold-start ~30–60s)
BASE_URL=https://wtc-ioxk.onrender.com

# Local backend (recommended for development — instant, offline)
BASE_URL=http://localhost:8080
```

Most endpoints require a **JWT bearer token**. The flow is: register a user → log in to get a
token → send it as `Authorization: Bearer <token>` on subsequent requests.

**1. Register** (`role` is `CLIENTE` or `OPERADOR`):

```bash
curl -X POST "$BASE_URL/api/v1/auth/register" \
  -H "Content-Type: application/json" \
  -d '{"email":"operador@wtc.com","password":"secret123","role":"OPERADOR"}'
```

**2. Log in** — returns `{ "token", "role", "userId" }`:

```bash
curl -X POST "$BASE_URL/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"operador@wtc.com","password":"secret123"}'
```

**3. Call an authenticated endpoint** — pass the token from step 2:

```bash
TOKEN="<paste-the-token-here>"

# List customers (paginated, with optional filters)
curl "$BASE_URL/customers?page=0&size=10" \
  -H "Authorization: Bearer $TOKEN"

# Create a customer
curl -X POST "$BASE_URL/customers" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Maria Silva","document":"12345678901","vip":false,"fidelidade":false,"ativo":true}'
```

> 💡 **Prefer a UI?** Open **`$BASE_URL/swagger-ui.html`**, click **Authorize**, paste your
> token, and every endpoint becomes callable from the browser — no curl needed.

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
