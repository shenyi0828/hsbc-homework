#!/bin/bash
cd "$(dirname "$0")/.."

echo "Building backend JAR package..."
mvn -f backend/pom.xml clean package -DskipTests

echo "Building Docker images..."
podman build -t transaction-management/backend:latest backend/
podman build -t transaction-management/backend:1.0.0 backend/

podman build -t transaction-management/frontend:latest frontend/
podman build -t transaction-management/frontend:1.0.0 frontend/

echo "Docker images built successfully!"