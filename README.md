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

---

## 🔐 Security

- JWT-based authentication
- Role-based authorization
- Unapproved users cannot log in

---

## 🧪 Testing the API

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

