# 📚 Library Management System - Backend (Spring Boot)

This is the **REST API** for a Library Management System built with **Java Spring Boot**.  
It supports user authentication, book management, and loan tracking — fully secured with JWT role-based access.

The project is **deployed live on AWS** and includes a full CI/CD pipeline with automated testing.

### 🚀 **[View Live API Documentation (Swagger UI)](http://13.53.36.164/swagger-ui/index.html)**

---

## ⚙️ Tech Stack

- **Java 17+**
- **Spring Boot 3**
- **Spring Security** (JWT Resource Server)
- **Spring Data JPA** (Hibernate)
- **H2 Database** (In-memory)
- **Maven**
- **JUnit 5 & Mockito** (Testing)
- **Docker & Docker Hub** (Containerization)
- **AWS EC2** (Cloud Deployment)
- **GitHub Actions** (CI/CD)
- **OpenAPI / Swagger** (API Documentation)

---

## 🧩 Features

### 📖 Books
- View all books
- Search by title, author, and availability
- Create, update, delete (Admin only)

### 👤 Users
- Register new users (requires Admin approval)
- Admin can view/update/delete users
- Role-based access: `ADMIN` or `USER`
- Secure Login returning a JWT token

### 🔁 Loans
- Users can borrow and return books
- **Smart Security:** Users can only view their own loans; Admins can view all.
- History tracking of returned books

---

## 📄 API Documentation

The API is fully documented using **Swagger UI (OpenAPI)**. You can explore endpoints and test them directly in the browser.

👉 **[Go to Swagger UI](http://13.53.36.164/swagger-ui/index.html)**

> **Tip:** To test secured endpoints in Swagger:
> 1. Login using the `/auth/login` endpoint to get a Token.
> 2. Click the **Authorize** button at the top of the Swagger page.
> 3. Paste the token as: `Bearer <your-token>`.

---

## 🚀 DevOps & Deployment

This project uses a fully automated **CI/CD Pipeline** using **GitHub Actions**.

### The Pipeline Workflow:
1.  **Push to Main:** Code is pushed to the GitHub repository.
2.  **Automated Testing:** `mvn test` runs Unit and Integration tests.
3.  **Build & Package:** Maven builds the JAR file.
4.  **Containerization:** A Docker image is built and pushed to **Docker Hub**.
5.  **Deployment:** GitHub Actions connects to **AWS EC2** via SSH, pulls the latest image, and restarts the container.

---

## 🧪 Automated Testing

The project maintains high code coverage using **JUnit 5** and **Mockito**.

The test suite includes:
- **Repository Tests:** Integration tests using H2 to verify database queries.
- **Controller Tests:** Slice tests using `MockMvc` for endpoints and validation.
- **Security Tests:** Verification of Role-based access control (RBAC).

Run tests locally:
```bash

mvn test
```


## 🔐 Security Architecture
- Authentication: Stateless JWT (JSON Web Token)
- Authorization: Method-level security (@PreAuthorize, @PostAuthorize)
- Protection: - Passwords are encrypted using BCrypt.
    - Unapproved users are locked out until Admin approval.
    - Users cannot access data belonging to others.

---



## 🔌 API Endpoints & Usage

You can use **Postman** or **cURL**:

### Register:

```json
POST /auth/register
{
  "name": "Test User",
  "email": "test@example.com",
  "password": "password123"
}
```

### Login:

```json
POST /auth/login
{
    "email": "admin@example.com",
    "password": "adminPass123"
}
```

💡 This returns a JWT. Include it in the `Authorization` header:

```
Authorization: Bearer <your-token>
```

---

## 🛠 How to Run

```bash
# From the root of the project
mvn spring-boot:run
```


📌 Main API base path:  
`http://localhost:8080/api`

OR

`http://13.53.36.164/api`


---

## 📝 License

This project is open-source and free to use under the [MIT License](LICENSE).



