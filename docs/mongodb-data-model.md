# MongoDB Data Model

This project uses **MongoDB** as its NoSQL database.

MongoDB follows the **document-oriented NoSQL model**. Instead of storing data in relational tables, the application stores data as flexible JSON/BSON-like documents inside collections.

The configured database name is:

```text
wtc_db
```

In the Java code, each MongoDB collection is represented by a class annotated with `@Document(collection = "...")`. The repositories use Spring Data MongoDB through `MongoRepository`.

## NoSQL Model Used

| Item | Description |
| --- | --- |
| Database type | NoSQL |
| NoSQL model | Document-oriented database |
| Database technology | MongoDB |
| Database name | `wtc_db` |
| Java integration | Spring Data MongoDB |
| Main persistence pattern | Document classes + MongoDB repositories |

## Collections

| Collection | Java document | Purpose |
| --- | --- | --- |
| `users` | `UserDocument` | Stores registered users, roles, passwords, and Firebase notification tokens. |
| `customers` | `CustomerDocument` | Stores customer profile data and status flags. |
| `segments` | `SegmentDocument` | Stores segmentation rules used to group/filter customers. |
| `messages` | `MessageDocument` | Stores sent messages, target information, status, and conversation references. |
| `conversations` | `ConversationDocument` | Stores customer/operator conversation metadata. |
| `attachments` | `AttachmentDocument` | Stores metadata for files uploaded to S3-compatible storage. |
| `campaigns` | `CampaignDocument` | Stores campaign information, targeting, status, and send metrics. |
| `customer_observations` | `CustomerObservationDocument` | Stores notes or observations written about customers. |
| `audit_logs` | `AuditLogDocument` | Stores audit events for important actions in the system. |

## Collection Details

### `users`

Stores application users.

Main fields:

| Field | Description |
| --- | --- |
| `id` | MongoDB document ID |
| `email` | User email |
| `password` | User password |
| `role` | User role, such as `CLIENTE` or `OPERADOR` |
| `fcmToken` | Firebase Cloud Messaging token for notifications |

### `customers`

Stores customer information.

Main fields:

| Field | Description |
| --- | --- |
| `id` | MongoDB document ID |
| `name` | Customer name |
| `document` | Customer document/identifier |
| `vip` | Indicates whether the customer is VIP |
| `fidelidade` | Indicates whether the customer is part of the loyalty program |
| `ativo` | Indicates whether the customer is active |
| `createdAt` | Creation date/time |

### `segments`

Stores customer segment rules.

Main fields:

| Field | Description |
| --- | --- |
| `id` | MongoDB document ID |
| `name` | Segment name |
| `vip` | VIP filter rule |
| `active` | Active customer filter rule |
| `minScore` | Minimum score rule |
| `minLoyalty` | Minimum loyalty rule |

### `messages`

Stores messages sent to customers, segments, or groups.

Main fields:

| Field | Description |
| --- | --- |
| `id` | MongoDB document ID |
| `targetType` | Message target type: `CUSTOMER`, `SEGMENT`, or `GROUP` |
| `subject` | Message subject |
| `content` | Message content |
| `senderId` | ID of the user who sent the message |
| `senderRole` | Role of the sender |
| `customerId` | Target customer ID, when sending to one customer |
| `segmentId` | Target segment ID, when sending to a segment |
| `groupName` | Group name, when sending to a group |
| `customerIds` | List of target customer IDs |
| `status` | Message status |
| `failureReason` | Failure details, when sending fails |
| `createdAt` | Creation date/time |
| `conversationId` | Related conversation ID |
| `originCampaignId` | Campaign ID that generated the message |

### `conversations`

Stores conversation metadata.

Main fields:

| Field | Description |
| --- | --- |
| `id` | MongoDB document ID |
| `customerId` | Customer who owns the conversation |
| `operatorId` | Operator assigned to the conversation |
| `status` | Conversation status, such as `OPEN` or `CLOSED` |
| `updatedAt` | Date/time of the last conversation update |

### `attachments`

Stores metadata for uploaded files. The file itself is stored in S3-compatible storage or MinIO, while MongoDB stores information about that file.

Main fields:

| Field | Description |
| --- | --- |
| `id` | MongoDB document ID |
| `fileName` | Original file name |
| `contentType` | File MIME type, such as `image/png` |
| `fileSize` | File size in bytes |
| `s3Key` | File path/key in S3-compatible storage |
| `url` | Final file access URL |
| `messageId` | Message associated with the attachment |
| `createdAt` | Creation date/time |
| `status` | Upload status, such as `Pending` or `Uploaded` |

### `campaigns`

Stores campaign data and metrics.

Main fields:

| Field | Description |
| --- | --- |
| `id` | MongoDB document ID |
| `name` | Campaign name |
| `description` | Campaign description |
| `type` | Campaign type, such as `EMAIL`, `SMS`, or `PUSH` |
| `content` | Base campaign message |
| `segmentTargetId` | Target segment ID |
| `callCode` | Campaign call code |
| `status` | Campaign status, such as `DRAFT`, `ACTIVE`, or `FINISHED` |
| `createdAt` | Creation date/time |
| `totalSends` | Total send attempts |
| `successSends` | Successful sends |
| `failureSends` | Failed sends |
| `responseCount` | Number of responses |

### `customer_observations`

Stores notes about customers.

Main fields:

| Field | Description |
| --- | --- |
| `id` | MongoDB document ID |
| `customerId` | Customer receiving the note |
| `content` | Observation text |
| `authorEmail` | Operator email that created the note |
| `createdAt` | Creation date/time |

### `audit_logs`

Stores audit events for traceability.

Main fields:

| Field | Description |
| --- | --- |
| `id` | MongoDB document ID |
| `action` | Action performed, such as `LOGIN`, `SEND_MESSAGE`, or `CREATE_CUSTOMER` |
| `userEmail` | Email of the user who performed the action |
| `details` | Additional description of the event |
| `timestamp` | Event timestamp |
| `correlationId` | Request correlation ID used for tracing |

## Relationship Overview

Although MongoDB does not require relational foreign keys, this project uses ID references between documents:

| Relationship | Description |
| --- | --- |
| `messages.customerId` -> `customers.id` | A message can target one customer. |
| `messages.customerIds` -> `customers.id` | A message can target multiple customers. |
| `messages.segmentId` -> `segments.id` | A message can target a segment. |
| `messages.conversationId` -> `conversations.id` | A message can belong to a conversation. |
| `messages.originCampaignId` -> `campaigns.id` | A message can be generated by a campaign. |
| `conversations.customerId` -> `customers.id` | A conversation belongs to a customer. |
| `conversations.operatorId` -> `users.id` | A conversation can be assigned to an operator. |
| `attachments.messageId` -> `messages.id` | An attachment belongs to a message. |
| `campaigns.segmentTargetId` -> `segments.id` | A campaign can target a segment. |
| `customer_observations.customerId` -> `customers.id` | An observation belongs to a customer. |

## Short Summary

The project uses MongoDB as a document-oriented NoSQL database. The main data is stored in the `wtc_db` database using collections for users, customers, segments, messages, conversations, attachments, campaigns, customer observations, and audit logs.
