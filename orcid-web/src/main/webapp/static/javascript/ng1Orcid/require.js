__webpack_public_path__ = window.resourceBasePath;

require("../../css/glyphicons.css");
require("../../css/social.css");
//  require("../../css/filetypes.css");
// require("../../css/nova-light/theme.css");
// require("../../css/primeicons.css");
// require("../../css/primeng.min.css");
require("../../twitter-bootstrap/3.3.6/css/bootstrap.min.css");
require("../../css/orcid.new.css");
require("../../css/academicons.css");
// require("../../css/jquery-ui-1.10.0.custom.min.css");
// require("../../css/noto-font.css");
require("../../css/noto-sans-googlefonts.css");
if (window.location.href.indexOf("/print") > 0) {
  require("../../css/orcid-print.css");
}

function requireAll(requireContext) {
  return requireContext.keys().map(requireContext);
}

require("./app/polyfills.ts");
require("jquery");
// require("../jqueryui/1.10.0/jquery-ui.min.js");
// require("../jquery-migrate/1.3.0/jquery-migrate-1.3.0.min.js");
require("../typeahead/0.9.3/typeahead.min.js");
require("../script.js");
require("../orcid.js");
require("../plugins.js");
require("./app/main.ts");
require("../XSRF.js");
requireAll(require.context("./app/directives", true, /^\.\/.*\.ts$/));
requireAll(require.context("./app/modules", true, /^\.\/.*\.ts$/));
requireAll(require.context("./app/pipes", true, /^\.\/.*\.ts$/));
requireAll(require.context("./app/shared", true, /^\.\/.*\.ts$/));
