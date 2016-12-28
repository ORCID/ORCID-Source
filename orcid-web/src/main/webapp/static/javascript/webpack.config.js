module.exports = {
    entry: "./require.js",
    output: {
        path: __dirname,
        filename: "main.js"
    }
    /*,
    module: {
        loaders: [
            { test: /\.css$/, loader: "style!css" }
        ]
    }*/
};