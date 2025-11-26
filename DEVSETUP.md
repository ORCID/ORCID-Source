# Development Environment Setup

## Table of Contents

* [1) Prerequisites](#1-prerequisites)

  * [Install Java OpenJDK 11](#install-java-openjdk-11)
  * [Install PostgreSQL 13](#install-postgresql-13)
  * [Install Tomcat 9.0.x](#install-tomcat-90x)
* [2) Clone & Build](#2-clone--build)
* [3) Databases](#3-databases)
* [4) Redis](#4-redis)
* [5) JWKs (OpenID Connect)](#5-jwks-for-openid-connect)
* [6) IDE Setup](#6-ide-setup)
* [7) Test your setup](#7-test-your-setup)
* [8) Configure Message Listener - optional](#8-configure-message-listener)
* [9) Configure SOLR - optional](#9-configure-solr)
* [10) Orcid Angular](#10-orcid-angular)

  * [Legacy: Configure frontend (Optional to run the old UI)](#legacy-configure-frontend-optional-to-run-the-old-ui)
* [11) Proxy for local registry](#11-proxy-for-local-registry)


---

## 1) Prerequisites

### Install java openjdk 11

**Windows (PowerShell)**

```powershell
# Download and install from https://openjdk.java.net/install/
java -version
echo %JAVA_HOME%
# set JAVA_HOME via Control Panel → System → Advanced → Environment Variables
# and add %JAVA_HOME%\bin to Path
```

**macOS (zsh)**

```zsh
brew install openjdk@11
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 11)' >> ~/.zshrc
echo 'export PATH="$JAVA_HOME/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
java -version && echo $JAVA_HOME
```

### Install PostgreSQL 13

**Windows (PowerShell)**

```powershell
# Download PG13: https://www.postgresql.org/download/windows/
psql -U postgres
```

**macOS (zsh)**

```zsh
brew install postgresql@13
echo 'export PATH="/opt/homebrew/opt/postgresql@13/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
psql --version
```

### Install Tomcat 9.0.x

**Windows (PowerShell)**

```powershell
# Download: https://tomcat.apache.org/download-90.cgi
# Unzip to C:\Tomcat9 and add C:\Tomcat9\bin to Path
catalina version
```

**macOS (zsh)**

```zsh
brew install tomcat@9
catalina version
```

---

## 2) Clone & Build

```bash
git clone https://github.com/ORCID/ORCID-Source.git
cd ORCID-Source
mvn clean install -DskipTests
```

---

## 3) Databases

> **ORCID employees:** **`[Private Guide Placeholder Link]`**.

Create users, DBs, and minimal features for local development.

```bash
# As a superuser (e.g., postgres):
psql -U postgres -c "CREATE USER orcid WITH PASSWORD 'orcid';"
psql -U postgres -c "CREATE USER statistics WITH PASSWORD 'statistics';"
psql -U postgres -c "CREATE USER orcidro WITH PASSWORD 'orcidro';"
psql -U postgres -c "CREATE USER dw_user WITH PASSWORD 'dw_user';"

psql -U postgres -c "CREATE DATABASE orcid;"
psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE orcid TO orcid;"

psql -U postgres -c "CREATE DATABASE statistics;"
psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE statistics TO statistics;"

psql -U postgres -c "CREATE DATABASE features;"
psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE features TO orcid;"

psql -U postgres -c "CREATE DATABASE message_listener;"
psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE message_listener TO orcid;"
```

Initialize schema & seed JSON:

```bash
cd ORCID-Source/orcid-core
mvn exec:java -Dexec.mainClass=org.orcid.core.cli.InitDb

cd ..
psql -U postgres -d orcid -f orcid-persistence/src/main/resources/db/updates/json-setup.sql
```

Seed **features** flags (minimum for local dev):

```bash
psql -U postgres -d features -c "
INSERT INTO togglz (feature_name, feature_enabled) VALUES
  ('PROFESSIONAL_ACTIVITIES', 1),
  ('REGISTRATION_2_0',        1),
  ('REGISTRATION_2_1',        1),
  ('SIGN_IN_UPDATES_V1',      1)
ON CONFLICT (feature_name) DO UPDATE SET feature_enabled = EXCLUDED.feature_enabled;
"
```

## 4) Redis

> **ORCID employees:** **`[Private Guide Placeholder Link]`**.

Install and run Redis locally

**Windows (PowerShell)**

```powershell
docker run -d --name redis-local -p 6379:6379 redis:7-alpine
docker exec -it redis-local redis-cli ping  # PONG
```

**macOS (zsh)**

```zsh
brew install redis
brew services start redis
redis-cli ping  # PONG
```

Set these in `properties/development.properties` and **restart Tomcat**:

TBD: More details on how to run reddis trhough a secure port need to be added


```properties
org.orcid.core.utils.cache.redis.enabled=true
org.orcid.core.utils.cache.redis.host=localhost
org.orcid.core.utils.cache.redis.port=6379
org.orcid.core.utils.cache.redis.password=

org.orcid.core.utils.cache.papi.redis.enabled=true
org.orcid.core.utils.cache.papi.redis.host=localhost
org.orcid.core.utils.cache.papi.redis.port=6379
org.orcid.core.utils.cache.papi.redis.password=

org.orcid.core.utils.cache.session.redis.host=localhost
org.orcid.core.utils.cache.session.redis.port=6379
org.orcid.core.utils.cache.session.redis.password=
```

---

## 5) JWKs for OpenID Connect
> **ORCID employees:** **`[Private Guide Placeholder Link]`**.


* Generate RS256 at [https://mkjwk.org](https://mkjwk.org) and set in the properties file:

```
org.orcid.openid.jwksKeyName=
org.orcid.openid.jwksLocation=
org.orcid.openid.jwksTestKey= xxxxxx
```

---

## 6) IDE Setup

### Option A: IntelliJ IDEA

**📕 [IntelliJ IDEA Setup Guide →](SETUP_INTELLIJ.md)**

* Full-featured Java IDE with excellent Maven support
* IntelliJ Ultimate recommended (Community works with Smart Tomcat plugin)
* Powerful refactoring and debugging tools
* Great for developers familiar with JetBrains IDEs

---

Both IDEs support:
* ✅ Debugging with breakpoints
* ✅ Hot code reload (limited)
* ✅ Maven integration
* ✅ Git integration
* ✅ Tomcat integration

---

Choose your preferred IDE and follow the corresponding setup guide:

### Option B: VS Code

**📘 [VS Code Setup Guide →](SETUP_VSCODE.md)**

* Modern, lightweight editor with excellent Java support
* Free and open source
* Built-in Git integration
* Automated setup script included
* Great for developers familiar with VS Code

**Quick start:**
```bash
./vscode-setup.sh  # Automated configuration
code .             # Open in VS Code
```

Also see: [VSCODE_QUICKSTART.md](VSCODE_QUICKSTART.md) for a quick reference guide.


---

## 7) Test your setup

Once your IDE is configured:

* Start Tomcat in debug mode (see your IDE setup guide)
* Browse to `http://localhost:8080/orcid-web/ping`
* You should see a blank page with the text `{tomcatUp:true}`

If you see this response, your setup is working correctly! 🎉
---

## 8) Configure Message Listener
> This step is optiona, and not require to run the orcid-web or api. 
> Please note this section has not been updated in a few years, and some of this content might be outdated.


* Create a directory to be used as the message store directory for the ActiveMQ broker (changing the [PATH]/git/ path to a path on your machine)

        mkdir [PATH]/git/mq

* Go to File -> New -> Other.

* Filter for 'Server', select and click Next.

* Expand the folder Apache, select Apache Tomcat.

* Choose the same version as you selected in Tomcat Setup above

* Change Server name field to Message Listener

* Click Next and Finish.

* Select Window -> Show View -> Servers

* Double Click "Message Listener"

* Select Open launch configuration

* Select Arguments 

* In VM Arguments add the following (changing org.orcid.persistence.path to the path to the directory you created above):

        -Dorg.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH=true -Dcom.mailgun.testmode=no -Dorg.orcid.message-listener.properties=classpath:message-listener.properties -Dorg.orcid.message-listener.development_mode=true -Dorg.orcid.activemq.config.file=classpath:orcid-activemq.properties -Dorg.orcid.persistence.messaging.enabled=true -Dorg.orcid.persistence.path="[PATH]/git/mq"

* Click Ok

* Save and close the server configuration view.

* In the Servers tab, right click on "Message Listener".

* Select "Add and Remove." Add orcid-activemq and orcid-message-listener

* In[orcid-message-listener.properties](https://github.com/ORCID/ORCID-Source/blob/master/orcid-message-listener/src/main/resources/message-listener.properties#L47), change the value of org.orcid.message-listener.api.read_public_access_token to a valid /read-public access token. See [Basic tutorial: Searching data using the ORCID API](https://members.orcid.org/api/tutorial/search-orcid-registry for instructions) for steps to generate a token.

* In the Servers tab, right click on "Message Listener" and click Start.

---

## 9) Configure SOLR
> This step is optiona, and not require to run the orcid-web or api. 
> Please note this section has not been updated in a few years, and some of this content might be outdated.

A local SOLR server is needed in order to test search functionality locally. SOLR includes its own development environment, which runs separately from the Tomact servers you created in Eclipse. 

To install and configure SOLR locally, follow the steps in solr-config/README.md (https://github.com/ORCID/ORCID-Source/tree/master/solr-config)

Optionally, you can connect your local environment to the QA SOLR instance by adding these to your VM Arguments

* Double Click "Apache Tomcat Server"

* Select Open launch configuration

* Select Arguments 

* In VM Arguments add the following to the existing list of arguments (don't delete the existing args!). Get the current QA SOLR machine from another developer.

         -Dorg.orcid.persistence.solr.url=http://[QA SOLR machine]/qa/solr -Dorg.orcid.persistence.solr.read.only.url=http://[QA SOLR machine]/qa/solr  

* Click Ok
         

---

## 10) Orcid Angular

Follow the instructions on the public repo [https://github.com/ORCID/orcid-angular](https://github.com/ORCID/orcid-angular)

### Legacy: Configure frontend (Optional to run the old ui)
> Please note this section has not been updated in a few years, and some of this content might be outdated.

Follow next instructions in order to generate the core javascript file.

See [How to produce angular_orcid_generated.js](https://github.com/ORCID/ORCID-Source/blob/master/orcid-nodejs/README.md). 
For background about webpack see [Webpack setup](https://github.com/ORCID/ORCID-Source/tree/master/orcid-web/src/main/webapp/static/javascript)
.

---

## 11) Proxy for local registry

> **ORCID employees:** **`[Private Guide Placeholder Link]`**.

As of now there is not a public version of this proxy.

---
