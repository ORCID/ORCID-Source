var webpack = require('webpack');

module.exports = {
    context: __dirname + "/",
    entry: {
        app: "./require.js",
        home: "./app/bootstrap_home.ts",
        signin: "./app/bootstrap_signin.ts",
        polyfills: "./app/polyfills.ts",
    },
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
        filename: "[name].js"
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
            'NODE_ENV': "'production'",
            'process.env': {
                'NODE_ENV': "'production'"
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
