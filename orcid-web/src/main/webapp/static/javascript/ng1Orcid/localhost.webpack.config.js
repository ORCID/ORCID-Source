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
    output: { ///opt/apache-tomcat-8.0.21/webapps
        path: "/Users/tom/Documents/workspace-sts/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/orcid-web/static/javascript",
        filename: "angular_orcid_generated.js"
    },
    plugins: [
    ],
    resolve: {
        alias: {
            "@angular/upgrade/static": "@angular/upgrade/bundles/upgrade-static.umd.js"
        }
    },
    watch: true
};