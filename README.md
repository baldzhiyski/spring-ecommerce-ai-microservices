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

## AI Service

- **AI Assistant & Personalization API**
  - **Conversational Q&A (Streaming RAG):** Unified endpoint for product/policy Q&A and chat with per-user memory; streams tokens via SSE/WebSocket.
  - **Visual Search:** Image-to-product matching (embeddings + vector DB) with “why this match” rationales.
  - **Checkout Upsell Bundles (Real-time):** Suggests add-on bundles before payment; validates stock/margin/thresholds in-flight.
  - **Search Re-ranking & Query Rewrite:** Reorders candidates using user signals and expands/clarifies queries for relevance.
  - Built on MVC .

---

## Event Flows (RabbitMQ)

- **Orders → Notifications**  
  `order.created`, `order.paid`, `order.shipped` → Notification Service delivers order confirmations, payment receipts, and shipping updates.

- **Payments → Notifications**  
  `payment.succeeded`, `payment.failed` → Notification Service sends success/failure receipts and follow-up actions.

---

##  Runtime Choices
- **Normal Spring Web (Servlet/MVC):**
    - Orders, Customer, Payment, Product, Notifications, Config, Eureka, Gateway.
- **Spring MVC :**
    - AI Assistant & Personalization API .
---

## Security & Observability

- **Security**
  - **Keycloak** provides centralized authentication and authorization (OAuth2/OpenID Connect).
  - API Gateway enforces **end-user auth** with JWT tokens issued by Keycloak.
  - Postman (or any OAuth2 client) can request tokens from Keycloak for testing secured APIs.
  - Service-to-service calls use short-lived **service JWTs**; internal traffic only.


- **Observability**
  - **Distributed tracing with Zipkin** to visualize requests across **Gateway → Services → AI**.
  - **AOP-based structured logging** for consistent application logs and audit trails.


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
- **Spring Cloud** – Service discovery, API Gateway (Per-user/IP rate limits to protect AI endpoints), configuration management
- **Spring Cloud CircuitBreaker (Resilience4j)** – Timeouts, retries, circuit breaking, bulkheads for downstream calls
- **Spring AI** – Generative AI integration (recommendations, smart search, AI-driven descriptions)
- **RabbitMQ** – Message broker for asynchronous, event-driven architecture
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

