var webpack = require('webpack');

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
