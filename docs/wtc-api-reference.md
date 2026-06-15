# WTC API Reference

This document summarizes the available WTC API endpoints from the Swagger/OpenAPI contract.

- **API name:** WTC API
- **Version:** v1
- **Base URL:** `http://localhost:8080`
- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

## Authentication

The API uses JWT bearer authentication.

Send the token in the `Authorization` header:

```http
Authorization: Bearer <token>
```

Public endpoints do not require a token:

- `POST /api/v1/auth/login`
- `POST /api/v1/auth/register`
- `GET /swagger-ui.html`
- `GET /swagger-ui/**`
- `GET /v3/api-docs/**`

All other endpoints require authentication.

## Enums

### UserRole

- `CLIENTE`
- `OPERADOR`

### MessageTargetType

- `CUSTOMER`
- `SEGMENT`
- `GROUP`

### MessageStatus

- `PENDING`
- `FAILED`

## Authentication APIs

### `POST /api/v1/auth/login`

Authenticates a user and returns a JWT token that can be used to access protected endpoints.

**Authentication:** Public

**Request body**

| Field | Type | Required | Notes |
| --- | --- | --- | --- |
| `email` | string | Yes | User email |
| `password` | string | Yes | User password |

**Success response:** `200 OK`

```json
{
  "token": "string",
  "role": "string"
}
```

### `POST /api/v1/auth/register`

Creates a new user account with either a customer or operator role.

**Authentication:** Public

**Request body**

| Field | Type | Required | Notes |
| --- | --- | --- | --- |
| `email` | string | Yes | Must be a valid email |
| `password` | string | Yes | User password |
| `role` | string | Yes | `CLIENTE` or `OPERADOR` |

**Success response:** `200 OK`

```text
Usuario cadastrado com sucesso!
```

## Customers APIs

### `POST /customers`

Creates a customer record with document information and optional status flags.

**Authentication:** Required

**Request body**

| Field | Type | Required | Notes |
| --- | --- | --- | --- |
| `name` | string | Yes | 3 to 100 characters |
| `document` | string | Yes | 11 to 14 numeric characters |
| `vip` | boolean | No | Marks the customer as VIP |
| `fidelidade` | boolean | No | Marks the customer as part of the loyalty program |
| `ativo` | boolean | No | Marks the customer as active |

**Success response:** `200 OK`

Returns a `CustomerResponse`.

### `GET /customers/{id}`

Finds a single customer by ID.

**Authentication:** Required

**Path parameters**

| Parameter | Type | Required | Notes |
| --- | --- | --- | --- |
| `id` | string | Yes | Customer ID |

**Success response:** `200 OK`

Returns a `CustomerResponse`.

### `GET /customers`

Lists customers with optional filters and pagination.

**Authentication:** Required

**Query parameters**

| Parameter | Type | Required | Notes |
| --- | --- | --- | --- |
| `vip` | boolean | No | Filter by VIP status |
| `fidelidade` | boolean | No | Filter by loyalty status |
| `ativo` | boolean | No | Filter by active status |
| `page` | integer | No | Default: `0` |
| `size` | integer | No | Default: `10` |

**Success response:** `200 OK`

Returns `Page<CustomerResponse>`.

## Segments APIs

### `POST /api/v1/segments`

Creates a customer segment using rules such as VIP status, active status, score, and loyalty level.

**Authentication:** Required

**Request body**

| Field | Type | Required | Notes |
| --- | --- | --- | --- |
| `name` | string | Yes | Segment name |
| `vip` | boolean | No | Matches VIP customers |
| `active` | boolean | No | Matches active customers |
| `minScore` | integer | No | Minimum customer score |
| `minLoyalty` | integer | No | Minimum loyalty value |

**Success response:** `201 Created`

Returns a `SegmentResponse`.

### `GET /api/v1/segments`

Lists all available customer segments.

**Authentication:** Required

**Success response:** `200 OK`

Returns `SegmentResponse[]`.

### `GET /api/v1/segments/{id}/customers`

Lists the customers that match a specific segment.

**Authentication:** Required

**Path parameters**

| Parameter | Type | Required | Notes |
| --- | --- | --- | --- |
| `id` | string | Yes | Segment ID |

**Success response:** `200 OK`

Returns a `SegmentCustomersResponse`.

## Messages APIs

### `POST /api/v1/messages`

Sends a message to a customer, a segment, or a group of customers.

**Authentication:** Required

**Request body**

| Field | Type | Required | Notes |
| --- | --- | --- | --- |
| `targetType` | string | Yes | `CUSTOMER`, `SEGMENT`, or `GROUP` |
| `subject` | string | Yes | 2 to 120 characters |
| `content` | string | Yes | 1 to 2000 characters |
| `customerId` | string | No | Used when sending to one customer |
| `segmentId` | string | No | Used when sending to a segment |
| `groupName` | string | No | Used when sending to a named group |
| `customerIds` | string[] | No | Used when sending to multiple customers |

**Success response:** `201 Created`

Returns a `MessageResponse`.

### `GET /api/v1/messages/conversation/{conversationId}`

Gets the message history for a conversation.

**Authentication:** Required

**Path parameters**

| Parameter | Type | Required | Notes |
| --- | --- | --- | --- |
| `conversationId` | string | Yes | Conversation ID |

**Success response:** `200 OK`

Returns `MessageResponse[]`.

## Conversations APIs

### `GET /api/v1/conversations/customer/{customerId}`

Lists conversations associated with a specific customer.

**Authentication:** Required

**Path parameters**

| Parameter | Type | Required | Notes |
| --- | --- | --- | --- |
| `customerId` | string | Yes | Customer ID |

**Success response:** `200 OK`

Returns `ConversationResponse[]`.

## Schemas

### CustomerResponse

```json
{
  "id": "string",
  "name": "string",
  "document": "string",
  "vip": "boolean",
  "fidelidade": "boolean",
  "ativo": "boolean",
  "createdAt": "string, date-time"
}
```

### SegmentResponse

```json
{
  "id": "string",
  "name": "string",
  "vip": "boolean",
  "active": "boolean",
  "minScore": "integer",
  "minLoyalty": "integer"
}
```

### SegmentCustomersResponse

```json
{
  "segmentId": "string",
  "totalCustomers": "integer",
  "customerIds": "string[]"
}
```

### MessageResponse

```json
{
  "id": "string",
  "targetType": "MessageTargetType",
  "subject": "string",
  "content": "string",
  "customerId": "string",
  "segmentId": "string",
  "groupName": "string",
  "customerIds": "string[]",
  "status": "MessageStatus",
  "failureReason": "string",
  "createdAt": "string, date-time"
}
```

### ConversationResponse

```json
{
  "id": "string",
  "customerId": "string",
  "operatorId": "string",
  "status": "string",
  "updatedAt": "string, date-time"
}
```

### ErrorResponse

```json
{
  "timestamp": "string, date-time",
  "status": "integer",
  "error": "string",
  "details": "string[]"
}
```
