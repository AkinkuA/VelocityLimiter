# Velocity Limiter

Velocity Limiter is a mini project that implements a rate limiter for processing load attempts. The application exposes 
REST API endpoints to process individual load attempts and process files containing multiple load attempts.

## Table of Contents

- [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
- [Running the Application](#running-the-application)
    - [Endpoints](#Endpoints)
    - [Using with Postman](#Using-with-Postman)
- [Testing](#testing)
- [Production Considerations](#production-considerations)
- [Trade-offs and Compromises](#trade-offs-and-compromises)
- [Conclusion](#conclusion)

## Getting Started

### Prerequisites

- [Java 11](https://www.oracle.com/ca-en/java/technologies/downloads/#jdk20-linux)
- Maven

### Installation

1. Clone the repository:
    ```console
   git clone https://github.com/AkinkuA/VelocityLimiter.git
    ```
2. Navigate to the project root directory:
    ```console
   cd VelocityLimiter
    ```
3. Build the project:
    ```console
   ./mvnw clean install
    ```
## Running the Application

To run the application locally:
```console
   ./mvnw spring-boot:run
   ```
The RESTful API will be available at http://localhost:8080.

When the application is started a HyperSQL Database Manager Tool will be started as well. This can be used to interact
with the Database. Keep in mind closing the Database Manager will stop the application as well.

### Endpoints

The following endpoints are exposed:
1. `POST /load_attempts` : Processes a single load attempt and returns the result.
   
   Request Body:
   ```json
      {
        "id": 1,
        "customer_id": 1,
        "load_amount": "$123.45",
        "time": "2023-05-06T12:34:56Z"
      }
   ```
   Response:
   ```json
      {
        "id": "1",
        "customer_id": "1",
        "accepted": true
      }
   ```
   
2. `POST /load_attempts/process-file` : Processes a file containing multiple load attempts and returns the result.

Request Body:
```console
   Content-Type: multipart/form-data
   ```

| Key      | Value |
| ----------- | ----------- |
| file      | (input file.txt)      |

Response:
   ```json
      {
        "message": "File uploaded check 'output_folder' for result. File name is output_yyyyMMddHHmmss.txt"
      }
   ```

### Using with Postman

To access the endpoints using [Postman](https://www.postman.com/)

1. Download and install Postman on your computer.
2. Ensure the application is running on your local machine.
3. Select the desired endpoint in Postman and provide the necessary parameters.
4. Click "Send" to make the request, and observe the response.

## Testing

To test the implementation, a combination of unit tests and integration tests were used. Unit tests were written for 
service and controller layers, while integration tests were used to test the overall functionality.

To run the tests, execute:

```console
   ./mvnw test
   ```

Keep in mind the integration test takes a bit more time to run than the unit tests

To verify the correctness of the implementation, we tested the application by uploading various files and
validating the response attributes using Postman 

## Production Considerations

If this application were destined for a production environment, the following changes would be made:

1. Improve security by implementing authentication and authorization mechanisms.
2. Try to increase the code coverage (to a minimum of 90%)
3. Add more extensive input validation and error handling to ensure the system behaves gracefully under different circumstances.
4. Optimize performance by implementing caching, connection pooling, and other best practices.
5. Scale the application horizontally by using load balancing and clustering.
6. Containerize the application using Docker or a similar technology for easier deployment and scalability.

## Trade-offs and Compromises

Due to time constraints, the following compromises were made:

1. Simplified error handling: The application could benefit from more granular error handling and custom error messages.
2. No security: The current implementation lacks authentication and authorization, which would be essential in a real-world application.
3. Limited input validation: The application could be more strict and specific in terms of input validation.

Despite these compromises, the core functionality of the application is complete, and it can be used as a starting point 
for further improvements and additions.

## Conclusion
The Velocity Limiter mini project demonstrates the implementation of a rate limiter for processing load attempts. The 
application uses REST API endpoints to process individual load attempts and files containing multiple load attempts. 
The project includes a combination of unit and integration tests to ensure the correct functioning of the application. 
By following the steps in the Getting Started section, you can easily set up and test the project locally. However, for 
production use, some improvements and changes should be made to ensure the security, reliability, and scalability of 
the application.
