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
        path: '../.',
        filename: "angular_orcid_generated.js"
    },
    plugins: [
        new webpack.DefinePlugin({
            'NODE_ENV': JSON.stringify("production"),
            'process.env':{
                'NODE_ENV': JSON.stringify("production")
            }
        })
    ],
    resolve: {
        alias: {
            "@angular/upgrade/static": "@angular/upgrade/bundles/upgrade-static.umd.js"
        }
    },
    watch: false
};
//console.log("CONFIG SET TO " + process.env.NODE_ENV)
