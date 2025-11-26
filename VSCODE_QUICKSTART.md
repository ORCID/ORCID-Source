# ORCID-Source VS Code Quick Reference

## 🚀 Getting Started

```bash
# 1. Run setup script
./vscode-setup.sh

# 2. Open in VS Code
code .

# 3. Build project
# Press Cmd+Shift+B (or Ctrl+Shift+B on Windows)
```

## ⌨️ Keyboard Shortcuts

| Action | macOS | Windows/Linux |
|--------|-------|---------------|
| Build project | `Cmd+Shift+B` | `Ctrl+Shift+B` |
| Run task | `Cmd+Shift+P` → Tasks | `Ctrl+Shift+P` → Tasks |
| Start debugging | `F5` | `F5` |
| Stop debugging | `Shift+F5` | `Shift+F5` |
| Toggle terminal | `` Cmd+` `` | `` Ctrl+` `` |
| Command Palette | `Cmd+Shift+P` | `Ctrl+Shift+P` |

## 📋 Common Tasks

### First Time Setup

```bash
# 1. Install dependencies
brew install openjdk@11 postgresql@13 tomcat@9 redis maven

# 2. Set up databases (see DEVSETUP.md section 3)
psql -U postgres < setup-commands.sql

# 3. Configure properties
# Edit properties/development.properties

# 4. Build project
mvn clean install -DskipTests

# 5. Deploy to Tomcat
# Command Palette → Tasks: Run Task → Deploy WARs to Tomcat
```

### Daily Development Workflow

```bash
# 1. Start Redis (if using)
brew services start redis

# 2. Deploy latest changes
# Command Palette → Deploy WARs to Tomcat

# 3. Start Tomcat with debugging
# Command Palette → Start Tomcat with ORCID

# 4. Attach debugger
# Press F5

# 5. Make code changes
# Hot reload works for some changes, others require restart

# 6. Stop Tomcat
# Command Palette → Stop Tomcat
```

## 🔍 Debugging

### Set Breakpoints
1. Click in the left margin (line number area) of any Java file
2. Red dot appears = breakpoint set
3. Start debugging (F5) and code will pause at breakpoint

### Debug Console
- View variables, call stack, and evaluate expressions
- Access via View → Debug Console or `` Cmd+Shift+Y ``

### Attach to Running Tomcat
```bash
# If Tomcat is already running with JPDA enabled
# Just press F5 and select "Attach to Tomcat (port 8000)"
```

## 🧪 Testing

```bash
# Run all tests
# Command Palette → Tasks: Run Task → Run Tests

# Or from terminal
mvn test

# Run specific test file
# Right-click test file → Run Java
```

## 🌐 URLs

| Service | URL | Description |
|---------|-----|-------------|
| ORCID Web | http://localhost:8080/orcid-web | Main web application |
| API | http://localhost:8080/orcid-api-web | API endpoints |
| Public API | http://localhost:8080/orcid-pub-web | Public API |
| Ping Test | http://localhost:8080/orcid-web/ping | Health check |
| HTTPS | https://localhost:8443/orcid-web | SSL enabled |

## 🛠️ Troubleshooting

### Build Fails
```bash
# Clean Maven cache
mvn clean install -U -DskipTests

# Check Java version
java -version  # Should be 11.x

# Verify JAVA_HOME
echo $JAVA_HOME
```

### Tomcat Won't Start
```bash
# Check if port is in use
lsof -i :8080
lsof -i :8443
lsof -i :8000

# Kill process if needed
kill -9 <PID>

# Check Tomcat logs
tail -f /opt/homebrew/Cellar/tomcat@9/9.0.108/libexec/logs/catalina.out
```

### Debugger Won't Attach
```bash
# Verify Tomcat started with JPDA
# Look for: "Listening for transport dt_socket at address: 8000"

# Test connection
telnet localhost 8000

# Restart Tomcat with debug enabled
# Use "Start Tomcat with ORCID" task
```

### Database Connection Issues
```bash
# Check PostgreSQL is running
pg_isready

# Start if needed
brew services start postgresql@13

# Test connection
psql -U orcid -d orcid -h localhost
```

### Hot Reload Not Working
- Some changes require Tomcat restart
- WAR deployment changes always need restart
- Simple method body changes often work without restart
- To force reload: Stop → Redeploy WARs → Start

## 📚 Additional Resources

- [DEVSETUP.md](DEVSETUP.md) - Complete setup guide
- [.vscode/README.md](.vscode/README.md) - VS Code configuration details
- [ORCID Angular](https://github.com/ORCID/orcid-angular) - Frontend setup

## 💡 Tips

- **Use Maven wrapper**: `./mvnw` instead of `mvn` for consistent versions
- **Check logs**: Terminal shows Tomcat output when running via task
- **Multiple workspaces**: Can run multiple Tomcat instances on different ports
- **Database tools**: Install PostgreSQL extension for VS Code to query databases
- **Git integration**: Built into VS Code, use Source Control panel (`Cmd+Shift+G`)

---

**Need help?** Check internal documentation or ask in team channels.
