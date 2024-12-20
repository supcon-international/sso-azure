# Teams App: Backend-Frontend Separation

This project implements a Microsoft Teams application using Java 17 and Spring Boot for the backend, coupled with a single-page application (SPA) frontend. The app features user authentication, push messaging, and displays hierarchical and channel data. This README provides an in-depth code analysis, file descriptions, and insights into the development of a Teams app with backend-frontend separation and SPA architecture.

---

## Table of Contents
1. [Overview](#overview)
2. [Code Structure](#code-structure)
3. [Backend Implementation](#backend-implementation)
4. [Frontend Implementation](#frontend-implementation)
5. [Developing Teams Apps](#developing-teams-apps)
6. [Registering and Configuring the App in Azure AD](#registering-and-configuring-the-app-in-azure-ad)
7. [Best Practices](#best-practices)

---

## Overview

The Teams App supports key functionalities:

- **Push Messaging**: Sending messages to specific users or channels.
- **User Data Display**: Viewing user details, organizational hierarchy, and associated Teams and channels.
- **Authentication**: Integration with Azure AD for OAuth-based login.

---

## Code Structure

### Key Directories and Files

#### Backend

- `src/main/java/com/sib/SsoAzureApplication.java`: The main entry point for the Spring Boot application.
- `src/main/java/com/sib/controller/`
  - `HealthController.java`: A health check endpoint to ensure the service is running.
  - `AuthController.java`: Handles user authentication and token management.
- `src/main/java/com/sib/service/`
  - `AuthService.java`: Encapsulates business logic related to authentication.
  - `TokenStorageService.java`: Manages temporary storage and retrieval of tokens.
- `src/main/java/com/sib/dto/`
  - `AuthRequest.java`: Data Transfer Object (DTO) for authentication requests.
  - `MessageRequest.java`: DTO for push message requests.
  - `TestLombok.java`: Demonstrates the usage of Lombok annotations for concise code.

#### Frontend

- `src/main/resources/static/`
  - `index.html`: Displays user information, hierarchy, and channel details.
  - `push.html`: Handles the push message feature.
  - `auth-end.html`: Handles post-authentication redirection and token display.

#### Packaging

- `src/main/resources/packaging/`: Contains the Teams app manifest and assets (e.g., icons and `manifest.json`).

---

## Backend Implementation

### Spring Boot Framework

The backend leverages Spring Boot for its simplicity and extensive ecosystem. Key highlights include:

1. **Controllers**:
   - `AuthController`: Exposes endpoints for authentication and token management.
   - `HealthController`: Simple endpoint for monitoring service health.

2. **Services**:
   - `AuthService`: Handles business logic for validating users and retrieving tokens from Azure AD.
   - `TokenStorageService`: Provides in-memory token storage for quick access during user sessions.

3. **DTOs**:
   - Encapsulate data sent between the frontend and backend.

4. **Security**:
   - Implements OAuth 2.0 to secure API endpoints.

### Dependencies

The `pom.xml` file manages dependencies like:

- Spring Boot Starter Web
- Spring Security OAuth2
- Lombok
- JSON Processing Libraries

---

## Frontend Implementation

The frontend consists of static HTML, CSS, and JavaScript files served by Spring Boot. Key features:

1. **Single-Page Architecture**:
   - The app dynamically updates content without reloading the page.
   - Uses SPA requests to fetch and display data.

2. **HTML Files**:
   - `index.html`: Fetches and displays user and channel data using backend APIs.
   - `push.html`: Provides a form for composing and sending push messages.
   - `auth-end.html`: Manages post-login actions, including token retrieval.

3. **Styling and Scripts**:
   - CSS and JavaScript files are embedded or linked to handle UI and API interactions.

---

## Developing Teams Apps

### Registration and Integration

1. **Register the App**:
   - Use the Microsoft Teams Developer Portal to register the app.
   - Configure the app manifest and icons in `packaging/`.

2. **Authentication**:
   - Integrate with Azure AD for secure user login.
   - Use the OAuth 2.0 flow for acquiring tokens.

3. **Microsoft Graph API**:
   - Access Teams, channel, and user data.
   - Send messages using the `push.html` interface.

---

## Registering and Configuring the App in Azure AD

1. **Create an App Registration**:
   - Go to the Azure portal and navigate to "Azure Active Directory" > "App registrations".
   - Click "New registration" and provide the app name, supported account types, and redirect URI (e.g., `https://your-app-url/auth-end.html`).

2. **Configure API Permissions**:
   - In the "API permissions" section, add the required Microsoft Graph permissions (e.g., `User.Read`, `ChannelMessage.Send`).
   - Grant admin consent for the requested permissions.

3. **Add a Client Secret**:
   - Go to the "Certificates & secrets" section.
   - Create a new client secret and note the value. Use this in your application properties.

4. **Update Application Settings**:
   - Configure the `application.properties` file in your Spring Boot application:
     ```properties
     azure.client-id=<your-client-id>
     azure.client-secret=<your-client-secret>
     azure.redirect-uri=https://your-app-url/auth-end.html
     azure.tenant-id=<your-tenant-id>
     ```

5. **Test the Integration**:
   - Ensure the app can authenticate users and access Teams data using the Microsoft Graph API.

---

## Best Practices

1. **Backend-Frontend Separation**:
   - Keep business logic isolated in the backend.
   - Serve static resources through a dedicated directory.

2. **Security**:
   - Always use HTTPS.
   - Implement proper error handling and input validation.

3. **Scalability**:
   - Leverage cloud services for hosting and scaling.

4. **Testing**:
   - Test APIs independently using tools like Postman.
   - Validate UI functionality across devices.

---


