# IntelliJ IDEA Setup for ORCID-Source

This guide covers setting up ORCID-Source for development in IntelliJ IDEA.

## Prerequisites

Before starting, ensure you have completed:
- [Section 1: Prerequisites](DEVSETUP.md#1-prerequisites) - Java 11, PostgreSQL 13, Tomcat 9
- [Section 2: Clone & Build](DEVSETUP.md#2-clone--build) - Project cloned and built
- [Section 3: Databases](DEVSETUP.md#3-databases) - Databases created and initialized
- [Section 4: Redis](DEVSETUP.md#4-redis) - Redis installed and running
- [Section 5: JWKs](DEVSETUP.md#5-jwks-for-openid-connect) - OpenID Connect keys configured

---

## IntelliJ IDEA Setup

### Initial Setup

**1. Install IntelliJ IDEA**

* **Ultimate** edition is recommended (includes Tomcat support)
* **Community** edition works with **Smart Tomcat** plugin

**2. Configure Project SDK**

* Open `File → Project Structure → Project`
* Set **Project SDK = 11** (Java 11)
* Click Apply

**3. Configure Maven**

* Open `Settings → Build, Execution, Deployment → Build Tools → Maven → Importing`
* Set **JDK for importer = 11**
* Enable **Automatically download: Sources** and **Documentation** (optional)

**4. Import Project**

* Open the `ORCID-Source` directory as a Maven project
* IntelliJ will automatically detect `pom.xml` and import modules
* Wait for Maven indexing to complete

---

### Configure Tomcat

**Add Tomcat to IntelliJ**

**For IntelliJ Ultimate:**

1. Open `Settings → Plugins`
2. Ensure **Tomcat and TomEE** plugin is enabled
3. Open `Settings → Build, Execution, Deployment → Application Servers`
4. Click **+** → **Tomcat Server**
5. Set Tomcat home:
   - **Windows:** `C:\Tomcat9`
   - **macOS (Homebrew):** `/opt/homebrew/Cellar/tomcat@9/<version>/libexec`

**For IntelliJ Community:**

1. Open `Settings → Plugins → Marketplace`
2. Search for and install **Smart Tomcat**
3. Restart IntelliJ when prompted
4. Note: Smart Tomcat requires both HTTP (8080) and HTTPS (8443) ports

---

### Create Run/Debug Configuration

**1. Create New Configuration**

* Go to `Run → Edit Configurations → +`
* Select:
  - **Ultimate:** Tomcat Server → Local
  - **Community:** Smart Tomcat

**2. Name Your Configuration**

* Name: `orcid-local-https` (or your preference)

**3. Configure Ports**

* **HTTPS:** `8443`
* **HTTP:** `8080`

**4. Set VM Options**

Add these VM arguments (replace `/ABS/PATH/ORCID-Source` with your actual project path):

```bash
-Dorg.orcid.config.file="file:/ABS/PATH/ORCID-Source/properties/development.properties"
-Dlog4j.configurationFile="file:/ABS/PATH/ORCID-Source/orcid-web/log4j2.xml"
```

**Example (macOS):**
```bash
-Dorg.orcid.config.file="file:/Users/username/code/ORCID-Source/properties/development.properties"
-Dlog4j.configurationFile="file:/Users/username/code/ORCID-Source/orcid-web/log4j2.xml"
```

**Example (Windows):**
```bash
-Dorg.orcid.config.file="file:C:/code/ORCID-Source/properties/development.properties"
-Dlog4j.configurationFile="file:C:/code/ORCID-Source/orcid-web/log4j2.xml"
```

> ⚠️ **Important:** Use absolute paths and keep the surrounding quotes.

**5. Configure Deployment**

* Click the **Deployment** tab
* Click **+** → **Artifact**
* Add these artifacts (choose **WAR exploded** for faster redeployment):
  - `orcid-web:war exploded`
  - `orcid-api-web:war exploded`
  - `orcid-pub-web:war exploded`
  - `orcid-scheduler-web:war exploded`

* For each artifact, set the **Application context**:
  - Change `/orcid_web_war` → `/orcid-web`
  - Change `/orcid_api_web_war` → `/orcid-api-web`
  - Change `/orcid_pub_web_war` → `/orcid-pub-web`
  - Change `/orcid_scheduler_web_war` → `/orcid-scheduler-web`

---

### Enable HTTPS in Tomcat

Edit Tomcat's `server.xml` file:

**Location:**
- **Windows:** `C:\Tomcat9\conf\server.xml`
- **macOS (Homebrew):** `/opt/homebrew/Cellar/tomcat@9/<version>/libexec/conf/server.xml`

**Add HTTPS Connector** (around line 90, after the HTTP connector):

```xml
<Connector SSLEnabled="true" 
           clientAuth="want" 
           keystoreFile="[ROOT_PATH]/orcid-api-web/src/test/resources/orcid-server-keystore.jks" 
           keystorePass="changeit" 
           maxThreads="150" 
           port="8443" 
           protocol="HTTP/1.1" 
           scheme="https" 
           secure="true" 
           sslProtocol="TLS" 
           truststoreFile="[ROOT_PATH]/orcid-api-web/src/test/resources/orcid-server-truststore.jks" 
           truststorePass="changeit"/>
```

Replace `[ROOT_PATH]` with your absolute project path.

**Example (macOS):**
```xml
keystoreFile="/Users/username/code/ORCID-Source/orcid-api-web/src/test/resources/orcid-server-keystore.jks"
truststoreFile="/Users/username/code/ORCID-Source/orcid-api-web/src/test/resources/orcid-server-truststore.jks"
```

**Example (Windows):**
```xml
keystoreFile="C:/code/ORCID-Source/orcid-api-web/src/test/resources/orcid-server-keystore.jks"
truststoreFile="C:/code/ORCID-Source/orcid-api-web/src/test/resources/orcid-server-truststore.jks"
```

---

## Running and Debugging

### Start the Application

1. Select your run configuration (e.g., `orcid-local-https`)
2. Click the **Debug** button (bug icon) or press `Shift+F9`
3. Wait for Tomcat to start (watch the console output)
4. Look for: `Server startup in [xxx] milliseconds`

### Test Your Setup

Open in your browser:
- **HTTP:** http://localhost:8080/orcid-web/ping
- **HTTPS:** https://localhost:8443/orcid-web/ping

You should see:
```json
{tomcatUp:true}
```

### Debugging

**Set Breakpoints:**
- Click in the left margin (line number area) of any Java file
- A red dot indicates a breakpoint is set

**Debug Controls:**
- **F8** - Step Over
- **F7** - Step Into
- **Shift+F8** - Step Out
- **F9** - Resume Program
- **Cmd+F8** / **Ctrl+F8** - Toggle Breakpoint

**View Variables:**
- Use the **Variables** panel in the Debug window
- Evaluate expressions in the **Watches** panel

---

## Troubleshooting

### Artifacts Not Listed

If WAR artifacts don't appear in the Deployment tab:

```bash
# Build the project
mvn -q -T 1C -DskipTests package
```

Or use IntelliJ's Maven tool window:
1. Open **Maven** panel (right sidebar)
2. Expand **Lifecycle**
3. Double-click **package**

Then reopen the Run Configuration dialog.

### 404 or Wrong URL

Ensure your browser URL matches the **Application context** you set:
- Correct: `http://localhost:8080/orcid-web/`
- Wrong: `http://localhost:8080/orcid_web_war/`

### SSL Errors

Verify:
- Keystore and truststore files exist at the specified paths
- Paths in `server.xml` are absolute and correct
- Files are readable by the Tomcat process

### Port Already in Use

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

### Maven Import Issues

1. Right-click on `pom.xml` → **Maven** → **Reimport**
2. Or use **Maven** tool window → Reload icon
3. Check that Java 11 is selected for Maven importer

### Hot Reload Not Working

Some changes require a restart:
- Configuration files
- WAR structure changes
- Dependency changes

For code-only changes:
- **IntelliJ Ultimate:** Should hot-reload automatically
- **Smart Tomcat:** May require manual redeploy

---

## Tips & Best Practices

### Performance

* **Use WAR exploded** for faster deployments during development
* **Enable parallel builds** in Maven: `Settings → Build Tools → Maven → Runner → Thread Count`
* **Increase heap size** if needed: Add to VM options: `-Xmx2g -Xms512m`

### Code Style

* Import the project code style:
  - Go to `Settings → Editor → Code Style`
  - Click the gear icon → **Import Scheme** → **Eclipse XML Profile**
  - Select `eclipse_formatter.xml` from the project root

### Keyboard Shortcuts

| Action | macOS | Windows/Linux |
|--------|-------|---------------|
| Run | `Ctrl+R` | `Shift+F10` |
| Debug | `Ctrl+D` | `Shift+F9` |
| Build Project | `Cmd+F9` | `Ctrl+F9` |
| Find in Files | `Cmd+Shift+F` | `Ctrl+Shift+F` |
| Run Anything | `Ctrl+Ctrl` (double) | `Ctrl+Ctrl` (double) |

### Maven Tool Window

Use IntelliJ's Maven panel for:
- Running Maven goals
- Viewing dependency tree
- Managing profiles
- Updating dependencies

---

## Next Steps

Once your IntelliJ setup is complete:

1. **Test Basic Functionality**
   - Access http://localhost:8080/orcid-web/ping
   - Verify database connectivity

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

- [DEVSETUP.md](DEVSETUP.md) - Main development setup guide
- [IntelliJ IDEA Documentation](https://www.jetbrains.com/idea/documentation/)
- [Maven Integration](https://www.jetbrains.com/help/idea/maven-support.html)
- [Tomcat Integration](https://www.jetbrains.com/help/idea/creating-run-debug-configuration-for-application-server.html)

---

**Questions?** Check with your team or refer to internal ORCID documentation.
