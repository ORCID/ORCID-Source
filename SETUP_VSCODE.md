# VS Code Setup for ORCID-Source

> 
> **Quick Start:** See [VSCODE_QUICKSTART.md](VSCODE_QUICKSTART.md) for a condensed reference guide.

This guide covers setting up ORCID-Source for development in Visual Studio Code.

## Prerequisites

Before starting, ensure you have completed:
- [Section 1: Prerequisites](DEVSETUP.md#1-prerequisites) - Java 11, PostgreSQL 13, Tomcat 9
- [Section 2: Clone & Build](DEVSETUP.md#2-clone--build) - Project cloned and built
- [Section 3: Databases](DEVSETUP.md#3-databases) - Databases created and initialized
- [Section 4: Redis](DEVSETUP.md#4-redis) - Redis installed and running
- [Section 5: JWKs](DEVSETUP.md#5-jwks-for-openid-connect) - OpenID Connect keys configured

---

## Quick Setup (Automated)

The project includes an automated setup script that configures VS Code for you.

### Run the Setup Script

```bash
cd ORCID-Source
./vscode-setup.sh
```

The script will:
- ✅ Verify all prerequisites (Java, Maven, PostgreSQL, Tomcat, Redis)
- ✅ Update VS Code configuration files with your system paths
- ✅ Check Tomcat SSL configuration
- ✅ Optionally build the project
- ✅ Optionally deploy WAR files to Tomcat

---

## Manual Setup (Step-by-Step)

If you prefer to set up manually or the script didn't work:

### 1. Open Project in VS Code

```bash
cd ORCID-Source
code .
```

### 2. Install Required Extensions

When VS Code opens, you'll see a prompt to install recommended extensions. Click **Install All**.

Or manually install:
- **Extension Pack for Java** (vscjava.vscode-java-pack)
- **Maven for Java** (vscjava.vscode-maven)
- **Language Support for Java** (redhat.java)
- **Debugger for Java** (vscjava.vscode-java-debug)

Optional but helpful:
- **PostgreSQL** (cweijan.vscode-postgresql-client2)
- **Docker** (ms-azuretools.vscode-docker)

### 3. Verify Java Configuration

The `.vscode/settings.json` file should already be configured. Verify these paths match your system:

**macOS (Homebrew):**
```json
{
  "java.jdt.ls.java.home": "/opt/homebrew/Cellar/openjdk@11/11.0.25/libexec/openjdk.jdk/Contents/Home",
  "maven.executable.path": "/opt/homebrew/bin/mvn"
}
```

**Windows:**
```json
{
  "java.jdt.ls.java.home": "C:\\Program Files\\Java\\jdk-11",
  "maven.executable.path": "C:\\Program Files\\Maven\\bin\\mvn.cmd"
}
```

> ⚠️ **Important:** Update the Java version number in the path to match your installed version.

**Check your Java version:**
```bash
# macOS
ls /opt/homebrew/Cellar/openjdk@11/

# Windows
dir "C:\Program Files\Java"
```

### 4. Build the Project

Open the integrated terminal (`` Ctrl+` `` or `` Cmd+` ``) and run:

```bash
mvn clean install -DskipTests
```

Or use the Command Palette:
- Press `Cmd+Shift+P` (macOS) or `Ctrl+Shift+P` (Windows)
- Type "Tasks: Run Task"
- Select "Maven: Clean Install"

---

## Configure Tomcat for Debugging

### 1. Enable HTTPS in Tomcat

Edit Tomcat's `server.xml` file:

**macOS (Homebrew):**
```bash
# Find your Tomcat version
ls /opt/homebrew/Cellar/tomcat@9/

# Edit server.xml (replace version number)
code /opt/homebrew/Cellar/tomcat@9/9.0.108/libexec/conf/server.xml
```

**Windows:**
```powershell
code C:\Tomcat9\conf\server.xml
```

**Add or verify this HTTPS connector** (around line 90, after the HTTP connector):

```xml
<Connector SSLEnabled="true" 
           clientAuth="want" 
           keystoreFile="/Users/YOUR_USERNAME/code/ORCID-Source/orcid-api-web/src/test/resources/orcid-server-keystore.jks" 
           keystorePass="changeit" 
           maxThreads="150" 
           port="8443" 
           protocol="HTTP/1.1" 
           scheme="https" 
           secure="true" 
           sslProtocol="TLS" 
           truststoreFile="/Users/YOUR_USERNAME/code/ORCID-Source/orcid-api-web/src/test/resources/orcid-server-truststore.jks" 
           truststorePass="changeit"/>
```

Replace `/Users/YOUR_USERNAME/code/ORCID-Source` with your actual project path.

**Windows example:**
```xml
keystoreFile="C:/code/ORCID-Source/orcid-api-web/src/test/resources/orcid-server-keystore.jks"
truststoreFile="C:/code/ORCID-Source/orcid-api-web/src/test/resources/orcid-server-truststore.jks"
```

### 2. Deploy WAR Files to Tomcat

Build the WAR files:

```bash
mvn -q -T 1C -DskipTests package
```

Copy WAR files to Tomcat:

**macOS:**
```bash
TOMCAT_HOME=/opt/homebrew/Cellar/tomcat@9/9.0.108/libexec
cp orcid-web/target/orcid-web.war $TOMCAT_HOME/webapps/
cp orcid-api-web/target/orcid-api-web.war $TOMCAT_HOME/webapps/
cp orcid-pub-web/target/orcid-pub-web.war $TOMCAT_HOME/webapps/
cp orcid-scheduler-web/target/orcid-scheduler-web.war $TOMCAT_HOME/webapps/
```

**Windows:**
```powershell
copy orcid-web\target\orcid-web.war C:\Tomcat9\webapps\
copy orcid-api-web\target\orcid-api-web.war C:\Tomcat9\webapps\
copy orcid-pub-web\target\orcid-pub-web.war C:\Tomcat9\webapps\
copy orcid-scheduler-web\target\orcid-scheduler-web.war C:\Tomcat9\webapps\
```

**Or use the VS Code task:**
- Command Palette (`Cmd+Shift+P` / `Ctrl+Shift+P`)
- "Tasks: Run Task" → "Deploy WARs to Tomcat"

### 3. Update Task Configuration

The `.vscode/tasks.json` file contains paths that need to match your system.

Verify the "Start Tomcat with ORCID" task has correct paths:

```json
"CATALINA_OPTS": "-Dorg.orcid.config.file=file:/ABSOLUTE/PATH/ORCID-Source/properties/development.properties -Dlog4j.configurationFile=file:/ABSOLUTE/PATH/ORCID-Source/orcid-web/log4j2.xml"
```

Replace `/ABSOLUTE/PATH/ORCID-Source` with your actual project path.

**macOS example:**
```json
"CATALINA_OPTS": "-Dorg.orcid.config.file=file:/Users/username/code/ORCID-Source/properties/development.properties -Dlog4j.configurationFile=file:/Users/username/code/ORCID-Source/orcid-web/log4j2.xml"
```

**Windows example:**
```json
"CATALINA_OPTS": "-Dorg.orcid.config.file=file:C:/code/ORCID-Source/properties/development.properties -Dlog4j.configurationFile=file:C:/code/ORCID-Source/orcid-web/log4j2.xml"
```

---

## Running and Debugging

### Start Tomcat in Debug Mode

**Option 1: Use VS Code Task**
1. Command Palette (`Cmd+Shift+P` / `Ctrl+Shift+P`)
2. "Tasks: Run Task"
3. "Start Tomcat with ORCID"

**Option 2: Use Terminal**

```bash
# Set environment variables
export CATALINA_OPTS="-Dorg.orcid.config.file=file:/Users/username/code/ORCID-Source/properties/development.properties -Dlog4j.configurationFile=file:/Users/username/code/ORCID-Source/orcid-web/log4j2.xml"
export JPDA_ADDRESS=8000
export JPDA_TRANSPORT=dt_socket

# Start Tomcat in debug mode
catalina jpda run
```

**Windows (PowerShell):**
```powershell
$env:CATALINA_OPTS="-Dorg.orcid.config.file=file:C:/code/ORCID-Source/properties/development.properties -Dlog4j.configurationFile=file:C:/code/ORCID-Source/orcid-web/log4j2.xml"
$env:JPDA_ADDRESS="8000"
$env:JPDA_TRANSPORT="dt_socket"
catalina.bat jpda run
```

Wait for the message: `Listening for transport dt_socket at address: 8000`

### Attach the Debugger

1. Press `F5` or click "Run and Debug" in the sidebar
2. Select "Attach to Tomcat (port 8000)"
3. The debugger will connect to Tomcat

### Set Breakpoints

1. Open any Java file
2. Click in the left margin (next to line numbers)
3. A red dot appears = breakpoint is set
4. Code execution will pause at breakpoints during debugging

### Stop Tomcat

**Option 1: Use VS Code Task**
- Command Palette → "Tasks: Run Task" → "Stop Tomcat"

**Option 2: Press `Ctrl+C` in the terminal where Tomcat is running**

---

## Testing Your Setup

### 1. Verify Tomcat is Running

Open in your browser:
- **HTTP:** http://localhost:8080/orcid-web/ping
- **HTTPS:** https://localhost:8443/orcid-web/ping

Expected response:
```json
{tomcatUp:true}
```

### 2. Test Debugging

1. Open `orcid-web/src/main/java/org/orcid/frontend/web/controllers/HomeController.java`
2. Set a breakpoint in a method
3. Access the corresponding URL in your browser
4. Execution should pause at your breakpoint
5. Use Debug controls to step through code

---

## Available VS Code Tasks

Access via Command Palette → "Tasks: Run Task":

| Task | Description | Shortcut |
|------|-------------|----------|
| **Maven: Clean Install** | Full build (skips tests) | `Cmd+Shift+B` |
| **Maven: Package (skip tests)** | Quick rebuild | - |
| **Deploy WARs to Tomcat** | Build and deploy all modules | - |
| **Start Tomcat with ORCID** | Start with debug on port 8000 | - |
| **Stop Tomcat** | Gracefully shutdown Tomcat | - |
| **Initialize Database** | Run InitDb utility | - |
| **Run Tests** | Execute Maven tests | - |

---

## Keyboard Shortcuts

| Action | macOS | Windows/Linux |
|--------|-------|---------------|
| Build project | `Cmd+Shift+B` | `Ctrl+Shift+B` |
| Run task | `Cmd+Shift+P` → Tasks | `Ctrl+Shift+P` → Tasks |
| Start debugging | `F5` | `F5` |
| Stop debugging | `Shift+F5` | `Shift+F5` |
| Step over | `F10` | `F10` |
| Step into | `F11` | `F11` |
| Step out | `Shift+F11` | `Shift+F11` |
| Toggle terminal | `` Cmd+` `` | `` Ctrl+` `` |
| Command Palette | `Cmd+Shift+P` | `Ctrl+Shift+P` |

---

## Troubleshooting

### Java Version Mismatch

**Problem:** VS Code shows wrong Java version or import errors.

**Solution:**
1. Check `java.jdt.ls.java.home` in `.vscode/settings.json`
2. Verify it points to Java 11 installation
3. Reload window: Command Palette → "Developer: Reload Window"

### Tomcat Won't Start

**Problem:** Port already in use.

**Solution:**

**macOS/Linux:**
```bash
# Find process using port 8080
lsof -i :8080

# Kill the process
kill -9 <PID>
```

**Windows:**
```powershell
# Find process using port 8080
netstat -ano | findstr :8080

# Kill the process
taskkill /PID <PID> /F
```

### Can't Attach Debugger

**Problem:** Debugger fails to connect.

**Solution:**
1. Ensure Tomcat started with "Start Tomcat with ORCID" task
2. Look for: "Listening for transport dt_socket at address: 8000"
3. Verify port 8000 is open: `lsof -i :8000` (macOS) or `netstat -ano | findstr :8000` (Windows)
4. Try restarting Tomcat

### 404 Errors

**Problem:** Application not found.

**Solution:**
1. Verify WAR files were deployed to Tomcat webapps directory
2. Check Tomcat logs for deployment errors
3. Ensure application context is correct: `/orcid-web` not `/orcid-web.war`
4. Wait for WAR files to fully deploy (check for directory in webapps)

### Build Fails

**Problem:** Maven build errors.

**Solution:**
```bash
# Clean Maven cache and rebuild
mvn clean install -U -DskipTests

# Check Java version
java -version  # Should be 11.x

# Verify JAVA_HOME
echo $JAVA_HOME  # macOS/Linux
echo %JAVA_HOME%  # Windows
```

### Hot Reload Not Working

**Problem:** Code changes don't take effect.

**Solution:**
- WAR deployment doesn't support hot reload
- You must redeploy WARs after changes:
  1. Stop Tomcat
  2. Run "Deploy WARs to Tomcat" task
  3. Start Tomcat
- Consider using JRebel for hot reload (commercial tool)

---

## Tips & Best Practices

### Performance

* **Increase memory:** Add to `CATALINA_OPTS`: `-Xmx2g -Xms512m`
* **Use parallel builds:** Maven already configured with `-T 1C`
* **Close unused projects:** In VS Code, close folders you're not working on

### Code Navigation

* **Go to definition:** `F12` or `Cmd+Click`
* **Find references:** `Shift+F12`
* **Search in files:** `Cmd+Shift+F` / `Ctrl+Shift+F`
* **Quick fix:** `Cmd+.` / `Ctrl+.`

### Database Tools

Install PostgreSQL extension to:
- View database tables
- Run SQL queries
- Inspect data

### Git Integration

* Built into VS Code
* Access via Source Control panel (`Cmd+Shift+G` / `Ctrl+Shift+G`)
* View changes, commit, push, pull

---

## VS Code Configuration Files

The `.vscode/` directory contains:

- **settings.json** - Java and Maven paths, workspace settings
- **launch.json** - Debug configurations
- **tasks.json** - Build and deployment tasks
- **extensions.json** - Recommended extensions
- **README.md** - Configuration documentation

These files are committed to the repository, so most configuration is automatic.

---

## Next Steps

Once your VS Code setup is complete:

1. **Test Basic Functionality**
   - Access http://localhost:8080/orcid-web/ping
   - Verify database connectivity
   - Test debugging with breakpoints

2. **Optional Components**
   - [Configure Message Listener](DEVSETUP.md#8-configure-message-listener) (optional)
   - [Configure SOLR](DEVSETUP.md#9-configure-solr) (optional)

3. **Frontend Setup**
   - [Orcid Angular](DEVSETUP.md#10-orcid-angular)
   - Follow instructions at https://github.com/ORCID/orcid-angular

4. **Proxy Setup** (ORCID employees only)
   - See internal documentation for proxy configuration

---

## Additional Resources

- [VSCODE_QUICKSTART.md](VSCODE_QUICKSTART.md) - Quick reference guide
- [.vscode/README.md](.vscode/README.md) - Configuration details
- [DEVSETUP.md](DEVSETUP.md) - Main development setup guide
- [VS Code Java Documentation](https://code.visualstudio.com/docs/java/java-tutorial)
- [Maven in VS Code](https://code.visualstudio.com/docs/java/java-build)

---

**Questions?** Check with your team or refer to internal ORCID documentation.
