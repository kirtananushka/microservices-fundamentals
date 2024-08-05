# ELK

1. Configuring logging in applications to send logs to the new added log storage:

    - Added the ELK (Elasticsearch, Logstash, Kibana) stack to the Docker Compose file. This provides a centralized log
      storage and visualization solution.

    - For each Spring Boot application, updated the `application.yml` file to include Logback configuration. This
      directs the application to use a custom Logback configuration file.

    - Created a `logback-spring.xml` file for each service. This file configures Logback to send logs to Logstash using
      a TCP appender.

    - Added the Logstash appender dependency to each service's `pom.xml` file. This provides the necessary classes to
      send logs to Logstash.

2. Gathering logs from services, transferring and persisting them into data storage:

    - The `logback-spring.xml` configuration sets up a Logstash TCP appender. This appender sends logs from each service
      to Logstash.

    - Logstash, as configured in the `logstash.conf` file, receives these logs on port 5000.

    - The Logstash pipeline processes the incoming logs. It can parse, filter, and transform them as needed.

    - Finally, Logstash sends the processed logs to Elasticsearch for storage.

    - Elasticsearch acts as the persistent data storage for all the logs.

    - Kibana, connected to Elasticsearch, allows for visualization and exploration of the stored logs.

This setup ensures that logs are gathered from all services, transferred to a central location (Logstash), and then
persisted in a searchable and queryable format in Elasticsearch. The entire process is automated and runs within the
Docker environment, making it consistent across all services in your microservices architecture.