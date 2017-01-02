module.exports = {
    entry: "./require.js",
    output: {
        path: __dirname,
        filename: "main.js"
    },
    watch: true
    /*,
    module: {
        loaders: [
            { test: /\.css$/, loader: "style!css" }
        ]
    }*/
};