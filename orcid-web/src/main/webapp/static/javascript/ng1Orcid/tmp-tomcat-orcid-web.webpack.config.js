/* 
 * Assumes:
 * 1. You made a directory under `/tmp-tomcat-orcid-web`. For linux:
 *  `sudo mkdir /tmp-tomcat-orcid-web; sudo chown -R $(whoami) /tmp-tomcat-orcid-web;`
 * 
 * 2. Your Eclipse Tomcat setup is the following
 *  Under `Server Location` (double click on the server, you should see the config
 *  in the eclipse edit window). Note, you must remove all applications and clean 
 *  Tomcat before you are allowed to edit this part of the config. After you make
 *  the change add orcid-web back.
 *     Server path: /tmp-tomcat-orcid-web
 *     Deploy path: wtpwebapps
 * 
 */
var webpack = require('webpack');

module.exports = {
    context: __dirname + "/",
    entry: "./require.js",
    mode: 'development',
    module: {
        rules: [
            {
                test: /\.ts$/, 
                use: [
                    {
                        loader: 'ts-loader'
                    }
                ]
            }
        ]
    },
    output: {
        path: "/tmp-tomcat-orcid-web/wtpwebapps/orcid-web/static/javascript/ng1Orcid",
        filename: "angular_orcid_generated.js"
    },
    plugins: [
        new webpack.DefinePlugin({
            'NODE_ENV': JSON.stringify(process.env.NODE_ENV || 'development'),
            'process.env':{
                'NODE_ENV': JSON.stringify(process.env.NODE_ENV || 'development')
            }
        })
    ],
    resolve: {
        alias: {
            "@angular/upgrade/static": "@angular/upgrade/bundles/upgrade-static.umd.js"
        },
        extensions: [ ".js", ".ts" ]
    },
    watch: true
}