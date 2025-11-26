#!/bin/bash

# VS Code ORCID Development Setup Helper
# This script helps configure and verify your VS Code development environment

set -e

echo "🔧 ORCID-Source VS Code Setup Helper"
echo "======================================"
echo ""

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Get project root
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
echo "📁 Project root: $PROJECT_ROOT"
echo ""

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Function to print status
print_status() {
    if [ $1 -eq 0 ]; then
        echo -e "${GREEN}✓${NC} $2"
    else
        echo -e "${RED}✗${NC} $2"
    fi
}

# Check prerequisites
echo "1️⃣  Checking Prerequisites"
echo "-------------------------"

# Check Java
if command_exists java; then
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
    if [ "$JAVA_VERSION" = "11" ]; then
        print_status 0 "Java 11 installed"
        echo "   Java Home: $JAVA_HOME"
    else
        print_status 1 "Java 11 required, found version $JAVA_VERSION"
        exit 1
    fi
else
    print_status 1 "Java not found"
    exit 1
fi

# Check Maven
if command_exists mvn; then
    print_status 0 "Maven installed"
else
    print_status 1 "Maven not found"
    exit 1
fi

# Check PostgreSQL
if command_exists psql; then
    print_status 0 "PostgreSQL installed"
else
    print_status 1 "PostgreSQL not found"
    exit 1
fi

# Check Tomcat
if command_exists catalina; then
    print_status 0 "Tomcat installed"
    CATALINA_HOME=$(catalina version 2>&1 | grep CATALINA_HOME | cut -d':' -f2 | xargs)
    echo "   Tomcat Home: $CATALINA_HOME"
else
    print_status 1 "Tomcat not found"
    exit 1
fi

# Check Redis
if command_exists redis-cli; then
    if redis-cli ping >/dev/null 2>&1; then
        print_status 0 "Redis running"
    else
        print_status 1 "Redis installed but not running (run: brew services start redis)"
    fi
else
    echo -e "${YELLOW}⚠${NC}  Redis not found (optional for development)"
fi

echo ""

# Update VS Code configuration files
echo "2️⃣  Updating VS Code Configuration"
echo "-----------------------------------"

# Update settings.json with actual paths
if [ -f ".vscode/settings.json" ]; then
    sed -i.bak "s|/opt/homebrew/Cellar/openjdk@11/[^\"]*|$JAVA_HOME|g" .vscode/settings.json
    print_status 0 "Updated settings.json with Java paths"
else
    print_status 1 "settings.json not found"
fi

# Update tasks.json with actual paths
if [ -f ".vscode/tasks.json" ]; then
    sed -i.bak "s|CATALINA_BASE\": \"[^\"]*|CATALINA_BASE\": \"$CATALINA_HOME|g" .vscode/tasks.json
    sed -i.bak "s|CATALINA_HOME\": \"[^\"]*|CATALINA_HOME\": \"$CATALINA_HOME|g" .vscode/tasks.json
    sed -i.bak "s|JAVA_HOME\": \"[^\"]*|JAVA_HOME\": \"$JAVA_HOME|g" .vscode/tasks.json
    sed -i.bak "s|file:/Users/[^/]*/code/ORCID-Source|file:$PROJECT_ROOT|g" .vscode/tasks.json
    print_status 0 "Updated tasks.json with system paths"
else
    print_status 1 "tasks.json not found"
fi

# Clean up backup files
rm -f .vscode/*.bak

echo ""

# Check Tomcat server.xml configuration
echo "3️⃣  Checking Tomcat Configuration"
echo "----------------------------------"

SERVER_XML="$CATALINA_HOME/conf/server.xml"
if [ -f "$SERVER_XML" ]; then
    if grep -q "orcid-server-keystore.jks" "$SERVER_XML"; then
        print_status 0 "HTTPS connector configured in server.xml"
    else
        echo -e "${YELLOW}⚠${NC}  HTTPS connector not found in server.xml"
        echo "   Add this to $SERVER_XML:"
        echo '   <Connector SSLEnabled="true" clientAuth="want"'
        echo "              keystoreFile=\"$PROJECT_ROOT/orcid-api-web/src/test/resources/orcid-server-keystore.jks\""
        echo '              keystorePass="changeit" maxThreads="150" port="8443"'
        echo '              protocol="HTTP/1.1" scheme="https" secure="true" sslProtocol="TLS"'
        echo "              truststoreFile=\"$PROJECT_ROOT/orcid-api-web/src/test/resources/orcid-server-truststore.jks\""
        echo '              truststorePass="changeit"/>'
    fi
else
    print_status 1 "server.xml not found at $SERVER_XML"
fi

echo ""

# Offer to build and deploy
echo "4️⃣  Build and Deploy Options"
echo "-----------------------------"

read -p "Do you want to build the project? (y/n) " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo "Building project (this may take a few minutes)..."
    mvn clean install -DskipTests
    print_status 0 "Project built successfully"
fi

echo ""
read -p "Do you want to deploy WAR files to Tomcat? (y/n) " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo "Building WAR files..."
    mvn -q -T 1C -DskipTests package
    
    echo "Deploying WAR files to $CATALINA_HOME/webapps/..."
    cp orcid-web/target/orcid-web.war "$CATALINA_HOME/webapps/"
    cp orcid-api-web/target/orcid-api-web.war "$CATALINA_HOME/webapps/"
    cp orcid-pub-web/target/orcid-pub-web.war "$CATALINA_HOME/webapps/"
    cp orcid-scheduler-web/target/orcid-scheduler-web.war "$CATALINA_HOME/webapps/"
    
    print_status 0 "WAR files deployed to Tomcat"
fi

echo ""
echo "✅ Setup complete!"
echo ""
echo "Next steps:"
echo "  1. Open VS Code: code ."
echo "  2. Install recommended extensions when prompted"
echo "  3. Start Tomcat: Command Palette → Tasks: Run Task → Start Tomcat with ORCID"
echo "  4. Attach debugger: Press F5"
echo "  5. Test: http://localhost:8080/orcid-web/ping"
echo ""
