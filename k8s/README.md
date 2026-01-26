# Kubernetes Deployment Guide for FinPulse Engine

This guide explains how to deploy the FinPulse Engine Spring Boot application to Kubernetes.

## Prerequisites

- Docker installed
- Kubernetes cluster (minikube, kind, EKS, GKE, AKS, etc.)
- kubectl configured to access your cluster
- PostgreSQL database accessible from the cluster

## Files Overview

- `Dockerfile` - Multi-stage Docker build for the Spring Boot application
- `k8s/configmap.yml` - Non-sensitive configuration
- `k8s/secret.yml` - Sensitive credentials (JWT secret, database credentials)
- `k8s/deployment.yml` - Application deployment with 2 replicas
- `k8s/service.yml` - ClusterIP and LoadBalancer services

## Deployment Steps

### 1. Build the Docker Image

```bash
# Build the image
docker build -t finpulse-engine:latest .

# For Minikube, use the Minikube Docker daemon
eval $(minikube docker-env)
docker build -t finpulse-engine:latest .

# For cloud registries (example with Docker Hub)
docker tag finpulse-engine:latest your-registry/finpulse-engine:latest
docker push your-registry/finpulse-engine:latest
```

### 2. Update Secret Configuration

**Important:** Before deploying, update `k8s/secret.yml` with your actual credentials:

```bash
# Edit the secret file
vi k8s/secret.yml

# Update these values:
# - JWT_SECRET: Your actual JWT secret
# - SPRING_DATASOURCE_URL: Your PostgreSQL connection string
# - SPRING_DATASOURCE_USERNAME: Your database username
# - SPRING_DATASOURCE_PASSWORD: Your database password
```

### 3. Deploy to Kubernetes

```bash
# Apply ConfigMap
kubectl apply -f k8s/configmap.yml

# Apply Secret
kubectl apply -f k8s/secret.yml

# Apply Deployment
kubectl apply -f k8s/deployment.yml

# Apply Service
kubectl apply -f k8s/service.yml
```

### 4. Verify Deployment

```bash
# Check deployment status
kubectl get deployments

# Check pods
kubectl get pods -l app=finpulse-engine

# Check services
kubectl get services

# View logs
kubectl logs -f deployment/finpulse-engine

# Describe a pod for more details
kubectl describe pod <pod-name>
```

### 5. Access the Application

**ClusterIP Service (internal access):**
```bash
# Port forward to access locally
kubectl port-forward service/finpulse-engine-service 8055:8055

# Access at http://localhost:8055
```

**LoadBalancer Service (external access):**
```bash
# Get external IP (may take a few minutes)
kubectl get service finpulse-engine-lb

# For Minikube
minikube service finpulse-engine-lb
```

## Health Checks

The application exposes health endpoints:
- Liveness: `http://<service-ip>:8055/actuator/health`
- Readiness: `http://<service-ip>:8055/actuator/health`

Note: You may need to add Spring Boot Actuator dependency if not already present.

## Scaling

```bash
# Scale up/down
kubectl scale deployment finpulse-engine --replicas=3

# Auto-scaling (HPA)
kubectl autoscale deployment finpulse-engine --cpu-percent=70 --min=2 --max=10
```

## Update Deployment

```bash
# Update image
kubectl set image deployment/finpulse-engine finpulse-engine=finpulse-engine:v2

# Or edit deployment directly
kubectl edit deployment finpulse-engine

# Rollback if needed
kubectl rollout undo deployment/finpulse-engine
```

## Troubleshooting

```bash
# Check pod events
kubectl describe pod <pod-name>

# View logs
kubectl logs <pod-name>
kubectl logs <pod-name> --previous  # Previous container logs

# Execute commands in pod
kubectl exec -it <pod-name> -- /bin/sh

# Check resource usage
kubectl top pods
kubectl top nodes
```

## Database Setup

Make sure your PostgreSQL database is accessible from the cluster. Options:

1. **External Database:** Update `SPRING_DATASOURCE_URL` in `secret.yml`
2. **In-cluster Database:** Deploy PostgreSQL in the same cluster
3. **Cloud Database:** Use managed services (RDS, Cloud SQL, Azure Database)

## Production Considerations

1. **Secrets Management:**
   - Use Kubernetes Secrets with encryption at rest
   - Consider using external secret managers (AWS Secrets Manager, HashiCorp Vault)
   - Never commit actual secrets to version control

2. **Resource Management:**
   - Adjust CPU/memory requests and limits based on load testing
   - Monitor resource usage and adjust accordingly

3. **High Availability:**
   - Use at least 2 replicas
   - Configure pod anti-affinity for distribution across nodes
   - Set up proper health checks

4. **Monitoring:**
   - Add Prometheus metrics
   - Set up logging aggregation (ELK, Loki)
   - Configure alerts for critical metrics

5. **Ingress:**
   - Use Ingress controller for production traffic routing
   - Configure TLS/SSL certificates
   - Set up rate limiting and WAF

6. **Database Migrations:**
   - Use Flyway or Liquibase for schema management
   - Run migrations as init containers or separate jobs

## Clean Up

```bash
# Delete all resources
kubectl delete -f k8s/

# Or delete individually
kubectl delete deployment finpulse-engine
kubectl delete service finpulse-engine-service finpulse-engine-lb
kubectl delete configmap finpulse-engine-config
kubectl delete secret finpulse-engine-secret
```
