# WatchWave Subscription Module Documentation

---

## Table of Contents

1. [Overview](#overview)
2. [Database Design](#database-design)
3. [Feature Set](#feature-set)
4. [Authorization & Security](#authorization--security)
5. [API Reference](#api-reference)
6. [Code Structure](#code-structure)
7. [Testing Guide](#testing-guide)
8. [Troubleshooting & Best Practices](#troubleshooting--best-practices)

---

## Overview

The **WatchWave Subscription Module** enables users to subscribe to creators, manage their subscriptions, and view their subscribers. This module is tightly integrated with the Auth module for user and role management and supports secure, scalable, and efficient subscription workflows.

### 🎯 What This Module Provides

- **🔔 Subscribe/Unsubscribe:** Users can subscribe to or unsubscribe from creators.
- **📋 Subscription Lists:** View all creators a user is subscribed to and all subscribers of a creator.
- **✅ Subscription Status:** Check if a user is subscribed to a creator.
- **🔒 Role Enforcement:** Only users with the CREATOR role can be subscribed to.

### 🛠️ Technology Stack

| Component     | Technology         |
|---------------|--------------------|
| Framework     | Spring Boot        |
| Security      | Spring Security    |
| Auth          | JWT Integration    |
| Database      | MySQL/PostgreSQL   |
| ORM           | JPA/Hibernate      |
| Build Tool    | Maven              |
| Java Version  | Java 21            |

---

## Database Design

### 🗄️ Table: `subscriptions.subscriptions`

| Column         | Type    | Description                        |
|----------------|---------|------------------------------------|
| id             | UUID    | Primary Key                        |
| subscriber_id  | UUID    | The user subscribing               |
| creator_id     | UUID    | The creator being followed         |
| subscribed_at  | TIMESTAMP | When the subscription was made   |

- **Unique Constraint:** Each (subscriber_id, creator_id) pair is unique.
- **Foreign Keys:** Both subscriber and creator reference `auth.users(id)`.

---

## Feature Set

### 🔔 Subscribe/Unsubscribe
- Users can subscribe to creators (must have CREATOR role).
- Duplicate subscriptions are prevented by a unique constraint.
- Users can unsubscribe at any time.

### 📋 Subscription Lists
- Users can view all creators they are subscribed to.
- Creators can view all their subscribers.

### ✅ Subscription Status
- Users can check if they are subscribed to a specific creator.

### 🔒 Role Enforcement
- Only users with the CREATOR role can be subscribed to.
- Users cannot subscribe to themselves.

---

## Authorization & Security

### 🏆 Role-Based Access Control

| Feature               | USER | CREATOR | ADMIN |
|-----------------------|------|---------|-------|
| Subscribe/Unsubscribe | ✅   | ✅      | ✅    |
| View Subscriptions    | ✅   | ✅      | ✅    |
| View Subscribers      | ✅   | ✅      | ✅    |

- **JWT Required:** All endpoints require authentication.
- **CREATOR Role Enforcement:** Service checks ensure only creators can be subscribed to.
- **Self-Subscription Blocked:** Users cannot subscribe to themselves.

### 🔒 Security Features

- **Method-Level Security:** Service-layer role checks.
- **Input Validation:** All DTOs validated.
- **SQL Injection Protection:** JPA parameterized queries.
- **Data Integrity:** Unique constraints and foreign keys.

---

## API Reference

### 🔔 Subscribe/Unsubscribe

| Endpoint                                         | Method | Auth | Description                       |
|--------------------------------------------------|--------|------|-----------------------------------|
| `/api/users/me/subscriptions`                    | POST   | Yes  | Subscribe to a creator            |
| `/api/users/me/subscriptions/{creatorId}`        | DELETE | Yes  | Unsubscribe from a creator        |

#### Example Request: Subscribe
```http
POST /api/users/me/subscriptions
Authorization: Bearer 
Content-Type: application/json

{ "creatorId": "abcd-1234" }
```

---

### 📋 Subscription Lists

| Endpoint                                         | Method | Auth | Description                       |
|--------------------------------------------------|--------|------|-----------------------------------|
| `/api/users/me/subscriptions`                    | GET    | Yes  | List all creators you follow      |
| `/api/users/me/subscriptions/to-me`              | GET    | Yes  | List all users who follow you     |

---

### ✅ Subscription Status

| Endpoint                                         | Method | Auth | Description                       |
|--------------------------------------------------|--------|------|-----------------------------------|
| `/api/users/me/subscriptions/contains/{creatorId}` | GET  | Yes  | Check if you are subscribed       |

---

## Code Structure

### 🏗️ Layers

- **Controller Layer:** Handles HTTP requests, extracts user info, delegates to services.
- **Service Layer:** Business logic, validation, role checks, transaction management.
- **Repository Layer:** Data access via JPA.
- **DTOs:** Request/response objects for API boundaries.
- **Entities:** JPA-mapped domain objects.

### 📁 Package Layout

```
org.learn.watchwave.subscriptions
├── controller
├── dto
│   ├── request
│   └── response
├── model
├── repository
├── services
│   └── impl
└── config
```

---

## Testing Guide

### 🧪 Manual Testing

- Use Postman/curl to test all endpoints.
- Test with/without JWT, as different users/roles.
- Test edge cases: subscribe to self, duplicate subscribe, unsubscribe not subscribed.

### 🧪 Automated Testing

- Use JUnit + MockMvc for integration tests.
- Mock authentication for role-based tests.
- Test all CRUD operations and error cases.

### 🔍 Testing Checklist

- [ ] Subscribe to a creator (success, duplicate, to self, to non-creator)
- [ ] Unsubscribe from a creator (success, not subscribed)
- [ ] List all subscriptions (empty, non-empty)
- [ ] List all subscribers (empty, non-empty)
- [ ] Check subscription status (true, false)
- [ ] Unauthorized access blocked

---

## Troubleshooting & Best Practices

- **403 Forbidden:** Check JWT and user roles; verify security config.
- **Cannot subscribe to self:** Ensure service logic blocks this.
- **Cannot subscribe to non-creator:** Ensure service checks for CREATOR role.
- **Performance:** Use indexes on `subscriber_id`, `creator_id` for large tables.

### 🛡️ Security Best Practices

- Principle of Least Privilege: Only allow necessary actions per role.
- Input validation everywhere (DTOs, service layer).
- Always use prepared statements (JPA does this by default).
- Deny all by default in security config; explicitly permit only needed endpoints.

---

**The Subscription Module is the backbone of creator-viewer engagement on WatchWave—modular, secure, and ready for scale.**

---

*For Spring Boot and Java best practices, see [Spring Boot Reference Documentation][1].*

---

[1]: https://docs.spring.io/spring-boot/docs/3.2.x/reference/htmlsingle/

[1] https://docs.spring.io/spring-boot/docs/3.2.x/reference/htmlsingle/
[2] https://orkes.io/blog/subscription-system-with-java-spring-boot-and-conductor/
[3] https://docs.spring.io/spring-boot/documentation.html
[4] https://github.com/conductor-sdk/orkes-java-springboot2-subscription-example
[5] https://github.com/hmcts/pip-subscription-management
[6] https://spring.io/guides/gs/multi-module/
[7] https://foojay.io/today/durable-subscription-with-jms-and-spring-boot/
[8] https://cloud.google.com/pubsub/docs/spring
[9] https://developer.okta.com/blog/2020/07/27/spring-boot-using-java-modules
[10] https://stackoverflow.com/questions/42083447/subscription-design-pattern-spring
[11] https://docs.stripe.com/billing/subscriptions/build-subscriptions
[12] https://github.com/DiUS/spring-boot-template/blob/master/README.md
[13] https://github.com/AnanthaRajuC/Spring-Boot-Application-Template/blob/master/documents/DOCUMENTATION.MD
[14] https://github.com/txtbits/subscription
[15] https://reflectoring.io/spring-boot-springdoc/
[16] https://cloud.spring.io/spring-cloud-contract/reference/pdf/spring-cloud-contract.pdf