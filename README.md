# 📚 Library Management System - Backend (Spring Boot)

This is the **REST API** for a Library Management System built with **Java Spring Boot**.  
It supports user authentication, book management, and loan tracking — with role-based access using JWT.

---

## ⚙️ Tech Stack

- **Java 17+**
- **Spring Boot**
- **Spring Security (JWT-based)**
- **Spring Data JPA (Hibernate)**
- **H2 Database (in-memory)**
- **Maven**

---

## 🧩 Features

### 📖 Books
- View all books
- Search by title, author, availability
- Create, update, delete (admin only)

### 👤 Users
- Register new users (requires admin approval)
- Admin can view/update/delete users
- Role-based access: `ADMIN` or `USER`
- Login returns JWT token

### 🔁 Loans
- Users can borrow/return books
- Admin can view all loan records

### ✅ Quality Assurance
- Full Controller layer testing (@WebMvcTest)
- Repository layer testing (@DataJpaTest)
- Security context and Role testing

---

## 🧪 Automated Testing
This project maintains high code coverage using **JUnit** 5 and **Mockito**.


The test suite includes:
- **Repository Tests**: Integration tests using H2 to verify database queries and custom finders.
- **Controller Tests**: Slice tests using MockMvc to verify HTTP endpoints, JSON serialization, and Input Validation.
- **Security Tests**: Verifies that endpoints are correctly protected by Roles (ADMIN vs USER) and that unauthorized access is blocked.



---

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

📌 Access H2 console (for dev):  
`http://localhost:8080/h2-console`

📌 Main API base path:  
`http://localhost:8080/api`

---

## 📝 License

This project is open-source and free to use under the [MIT License](LICENSE).

---

## 🙌 Author

Made by **Azim Maksatbek uulu**

