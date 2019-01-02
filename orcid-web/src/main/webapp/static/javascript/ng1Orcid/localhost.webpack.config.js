var webpack = require('webpack');

module.exports = {
    context: __dirname + "/",
    entry: {
        app: "./require.js",
        signin: "./app/signin_bootstrap.ts",
        polyfills: "./app/polyfills.ts",
    },
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
        path: __dirname + '/',
        filename: "[name].js"
    },
    plugins: [
        new webpack.DefinePlugin(
            {
                'NODE_ENV': "'development'",
                'process.env': {
                    'NODE_ENV': "'development'"
                }
            }
        )
    ],
    watch: true
};
