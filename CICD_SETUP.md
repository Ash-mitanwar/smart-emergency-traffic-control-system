# 🚀 CI/CD Setup Guide — Railway Deployment

## 📋 Pipeline Overview

```
Push to main → GitHub Actions → Build & Test (Maven) → Deploy to Railway
```

### CI Job (runs on every push & PR to `main`)
- ✅ Checkout code
- ✅ Set up Java 17 (Temurin) with Maven caching
- ✅ Build with `mvn clean install`
- ✅ Run tests with `mvn test`

### CD Job (runs only on push to `main`, after CI passes)
- ✅ Install Railway CLI
- ✅ Deploy via `railway up --detach`

---

## 🔧 Setup Instructions

### Step 1: Create a Railway Project

1. Sign up at [railway.app](https://railway.app)
2. Click **New Project** → **Empty Project**
3. Add a **MySQL** plugin:
   - Click **+ New** → **Database** → **MySQL**
   - Railway auto-generates `MYSQL_URL`, `MYSQL_HOST`, `MYSQL_PORT`, `MYSQL_USER`, `MYSQL_PASSWORD`, `MYSQL_DATABASE`

### Step 2: Configure Environment Variables in Railway

Go to your Railway service → **Variables** tab and add:

| Variable | Value |
|----------|-------|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://${{MYSQL_HOST}}:${{MYSQL_PORT}}/${{MYSQL_DATABASE}}?useSSL=false&allowPublicKeyRetrieval=true` |
| `SPRING_DATASOURCE_USERNAME` | `${{MYSQL_USER}}` |
| `SPRING_DATASOURCE_PASSWORD` | `${{MYSQL_PASSWORD}}` |

> **Note**: Railway supports variable references with `${{...}}` syntax, so these will automatically resolve to the MySQL plugin's actual credentials.

### Step 3: Generate a Railway API Token

1. Go to [railway.app/account/tokens](https://railway.app/account/tokens)
2. Click **Create Token**
3. Copy the generated token

### Step 4: Add the Token to GitHub Secrets

1. Go to your GitHub repo → **Settings** → **Secrets and variables** → **Actions**
2. Click **New repository secret**
3. Name: `RAILWAY_TOKEN`
4. Value: paste the token from Step 3

### Step 5: Deploy!

```bash
git add .
git commit -m "Configure Railway deployment"
git push origin main
```

Then go to your repo → **Actions** tab to watch the pipeline run.

---

## 📂 Configuration Files

| File | Purpose |
|------|---------|
| `.github/workflows/ci.yml` | GitHub Actions CI/CD pipeline |
| `Dockerfile` | Multi-stage Docker build (Maven → Alpine JRE) |
| `railway.json` | Railway build & deploy configuration |
| `Procfile` | Fallback start command for Railway |

---

## 🧪 Testing Locally

```bash
# Using Docker Compose (MySQL + App)
docker-compose up

# Access the app
# Web UI: http://localhost:8080
# Login:  http://localhost:8080/login.html
# API:    http://localhost:8080/api/signal/status
```

---

## 🐛 Troubleshooting

| Problem | Solution |
|---------|----------|
| Build fails in CI | Run `mvn clean install` locally to reproduce |
| Railway deploy fails | Verify `RAILWAY_TOKEN` is correct in GitHub Secrets |
| App can't connect to DB | Check Railway Variables — ensure `SPRING_DATASOURCE_*` are set |
| Health check fails | App might be slow to start; Railway will retry automatically |

---

## 🔗 Useful Links

- [Railway Docs](https://docs.railway.app)
- [Railway CLI Reference](https://docs.railway.app/reference/cli-api)
- [GitHub Actions Docs](https://docs.github.com/en/actions)
