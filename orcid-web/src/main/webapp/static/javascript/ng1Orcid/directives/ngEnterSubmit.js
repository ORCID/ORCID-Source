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

/*
 * For forms submitted using the default submit function (Scope: document)
 * Not necessary to be inside an element, for inputs use ngEnter
 */
orcidNgModule.directive('ngEnterSubmit', function($document) {
    return {
        restrict: 'A',
        link: function(scope, element, attr) {
        	$document.bind("keydown keypress", function(event) {
                if (event.which === 13) {
                   element.submit();
                }
            });

        }
    };
});