# Refactoring JS libraries

## Requirements
- [NodeJS] (https://nodejs.org/en/)
- [Npm] (https://docs.npmjs.com/getting-started/installing-node) (global install)
- [Webpack] (http://webpack.github.io/docs/tutorials/getting-started/) (global install)

This setup will allow us to integrate libraries and other tools like scss, typescript, etc easily.

## Steps (assuming you already have your dev environment running)
- Open your console.
- Go to the folder "...\orcid-web\src\main\webapp\static\javascript\ng1Orcid\package.json"
- If this is the **FIRST time** or a **NEW MODULE WAS ADDED** run the command "npm install". Otherwise, skip this step.
- Run the command "webpack".
- Start working, everything will be updated automatically, you are ready!

## Folder Structure

### ng1Orcid
- app/ It will contain the folder App with all the js functionality.
- package.json It has the NPM dependencies that we are using/will be using in the project. **Don't edit this manually**. Instead use the command npm install modulename --save-dev to add the new modules.
- require.js This file has the paths of the files/folders that will be bundled. 
- webpack.config.js This file has the configuration for Webpack. When you run the command "webpack" in the console, it will load the config from here, including the source libraries, destination file, watch for live changes, etc. 


#### app
It will contain all the js functionality. Currently there are other js files with legacy functionality in the /static/javascript folder. The idea is to move this functionality here after the refactor is more stable.

Currently only has angularOrcidOriginal.js. This file is where all the base code previously saved on "/static/javascript/angularOrcid.js" has been moved too.

Eventually you will find other subfolders: directives, controllers, services, filters, modules, etc.

#### node_modules
It has all the modules installed with "NPM". **Don't modify this**, it is generated automatically.

## Do/don't
- **DON'T work on "/static/javascript/angularOrcid.js" anymore**! This file will be overwritted always and you will lose your work.
- **DO work on /static/javascript/ng1Orcid/app**. Here will be stored all the js files, and they will be bundled in a single js file by webpack.

- If you need to modify functionality previously saved on "/static/javascript/angularOrcid.js" please work on "/static/javascript/ng1Orcid/app/angularOrcidOriginal.js" instead, and verify that the feature hasn't been moved to a different file.

- **DON'T put all the code in a single js file**. Currently we are working to divide the big js file to follow the single responsability approach (one task per file - controllers, directives, etc).
- **DO** feel free to create new files inside the App folder and the eventual subfolders if you need to add new functionality.

- **DO** name files using the function/mmodule name that you defined in your logic for ease of use. I.E: if your controller is named "awesomeController", name the file: awesomeController.js