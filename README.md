# Serch Server
Welcome to the Serch Backend Server! This server powers the core functionality of the Serch platform, providing robust authentication, user management, session handling, and more. Below is an extensive guide to help you navigate through the various components and features of the backend server.

## Table of Contents
1. [Introduction](#introduction)
2. [Features](#features)
3. [Technologies Used](#technologies-used)
4. [Installation](#installation)
5. [Usage](#usage)
6. [Documentation](#documentation)
7. [Contributing](#contributing)
8. [License](#license)

> [!NOTE]
> Useful information that users should know, even when skimming content.

> [!TIP]
> Helpful advice for doing things better or more easily.

> [!IMPORTANT]
> Key information users need to know to achieve their goal.

> [!WARNING]
> Urgent info that needs immediate user attention to avoid problems.

> [!CAUTION]
> Advises about risks or negative outcomes of certain actions.

## Introduction
The Serch Backend Server is a critical component of the Serch platform, designed to handle user authentication, session management, and various backend functionalities. It serves as the backbone of the Serch ecosystem, facilitating seamless interactions between users, data, and services.

## Features
- **Authentication**: Provides robust authentication mechanisms for user login, signup, and password reset.
- **Session Management**: Manages user sessions, including session creation, refreshing, validation, and revocation.
- **User Management**: Handles user profile management, including profile creation, update, and verification.
- **Token Generation**: Generates secure tokens, including OTP (One-Time Passwords) and refresh tokens.
- **Error Handling**: Implements comprehensive error handling to ensure system reliability and user feedback.
- **Extensible Architecture**: Built with extensibility in mind, allowing for easy integration with other services and components.

## Technologies Used
- **Java**: Primary programming language for backend development.
- **Spring Framework**: Utilized for dependency injection, MVC architecture, and RESTful web services.
- **Spring Security**: Provides authentication and authorization features.
- **JWT (JSON Web Tokens)**: Used for stateless authentication and session management.
- **Hibernate**: Object-relational mapping (ORM) framework for database interaction.
- **PostgreSQL**: Relational database management system for storing user data.
- **Maven**: Dependency management tool for building and managing Java projects.

## Installation
To install and run the Serch Backend Server locally, follow these steps:

1. Clone the repository to your local machine.
2. Navigate to the project directory.
3. Set up a PostgreSQL database and configure the database connection in the application properties.
> List of Schemas:
> - create schema if not exists company
> - create schema if not exists platform
> - create schema if not exists conversation 
> - create schema if not exists account 
> - create schema if not exists identity 
> - create schema if not exists sharing 
> - create schema if not exists subscription 
> - create schema if not exists verified 
> - create schema if not exists pricing
4. Build the project using Maven: `mvn clean install`.
5. Run the application: `java -jar target/serch-backend-server.jar`.

## Usage
Once the server is up and running, you can interact with it using HTTP requests. The server exposes various endpoints for authentication, user management, session handling, etc. Refer to the API documentation for detailed information on available endpoints and their usage.

## Documentation
- [API Documentation](#) (Coming soon): Comprehensive documentation of all endpoints and their functionalities.
- [Codebase Documentation](#) (Coming soon): Detailed documentation of the server's codebase, including classes, interfaces, and methods.

## Contributing
Contributions to the Serch Backend Server are welcome! Whether it's bug fixes, feature enhancements, or documentation improvements, feel free to contribute to make the platform even better. Please refer to the [contribution guidelines](CONTRIBUTING.md) for more information.

## License
The Serch Backend Server is licensed under the [MIT License](LICENSE), allowing for free use, modification, and distribution.

---

Thank you for choosing the Serch Backend Server! If you have any questions, feedback, or suggestions, please don't hesitate to reach out. Happy coding!

## Environment Variables
```markdown
    Environment variables:

    ACCOUNT_DURATION=6
    AGORA_APP_ID=a1197db8b1a04d22bf83410a38679423
    AWS_BUCKET_PLATFORM=platform-store
    AWS_BUCKET_SERCH_ASSET=serch-asset-store
    AWS_REGION=eu-west-1
    AWS_SECRET_ACCESS_KEY=GG7dzJPMwzxcXk0D3NH4vOVmwVFp0j15Ppb+2mgG
    AWS_SECRET_KEY=AKIAV3ZJZGEVN3UKJO3T
    DB_URL=jdbc:postgresql://aws-0-us-east-1.pooler.supabase.com:5432/postgres?user=postgres.fwnjtqieqvuowbuschhs&password=iamEvaristus
    FUND_WALLET_AMOUNT_LIMIT=2000
    JWT_EXPIRATION_TIME=604800000
    JWT_SECRET_KEY=35d82b3737b72bcc9f56308110a36b60631dd2d6597b3f21f7363dde9a8c294cd79640b8ea4f687f9b31a132e4cd947a433d504f68ffa808851ade5b4da49d69
    MAIL_HOST=smtp.resend.com
    MAIL_PASSWORD=re_TuXmeHo8_JDiKoa3mC49bbmLU3Qa73gjv
    MAIL_PORT=465
    MAIL_USERNAME=resend
    MAP_API_KEY=a1197db8b1a04d22bf83410a38679423
    MAP_SEARCH_RADIUS=5000
    OTP_EXPIRATION_TIME=15
    OTP_TOKEN_CHARACTERS=0123456789
    OTP_TOKEN_LENGTH=6
    PS_LIVE_PUBLIC=pk_test_b9c750d818729acca2efc6fc3c9ef7b046526f4c
    PS_LIVE_SECRET=sk_test_f4e8f0f4b5d546d2f2bcde383076a45fb3baa7d8
    PS_TEST_PUBLIC=pk_test_b9c750d818729acca2efc6fc3c9ef7b046526f4c
    PS_TEST_SECRET=sk_test_f4e8f0f4b5d546d2f2bcde383076a45fb3baa7d8
    REFRESH_TOKEN_CHARACTERS=0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz
    REFRESH_TOKEN_LENGTH=64
    RESEND_API_KEY=re_TuXmeHo8_JDiKoa3mC49bbmLU3Qa73gjv
    SERVER_PORT=8080
    SPECIALTY_LIMIT=5
    TIP2FIX_CALL_LIMIT=2000
    WITHDRAW_AMOUNT_LIMIT=5000
```