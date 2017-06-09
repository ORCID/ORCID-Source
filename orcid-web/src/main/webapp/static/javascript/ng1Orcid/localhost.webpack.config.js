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
        path: __dirname,
        filename: "../angular_orcid_generated.js"
    },/*
    output: {
        path: "/opt/apache-tomcat-8.0.21/webapps/orcid-web/static/javascript",
        filename: "angular_orcid_generated.js"
    },*/
    plugins: [
    ],
    resolve: {
        alias: {
            "@angular/upgrade/static": "@angular/upgrade/bundles/upgrade-static.umd.js"
        }
    },
    watch: true
};