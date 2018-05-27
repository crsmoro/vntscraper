var webpackMerge = require('webpack-merge');

module.exports = webpackMerge(module.exports, {
	mode : 'development',
	devtool : 'eval'
});