# 🚀 CI/CD Setup Guide - Smart Emergency Traffic Control System

## 📋 What's in Place Now?

### Current CI Pipeline (`.github/workflows/ci.yml`)
Your GitHub Actions workflow now has **two jobs**:

#### 1️⃣ **BUILD (Continuous Integration)**
- ✅ Triggers on every push to `main` branch
- ✅ Runs on Ubuntu latest
- ✅ Java 17 environment setup
- ✅ Builds project with Maven (`mvn clean install`)
- ✅ Runs tests (`mvn test`)
- ⏱️ Takes ~2-3 minutes to complete

#### 2️⃣ **DEPLOY (Continuous Deployment)**
- ✅ Runs **only after** successful build
- ✅ Only triggers on `main` branch pushes (not PRs)
- ✅ Builds Docker image with multi-stage build
- ✅ Pushes to Docker Hub registry
- ✅ Deploys to chosen platform (Railway/Render/Azure)

---

## 📦 Docker Files Added

### `Dockerfile` - Containerization
- **Multi-stage build**: Smaller final image
- **Base image**: Alpine Linux (lightweight)
- **Java runtime**: Eclipse Temurin JRE 17
- **Exposed port**: 8080 (Spring Boot)
- **Environment variables**: Pre-configured for MySQL connection

### `docker-compose.yml` - Local Testing
Run the entire stack locally:
```bash
docker-compose up
```
This starts:
- MySQL database (port 3306)
- Spring Boot app (port 8080)

---

## 🔧 Setup Instructions

### Step 1: Create Secrets in GitHub
Go to your repo → **Settings → Secrets and variables → Actions** → Add these:

#### For Docker Hub (Push Container Images):
```
DOCKER_USERNAME = your-docker-hub-username
DOCKER_PASSWORD = your-docker-hub-password or token
```

#### For Railway (Recommended - Free Tier):
```
RAILWAY_TOKEN = your-railway-api-token
```
Get it from: https://railway.app/account/tokens

#### For Render (Alternative):
```
RENDER_SERVICE_ID = your-render-service-id
RENDER_DEPLOY_KEY = your-render-deploy-key
```

#### For Azure (Alternative):
```
AZURE_RESOURCE_GROUP = your-azure-resource-group
```

---

## 📊 Deployment Options

### **Option 1: Railway (🌟 RECOMMENDED)**

**Why Railway?**
- Free tier with $5/month credits
- Simple one-click deployment
- Great for Spring Boot apps
- MySQL support built-in

**Steps:**
1. Sign up at https://railway.app
2. Create new project
3. Add GitHub as source
4. Generate API token (Settings → Tokens)
5. Add `RAILWAY_TOKEN` secret
6. Deploy: Push to `main` branch

**After deployment:**
```
Your app will be live at: https://smart-traffic-control-{random}.railway.app
```

---

### **Option 2: Render**

**Steps:**
1. Sign up at https://render.com
2. Create Web Service → Connect GitHub
3. Select this repository
4. Configure:
   - Runtime: Docker
   - Build command: (leave empty)
   - Start command: (leave empty)
5. Deploy

---

### **Option 3: Docker Hub + Your Own Server**

**Steps:**
1. Create Docker Hub account: https://hub.docker.com
2. Run secrets: `DOCKER_USERNAME`, `DOCKER_PASSWORD`
3. Your image builds and pushes automatically
4. Pull and run on your server:
```bash
docker pull your-username/smart-traffic-control:latest
docker run -p 8080:8080 your-username/smart-traffic-control:latest
```

---

## 🧪 Testing Locally Before Deployment

```bash
# Build Docker image
docker build -t smart-traffic-control:test .

# Start with docker-compose (MySQL + App)
docker-compose up

# Access the app
# Web UI: http://localhost:8080
# Login page: http://localhost:8080/login.html
# API: http://localhost:8080/api
```

---

## 📈 How the Pipeline Works (Flow Diagram)

```
Developer pushes to main
         ↓
GitHub Actions triggered
         ↓
─────────┴─────────┬──────────────────
         ↓                    ↓
    BUILD JOB          DEPLOY JOB
    (runs always)   (if build succeeds)
         ↓                    ↓
  - Maven build      - Docker build
  - Run tests        - Push to image
  - Unit tests       - Deploy to Railway
         ↓                    ↓
    Pass/Fail        App goes LIVE
         ↓                    ↓
    (waits)         Users can access
```

---

## 🔐 Security Notes

⚠️ **Important**: 
- Never commit secrets to GitHub
- Use GitHub Secrets for sensitive data
- Docker image contains plaintext credentials in environment (for now)
- For production: Use secrets management (AWS Secrets Manager, Azure Key Vault, etc.)

---

## 🚀 To Make It Live Right Now

1. **Push to main branch:**
```bash
git add .
git commit -m "Add CI/CD pipeline with Docker"
git push origin main
```

2. **Watch it deploy:**
   - Go to repo → **Actions** tab
   - Watch the workflow run
   - Check deployment logs

3. **Access your app:**
   - Railway: `https://smart-traffic-control-{id}.railway.app`
   - Or your custom domain if configured

---

## 🐛 Troubleshooting

### Build fails?
- Check Java/Maven versions match `pom.xml`
- Run locally: `mvn clean install`

### Docker image won't push?
- Verify `DOCKER_USERNAME` and `DOCKER_PASSWORD` secrets
- Check Docker Hub credentials

### Deployment fails?
- Verify API tokens are correct
- Check platform-specific logs (Railway dashboard, etc.)
- Database might be missing - configure MySQL in your platform

---

## 📝 Next Steps

1. ✅ Add secrets to GitHub (from Step 1)
2. ✅ Choose deployment platform
3. ✅ Push to main branch
4. ✅ Monitor Actions tab
5. ✅ Access your live app!

---

## 📚 Useful Links

- 🔗 GitHub Actions: https://github.com/features/actions
- 🔗 Railway: https://railway.app
- 🔗 Docker: https://www.docker.com
- 🔗 Spring Boot on Railway: https://docs.railway.app/guides/start-here
