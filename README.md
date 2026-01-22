# Property Management System (PMS) - Backend API

A robust RESTful API built with **Java 21** and **Spring Boot 4** designed to help real estate agents and investors manage properties, tenants, financials, and tax reporting in Ontario, Canada.

## 🚀 Tech Stack

*   **Framework:** Spring Boot 4.0.0 / Spring Framework 7
*   **Language:** Java 21
*   **Database:** PostgreSQL 17 (Managed via AWS RDS in Prod)
*   **Migration:** Flyway
*   **Security:** Spring Security 7, JWT (Stateless), BCrypt
*   **Storage:** AWS S3 (Tenant Documents)
*   **ORM:** Spring Data JPA / Hibernate
*   **Utilities:** Lombok, Jakarta Validation

## ✨ Key Features

*   **Multi-Tenancy:** Data isolation per Organization (User).
*   **Auth System:** Register, Email Verification (OTP), Login, Token Blacklist Logout.
*   **Core Management:** Properties, Tenants, Leases.
*   **Financials:** Income & Expense tracking, Mortgage payments.
*   **Documents:** Encrypted file upload/download via AWS S3.
*   **Reporting:** T776 Tax Report generation (CRA Tax Lines).
*   **Automation:** Daily email notifications for expiring leases (60-day notice).

## 🛠️ Prerequisites

*   Java JDK 21+
*   Maven 3.8+
*   PostgreSQL 16+

## ⚙️ Configuration & Environment Variables

The application relies on environment variables for security. You must set these in your IDE (Run Configurations) or Server (AWS Elastic Beanstalk Environment Properties).

| Variable Name | Description | Example Value |
| :--- | :--- | :--- |
| `SPRING_DATASOURCE_URL` | JDBC URL for Postgres | `jdbc:postgresql://localhost:5432/pms_db` |
| `SPRING_DATASOURCE_USERNAME`| Database Username | `pms_user` |
| `SPRING_DATASOURCE_PASSWORD`| Database Password | `securePassword123` |
| `JWT_SECRET` | Secret key for signing tokens | `(A long random string)` |
| `AWS_ACCESS_KEY_ID` | AWS IAM Key | `AKIA...` |
| `AWS_SECRET_ACCESS_KEY` | AWS IAM Secret | `...` |
| `AWS_REGION` | AWS Region | `us-east-2` |
| `MAIL_USERNAME` | SMTP Email Address | `your.email@gmail.com` |
| `MAIL_PASSWORD` | SMTP App Password | `abcd efgh ijkl mnop` |

## 🏃‍♂️ Running Locally

1.  **Clone the repository.**
2.  **Start PostgreSQL** and create a database named `pms_db`.
3.  **Build the project:**
    ```bash
    mvn clean install
    ```
4.  **Run the application:**
    ```bash
    mvn spring-boot:run
    ```
    *The app will start on `http://localhost:8080` (default).*

## 📦 Deployment (AWS Elastic Beanstalk)

This application is configured for AWS Elastic Beanstalk (Tomcat/Java 21 Platform).

1.  **Profile:** Set `SPRING_PROFILES_ACTIVE=prod` in AWS Configuration.
2.  **Port:** Set `SERVER_PORT=5000` (Nginx default).
3.  **Database:** Ensure the EC2 security group allows traffic to the RDS instance.

## 🗄️ Database Migrations

Flyway automatically manages the schema.
*   Migration scripts are located in: `src/main/resources/db/migration/`
*   **Dev:** Runs automatically on startup.
*   **Prod:** Runs automatically on deployment (ensure DB credentials are set in AWS).

## 📄 License

Proprietary / Internal Use.
