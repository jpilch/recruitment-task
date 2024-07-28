# Recruitment task application

This application provides an API to list all GitHub repositories for a given username. It fetches the repository details including branch names and their last commit SHA.

## Features

- List all non-fork GitHub repositories for a given username.
- Fetch repository name, owner login, branch names, and last commit SHA.
- Handle non-existing GitHub users with a proper 404 response.

## Usage

#### Request

```
GET /{username}
Accept: application/json
```

#### Response

```json
[
    {
        "repositoryName": "jpilch",
        "ownerLogin": "recruitment-task",
        "branches": [
            {
                "name": "master",
                "lastCommitSha": "9d40e4b"
            }
        ]
    }
]
```

In case of non existing user the API will return an error response

```json
{
    "status": 404,
    "message": "User not found"
}
```

## Installation

1. Clone the repository: 
    ```shell
    git clone git@github.com:jpilch/recruitment-task.git
    cd recruitment-task
    ```
2. Paste your GitHub access token in ```src/main/resources/application.properties``` in place of ```<your-key>```.

2. Build the application using Maven wrapper.

   ```shell
   ./mvnw clean install
   ```

3. Run the application.

    ```
    ./mvnw spring-boot:run
    ```


## Technologies used:

 - Java 21
 - Spring Boot 3.3.2
 - Spring WebFlux
 - Wiremock
 - Mapstruct
 - Instancio
 - Project Reactor
 - Maven
