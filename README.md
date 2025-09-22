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
  - Detects user inactivity / churn risk (e.g., no views or cart activity for N days) and generates targeted nudges.
  - Proposes guarded incentives (small, budget-aware discounts with floors/MAP, category exclusions) when risk is high.
  - Uses Spring AI to craft subject/body with clear CTAs; A/B tests subject lines; logs outcomes for learning.
  - Publishes `EmailSendCommand` and `OfferGenerated` events to Notification Service via RabbitMQ.

- **AI Assistant & Personalization API (WebFlux)**
  - **Conversational Q&A (Streaming RAG):** Unified endpoint for product/policy Q&A and chat with per-user memory; streams tokens via SSE/WebSocket.
  - **Visual Search:** Image-to-product matching (embeddings + vector DB) with “why this match” rationales.
  - **Checkout Upsell Bundles (Real-time):** Suggests add-on bundles before payment; validates stock/margin/thresholds in-flight.
  - **Search Re-ranking & Query Rewrite:** Reorders candidates using user signals and expands/clarifies queries for relevance.
  - Built on WebFlux for non-blocking fan-out to Product/Price/Inventory/Vector stores and low-latency streaming responses.

---

## Event Flows (RabbitMQ)

- **Orders → Notifications**  
  `order.created`, `order.paid`, `order.shipped` → Notification Service delivers order confirmations, payment receipts, and shipping updates.

- **Payments → Notifications**  
  `payment.succeeded`, `payment.failed` → Notification Service sends success/failure receipts and follow-up actions.

- **AI Email Worker → Notifications**  
  `email.send` → Notification Service dispatches personalized emails.  
  The **AI Email Worker**:
  - Curates **per-user product recommendations** and **guard-railed discounts**.
  - **Detects user inactivity/churn risk** and triggers targeted nudges.
  - Generates localized **subject/body** via Spring AI and logs outcomes for learning (A/B testing ready).
  - Uses **RAG (Retrieval-Augmented Generation)** to combine:
    - **Global knowledge** (products, FAQs, policies).  
    - **User-scoped signals** (preferences, intents, recency).  

This ensures every message is **context-aware** and tailored to the user’s journey.


---

##  Runtime Choices
- **Normal Spring Web (Servlet/MVC):**
    - Orders, Customer, Payment, Product, Notifications, Config, Eureka, Gateway.
- **Spring WebFlux (Reactive):**
    - AI Assistant & Personalization API (real-time streaming, high concurrency).
- **Headless (no web server):**
    - AI Email Worker (scheduled + RabbitMQ consumer).

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
- **springdoc-openapi** – Auto-generated OpenAPI + Swagger UI for every service


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

