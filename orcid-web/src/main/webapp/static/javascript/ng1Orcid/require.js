//npm install -g typescript

function requireAll(requireContext) {
  return requireContext.keys().map(requireContext);
}
require('../jquery/2.2.3/jquery.min.js')
require('../jqueryui/1.10.0/jquery-ui.min.js')
require('../jquery-migrate/1.3.0/jquery-migrate-1.3.0.min.js')
require('../typeahead/0.9.3/typeahead.min.js')
require('../script.js');
require('../orcid.js');
require('../plugins.js');
require('./app/polyfills.ts');
require('./app/main.ts');
require('bluebird')
requireAll(require.context("./app/directives", true, /^\.\/.*\.ts$/));
requireAll(require.context("./app/modules", true, /^\.\/.*\.ts$/));
requireAll(require.context("./app/pipes", true, /^\.\/.*\.ts$/));
requireAll(require.context("./app/shared", true, /^\.\/.*\.ts$/));