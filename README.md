# COMP 4442 Group Project

## Team Member and Contributions

| Name | Student ID | Core Role | Key Responsibilities |
| :--- | :--- | :--- | :--- |
| **Djung Kelly Ka Yee** | 24035008D | **Cloud Architect and DevOps** | AWS infrastructure design, Terraform IaC scripts, k3s cluster orchestration, and GitHub Actions CI/CD pipeline. |
| **Leung Shing Huen** | 24026257D | **Backend Engineer (Callee)** | Core business logic in backend services, Amazon RDS schema design, and service layer implementation. |
| **Kwan Chi Fung** | 24022267D | **Full-stack Engineer (Caller)** | UI service development, vanilla JavaScript frontend, async API integration, and S3 media management. |

---

## Project Introduction

ShareU is a social media platform built for **COMP4442 Service and Cloud Computing**. The project uses a **microservices architecture** with Spring Boot services, a gateway-based UI service, MySQL database storage, and AWS cloud deployment.

The goal is to provide a scalable platform for posting topics, commenting, reacting, reporting content, and managing users through an admin dashboard.

---

## Tech Stack

* **Backend Framework:** Spring Boot 3.x, Java 17
* **Frontend:** HTML, Bootstrap, vanilla JavaScript
* **Database:** MySQL 8.0, Flyway migrations
* **Gateway:** Spring Cloud Gateway
* **Orchestration:** k3s, Kubernetes YAML
* **Cloud Platform:** AWS EC2, RDS, S3
* **Infrastructure as Code:** Terraform
* **CI/CD:** GitHub Actions, Docker Hub

---

## System Architecture

The backend is split into independent services under the `backend` folder.

| Service | Port | Responsibility |
| :--- | :--- | :--- |
| `ui-service` | 8089 | Serves `index.html`, `admin.html`, and routes frontend API calls through Spring Cloud Gateway. |
| `auth-service` | 8081 | Handles login, registration, user identity, and password verification. |
| `topic-service` | 8082 | Handles posts, feed loading, soft delete, and topic reaction count updates. |
| `comment-service` | 8083 | Handles comments and comment soft delete. |
| `reaction-service` | 8084 | Handles like/dislike actions and syncs reaction counts to topic-service. |
| `report-service` | 8085 | Handles user reports for moderation. |
| `admin-service` | 8086 | Runs Flyway database migrations and provides admin APIs. |
| `common-shared` | N/A | Shared DTOs, constants, or reusable code for services. |

Only `admin-service` runs Flyway migrations. Other services connect to the existing schema with `spring.flyway.enabled=false`.

---

## Getting Started

### 1. Prerequisites

Install the following tools:

* Java 17
* Docker Desktop or MySQL 8.0
* PowerShell
* Git

Maven does not need to be installed globally because every backend service includes `mvnw.cmd`.

### 2. Clone the Repository

```powershell
git clone <repository-url>
cd Comp4442-group-project
```

### 3. Start MySQL

Create a local MySQL database named `shareudb`.

Default local connection used by the services:

```text
jdbc:mysql://localhost:3306/shareudb
```

The default username is usually configured as:

```text
root
```

Check each service file if your local MySQL password is different:

```text
backend/<service-name>/src/main/resources/application.properties
```

### 4. Start All Services

Run the helper script from the project root:

```powershell
.\start-all.ps1
```

The script starts `admin-service` first so Flyway can create and seed the database tables. It then starts the remaining backend services and the UI service.

### 5. Open the Application

Main app:

```text
http://localhost:8089/index.html
```

Admin dashboard:

```text
http://localhost:8089/admin.html
```

---

## Run One Service Manually

Each service can also be started by itself.

Example:

```powershell
cd backend\admin-service
.\mvnw.cmd spring-boot:run
```

Build one service:

```powershell
cd backend\topic-service
.\mvnw.cmd clean package
```

Build all backend modules:

```powershell
cd backend
mvn clean package
```

---

## Test Accounts

The project includes seeded test accounts for local development. Passwords are stored as BCrypt hashes in the database. The login password for all seeded accounts is:

```text
123456
```

Admins:

| Email | Username | Password |
| :--- | :--- | :--- |
| `kelly@admin.com` | `kelly` | `123456` |
| `kwanloan@admin.com` | `kwanloan` | `123456` |
| `cheesring@admin.com` | `cheesring` | `123456` |

Normal users:

| Email | Username | Password |
| :--- | :--- | :--- |
| `kelly_user@user.com` | `kelly_user` | `123456` |
| `kwanloan_user@user.com` | `kwanloan_user` | `123456` |
| `cheesring_user@user.com` | `cheesring_user` | `123456` |

The accounts are inserted by:

```text
backend/admin-service/src/main/resources/db/migration/V4__add_test_users.sql
```

---

## Main Features

* User registration and login
* Topic posting and feed browsing
* Comment creation and display
* Like and dislike reactions
* Report content for moderation
* Admin user management
* Admin activity view for posts, comments, deleted records, and ban history
* Soft delete for topics and comments
* Cloud deployment with EC2, k3s, RDS, and S3

---

## Cloud Deployment

Production deployment uses AWS and Kubernetes:

| Component | Usage |
| :--- | :--- |
| EC2 `t3.micro` | Runs the k3s Kubernetes node. |
| Elastic IP | Provides a stable public IP for the UI. |
| RDS MySQL 8.0 | Stores the `shareudb` database. |
| S3 | Stores uploaded media files. |
| Docker Hub | Stores service container images. |
| GitHub Actions | Builds, pushes, and restarts deployments on EC2. |

Kubernetes deployment file:

```text
infrastructure/k8s/microservices.yaml
```

Terraform file:

```text
infrastructure/main.tf
```

Production UI URL format:

```text
http://<EC2_ELASTIC_IP>:30080
```

After Terraform is applied, print the exact URL:

```powershell
cd infrastructure
terraform output -raw shareu_public_url
```

---

## CI/CD Flow

The GitHub Actions workflow is located at:

```text
.github/workflows/deploy.yml
```

Deployment flow:

1. Build Spring Boot services with Maven.
2. Build Docker images for each service.
3. Push images to Docker Hub.
4. SSH into the EC2 k3s node.
5. Update Kubernetes deployment images.
6. Run `kubectl rollout restart`.

Required GitHub Secrets:

| Secret | Description |
| :--- | :--- |
| `DOCKER_USERNAME` | Docker Hub username. |
| `DOCKER_PASSWORD` | Docker Hub password or token. |
| `EC2_HOST` | EC2 Elastic IP or host. |
| `EC2_SSH_KEY` | Private SSH key for the EC2 instance. |

---

## Useful Commands

Check service status in Kubernetes:

```bash
kubectl get pods
kubectl get services
```

Restart one deployment:

```bash
kubectl rollout restart deployment shareu-ui
```

Apply production Kubernetes YAML:

```bash
kubectl apply -f infrastructure/k8s/microservices.yaml
```

Run Terraform:

```bash
cd infrastructure
terraform init
terraform plan
terraform apply
```

---

## Troubleshooting

### Login Returns 500

Start `admin-service` first and confirm Flyway migrations completed. The user table and seeded test accounts are created by `admin-service`.

### UI Cannot Reach API

Confirm all backend services are running and that `ui-service` is running on port `8089`.

### Database Connection Fails

Check the MySQL username, password, and database name in each `application.properties` file. For cloud deployment, check the `RDS_ENDPOINT` and Kubernetes Secret values in `microservices.yaml`.

### New Docker Image Does Not Appear

Confirm `imagePullPolicy: Always` is set in `microservices.yaml`, then restart the deployment:

```bash
kubectl rollout restart deployment <deployment-name>
```

---

## More Documentation

Detailed architecture notes are available in:

```text
ARCHITECTURE.md
```
