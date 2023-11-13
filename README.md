# distributed-logger
## Introduction
The Distributed Logger is designed to streamline log management and sharing within an organization. It captures log outputs from various applications, facilitates their transfer through Kafka, and allows users to access logs from different sources. The motivation behind this project is to provide a centralized, efficient, and user-friendly logging system that enhances debugging and monitoring processes.

## Architecture and Design
The architecture comprises several key components:

### Bytecode Logger:
 Implements bytecode manipulation to capture and log print statements with line numbers.
### FileWatcher:
 Monitors the .dlogger directory for new log entries.
### KafkaLogger:
 Handles the production of log messages to Kafka topics.
### MainController:
 Manages the environment setup and schedules the FileWatcher.
### LogConsumer:
 Allows users to query and retrieve logs based on username and classname.

## Data Flow
Data flows as follows:

### Log Generation: 
User applications generate logs.
### Capture:
 The Bytecode Logger intercepts these logs and writes them to hidden files.
### Monitoring:
 FileWatcher detects new log entries.
### Kafka Production:
 KafkaLogger produces these logs to specific Kafka topics.
### Consumption:
 Logs are consumed and retrieved using LogConsumer.

## User Interaction
Users interact with the system by:

 Providing a username for personalized logging.
 Using the MainController to set up and run background log monitoring.
 Injecting agent.jar into their projects for log capture.
 Employing LogConsumer to fetch logs from desired sources.

## Challenges
Key challenges include:

### Scheduling: 
Ensuring efficient and timely execution of the FileWatcher.
### Coupling: 
Managing dependencies between KafkaLogger and FileWatcher.
### Duplication: 
Preventing duplicate messages in logs.
### Scalability and Performance: 
Ensuring the system can handle large volumes of log data.

 These challenges are addressed through careful design considerations and the implementation of robust software engineering practices.