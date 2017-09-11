//npm install -g typescript

function requireAll(requireContext) {
  return requireContext.keys().map(requireContext);
}

require('./app/polyfills.ts');
require('./app/main.ts');
requireAll(require.context("./app/controllers", true, /^\.\/.*\.js$/));
requireAll(require.context("./app/controllers", true, /^\.\/.*\.ts$/));
requireAll(require.context("./app/directives-ng1", true, /^\.\/.*\.js$/));
requireAll(require.context("./app/directives-ng1", true, /^\.\/.*\.ts$/));
//requireAll(require.context("./app/directives", true, /^\.\/.*\.ts$/));
requireAll(require.context("./app/filters", true, /^\.\/.*\.js$/));
requireAll(require.context("./app/filters", true, /^\.\/.*\.ts$/));
requireAll(require.context("./app/modules", true, /^\.\/.*\.js$/));
requireAll(require.context("./app/modules", true, /^\.\/.*\.ts$/));
requireAll(require.context("./app/services", true, /^\.\/.*\.js$/));
requireAll(require.context("./app/services", true, /^\.\/.*\.ts$/));
requireAll(require.context("./app/shared", true, /^\.\/.*\.ts$/));