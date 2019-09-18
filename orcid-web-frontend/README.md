# orcid-web-frontend
Tomcat container used to run the Angular frontend of the ORCID Registry [orcid-angular](https://github.com/ORCID/orcid-angular)

## Run locally in Spring Tool Suite

### Prerequisites
- Set up the ORCID registry dev environment per [Development environment setup](https://github.com/ORCID/ORCID-Source/blob/master/DEVSETUP.md)

### Setup
1. CD into the parent directory of your local ORCID-Source repo, for example if ORCID-Source is in ~/git/ORCID-Source, then
    cd ~/git

2. Clone [orcid-angular](https://github.com/ORCID/orcid-angular)
    git clone git@github.com:ORCID/orcid-angular.git

3. In Spring Tool Suite, 

### Build and deploy

1. CD to ORCID-Source/orcid-web-frontend directory
    cd ~/git/ORCID-Source/orcid-web-frontend

2. Build the Angular project and copy it into orcid-web-frontend
    mvn -P local clean install

3. In Spring Tool Suite, right-click your Tomcat server and select "Add and Remove" Add orcid-web-frontend

4. Right-click your Tomcat server and choose Start

5. Navigate to https://localhost:8443/orcid-web-frontend/en/ to see the Angular app
    
# License
See [LICENSE.md](https://github.com/ORCID/ORCID-Source/blob/master/LICENSE)

