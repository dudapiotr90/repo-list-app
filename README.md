# Overview

This application provides a REST Api to interact with GitHub Api. It allows to list all user's GitHub 
repositories, excluding forks.

## Getting Stated

### Prerequisites

Before you begin, ensure you have the following installed:

- Java 21 Development Kit (JDK)
- Gradle 8.5

```bash
git clone https://github.com/dudapiotr90/repo-list-app.git
```

### Build and Run with Gradle

```bash
cd repo-list-app
gradlew build
gradlew bootRun
```

## Documentation

To access the Swagger UI and explore API: [Swagger-UI](http://localhost:8080/api/swagger/swagger-ui/index.html).  
To access OpenAPI json file use: [OpenApi 3 JSON](http://localhost:8080/api/v3/api_docs).

## Server URL

The API is hosted at [Local-host](http://localhost:8080/api).

## Endpoints

### Get Repositories

- **Endpoint**: `/repos/{username}`
- **Method**: GET
- **Summary**: Get repositories
- **Description**: List all user GitHub repositories, excluding forks.
- **Operation ID**: getNonForkRepositories
- **Parameters**:
    - **Accept** (header, required) - Type: string, Enum: ["application/json"]
    - **username** (path, required) - Owner of the GitHub account, Type: string

#### Responses

- **200 OK**
    - Content: application/json
    - Schema: [UserRepository](#userrepository)
- **400 BAD REQUEST**
    - Content: application/json
    - Schema: [ErrorMessage](#errormessage)
- **404 NOT FOUND**
    - Content: application/json
    - Schema: [ErrorMessage](#errormessage)

## Data Response Models

### UserRepository

```json
{
"repositoryName": "string",
"ownerLogin": "string",
"branches": [
    {
    "name": "string",
    "lastCommitSha": "string"
    }
]
}
```

### Branch

```json
{
"name": "string",
"lastCommitSha": "string"
}
```

### ErrorMessage

```json
{
  "status": "integer",
  "message": "string"
}
```

## API Consumed

Application consumes: [GitHub API](https://developer.github.com/v3). 

## Restrictions

Application accepts only 60 requests per hour due to github restriction policy: [Policy](https://docs.github.com/en/rest/using-the-rest-api/rate-limits-for-the-rest-api?apiVersion=2022-11-28#primary-rate-limit-for-unauthenticated-users).

## Technologies used

### Development

- Java21
- Spring
- Lombok
- jsonschema2pojo
- springdoc-openapi

### Testing

- Mockito
- Wiremock
- RestAssured
- AssertJ

## Contact

- **Author**: Piotr
- **Email**: dudapiotr90@gmail.com

