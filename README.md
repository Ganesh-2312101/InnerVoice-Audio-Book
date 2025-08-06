

# InnerVoice — Audiobook Platform

**InnerVoice** is a full-stack web application that allows users to browse and listen to audiobooks online. It is built using **Spring Boot (Java)** for the backend and **HTML, Tailwind CSS, and JavaScript** for the frontend.

This project started as a simple landing page and eventually evolved into a fully functional audiobook platform. It offers a clean user interface, audio streaming, and a secure user system.

---

# Screenshots

<img width="1920" height="1024" alt="image" src="https://github.com/user-attachments/assets/dfbd6ab1-4c28-4273-99a8-9b4816e3be0a" />

<img width="1920" height="1080" alt="Screenshot (57)" src="https://github.com/user-attachments/assets/400c15fd-7ddd-426b-8d2d-8c57fc6d2341" />

<img width="1920" height="1024" alt="Screenshot (54)" src="https://github.com/user-attachments/assets/ca6f99d4-633d-4cb9-b3e4-2f190d1cb593" />

<img width="1920" height="1021" alt="Screenshot (49)" src="https://github.com/user-attachments/assets/bb7d27ab-24e6-484a-b607-65a9d701be23" />

<img width="1920" height="1028" alt="Screenshot (51)" src="https://github.com/user-attachments/assets/255c3e37-0e69-4852-bbb2-9a41003ebce3" />

<img width="1920" height="1028" alt="Screenshot (52)" src="https://github.com/user-attachments/assets/34a85664-83f1-4d83-b10b-ceee6d9d2838" />

<img width="1920" height="1031" alt="Screenshot (53)" src="https://github.com/user-attachments/assets/aa937284-6616-4e55-bd1f-44b22f067a7d" />

<img width="1920" height="1028" alt="Screenshot (50)" src="https://github.com/user-attachments/assets/7eef63d8-6bbb-4ee4-bee7-0e502d1c1664" />

<img width="1920" height="1025" alt="Screenshot (56)" src="https://github.com/user-attachments/assets/b3c95654-aa9b-43a9-b210-739c01418a54" />

## Features

- User registration and login system
- Audiobook browsing and playback
- Support for premium and free books
- Responsive design using Tailwind CSS
- Audio file integration with HTML audio player
- Backend powered by Spring Boot and MySQL
- Clean and minimal user interface

---

## Tech Stack

- **Backend:** Spring Boot, Java, JPA/Hibernate
- **Frontend:** HTML, Tailwind CSS, JavaScript
- **Database:** MySQL
- **Build Tool:** Maven

---

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/InnerVoice.git
cd InnerVoice
```
2. Configure the Database
Before launching the application, insert book records into the database manually.
Each book entry must include:

Book name

Author

Audio file name (e.g., dummy.mp3)

Premium or free flag

Ensure your database is running and the connection settings in application.properties are correct.

3. Build and Run the Project
Open the project in IntelliJ IDEA or any Java IDE, and run the application using:

./mvnw spring-boot:run

5. Access the Web Application
Visit the application at:

http://localhost:8080

Usage Instructions
On first use, sign up using your email and password.

After signing in, you can explore available audiobooks.

Audio streaming is supported directly from the web interface.

Premium content will be marked accordingly.
