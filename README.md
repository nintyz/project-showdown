# Project Showdown

## Table of Contents
1. [Introduction](#introduction)
2. [DB Schema](#db-schema)
3. [Technologies Used](#technologies-used)
4. [Running the Application Locally](#running-the-application-locally)
5. [License](#license)

## Introduction
Welcome to G2T5's Tennis Tournament Management System - Project Showdown! Project Showdown is a full-stack application that utilises Springboot for backend and Vue.JS for frontend, seamlessly integrated through Docker for streamlined development and deployment.

Members:
1. Yap Seng How Coben (Product Owner, Developer)
2. Aaron Poh Ji Teck (Developer)
3. Leo Cong Liam Andre (Scrum Master, Developer)
4. Peh Siew Yu (Developer)
5. Pauliine Chew Gan Enn (Developer)
6. Arthur Chan Yeat Fuen (Developer)

## Workload Distribution
1. **Coben:** 
* Worked on various backend logics including player, match and tournament services  
2. **Aaron:** 
* Configured authentication and security for the webapp, and helped deploy the app  
3. **Andre:** 
* Built frontend pages (e.g. landing page) and worked on the chatbot
4. **Siew Yu:** 
* Worked on backend logic for email notifications and integration with frontend for notifications  
5. **Pauline:** 
* Designed and built the frontend of the webapp, including integration with the backend  
6. **Arthur:** 
* Worked on matchmaking logic and testing, and its implementation in backend  

## DB Schema
![Database Diagram](drawSQL-image-export-2024-11-20.png)
[View Database Diagram](https://drawsql.app/teams/showdown/diagrams/showdown)

## Technologies Used
- Backend:
    - Java 17
    - Spring Boot 3.x
    - Spring Data JPA
    - Spring Security
- Frontend:
    - Vue.js
    - CSS
    - Bootstrap
- Database:
    - Firebase Firestore
- DevOps:
    - Docker version 27.2.0, build 3ab4256
    - Docker Compose

## Running the Application Locally
For front-end development:
1. Navigate to `frontend` directory
2. Run `npm install`
3. Start the server with `npm run serve`

For back-end development
1. Navigate to `backend` directory
2. Run `mvn spring-boot:run`

## License 
This project is licensed under the [MIT License](LICENSE).