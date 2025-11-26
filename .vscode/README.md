# VS Code Setup for ORCID-Source

This directory contains VS Code workspace configuration for developing ORCID-Source.

## Files

- **settings.json** - Java and Maven configuration
- **launch.json** - Debug configurations for attaching to Tomcat
- **tasks.json** - Build, run, and deployment tasks
- **extensions.json** - Recommended VS Code extensions

## Quick Start

1. **Run the setup script** (from project root):
   ```bash
   ./vscode-setup.sh
   ```

2. **Install recommended extensions** when prompted by VS Code

3. **Deploy WAR files**:
   - Command Palette (`Cmd+Shift+P` / `Ctrl+Shift+P`)
   - Run Task → "Deploy WARs to Tomcat"

4. **Start Tomcat with debugging**:
   - Run Task → "Start Tomcat with ORCID"

5. **Attach debugger**:
   - Press `F5` or Run → Start Debugging
   - Select "Attach to Tomcat (port 8000)"

6. **Test**:
   - Open http://localhost:8080/orcid-web/ping
   - Should see: `{tomcatUp:true}`

## Available Tasks

Access via Command Palette → "Tasks: Run Task":

- **Maven: Clean Install** - Full build (skips tests) - `Cmd+Shift+B`
- **Maven: Package (skip tests)** - Quick rebuild
- **Deploy WARs to Tomcat** - Build and deploy all WAR files
- **Start Tomcat with ORCID** - Start with debug on port 8000
- **Stop Tomcat** - Gracefully stop Tomcat
- **Initialize Database** - Run InitDb utility
- **Run Tests** - Execute Maven tests

## Debug Configurations

Two debug configurations are available:

1. **ORCID Local (Tomcat)** - Starts Tomcat and attaches debugger
2. **Attach to Tomcat (port 8000)** - Attach to already-running Tomcat

## Customization

If your Java, Maven, or Tomcat installations are in different locations, update:

- `settings.json` - Java home path
- `tasks.json` - CATALINA_HOME, CATALINA_BASE, JAVA_HOME

## Troubleshooting

**Port already in use**:
```bash
# Find what's using port 8080
lsof -i :8080
# Kill the process
kill -9 <PID>
```

**Can't attach debugger**:
- Ensure Tomcat started with "Start Tomcat with ORCID" task
- Verify port 8000 is open: `lsof -i :8000`

**WAR deployment fails**:
- Check Tomcat is not running
- Verify write permissions to Tomcat webapps directory

For more details, see [DEVSETUP.md](../DEVSETUP.md#vs-code-setup-step-by-step)
