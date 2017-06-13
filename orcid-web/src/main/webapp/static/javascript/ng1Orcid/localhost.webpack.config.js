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
        path: "/Users/tom/Documents/workspace-sts/.metadata/.plugins/org.eclipse.wst.server.core/tmp0",
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