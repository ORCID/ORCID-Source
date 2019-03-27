//npm install -g typescript

function requireAll(requireContext) {
  return requireContext.keys().map(requireContext);
}

require('./app/polyfills.ts');
require('./app/main.ts');
require('bluebird')
requireAll(require.context("./app/directives", true, /^\.\/.*\.ts$/));
requireAll(require.context("./app/modules", true, /^\.\/.*\.ts$/));
requireAll(require.context("./app/pipes", true, /^\.\/.*\.ts$/));
requireAll(require.context("./app/shared", true, /^\.\/.*\.ts$/));