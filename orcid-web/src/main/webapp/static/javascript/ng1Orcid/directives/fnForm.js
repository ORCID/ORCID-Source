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
 * For forms submitted using a custom function, Scope: Document
 * 
 * Example:
 * <fn-form update-fn="theCustomFunction()">
 * 
 * </fn-form>
 * 
 */
angular.module('orcidApp').directive('fnForm', function($document) {
    return {
        restrict: 'E',
        scope: {
            updateFn: '&'
        },
        link: function(scope, elm, attrs) { 
            $document.bind("keydown", function(event) {
                if (event.which === 13) {
                      scope.updateFn();                      
                      event.stopPropagation();
                }
            });
                    
        }
    }
});