# Quantify - A Modern Trading & Investment Platform 📈

Quantify is a full-featured, data-driven trading and investment platform built with a modern, enterprise-grade technology stack...

## ✨ Key Features

* **Secure User Authentication:** Supports email/password (JWT) and Google (OAuth 2.0).
* **Complete Portfolio Management:** Track cash balance, holdings, and transaction history.
* **Asynchronous Paper Trading:** Resilient trading engine using RabbitMQ.
* **Personalized Watchlists:** Create and manage multiple custom watchlists.
* **Advanced Analytics:** Interactive charting and a backtesting engine.
* **High-Performance Architecture:** Utilizes a Redis cache for low-latency data fetching.

## 🛠️ Technology Stack

* **Backend:** Java, Spring Boot, Spring Security
* **Database:** PostgreSQL
* **Caching:** Redis
* **Messaging:** RabbitMQ
* **DevOps:** Docker, GitHub Actions (CI/CD)

## 🗂️ Database Schema

The application uses a normalized PostgreSQL database to manage all user and financial data.

![Database Schema Diagram](asset/schema.png)

## Properties

spring.datasource.url=jdbc:postgresql://...
spring.datasource.username=postgres...
spring.datasource.password=YOUR_DATABASE_PASSWORD_HERE

## ⚙️ Configuration

This project uses an `application.properties` file for configuration. Since this file contains sensitive information, it is ignored by Git.

To configure your local environment, follow these steps:

1.  Create a file named `application.properties` inside the `src/main/resources` directory.
2.  Copy the contents from `application-example.properties`.
3.  Replace the placeholder values (like `YOUR_DATABASE_PASSWORD_HERE`) with your actual credentials for your PostgreSQL database.
