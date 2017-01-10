var webpack = require('webpack');

module.exports = {
    entry: "./require.js",
    module: {
        loaders: [
            //{ test: /\.js$/, loader: 'babel', exclude: [/node_modules/] },
            //{ test: /\.html$/, loader: 'html-loader', exclude: [/node_modules/] },
            //{ test: /\.css$/, loader: "style!css", exclude: [/node_modules/] },
            //{ test: /\.scss$/, loader: "style!css!sass", exclude: [/node_modules/] }
        ]
    },
    output: {
        //library: "orcid",
        //libraryTarget: "var",
        path: __dirname,
        filename: "../angularOrcid.js"
    },
    watch: true
};
