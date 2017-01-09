//require('./app/angularOrcidOriginal.js');
//require('./app/directives/focusMe.js');
//require(/Test$/);
//require('./app/', true, /\.js$/);
//require.context('./app/directives', true, /\.js$/);

function requireAll(requireContext) {
  return requireContext.keys().map(requireContext);
}

require('./app/angularOrcidOriginal.js');
requireAll(require.context("./app/controllers", true, /^\.\/.*\.js$/));
requireAll(require.context("./app/directives", true, /^\.\/.*\.js$/));
requireAll(require.context("./app/services", true, /^\.\/.*\.js$/));