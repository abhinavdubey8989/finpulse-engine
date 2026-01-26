# Docker Compose Local Development Guide

This guide explains how to run the FinPulse Engine locally using Docker Compose.

## Prerequisites

- Docker and Docker Compose installed
- PostgreSQL running (either locally or in a separate Docker container on port 5431)
- Port 8055 available on your machine

## Quick Start

### 1. Start Services

```bash
# Start the application
docker-compose up -d

# View logs
docker-compose logs -f

# View logs for the application
docker-compose logs -f finpulse-engine
```

### 2. Verify Services

```bash
# Check running containers
docker-compose ps

# Test the application
curl http://localhost:8055/health
```
### 3. Stop Services

```bash
### 3. Stop Services

```bash
# Stop container (keeps image)
docker-compose stop

# Stop and remove container
docker-compose down
```

## Customizing JAVA_OPTS

You can pass custom JVM options when starting the container:

### Using inline environment variable

```bash
# Set JAVA_OPTS when starting
JAVA_OPTS="-Xmx1024m -Xms512m -XX:+UseG1GC" docker-compose up -d

# For debugging (exposes debug port 5005)
JAVA_OPTS="-Xmx512m -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005" docker-compose up -d
```

### Editing docker-compose.yml

Modify the `JAVA_OPTS` environment variable directly in `docker-compose.yml`:

```yaml
environment:
  JAVA_OPTS: -Xmx2048m -Xms1024m -XX:+UseG1GC
```

## Common JAVA_OPTS Examples

```bash
# Increase memory
JAVA_OPTS="-Xmx2048m -Xms1024m"

# Enable debugging on port 5005
JAVA_OPTS="-Xmx512m -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"

# Use G1 garbage collector
JAVA_OPTS="-Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# Enable JMX monitoring
JAVA_OPTS="-Xmx1024m -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9090 -Dcom.sun.management.jmxremote.authenticate=false"

# Set active profile
JAVA_OPTS="-Xmx512m -Dspring.profiles.active=dev"

# Combine multiple options
JAVA_OPTS="-Xmx1024m -Xms512m -XX:+UseG1GC -Dspring.profiles.active=dev -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
```

## Rebuilding After Code Changes

```bash
# Rebuild and restart application
docker-compose up -d --build finpulse-engine

# Force complete rebuild (no cache)
docker-compose build --no-cache finpulse-engine
docker-compose up -d
```

### Port already in use

```bash
# Check what's using the port
lsof -i :8055

# Kill the process or change port in docker-compose.yml
```

### Clean slate restart

```bash
# Remove container and start fresh
docker-compose down
docker-compose up -d --build
```