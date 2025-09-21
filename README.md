# Microservices E-Commerce App

This project is a **Microservices-based E-Commerce Application** built with **Spring Cloud**, **Spring Boot**, and **Spring AI**. It demonstrates how modern distributed systems can power scalable and intelligent e-commerce platforms.

---

## Core Domain Services
- **Orders Service**
    - Manages order creation, status, and lifecycle.
    - Publishes order events (created, paid, shipped) to RabbitMQ.
- **Customer Service**
    - Manages customer profiles, preferences, addresses.
    - Exposes APIs for profile lookup used by other services.
- **Payment Service**
    - Handles payment intents, captures, refunds.
    - Publishes payment success/failed events to RabbitMQ.
- **Product Service**
    - Catalog CRUD, pricing, inventory, search metadata.
    - Serves product details used by Orders/AI services.
- **Notification Service**
    - Consumes order/payment events from RabbitMQ.
    - Sends email/SMS/Push confirmations and updates.

---

## Platform & Infrastructure
- **API Gateway (Spring Cloud Gateway)**
    - Single entry point for web/mobile.
    - AuthN/Z, rate limiting, routing to services.
- **Service Discovery (Eureka)**
    - Registers all services for load-balanced discovery.
- **Config Service (Spring Cloud Config)**
    - Centralized configuration for all services and profiles.
- **Message Broker (RabbitMQ)**
    - Event bus for order/payment → notifications.
    - Also used by AI/batch pipelines where appropriate.

---

## AI Services
- **AI Email Worker (Headless)**
    - Scheduled weekly recommendations & personalized discounts.
    - Generates subject/body via Spring AI.
    - Publishes `EmailSendCommand` to Notification Service via RabbitMQ.
- **AI Assistant & Personalization API (WebFlux)**
    - Real-time endpoints: chat, semantic suggestions, on-page recommendations.
    - Optional SSE/WebSocket streaming for token-by-token responses.
    - Aggregates Product/Price/Inventory/Vector DB in parallel (non-blocking).

---

## Event Flows (RabbitMQ)
- **Orders → Notifications**
    - `order.created`, `order.paid`, `order.shipped` → Notification Service sends confirmations.
- **Payments → Notifications**
    - `payment.succeeded`, `payment.failed` → Notification Service sends receipts/alerts.
- **AI Email Worker → Notifications**
    - `email.send` (to, subject, html) → Notification Service dispatches weekly mail.

---

## Tech & Runtime Choices
- **Normal Spring Web (Servlet/MVC):**
    - Orders, Customer, Payment, Product, Notifications, Config, Eureka, Gateway.
- **Spring WebFlux (Reactive):**
    - AI Assistant & Personalization API (real-time streaming, high concurrency).
- **Headless (no web server):**
    - AI Email Worker (scheduled + RabbitMQ consumer).

---

## Security & Observability
- **Security**
    - Gateway enforces end-user auth (JWT/OAuth2).
    - Service-to-service calls use service JWTs; internal networks only.
- **Observability**
    - Spring Boot Actuator + metrics (p95/p99).
    - Distributed tracing across Gateway → Services → AI.
    - Correlation IDs (`X-Request-Id`) propagated end-to-end.

---

## Config & Deployment Notes
- External traffic only via **Gateway**.
- All services registered in **Eureka**, configured via **Config Service**.
- RabbitMQ used for **asynchronous** and **reliable** communication.
- Each service has its own **database** (polyglot persistence allowed).
- CI/CD builds versioned containers; rolling or blue/green deployments.

---

## 🔧 Tech Stack
- **Spring Boot** – Core microservices framework
- **Spring Cloud** – Service discovery, API Gateway, configuration management
- **Spring AI** – Generative AI integration for enhanced user experiences (e.g., product recommendations, smart search, AI-driven descriptions)
- **RabbitMQ** – Message broker for asynchronous communication and event-driven architecture
- **REST APIs** – Clean and well-structured endpoints for e-commerce workflows

---

## ✨ Features
- Modular microservices architecture for scalability
- API Gateway & centralized configuration using Spring Cloud
- Event-driven communication with RabbitMQ
- Integration of Generative AI to enhance the shopping experience
- Example APIs for products, orders, payments, and user management

---

## 🚀 Goals
This repository is designed as a **learning project** and a **portfolio showcase**.  
It demonstrates not only how to build a production-ready microservices e-commerce system, but also how to integrate **GenAI capabilities** into a modern Spring ecosystem.

---

📌 *Inspired by a [YouTube tutorial](https://www.youtube.com/watch?v=jdeSV0GRvwI&t=6s)*

