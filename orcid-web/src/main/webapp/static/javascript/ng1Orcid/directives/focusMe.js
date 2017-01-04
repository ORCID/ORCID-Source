/*
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */

/*angular.module('orcidApp').directive('focusMe', function($timeout) {
    return {
      scope: { trigger: '=focusMe' },
      link: function(scope, element) {
        $timeout( //[fn], [delay], [invokeApply], [Pass]
            function(){
                if (scope.trigger) {
                    element[0].focus();
                    scope.trigger = false;
                }
            },
            1000
        );
      }
    };
});*/

orcidNgModule.directive('focusMe', function($timeout) {
    return {
      scope: { trigger: '=focusMe' },
      link: function(scope, element) {
        $timeout( //[fn], [delay], [invokeApply], [Pass]
            function(){
                if (scope.trigger) {
                    element[0].focus();
                    scope.trigger = false;
                }
            },
            1000
        );
      }
    };
});