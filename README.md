# Report-System



## Overview

The Report-System is a robust application designed to generate reports from log files. Leveraging hexagonal architecture and Domain-Driven Design (DDD) principles, it ensures maintainability and extensibility. 




## Usage


### Uploading a Log File for Reporting

To generate a report from a log file, use the following API endpoint.

**Endpoint:**


POST /report


**Parameters:**
- `file`: The log file you wish to process.

**Request Type:**
`multipart/form-data`

**CURL Example:**
```sh
curl -X POST -F "file=@path_to_your_file.log" http://localhost:8181/report
```

Example Response : 

```json
{
    "distinctIpsCount": 11,
    "topIps": [
        "168.41.191.40",
        "177.71.128.21",
        "50.112.0.11"
    ],
    "topEndpoints": [
        "/docs/manage-websites/",
        "/blog/2018/08/survey-your-opinion-matters/",
        "/newsletter/"
    ],
    "errorMessages": []
}
```

Notes : 

 - Replace path_to_your_file.log with the actual path to the log file you want to upload
 - This endpoint only accepts .log and .txt files. 
 - Make sure the server is running and accessible at 'http://localhost:8181/' (this is true regardless of a local run, docker or docker-compose). 

## Assumptions and Design Decisions

* Extensibility and Maintainability:
The use of hexagonal architecture and DDD was purposeful to allow for scalability and easy integration of future changes.

* ***Security***:
Input is sanitized to prevent injection attacks.

* ***Log Report***:
Reports are returned in JSON format. The system is designed not to fail due to lines with invalid data. Only generic error messages are exposed to the user, with detailed logging done server-side.

* ***Log Format***:
The system expects the IP address as the first element of each log entry and supports both IPv4 and IPv6 formats. Endpoints are expected to follow the HTTP method.

* ***Performance***:
The log processing operates with O(n) linear time complexity, optimizing the performance even as data scales.

* ***File Handling***:
The system currently accepts .log or .txt files. There's a future consideration to reject files exceeding a certain size limit.

* ***Scalability***:
Potential future enhancements include integration with message brokers like Kafka for distributed processing and database persistence for analytics and storage.



## Running the Application

### Prerequisites
Ensure you have Maven and Java 17 installed on your system to run the application locally. Docker is required for containerization.

### Installation
Clone the repository and navigate to the project directory:

### With Docker

Build and run the application using Docker : 

```
$ docker build -t report-system .
$ docker run -p 8181:8181 report-system
```
### With Docker-Compose (Recommended)
```
$ docker-compose up 
```
### Local Setup
To run the application locally without Docker : 
```
mvn clean install
java -jar report-service/report-container/target/report-container-1.0-SNAPSHOT.jar
```

