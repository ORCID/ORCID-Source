var webpack = require('webpack');
const UglifyJsPlugin = require('uglifyjs-webpack-plugin');

module.exports = {
    context: __dirname + "/",
    entry: "./require.js",
    mode: 'production',
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
        path: __dirname + '/',
        filename: "angular_orcid_generated.js"
    },
    optimization: {
        minimizer: [
          new UglifyJsPlugin({
            uglifyOptions: {
                mangle: false,
                compress: true,
                comments: false

            }
            
          })
        ]
    },
    plugins: [
        function()
        {
            this.plugin("done", function(stats)
            {
                if (stats.compilation.errors && stats.compilation.errors.length)
                {
                    console.log(stats.compilation.errors[0].message);
                    process.exit(1);
                }
            });
        },
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
        }
    },
    watch: false
}
