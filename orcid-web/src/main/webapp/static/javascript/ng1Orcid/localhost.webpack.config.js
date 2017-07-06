var webpack = require('webpack');

module.exports = {
    entry: "./require.js",
    module: {
        loaders: [
            { 
                test: /\.ts$/, 
                loader: 'ts-loader' 
            }
        ]
    },
    output: {
        path: "__dirname",
        filename: "../angular_orcid_generated.js"
    },/*
    Examples:
    output: {
        path: "/Users/tom/Documents/workspace-sts/.metadata/.plugins/org.eclipse.wst.server.core/tmp0",
        filename: "angular_orcid_generated.js"
    },
    
    C:/Documents and Settings/%USER%/workspace/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/orcid-web/static/javascript
    
    ~/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/orcid-web/static/javascript

    /opt/apache-tomcat-8.0.21/webapps/orcid-web/static/javascript/ng1Orcid/
    */
    plugins: [
    ],
    resolve: {
        alias: {
            "@angular/upgrade/static": "@angular/upgrade/bundles/upgrade-static.umd.js"
        }
    },
    watch: true
};