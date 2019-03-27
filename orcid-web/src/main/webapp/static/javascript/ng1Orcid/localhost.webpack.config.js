var webpack = require("webpack");
const path = require("path");

module.exports = {
  context: __dirname + "/",
  entry: "./require.js",
  mode: "development",
  module: {
    rules: [
      {
        test: /\.ts$/,
        use: [
          {
            loader: "ts-loader"
          }
        ]
      },
      {
        test: /((jquery.min)|(jquery-ui.min)|(jquery-migrate-1.3.0.min)|(typeahead.min)|(script)|(orcid)|(plugins))\.js/,
        use: ["script-loader"]
      },
      {
        test: /\.css$/,
        loaders: ["style-loader", "css-loader"]
      },
      {
        test: /\.woff($|\?)|\.woff2($|\?)|\.ttf($|\?)|\.eot($|\?)|\.svg($|\?)|\.png($|\?)|\.gif($|\?)/,
        loader: "file-loader",
      }
    ]
  },
  output: {
    path: __dirname + "/",
    filename: "angular_orcid_generated.js"
  },
  plugins: [
    new webpack.DefinePlugin({
      NODE_ENV: "'development'",
      "process.env": {
        NODE_ENV: "'development'"
      }
    })
  ],
  resolve: {
    alias: {
      "@angular/upgrade/static":
        "@angular/upgrade/bundles/upgrade-static.umd.js"
    },
    extensions: [".js", ".ts"]
  },
  watch: true
};
