# 📚 ORCID-Source Setup Documentation Guide

This repository now includes comprehensive setup documentation for multiple IDEs. Choose the guide that matches your preferred development environment.

## 🚀 Quick Navigation

### Main Setup Guide
- **[DEVSETUP.md](DEVSETUP.md)** - Main development environment setup
  - Prerequisites (Java, PostgreSQL, Tomcat, Redis)
  - Database configuration
  - Redis setup
  - JWKs configuration
  - Points to IDE-specific guides

### IDE-Specific Guides

#### For VS Code Users
- **[SETUP_VSCODE.md](SETUP_VSCODE.md)** - Complete VS Code setup guide
  - Automated setup script included
  - Step-by-step manual setup
  - Debugging configuration
  - Troubleshooting tips

- **[VSCODE_QUICKSTART.md](VSCODE_QUICKSTART.md)** - Quick reference
  - Keyboard shortcuts
  - Common tasks
  - Daily workflow
  - Troubleshooting quick fixes

#### For IntelliJ IDEA Users
- **[SETUP_INTELLIJ.md](SETUP_INTELLIJ.md)** - Complete IntelliJ setup guide
  - Works with Ultimate and Community editions
  - Tomcat configuration
  - Run/Debug configurations
  - Tips and best practices

## 📋 Setup Order

Follow these steps in order:

### 1. Prerequisites
Start with [DEVSETUP.md](DEVSETUP.md) sections 1-5:
- Install Java 11, PostgreSQL 13, Tomcat 9
- Clone and build the project
- Set up databases
- Configure Redis
- Set up JWKs for OpenID Connect

### 2. Choose Your IDE

**Option A: VS Code** (Recommended for new developers)
```bash
# Quick setup
./vscode-setup.sh
code .
```
Then follow [SETUP_VSCODE.md](SETUP_VSCODE.md)

**Option B: IntelliJ IDEA**
Follow [SETUP_INTELLIJ.md](SETUP_INTELLIJ.md)

### 3. Test Your Setup
- Start Tomcat in debug mode
- Visit http://localhost:8080/orcid-web/ping
- Should see: `{tomcatUp:true}`

### 4. Optional Components
Configure as needed:
- Message Listener (see DEVSETUP.md section 8)
- SOLR (see DEVSETUP.md section 9)
- Frontend Angular app (see DEVSETUP.md section 10)

## 🎯 Which IDE Should I Choose?

### Choose VS Code if you:
- ✅ Want a lightweight, fast editor
- ✅ Are familiar with VS Code
- ✅ Prefer open-source tools
- ✅ Want automated setup scripts
- ✅ Work with multiple languages/frameworks

### Choose IntelliJ IDEA if you:
- ✅ Want the most powerful Java IDE
- ✅ Are familiar with JetBrains tools
- ✅ Need advanced refactoring tools
- ✅ Have IntelliJ Ultimate license
- ✅ Primarily work with Java/Maven projects

**Both are fully supported!** Choose based on your preference.

## 📖 Additional Resources

### VS Code Resources
- [.vscode/README.md](.vscode/README.md) - VS Code configuration details
- [vscode-setup.sh](vscode-setup.sh) - Automated setup script
- VS Code configuration files in `.vscode/` directory

### General Resources
- [CONTRIBUTING.md](CONTRIBUTING.md) - Contribution guidelines
- [DEVSETUP.md](DEVSETUP.md) - Prerequisites and common setup
- Internal ORCID documentation (employees only)

## 🆘 Getting Help

### Troubleshooting
Each setup guide includes a comprehensive troubleshooting section:
- **VS Code:** See [SETUP_VSCODE.md](SETUP_VSCODE.md#troubleshooting)
- **IntelliJ:** See [SETUP_INTELLIJ.md](SETUP_INTELLIJ.md#troubleshooting)
- **Quick fixes:** See [VSCODE_QUICKSTART.md](VSCODE_QUICKSTART.md#troubleshooting)

### Common Issues
- **Port conflicts:** Kill processes on ports 8080, 8443, 8000
- **Java version:** Must be Java 11
- **Build failures:** Run `mvn clean install -U -DskipTests`
- **Database connection:** Ensure PostgreSQL is running

### Support Channels
- Check internal ORCID documentation
- Ask your team
- Review the troubleshooting sections
- Check existing GitHub issues

## 🔄 Switching Between IDEs

You can use both IDEs on the same project:
- VS Code config is in `.vscode/` directory
- IntelliJ config is in `.idea/` directory (gitignored)
- Both can coexist without conflicts
- Make sure only one IDE is running Tomcat at a time

## ✅ Features Comparison

| Feature | VS Code | IntelliJ |
|---------|---------|----------|
| **Free** | ✅ Yes | ⚠️ Community only |
| **Java Support** | ✅ Excellent | ✅ Best-in-class |
| **Maven Integration** | ✅ Good | ✅ Excellent |
| **Debugging** | ✅ Full support | ✅ Advanced features |
| **Hot Reload** | ⚠️ Limited | ⚠️ Limited (Ultimate better) |
| **Git Integration** | ✅ Built-in | ✅ Built-in |
| **Refactoring** | ✅ Good | ✅ Excellent |
| **Learning Curve** | ✅ Easy | ⚠️ Moderate |
| **Performance** | ✅ Fast | ⚠️ Resource-intensive |
| **Setup Time** | ✅ Quick (automated) | ⚠️ Manual |

## 📝 Contributing

If you improve the setup process or documentation:
1. Update the relevant setup guide
2. Test your changes on a clean setup
3. Submit a pull request
4. Update this README if you add new documentation

## 🎓 For New Developers

**Recommended path:**
1. Read [DEVSETUP.md](DEVSETUP.md) introduction
2. Complete sections 1-5 (prerequisites)
3. Choose VS Code for easier setup
4. Run `./vscode-setup.sh`
5. Follow [SETUP_VSCODE.md](SETUP_VSCODE.md)
6. Keep [VSCODE_QUICKSTART.md](VSCODE_QUICKSTART.md) handy as reference

**Total time estimate:** 1-2 hours for complete setup

---

**Ready to start?** Begin with [DEVSETUP.md](DEVSETUP.md) →
