var AngularCompilerPlugin = require('@ngtools/webpack').AngularCompilerPlugin
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
                        loader: '@ngtools/webpack'
                    }
                ]
            }
        ]
    },
    output: {
        path: __dirname + '/',
        filename: "angular_orcid_generated.js"
    },
    plugins: [
        new webpack.DefinePlugin(
            {
                'NODE_ENV': "'development'",
                'process.env': {
                    'NODE_ENV': "'development'"
                }
            }
        ),
        new AngularCompilerPlugin({
            tsConfigPath: 'tsconfig.json',
            entryModule: 'app/modules/ng2_app#Ng2AppModule',
            sourceMap: true
          })
    ],
    resolve: {
        alias: {
            "@angular/upgrade/static": "@angular/upgrade/bundles/upgrade-static.umd.js"
        }
    },
    watch: true
};
