//require('./app/angularOrcidOriginal.js');
//require('./app/directives/focusMe.js');
//require(/Test$/);
//require('./app/', true, /\.js$/);
//require.context('./app/directives', true, /\.js$/);

function requireAll(requireContext) {
  return requireContext.keys().map(requireContext);
}

var modules = requireAll(require.context("./app/", true, /^\.\/.*\.js$/));