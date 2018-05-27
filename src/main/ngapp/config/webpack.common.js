var path = require('path');
var webpack = require('webpack');
var helpers = require('./helpers');
const HtmlWebpackPlugin = require('html-webpack-plugin')
const MiniCssExtractPlugin = require("mini-css-extract-plugin");

module.exports = {
	
	mode : 'development',
	
	output : {
		path: helpers.root('', 'dist'),
		filename : '[name].js',
		chunkFilename : '[id].chunk.js'
	},
	
	entry : {
		'polyfills' : './src/polyfills.ts',
		'vendor' : './src/vendor.ts',
		'app' : './src/main.ts'
	},

	resolve : {
		extensions : [ '.ts', '.js', '.css', '.html', '.scss' ]
	},

	module : {
		rules : [
			{
				test : /\.ts$/,
				loader : 'ts-loader',
				exclude : /node_modules/,
				options : {
					transpileOnly : true
				}
			},
			{
				test : /\.ts$/,
				loader : 'angular2-template-loader',
				exclude : /node_modules/
			},
			{
				test : /\.html$/,
				loader : 'raw-loader',
				options : {
					caseSensitive : true
				}
			},
			{
				test : /\.(png|jpe?g|gif|svg|woff|woff2|ttf|eot|ico)$/,
				loader : 'url-loader?name=assets/[name].[hash].[ext]&limit=100000'
			},
			{
				test : /\.css$/,
				exclude : helpers.root('src', 'app'),
				use: [
		          MiniCssExtractPlugin.loader,
		          "css-loader"
		        ]
			},
			{
				test : /\.css$/,
				include : helpers.root('src', 'app'),
				loader : 'raw-loader'
			},
			{
				test : /\.scss$/,
	            use: [{
	                loader: "style-loader" // creates style nodes from JS strings
	            }, {
	                loader: "css-loader" // translates CSS into CommonJS
	            }, {
	                loader: "sass-loader" // compiles Sass to CSS
	            }]
			}
		]
	},

	plugins : [
		new HtmlWebpackPlugin({
			template : 'src/public/index.html',
			production: true,
			minify: true && {
		        removeComments: true,
		        collapseWhitespace: true,
		        removeRedundantAttributes: true,
		        useShortDoctype: true,
		        removeEmptyAttributes: true,
		        removeStyleLinkTypeAttributes: true,
		        keepClosingSlash: true,
		        minifyJS: true,
		        minifyCSS: true,
		        minifyURLs: true,
			}
		}),
		new MiniCssExtractPlugin({
	      // Options similar to the same options in webpackOptions.output
	      // both options are optional
	      filename: "[name].css",
	      chunkFilename: "[id].css"
	    })
	]
};