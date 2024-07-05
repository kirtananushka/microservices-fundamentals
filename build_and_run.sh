#!/bin/bash

# Function to build a Maven project
build_service() {
  service_dir=$1
  echo "Building $service_dir..."
  cd $service_dir || exit
  mvn clean package -DskipTests || {
    echo "Failed to build $service_dir"
    exit 1
  }
  cd - || exit
}

# List of service directories
services=("eureka-svc" "resource-proc" "resource-svc" "song-svc")

# Build all services
for service in "${services[@]}"; do
  build_service $service
done

# Run compose-v2 with --build option
echo "Running compose-v2..."
docker-compose -f compose-v2.yml up --build
