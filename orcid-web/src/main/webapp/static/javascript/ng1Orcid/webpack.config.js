var webpack = require('webpack');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const CopyPlugin = require('copy-webpack-plugin');


module.exports = {
    context: __dirname + "/",
    entry: "./require.js",
    mode: 'development',
    module: {
        rules: [{
            test: /\.ts$/, 
            use: [{
                loader: 'ts-loader'
            }]
        }]
    },
    output: {
        path: __dirname + '/dist',
        filename: "angular_orcid_generated.js"
    },
    plugins: [
    new webpack.DefinePlugin({
        'NODE_ENV': JSON.stringify(process.env.NODE_ENV || 'development'),
        'process.env':{
            'NODE_ENV': JSON.stringify(process.env.NODE_ENV || 'development')
        }
    }),
    new HtmlWebpackPlugin({
        template: __dirname + '/app/templates/index.html',
        output: __dirname + '/dist',
        // inject: 'head'
    }),
    new CopyPlugin([
        { from: '../../css/**/*.css', to: './ng1Orcid//dist' },
        { from: '../../fonts/**/*', to: './ng1Orcid//dist' },
        { from: '../../img/**/*', to: './ng1Orcid//dist' },
        { from: '../../twitter-bootstrap/**/*', to: './ng1Orcid/dist' },
        {from: '../jquery/**/*', to: './javascript/' },
        {from: '../jquery-migrate/**/*', to: './javascript/' },
        {from: '../jqueryui/**/*', to: './javascript/' },
        {from: '../typeahead/**/*', to: './javascript/' },
        {from: '../plugins.js', to: './javascript/' },
        {from: '../orcid.js', to: './javascript/' },
        {from: '../script.js', to: './javascript/' },



      ]),
     ],
    resolve: {
        alias: {
            "@angular/upgrade/static": "@angular/upgrade/bundles/upgrade-static.umd.js"
        }
    },
    watch: false
}
