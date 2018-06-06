//npm install -g typescript

function requireAll(requireContext) {
  return requireContext.keys().map(requireContext);
}

require('./app/polyfills.ts');
require('./app/main.ts');
requireAll(require.context("./app/controllers", true, /^\.\/.*\.ts$/));
requireAll(require.context("./app/directives-ng1", true, /^\.\/.*\.js$/));
requireAll(require.context("./app/filters", true, /^\.\/.*\.js$/));
requireAll(require.context("./app/modules", true, /^\.\/.*\.ts$/));
requireAll(require.context("./app/pipes", true, /^\.\/.*\.ts$/));
requireAll(require.context("./app/services", true, /^\.\/.*\.js$/));
requireAll(require.context("./app/shared", true, /^\.\/.*\.ts$/));