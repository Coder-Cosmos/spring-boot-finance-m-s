# Railway Deployment Guide

This guide explains how to deploy the Personal Finance Manager Spring Boot application to Railway.

## Prerequisites

1. GitHub account with your project repository
2. Railway account (sign up at https://railway.app)

## Deployment Steps

### 1. Prepare Your Repository

Ensure your repository contains:
- `pom.xml` (Maven configuration)
- `railway.toml` (Railway configuration)
- `nixpacks.toml` (Build configuration)
- `src/main/resources/application-prod.properties` (Production config)

### 2. Connect to Railway

1. Go to [Railway Dashboard](https://railway.app/dashboard)
2. Click "New Project"
3. Select "Deploy from GitHub repo"
4. Choose your repository
5. Railway will automatically detect it's a Java project

### 3. Configure Environment Variables

In Railway dashboard, go to your project → Variables tab and add:

```
SPRING_PROFILES_ACTIVE=prod
PORT=8000
```

### 4. Deploy

1. Railway will automatically build and deploy your application
2. The build process will:
   - Install Java 17 and Maven
   - Run `mvn clean install -DskipTests`
   - Run `mvn package -DskipTests`
   - Start the application with `java -jar target/personal-finance-manager-1.0.0.jar`

### 5. Access Your Application

1. Go to the "Deployments" tab in Railway
2. Click on your deployment
3. Copy the generated URL (e.g., `https://your-app-name.railway.app`)
4. Your API will be available at `https://your-app-name.railway.app/api`

## API Endpoints

- Health Check: `GET /api/health`
- Authentication: `POST /api/auth/register`, `POST /api/auth/login`
- Transactions: `GET /api/transactions`, `POST /api/transactions`
- Categories: `GET /api/categories`
- Reports: `GET /api/reports/monthly`, `GET /api/reports/yearly`
- Savings Goals: `GET /api/savings-goals`, `POST /api/savings-goals`

## Database

- H2 database is used (file-based)
- Database file is stored in the `./data/` directory
- Data persists between deployments

## Monitoring

- Railway provides built-in monitoring
- Check logs in the "Deployments" tab
- Health check endpoint: `/api/health`

## Troubleshooting

1. **Build fails**: Check Railway logs for Maven errors
2. **App won't start**: Verify PORT environment variable is set
3. **Database issues**: Ensure H2 dependency is in pom.xml
4. **Health check fails**: Verify the `/api/health` endpoint is accessible

## Local Development

To run locally with production settings:

```bash
mvn spring-boot:run -Dspring.profiles.active=prod
```

## Important Notes

- H2 database is suitable for development and small applications
- For production with multiple users, consider using PostgreSQL or MySQL
- Railway provides automatic HTTPS
- Sessions are configured for secure cookies 