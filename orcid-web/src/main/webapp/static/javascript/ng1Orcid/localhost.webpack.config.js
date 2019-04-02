var webpack = require("webpack");
const path = require("path");

module.exports = {
  context: __dirname + '/',
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
        test: /((script)|(orcid)|(plugins)|(typeahead.min)|(XSRF.js))\.js/, //(jquery.min)|(jquery-ui.min)|(typeahead.min)|(jquery-migrate-1.3.0.min)|
        use: ["script-loader"]
      },
      {
        test: /\.css$/,
        loaders: ["style-loader", "css-loader"]
      },
      {
        test: /\.svg($|\?)|\.png($|\?)|\.gif($|\?)/,
        loader: "url-loader",
        options: {
          limit: 8192,
          fallback: 'file-loader',
          emitFile: false,
          path: path.resolve(__dirname, 'dist/assets'),
          name: "/[path][name].[ext]",
          context: '../../',
        },
      },
      {
        test: /\.woff($|\?)|\.woff2($|\?)|\.ttf($|\?)|\.eot($|\?)/,
        loader: "file-loader",
        options: {
          emitFile: false,
          path: path.resolve(__dirname, 'dist/assets'),
          name: "/[path][name].[ext]",
          context: '../../',
        },
      },
      {
        test: require.resolve('jquery'),
        use: [{
            loader: 'expose-loader',
            options: 'jQuery'
        },{
            loader: 'expose-loader',
            options: '$'
        }]
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
